package com.rh.seattle.parking.sinks

import com.rh.seattle.parking.Settings
import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkFunction, RequestIndexer}
import org.elasticsearch.client.Requests

import scala.collection.JavaConverters._
import scala.collection.mutable

class OccupancyPeakTimesESSink extends ElasticsearchSinkFunction[(Long, Float)]{
  override def process(element: (Long, Float), ctx: RuntimeContext, indexer: RequestIndexer): Unit = {
    val json = new mutable.HashMap[String, String]()
    json.put(Settings.Sink.timestampFieldName, element._1.toString)
    json.put(Settings.Sink.paidOccupancyRatio, element._2.toString)

    val indexRequest = Requests.indexRequest()
      .index(Settings.Sink.esOccupancyIndexName)
      .`type`(Settings.Sink.esDocumentType)
      .source(json.asJava)

    indexer.add(indexRequest)
  }
}
