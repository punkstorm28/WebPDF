package in.youngpioneer.dps.serverRelations;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.artifex.mupdfdemo.YoungPioneerMain;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.youngpioneer.dps.notificationMessages.FileIO.LoadStrings;

/**
 * Created by vyomkeshjha on 30/07/16.
 * gets the key value stored on the server url and populates a hash-map based on it
 *
 */
public class MessageFromUrl
{
    private String LOG_TAG = "MessageFromUrl";
    private String URLdirectory= LoadStrings.RuntimeStrings.getBaseURL()+"Messages/echo2.php";
    InputStream is;
    StringBuilder sb;
    static boolean checkForCat_mess()
    {
        return true;
    }

    public long getLastTimestamp()
    {
        SharedPreferences prefs = YoungPioneerMain.mReference.getPreferences(YoungPioneerMain.mReference.MODE_PRIVATE);
        if(!prefs.contains("lastTimestamp"))
        {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putLong("lastTimestamp",0000000000);
            edit.apply();
            return 00000000;
        }
        return prefs.getLong("lastTimestamp",0000000000);

    }
/*
* getStringsFromServer gets the data required for the hashmap
* the input arguments are:
*
* 1. String correspondingInput
*
* */
    public ArrayList<String> getMessagesFromServer()
    {
        final ArrayList<String> returnList=new ArrayList<String>();

        new AsyncTask<Void,String,String>(){
            @Override
            protected String doInBackground(Void... params) {
                try {
                    int timeout= 7000;
                    HttpGet httpGet= null;
                    HttpClient httpclient = new DefaultHttpClient();
                    httpclient.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, timeout);
                    httpGet = new HttpGet(URLdirectory+"?timestamp="+String.valueOf(YoungPioneerMain.Pushstore.getLatestTimestamp()));
                    HttpResponse response = httpclient.execute(httpGet);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                }
                catch(Exception e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG,"HTTP exception caught :"+e.toString());
                }
                String result = null;
                try{
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"),8);
                    sb = new StringBuilder();
                    sb.append(reader.readLine() + "\n");
                    String line="0";

                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    is.close();
                    result=sb.toString();
                }
                catch(Exception e){
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            protected void onPostExecute(String messageList) {
                super.onPostExecute(messageList);
                Log.i(LOG_TAG,"message is "+messageList);
                String result=null;
                try {
                    result = messageList.substring(messageList.indexOf("<H4>") + 4, messageList.indexOf("</H4>"));
                }
                catch (NullPointerException f)
                {
                    f.printStackTrace();
                }
                catch (StringIndexOutOfBoundsException e)
                {
                e.printStackTrace();
                }
                if(result!=null) {
                    List<String> messages = Arrays.asList(result.split("->"));
                    Log.i(LOG_TAG, "message list is  " + messages);

                    for (String iterator : messages) {
                        //Log.i(LOG_TAG,"message token is  "+iterator);
                        if (iterator != null) {
                            int time = (int) (System.currentTimeMillis());
                            String ts = String.valueOf(time);
                            String[] tokens = iterator.split("@");
                            if (tokens.length == 3) {
                                YoungPioneerMain.Pushstore.insertProvider(tokens[0], tokens[2], tokens[1]);
                                //Title message Timestamp
                                Log.i(LOG_TAG, "message topic " + tokens[0] + " is " + tokens[1] + " time " + ts);
                            }
                            if (tokens.length == 4) {
                                YoungPioneerMain.Pushstore.insertProvider(tokens[0], tokens[2], tokens[1], tokens[3]);
                                //Title Message Timestamp ImageURL
                                Log.i(LOG_TAG, "message topic " + tokens[0] + " is " + tokens[1] + " time " + ts);
                            }
                            Log.i(LOG_TAG, "LAST TIMESTAMP is :" + YoungPioneerMain.Pushstore.getLatestTimestamp());
                        }
                    }
                }
            }
        }.execute();
        return returnList;
    }

}
