package com.overandoutnerd.deviceinfo.home.widgets

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import android.appwidget.AppWidgetManager
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class StorageWidgetReceiver: GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget
        get() = StorageWidget()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        StorageWorker.enqueue(context!!)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        StorageWorker.enqueue(context)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        StorageWorker.cancel(context!!)
    }
}