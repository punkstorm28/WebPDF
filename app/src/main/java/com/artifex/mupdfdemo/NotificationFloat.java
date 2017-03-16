package com.artifex.mupdfdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdfdemo.downloadManager.DownloadHandler;

import java.util.ArrayList;

import in.youngpioneer.dps.R;
import in.youngpioneer.dps.notificationMessages.DbDataMap;
import in.youngpioneer.dps.notificationMessages.NotificationListActivity;
import in.youngpioneer.dps.notificationMessages.NotificationListFragment;
import in.youngpioneer.dps.serverRelations.MessageFromUrl;

/**
 * Created by vyomkeshjha on 31/08/16.
 */
public class NotificationFloat {
    ArrayList<DbDataMap> map;
    public static int pushIndex=0;
    WebView web;
    final int width;
    final int height;
    AlertDialog notificationDialog;
    private Context localContext ;
    MessageFromUrl url;

    NotificationFloat(Context ctx)
    {
        url = new MessageFromUrl();
        ConnectivityManager cm =
                (ConnectivityManager) YoungPioneerMain.mReference.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            url.getMessagesFromServer();
        }
        this.localContext=ctx;
        width = YoungPioneerMain.mReference.getWindowManager().getDefaultDisplay().getWidth();
        height = YoungPioneerMain.mReference.getWindowManager().getDefaultDisplay().getHeight();




      // showNotificationInPage();
    }
   public void showNotificationInList()
    {
        Intent ActivityIntent = new Intent(YoungPioneerMain.mReference, NotificationListActivity.class);
        localContext.startActivity(ActivityIntent);


    }
   public void showNotificationInPage()
    {
        notificationDialog= new AlertDialog.Builder(localContext).show();

        notificationDialog.getWindow().setLayout(width, height);

        notificationDialog.setContentView(R.layout.notificationfloat);
        web = (WebView) notificationDialog.findViewById(R.id.notificationImage);
        web.setVisibility(View.GONE);
        web.setLongClickable(true);
        web.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DownloadHandler.enqueueDownload(map.get(pushIndex).getImageUrl());
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(YoungPioneerMain.mReference, "Downloading image...", duration);
                toast.show();
                return false;
            }
        });
        map = YoungPioneerMain.Pushstore.getDataFromDB();


        final TextView notificationTitle = (TextView) notificationDialog.findViewById(R.id.notificationTitle);
        final FrameLayout.LayoutParams titleParams = (FrameLayout.LayoutParams) notificationTitle.getLayoutParams();

        final TextView Message = (TextView) notificationDialog.findViewById(R.id.notificationMessage);
        final FrameLayout.LayoutParams MessageParams = (FrameLayout.LayoutParams) Message.getLayoutParams();

        Button nextNotification = (Button) notificationDialog.findViewById(R.id.NextMessage);
        Button PreviousNotification = (Button) notificationDialog.findViewById(R.id.previousMessage);

        if (map.size() > 0) {
            notificationTitle.setText(map.get(pushIndex).getTitle());
            Message.setText(map.get(pushIndex).getMessage());
            Log.i("IMAGE", map.get(pushIndex).getImageUrl());
            web.setVisibility(View.GONE);

            if (map.get(pushIndex).getImageUrl().equals("null")) {
                web.setVisibility(View.GONE);


                titleParams.setMargins(0, 0, 0, height/6);

                notificationTitle.setLayoutParams(titleParams);

                notificationDialog.getWindow().setLayout(width, height / 2);
                notificationTitle.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
                Message.setGravity(Gravity.CENTER);
                Log.i("IMAGE", "NO image bro.");

            }

            if (!map.get(pushIndex).getImageUrl().equals("null")) {

                Log.i("IMAGE", map.get(pushIndex).getImageUrl());
                titleParams.setMargins(0, 65, 0, 0);

                notificationTitle.setLayoutParams(titleParams);

                web.setVisibility(View.VISIBLE);
                try {
                    String data = "<html><head><title>Example</title><meta name=\"viewport\"\"content=\"width=" + width + ", initial-scale=0.65 \" /></head>";
                    data = data + "<body><center><img width=\"" + width + "\" src=\"" + map.get(pushIndex).getImageUrl() + "\" /></center></body></html>";
                    web.loadData(data, "text/html", null);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        if (map.size() == 0) {
            notificationTitle.setText("  ");
            Message.setText("No Messages");
            nextNotification.setVisibility(View.INVISIBLE);
            PreviousNotification.setVisibility(View.GONE);

        }

        nextNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pushIndex < map.size() - 1)
                    pushIndex++;


                notificationTitle.setText(map.get(pushIndex).getTitle());
                Message.setText(map.get(pushIndex).getMessage());
                web.setVisibility(View.GONE);

                notificationDialog.getWindow().setLayout(width, height);

                if (map.get(pushIndex).getImageUrl().equals("null")) {
                    web.setVisibility(View.GONE);


                    titleParams.setMargins(0, 0, 0, height/6);

                    notificationTitle.setLayoutParams(titleParams);

                    notificationDialog.getWindow().setLayout(width, height / 2);
                    notificationTitle.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
                    Message.setGravity(Gravity.CENTER);
                    Log.i("IMAGE", "NO image bro.");

                }

                if (!map.get(pushIndex).getImageUrl().equals("null")) {

                    Log.i("IMAGE", map.get(pushIndex).getImageUrl());
                    titleParams.setMargins(0, 65, 0, 0);

                    notificationTitle.setLayoutParams(titleParams);


                    web.setVisibility(View.VISIBLE);
                    try {
                        String data = "<html><head><title>Example</title><meta name=\"viewport\"\"content=\"width=" + width + ", initial-scale=0.65 \" /></head>";
                        data = data + "<body><center><img width=\"" + width + "\" src=\"" + map.get(pushIndex).getImageUrl() + "\" /></center></body></html>";
                        web.loadData(data, "text/html", null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

        });
        PreviousNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pushIndex > 0)
                    pushIndex--;
                notificationTitle.setText(map.get(pushIndex).getTitle());
                Message.setText(map.get(pushIndex).getMessage());
                web.setVisibility(View.GONE);

                notificationDialog.getWindow().setLayout(width, height);

                if (map.get(pushIndex).getImageUrl().equals("null")) {
                    web.setVisibility(View.GONE);


                    titleParams.setMargins(0, 0, 0, height/6);

                    notificationTitle.setLayoutParams(titleParams);

                    notificationDialog.getWindow().setLayout(width, height / 2);
                    notificationTitle.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
                    Message.setGravity(Gravity.CENTER);
                    Log.i("IMAGE", "NO image bro.");

                }

                if (!map.get(pushIndex).getImageUrl().equals("null")) {

                    Log.i("IMAGE", map.get(pushIndex).getImageUrl());
                    titleParams.setMargins(0, 65, 0, 0);

                    notificationTitle.setLayoutParams(titleParams);


                    web.setVisibility(View.VISIBLE);
                    try {
                        String data = "<html><head><title>Example</title><meta name=\"viewport\"\"content=\"width=" + width + ", initial-scale=0.65 \" /></head>";
                        data = data + "<body><center><img width=\"" + width + "\" src=\"" + map.get(pushIndex).getImageUrl() + "\" /></center></body></html>";
                        web.loadData(data, "text/html", null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


            }
        });

    }
}
