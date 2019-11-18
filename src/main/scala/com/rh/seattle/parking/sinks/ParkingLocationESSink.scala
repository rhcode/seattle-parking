package com.rh.seattle.parking.sinks

import com.rh.seattle.parking.utils.GeoUtil.GeoCoordinates
import com.rh.seattle.parking.Settings
import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkFunction, RequestIndexer}
import org.elasticsearch.client.Requests

import scala.collection.JavaConverters._
import scala.collection.mutable

class ParkingLocationESSink extends ElasticsearchSinkFunction[(Int, Long, GeoCoordinates, Long)] {
  override def process(element: (Int, Long, GeoCoordinates, Long), ctx: RuntimeContext, indexer: RequestIndexer): Unit = {
    val json = new mutable.HashMap[String, String]()
    json.put(Settings.Sink.locationFieldName, s"${element._3.latitude},${element._3.longitude}")
    json.put(Settings.Sink.countFieldName, element._4.toString)
    json.put(Settings.Sink.timestampFieldName, element._2.toString)

    val indexRequest = Requests.indexRequest()
      .index(Settings.Sink.esIndexName)
      .`type`(Settings.Sink.esDocumentType)
      .source(json.asJava)

    indexer.add(indexRequest)
  }
}
