package com.rasifara.workmanagerdemo

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PeriodicRequestWorker(context: Context, params: WorkerParameters) : Worker(context, params){
    override fun doWork(): Result {
        val data = getDate(System.currentTimeMillis())
        Log.i("PeriodicWorkRequest", "do work execution time $data")

        return Result.success()
    }

    private fun getDate(milliseconds: Long) : String {
        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS", Locale.getDefault())

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliseconds
        return formatter.format(calendar.time)
    }
}