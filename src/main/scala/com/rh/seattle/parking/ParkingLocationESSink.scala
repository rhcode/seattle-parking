package com.rh.seattle.parking

import com.rh.seattle.parking.GeoUtil.GeoCoordinates
import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkFunction, RequestIndexer}
import org.elasticsearch.client.Requests
import scala.collection.JavaConverters._
import scala.collection.mutable

class ParkingLocationESSink extends ElasticsearchSinkFunction[(Int, Long, GeoCoordinates, Long)] {
  override def process(element: (Int, Long, GeoCoordinates, Long), ctx: RuntimeContext, indexer: RequestIndexer): Unit = {
    val json = new mutable.HashMap[String, String]()
    json.put("location", s"${element._3.latitude},${element._3.longitude}")
    json.put("count", element._4.toString)
    json.put("time", element._2.toString)

    val indexRequest = Requests.indexRequest()
      .index(Settings.esIndexName)
      .`type`(Settings.esDocumentType)
      .source(json.asJava)

    indexer.add(indexRequest)
  }
}
