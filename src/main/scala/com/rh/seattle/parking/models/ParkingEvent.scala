package com.rh.seattle.parking.models

import com.rh.seattle.parking.Settings
import com.rh.seattle.parking.utils.GeoUtil
import com.rh.seattle.parking.utils.GeoUtil.GeoCoordinates
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.util.Try

object ParkingEvent {
  def apply(timestamp: String, paidOccupancyStr: String, coordinatesString: String): Try[ParkingEvent] = Try {
    val parkingTimeStamp = convertStringToDateTime(timestamp)
    val coordinates = GeoUtil.extractCoordinatesFromString(coordinatesString)
    val numberOfPaidSpots = paidOccupancyStr.toInt
    ParkingEvent(parkingTimeStamp, numberOfPaidSpots, coordinates.get)
  }

  private def convertStringToDateTime(timestamp: String): DateTime = {
    val dateTimeFormatter = DateTimeFormat.forPattern(Settings.timestampFormat)
    dateTimeFormatter.parseDateTime(timestamp)
  }
}

case class ParkingEvent(occupancyDateTime: DateTime, numberOfPaidSpots: Int, location: GeoCoordinates)
