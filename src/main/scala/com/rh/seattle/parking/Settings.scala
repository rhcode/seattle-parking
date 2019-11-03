package com.rh.seattle.parking

import com.typesafe.config.ConfigFactory

object Settings {
  val config = ConfigFactory.load()

  val timestampFormat = config.getString("ingest.timestampFormat")
}
