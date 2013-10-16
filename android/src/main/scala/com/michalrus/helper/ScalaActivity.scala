package com.michalrus.helper

import android.app.Activity

trait ScalaActivity extends Activity with ViewHelper {

  def find[T](id: Int) = findViewById(id).asInstanceOf[T]

}