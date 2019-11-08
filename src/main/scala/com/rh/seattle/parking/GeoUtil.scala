package com.rh.seattle.parking

import java.util.regex.Pattern

object GeoUtil {
  case class GeoCoordinates(latitude: Double, longitude: Double)

  def extractCoordinatesFromString(coordinatesString: String): Option[GeoCoordinates] = {
    val pattern = Pattern.compile("POINT \\((-?\\p{Digit}+(.\\p{Digit}+)?) (-?\\p{Digit}+(.\\p{Digit}+)?)\\)")
    val matcher = pattern.matcher(coordinatesString)

    if (matcher.find()) {
      Some(GeoCoordinates(matcher.group(3).toDouble, matcher.group(1).toDouble))
    } else
      None
  }

  object SeattleBoundaries {
    val North = 47.736636
    val South = 47.497270
    val East = -122.226870
    val West = -122.452929
    val Width = Math.abs(West - East)
    val Length = Math.abs(South - North)
  }

  // arbitrary values
  val cellLength = 0.0010
  val cellWidth = 0.0005

  val horizontalCellCount = Math.floor(SeattleBoundaries.Width / cellWidth).toInt
  val verticalCellCount = Math.floor(SeattleBoundaries.Length / cellLength).toInt

  def isInSeattle(location: GeoCoordinates): Boolean = {
    location.longitude <= SeattleBoundaries.East && location.longitude >= SeattleBoundaries.West &&
      location.latitude >= SeattleBoundaries.South && location.latitude <= SeattleBoundaries.North
  }

  // we create a grid with numbers starting from 0. Numbering goes from top left to right bottom
  def getCellIdFromCoordinates(location: GeoCoordinates): Int = {
    val xCoordinate = Math.floor((Math.abs(SeattleBoundaries.West) - Math.abs(location.longitude)) / cellWidth).toInt
    val yCoordinate = Math.floor((Math.abs(SeattleBoundaries.North) - Math.abs(location.latitude)) / cellLength).toInt

    (yCoordinate * horizontalCellCount) + xCoordinate
  }

  def getCellCentreCoordinates(cellId: Int): GeoCoordinates = {
    val cellTopLeftCornerXCoordinate = (cellId % horizontalCellCount) * cellWidth
    val cellCentreXCoordinate = cellTopLeftCornerXCoordinate + (cellWidth / 2)

    val physicalCellCentreXCoordinate = (Math.abs(SeattleBoundaries.West) - cellCentreXCoordinate).toFloat * -1.0F

    val cellTopLeftCornerYCoordinate = (cellId / horizontalCellCount) * cellLength
    val cellCentreYCoordinate = cellTopLeftCornerYCoordinate + (cellLength / 2)

    val physicalCellCentreYCoordinate = (Math.abs(SeattleBoundaries.North) - cellCentreYCoordinate).toFloat

    GeoCoordinates(physicalCellCentreYCoordinate, physicalCellCentreXCoordinate)
  }
}
