import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.moodtrackr.R
import com.example.moodtrackr.extractors.StepsCountExtractor
import com.example.moodtrackr.extractors.unlocks.DeviceUnlockReceiver

class PeriodicWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    private var context: Context = context
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

    override suspend fun doWork(): Result {
        // Mark the Worker as important
        val progress = "Starting Download"
        setForeground(createForegroundInfo(progress))
        setupExtractors()
        return Result.success()
    }

    private fun setupExtractors() {
        // Downloads a file and updates bytes read
        // Calls setForegroundInfo() periodically when it needs to update
        // the ongoing Notification
//        DeviceUnlockReceiver(context)
        StepsCountExtractor(context!!)
    }

    // Creates an instance of ForegroundInfo which can be used to update the
    // ongoing notification.
    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val id = 0
        val title = "MDTKR"
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(getId())

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext, id.toString())
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .build()

        return ForegroundInfo(0, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        // Create the NotificationChannel
        val name = "MDTKR"
        val descriptionText = "Used by Mood Tracker"
        val importance = NotificationManager.IMPORTANCE_MIN
        val mChannel = NotificationChannel("0", name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
}