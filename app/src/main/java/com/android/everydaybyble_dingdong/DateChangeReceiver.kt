import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

class DateChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_DATE_CHANGED) {
            // Update your variables here
            Log.e("열두시가 지나면","브라보콘")
        }
    }
}
