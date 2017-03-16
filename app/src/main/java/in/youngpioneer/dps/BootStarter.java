package in.youngpioneer.dps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by vyomkeshjha on 11/06/16.
 */
public class BootStarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, PushListenerService.class);
            context.startService(pushIntent);
            Log.i("BOOT__","STARTING SERVICE");
        }
    }
}
