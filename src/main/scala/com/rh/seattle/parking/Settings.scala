package com.rh.seattle.parking

import com.typesafe.config.ConfigFactory

object Settings {
  val config = ConfigFactory.load()

  val timestampFormat = config.getString("ingest.timestampFormat")

  val windowSize = config.getInt("stream.slidingWindowSize")

  val windowSlide = config.getInt("stream.slideSize")

  val latenessTolerance = config.getInt("stream.latenessTolerance")

  val filePath = config.getString("ingest.filePath")

  val fieldSeparator = config.getString("ingest.fieldSeparator")

  val paidOccupantsWindowSize = config.getInt("stream.paidOccupants.windowSize")

  val paidOccupantsAllowedLateness = config.getInt("stream.paidOccupants.allowedLateness")

  object Sink {
    val esHost = config.getString("sink.host")

    val esPort = config.getInt("sink.port")

    val esIndexName = config.getString("sink.index")

    val esOccupancyIndexName = config.getString("sink.occupancyIndex")

    val esDocumentType = config.getString("sink.docType")

    val countFieldName = config.getString("sink.fields.count")

    val timestampFieldName = config.getString("sink.fields.timestamp")

    val locationFieldName = config.getString("sink.fields.location")

    val paidOccupancyRatio = config.getString("sink.fields.occupancyRatio")
  }
}
