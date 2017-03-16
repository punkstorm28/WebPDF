package in.youngpioneer.dps.notificationMessages;

import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.artifex.mupdfdemo.YoungPioneerMain;

import in.youngpioneer.dps.R;

/**
 * Created by shanu on 6/6/16.
 */
public abstract class SingleFragmentActivity extends FragmentActivity {
    protected abstract Fragment createFragment();
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_fragment);
       TextView noNotifications= (TextView)findViewById(R.id.noNotifications);
        if(YoungPioneerMain.Pushstore.getAllMessages().size()!=0)
        {
            noNotifications.setVisibility(View.GONE);
        }

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.FragmentContainer);
        if(fragment == null)
            fragment = createFragment();
        fm.beginTransaction().add(R.id.FragmentContainer, fragment).commit();
    }


}