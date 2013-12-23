package com.michalrus.helper

import android.app.Activity

trait ScalaActivity extends ViewHelper with MiscHelper with ConcurrencyHelper {
  this: Activity =>

  override def currentActivity = this

  def find[T](id: Int) = findViewById(id).asInstanceOf[T]

}
