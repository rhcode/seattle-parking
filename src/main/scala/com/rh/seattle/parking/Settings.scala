package com.rh.seattle.parking

import com.typesafe.config.ConfigFactory

object Settings {
  val config = ConfigFactory.load()

  val timestampFormat = config.getString("ingest.timestampFormat")

  val esIndexName = config.getString("sink.index")

  val esDocumentType = config.getString("sink.docType")
}
