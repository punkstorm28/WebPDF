package in.youngpioneer.dps.notificationMessages;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;


import com.artifex.mupdfdemo.NotificationFloat;
import com.artifex.mupdfdemo.YoungPioneerMain;

import java.util.ArrayList;

import in.youngpioneer.dps.R;

/**
 * Created by shanu on 6/6/16.
 */
public class NotificationListFragment extends ListFragment {

  //  private ArrayList<String> messages;
    ArrayList<DbDataMap> map;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("");


//        messages =  YoungPioneerMain.Pushstore.getAllMessages();
        map=YoungPioneerMain.Pushstore.getDataFromDB();
       // messages.add("papaya g ot talent");
       // messages.add("apples'got talent");
      //  if(messages.size()==0)
      //  {
        //    messages.add("    No Notifications");
        //}

        MessageAdapter adapter = new MessageAdapter(map);
        setListAdapter(adapter);
    }

    public class MessageAdapter extends ArrayAdapter<DbDataMap> {
        public MessageAdapter(ArrayList<DbDataMap> Messages) {
            super(getActivity(), 0, Messages);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_notifications,null);

            TextView title_text = (TextView) convertView.findViewById(R.id.notificationTitle);
            title_text.setText(map.get(position).getTitle());
            Log.i("TITLE__",map.get(position).getTitle());

            TextView date_text = (TextView) convertView.findViewById(R.id.notification_message);
            date_text.setText(map.get(position).getMessage());

            CheckBox isSolvedCB = (CheckBox) convertView.findViewById(R.id.list_fragment_checkbox_id);
            isSolvedCB.bringToFront();

            return convertView;
        }
    }
    public void onListItemClick (ListView l, View v, int position, long id) {
        NotificationFloat.pushIndex=position;
        YoungPioneerMain.floatingNotificationScreen.showNotificationInPage();
       NotificationListActivity.contextStore.finish();
    }

    public void onResume() {
        super.onResume();
        ((MessageAdapter)getListAdapter()).notifyDataSetChanged();
    }

    //Used ListFragment ka onCreateView()
}
