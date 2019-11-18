package com.rh.seattle.parking.assigners

import com.rh.seattle.parking.Settings
import com.rh.seattle.parking.models.ParkingEvent
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.windowing.time.Time

class TimestampAssigner extends BoundedOutOfOrdernessTimestampExtractor[ParkingEvent](Time.minutes(Settings.latenessTolerance)) {
  override def extractTimestamp(parkingEvent: ParkingEvent): Long = parkingEvent.occupancyDateTime.getMillis
}
