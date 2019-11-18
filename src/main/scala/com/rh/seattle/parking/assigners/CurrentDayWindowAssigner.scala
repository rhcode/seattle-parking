package com.rh.seattle.parking.assigners

import java.util
import java.util.Collections

import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.typeutils.TypeSerializer
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner.WindowAssignerContext
import org.apache.flink.streaming.api.windowing.triggers.{EventTimeTrigger, Trigger}
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.joda.time.DateTime

class CurrentDayWindowAssigner extends WindowAssigner[Object, TimeWindow] {
  override def assignWindows(event: Object, timestamp: Long, context: WindowAssignerContext): util.Collection[TimeWindow] = {
    val windowStart = new DateTime(timestamp).withTimeAtStartOfDay()
    val windowEnd = new DateTime(timestamp).withTime(23, 59, 59, 59)
    Collections.singletonList(new TimeWindow(windowStart.getMillis, windowEnd.getMillis))
  }

  override def isEventTime: Boolean = true

  override def getDefaultTrigger(env: StreamExecutionEnvironment): Trigger[Object, TimeWindow] = {
    EventTimeTrigger.create()
  }

  override def getWindowSerializer(executionConfig: ExecutionConfig): TypeSerializer[TimeWindow] = {
    new TimeWindow.Serializer
  }
}
