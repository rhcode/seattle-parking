package com.rh.seattle.parking

import java.util

import com.rh.seattle.parking.GeoUtil.GeoCoordinates
import com.typesafe.scalalogging.LazyLogging
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink
import org.apache.flink.util.Collector
import org.apache.http.HttpHost

import scala.util.{Failure, Success}

object Driver extends App with LazyLogging {
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

  val text: DataStream[String] = env.readTextFile("./data/Paid_Parking_Nov_4_Downloaded.csv.gz")

  val parkingEventStream: DataStream[ParkingEvent] = text.flatMap { line =>
    val tokens = line.split(',')
    ParkingEvent(tokens(0), tokens(11)) match {
      case Success(event) => Some(event)
      case Failure(_) =>
        None
    }
  }

  val parkingEventStreamWithTimestamps: DataStream[ParkingEvent] = parkingEventStream.assignTimestampsAndWatermarks(
    new BoundedOutOfOrdernessTimestampExtractor[ParkingEvent](Time.minutes(Settings.latenessTolerance)) {
    override def extractTimestamp(parkingEvent: ParkingEvent): Long = parkingEvent.occupancyDateTime.getMillis
  })

  val cellIdStream: DataStream[(Int, Long)] = parkingEventStreamWithTimestamps.map { parkingEvent =>
    (GeoUtil.getCellIdFromCoordinates(parkingEvent.location), 1L)
  }

  val parkingCountsPerCell: DataStream[(Int, Long, Long)] = cellIdStream
    .keyBy(_._1)
    .timeWindow(Time.minutes(Settings.windowSize), Time.minutes(Settings.windowSlide))
    .apply {
      (
        cell: Int,
        window: TimeWindow,
        events: Iterable[(Int, Long)],
        out: Collector[(Int, Long, Long)]
      ) => out.collect((cell, window.getEnd, events.map(_._2).sum))
    }

  val parkingCountsByLocation: DataStream[(Int, Long, GeoCoordinates, Long)] = parkingCountsPerCell.map { event =>
    (event._1, event._2, GeoUtil.getCellCentreCoordinates(event._1), event._3)
  }

  val httpHost = new util.ArrayList[HttpHost]
  httpHost.add(new HttpHost(Settings.Sink.esHost, Settings.Sink.esPort, "http"))

  val esSinkBuilder = new ElasticsearchSink.Builder[(Int, Long, GeoCoordinates, Long)](
    httpHost,
    new ParkingLocationESSink
  )

  esSinkBuilder.setBulkFlushMaxActions(1)
  parkingCountsByLocation.addSink(esSinkBuilder.build())

  env.execute()
}
