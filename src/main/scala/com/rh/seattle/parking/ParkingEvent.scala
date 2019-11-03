package com.rh.seattle.parking

import com.rh.seattle.parking.GeoUtil.GeoCoordinates
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.util.Try

object ParkingEvent {
  def apply(timestamp: String, coordinatesString: String): Try[ParkingEvent] = Try {
    val parkingTimeStamp = convertStringToDateTime(timestamp)
    val coordinates = GeoUtil.extractCoordinatesFromString(coordinatesString)
    ParkingEvent(parkingTimeStamp, coordinates.get)
  }

  private def convertStringToDateTime(timestamp: String): DateTime = {
    val dateTimeFormatter = DateTimeFormat.forPattern(Settings.timestampFormat)
    dateTimeFormatter.parseDateTime(timestamp)
  }
}

case class ParkingEvent(occupancyDateTime: DateTime, location: GeoCoordinates)
