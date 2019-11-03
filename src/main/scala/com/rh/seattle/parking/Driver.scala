package com.rh.seattle.parking

import com.typesafe.scalalogging.LazyLogging
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.scala._

import scala.util.{Failure, Success}

object Driver extends App with LazyLogging {
  val env = StreamExecutionEnvironment.getExecutionEnvironment

  val text: DataStream[String] = env.readTextFile("./data/Paid_Parking__Last_48_Hours_.csv.gz")

  val parkingEventStream = text.flatMap { line =>
    val tokens = line.split(',')
    val event = ParkingEvent(tokens(0), tokens(11)) match {
      case Success(event) => Some(event)
      case Failure(exception) =>
        logger.debug("Could not extract event from line", exception)
        None
    }
    event
  }

  parkingEventStream.print()

  env.execute()
}
