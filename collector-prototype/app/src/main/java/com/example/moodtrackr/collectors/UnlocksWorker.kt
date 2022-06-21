import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.*
import com.example.moodtrackr.R
import com.example.moodtrackr.db.AppDatabase
import com.example.moodtrackr.db.realtime.RTUsageRecord
import com.example.moodtrackr.extractors.StepsCountExtractor
import com.example.moodtrackr.extractors.unlocks.DeviceUnlockReceiver
import com.example.moodtrackr.utilities.DatabaseManager
import com.example.moodtrackr.utilities.DatesUtil
import kotlinx.coroutines.runBlocking

class UnlocksWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    private var context: Context = context

    override suspend fun doWork(): Result {
        Log.e("DEBUG", "Test")
        runBlocking {
            val time = DatesUtil.getTodayTruncated().time
            var unlocks: RTUsageRecord? = DatabaseManager.getInstance(context).rtUsageRecordsDAO.getUnlockObjOnDay(time)
            unlocks = checkSequence(unlocks)
            updateDBUnchecked(unlocks!!.usageVal.toLong()+1)
        }
        return Result.success()
    }

    private fun updateDBUnchecked(unlocks: Long) {
        runBlocking {
            var unlocksDB = DatabaseManager.getInstance(context).rtUsageRecordsDAO.getUnlockObjOnDay(
                DatesUtil.getTodayTruncated().time
            )
            unlocksDB!!.usageVal = unlocks.toString()
            DatabaseManager.getInstance(context).rtUsageRecordsDAO.update( unlocksDB )
        }
    }

    private fun checkSequence(unlocksDB: RTUsageRecord?): RTUsageRecord {
        var unlocksDBNew : RTUsageRecord
        runBlocking {
            if (unlocksDB === null) {
                unlocksDBNew = RTUsageRecord(
                    DatesUtil.getTodayTruncated(),
                    "unlocks",
                    "0"
                )
                DatabaseManager.getInstance(context).rtUsageRecordsDAO.insertAll(unlocksDBNew)
            }
            else {
                unlocksDBNew = unlocksDB
            }
        }
        return unlocksDBNew
    }
}