import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.everydaybyble_dingdong.*

class DateChangeReceiver(
    private val mainActivity: AppCompatActivity, private val mainContext: Context
    ) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_DATE_CHANGED) {
            initToday(mainActivity,mainContext)
        }
    }
}
