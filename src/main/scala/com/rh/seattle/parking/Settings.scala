package com.rh.seattle.parking

import com.typesafe.config.ConfigFactory

object Settings {
  val config = ConfigFactory.load()

  val timestampFormat = config.getString("ingest.timestampFormat")

  val windowSize = config.getInt("stream.slidingWindowSize")

  val windowSlide = config.getInt("stream.slideSize")

  val latenessTolerance = config.getInt("stream.latenessTolerance")

  object Sink {
    val esHost = config.getString("sink.host")

    val esPort = config.getInt("sink.port")

    val esIndexName = config.getString("sink.index")

    val esDocumentType = config.getString("sink.docType")

    val countFieldName = config.getString("sink.fields.count")

    val timestampFieldName = config.getString("sink.fields.timestamp")

    val locationFieldName = config.getString("sink.fields.location")
  }
}
