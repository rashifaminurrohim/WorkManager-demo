package com.rasifara.workmanagerdemo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnOneTimeRequest = findViewById<Button>(R.id.btnOneTimeRequest)
        val tvOneTimeRequest = findViewById<TextView>(R.id.tvOneTimeRequest)
        val btnPeriodicRequest = findViewById<Button>(R.id.btnPeriodicRequest)

        btnOneTimeRequest.setOnClickListener {
            val oneTimeRequestConstraints = Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val data = Data.Builder()
            data.putString("inputKey", "input value")

            val sampleWork = OneTimeWorkRequest
                .Builder(OneTimeRequestWorker::class.java)
                .setInputData(data.build())
                .setConstraints(oneTimeRequestConstraints)
                .build()

            WorkManager.getInstance(this@MainActivity).enqueue(sampleWork)

            WorkManager.getInstance(this@MainActivity).getWorkInfoByIdLiveData(sampleWork.id)
                .observe(this, Observer { workInfo: WorkInfo ->
                    OneTimeRequestWorker.Companion.logger("Status: ${workInfo.state.name}")
                    if (workInfo != null) {
                        when (workInfo.state) {
                            WorkInfo.State.ENQUEUED -> {
                                tvOneTimeRequest.text = "Status: Task Enqueued."
                            }

                            WorkInfo.State.RUNNING -> {
                                tvOneTimeRequest.text = "Status: Task Running."
                            }

                            WorkInfo.State.BLOCKED -> {
                                tvOneTimeRequest.text = "Status: Task Blocked."
                            }

                            else -> {
                                tvOneTimeRequest.text = "Status: Task State Else Part."
                            }

                        }
                    }
                    if (WorkInfo != null && workInfo.state.isFinished) {
                        when (workInfo.state) {
                            WorkInfo.State.SUCCEEDED -> {
                                tvOneTimeRequest.text = "Task successful."

                                val successOutputData = workInfo.outputData
                                val outputText = successOutputData.getString("outputKey")
                                Log.i("Worker Output", "$outputText")
                            }

                            WorkInfo.State.FAILED -> {
                                tvOneTimeRequest.text = "Task Failed."
                            }

                            WorkInfo.State.CANCELLED -> {
                                tvOneTimeRequest.text = "Task cancelled."
                            }

                            else -> {
                                tvOneTimeRequest.text = "Task state isFinished else part."
                            }
                        }
                    }
                })
        }

        btnPeriodicRequest.setOnClickListener {
            val periodicRequestConstraints = Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val periodicWorkRequest = PeriodicWorkRequest.Builder(
                PeriodicRequestWorker::class.java,
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(periodicRequestConstraints)
                .build()
            WorkManager.getInstance(this@MainActivity).enqueueUniquePeriodicWork("Periodic Work Request", ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest)
        }
    }
}