package com.rh.seattle.parking

import java.util.regex.Pattern

object GeoUtil {
  case class GeoCoordinates(latitude: Double, longitude: Double)

  def extractCoordinatesFromString(coordinatesString: String): Option[GeoCoordinates] = {
    val pattern = Pattern.compile("POINT \\((-?\\p{Digit}+(.\\p{Digit}+)?) (-?\\p{Digit}+(.\\p{Digit}+)?)\\)")
    val matcher = pattern.matcher(coordinatesString)

    if (matcher.find()) {
      Some(GeoCoordinates(matcher.group(1).toDouble, matcher.group(3).toDouble))
    } else
      None
  }
}
