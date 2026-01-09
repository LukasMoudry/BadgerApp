package com.badgerapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
object AlarmScheduler {
    private const val ALARM_REQUEST_CODE = 1000

    /** Schedule an exact alarm at task.nextAskEpoch that will be handled by AlarmReceiver */
    fun schedule(task: Task, ctx: Context) {
        val alarmMgr = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(ctx, AlarmReceiver::class.java).apply {
            putExtra("task_id", task.id)
        }
        val pending = PendingIntent.getBroadcast(
            ctx,
            ALARM_REQUEST_CODE + task.id,  // unique per task
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // On Android 12+, you need exact-alarm permission
            alarmMgr.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                task.nextAskEpoch,
                pending
            )
        } else {
            alarmMgr.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                task.nextAskEpoch,
                pending
            )
        }
    }

    /** Cancel a previously scheduled alarm for this task */
    fun cancel(task: Task, ctx: Context) {
        val alarmMgr = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(ctx, AlarmReceiver::class.java)
            .apply { putExtra("task_id", task.id) }
        val pending = PendingIntent.getBroadcast(
            ctx,
            ALARM_REQUEST_CODE + task.id,
            intent,
            PendingIntent.FLAG_NO_CREATE or  // only if it exists
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        PendingIntent.FLAG_IMMUTABLE
                    else
                        0
        )
        if (pending != null) {
            alarmMgr.cancel(pending)
            pending.cancel()
        }
    }
}
