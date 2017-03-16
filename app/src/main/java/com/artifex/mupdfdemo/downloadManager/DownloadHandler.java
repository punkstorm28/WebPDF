package com.artifex.mupdfdemo.downloadManager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.artifex.mupdfdemo.AsyncTask;
import com.artifex.mupdfdemo.YoungPioneerMain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import in.youngpioneer.dps.R;

/**
 * Created by vyomkeshjha on 31/05/16.
 */
public class DownloadHandler {
    static DownloadManager downloadManager;
    private long   downloadReference;
    Uri FileUri;
    File flag = null;
    public boolean downloading=false;
    int bytes_downloaded=0;
    int bytes_total=0;
    URI FileURI;

    String DOWNLOAD_TAG="DOWNLOAD";

    public DownloadHandler()
    {
    }

    static void initialisePreferences(boolean isDownloading,long downloadRef, int fileSize)
    {
        SharedPreferences prefs = YoungPioneerMain.mReference.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean("isDownloading",true);
        edit.putLong("download_id",downloadRef);
        edit.putInt("fileSize",fileSize);
        edit.apply();
    }

    static void resetPreferences()
    {
        SharedPreferences prefs = YoungPioneerMain.mReference.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean("isDownloading",false);
        edit.putLong("download_id",0);
        edit.putInt("fileSize",0);
        edit.commit();
    }
    public static void enqueueDownload(String downloadFrom)
    {
        downloadManager = (DownloadManager) YoungPioneerMain.mReference.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri Download_Uri = Uri.parse(downloadFrom);
        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

        //Restrict the types of networks over which this download may proceed.
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Image Notice");
        //Set a description of this download, to be displayed in notifications (if enabled)
        request.setDescription("Downloading...");
        //request.setDestinationInExternalFilesDir(YoungPioneerMain.mReference, "images", "notice.png");
        request.setDestinationUri(URI2Uri(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toURI()));



        try {
           long downloadReference = downloadManager.enqueue(request);
        }
        catch (SecurityException e)
        {

        }
    }

    public static Uri URI2Uri(URI convertFrom)
    {
        String URL = convertFrom.toString();
        Uri returnUri = Uri.parse(URL);
        return returnUri;
    }
    public void downloadFiles(Context context, String downloadFrom, String downloadTo)
    {
        try {
            context.unregisterReceiver(receiver);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if(context.getExternalFilesDir("/")!=null)
         FileURI = context.getExternalFilesDir("/").toURI();

        else
        {
            FileURI=context.getFilesDir().toURI();
        }
        FileUri = Uri.parse(FileURI.toString() + "pdfFile.pdf");
        Log.i("DOWNLOAD", "Uri is " + FileUri.toString());
        try {
            flag = new File(FileUri.getPath());
        } catch (NullPointerException e) {

        }
        if(flag.exists())
        {
            try{
            flag.delete();}
            catch (Exception e){e.printStackTrace();}
        }
        Log.i("DOWNLOAD", " flag and existence : " + flag + " " + flag.exists());
        if (flag != null && !flag.exists()) {


            downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri Download_Uri = Uri.parse(downloadFrom);
            DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

            //Restrict the types of networks over which this download may proceed.
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setTitle(YoungPioneerMain.mReference.getResources().getString(R.string.app_name));
            //Set a description of this download, to be displayed in notifications (if enabled)
            request.setDescription("Downloading Latest Release...");
            request.setDestinationUri(FileUri);


            Log.i("DOWNLOAD", context.getFilesDir().getAbsolutePath());
            try {
                 downloadReference = downloadManager.enqueue(request);
                 downloading=true;
                 //statusChecker();
                statusRefresher();

            }
            catch (SecurityException e)
            {
                e.printStackTrace();
            }

            Log.i("DOWNLOAD", "Downloading File");
            context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


        } else {
            Log.i("DOWNLOAD", "not Downloading File");

        }

    }

    void statusChecker()
    {
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                Log.i("DOWNLOADING","inside Async");
                while(downloading)
                    queryDownloadStatus();
                return null;
            }
        }.execute();

    }
void statusRefresher()
{
    new Thread()
    {
        @Override
        public void run() {
            super.run();
            Log.i("DOWNLOADING","inside Async");
            while(downloading)
                queryDownloadStatus();
        }
    }.start();
}
   public void registerDownloadService(Context context)
   {
       context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
   }

    public void unRegisterDownloadServiceListener(Context context)
    {
        context.unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("DOWNLOAD","Download Complete, Initiate copy and Delete");
            //queryDownloadStatus();
            //Put in here the file Copy codes


               /* URI FileURI = context.getFilesDir().toURI();
                Uri ToFileUri = Uri.parse(FileURI.toString() + "dps.pdf");
                File ToFile = new File(ToFileUri.getPath());
*/

/*
                copyFile(flag,ToFile);

                String[] fileslist =context.getFilesDir().list();
                for(int i=0;i<fileslist.length;i++)
                {
                 Log.i("DOWNLOAD","FILE NAME IS: "+fileslist[i]);
                }
                flag.delete();*/
                moveDownloadedFile(context);
                context.unregisterReceiver(receiver);

                YoungPioneerMain.mReference.finish();
                pdfView(context);
               // YoungPioneerMain.mReference.endFirstRun();




        }
    };
    public void moveDownloadedFile(Context context)
    {

        URI FileURI = context.getFilesDir().toURI();
        Uri ToFileUri = Uri.parse(FileURI.toString() + "dps.pdf");
        File ToFile = new File(ToFileUri.getPath());

        if(flag==null)
        {
            if(context.getExternalFilesDir("/")!=null)
                FileURI = context.getExternalFilesDir("/").toURI();

            else
            {
                FileURI=context.getFilesDir().toURI();
            }
            FileUri = Uri.parse(FileURI.toString() + "pdfFile.pdf");
            Log.i("DOWNLOAD", "Uri is " + FileUri.toString());
            try {
                flag = new File(FileUri.getPath());
            } catch (NullPointerException e) {

            }

        }
        try {
            copyFile(flag,ToFile);
            flag.delete();
            makeVersionsLevel();
            setUpdateAvailable(false);

            //inits the levels
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void pdfView(Context context) {
        //Log.i(TAG, "post intent to open file "+f);
        Intent intent = new Intent();

        intent.setClass(context, YoungPioneerMain.class);


        intent.setAction("android.intent.action.VIEW");
        context.startActivity(intent);
    }
    private void setUpdateAvailable(boolean isAvailable)
    {
        SharedPreferences prefs = YoungPioneerMain.mReference.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean("isUpdateAvailable",isAvailable);
        edit.apply();
    }

    public int getFileSize()
    {

            return bytes_total;
    }


    void makeVersionsLevel()
    {
        SharedPreferences prefs = YoungPioneerMain.mReference.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        String Latestrelease=prefs.getString("_latestRelease","none");
        edit.putString("_currentRelease",Latestrelease);
        edit.apply();
    }
    public static void endDownload(Context context)
    {
        SharedPreferences prefs = YoungPioneerMain.mReference.getPreferences(Context.MODE_PRIVATE);
        DownloadManager stopper = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        if(prefs.getLong("download_id",0)!=0) {
            long reference = prefs.getLong("download_id", 0);
            stopper.remove(reference);
            SharedPreferences.Editor edit = prefs.edit();
            if(prefs.contains("isDownloading"));
            edit.putBoolean("isDownloading",false);
            edit.putInt("fileSize",0);
            edit.apply();

        }

    }
public static boolean isDownloadIncomplete(Context context)
{
    SharedPreferences prefs = YoungPioneerMain.mReference.getPreferences(Context.MODE_PRIVATE);
    boolean isDownloading=prefs.getBoolean("isDownloading",false);
    if(isDownloading)
    {    if(downloadManager==null)
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Log.i("DOWNLOAD","A download was/is in progress");
        long reference =prefs.getLong("download_id",0);
        DownloadManager.Query q = new DownloadManager.Query();
        Log.i("DOWNLOAD_Ref","reference is : "+reference);
        q.setFilterById(reference);
        Cursor cursor = downloadManager.query(q);
        cursor.moveToFirst();
        try {
            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                Log.i("DOWNLOAD", "Download state= complete-0");

                resetPreferences();

                //This part of the code is redundant because the previous placement is not static
                //TODO:make this code non redundant by restructuring it


            }
        }
        catch (CursorIndexOutOfBoundsException e)
        {
            Log.i("DOWNLOAD","Cursor index out of bounds");
            Log.i("DOWNLOAD", "Download state= complete-1");

        }
        //TODO:check if needed
        //downloadManager.remove(reference);
        return true;
    }
    return false;
}
    //returns The download status whether the download has commenced or is pending or whatever the case is
    public void queryDownloadStatus() {

                 bytes_downloaded=0;
                 bytes_total=0;

                if (downloading) {
                    Log.i("DOWNLOAD","querying");
                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(downloadReference);

                    Cursor cursor = downloadManager.query(q);
                    cursor.moveToFirst();
                    try {
                        bytes_downloaded = cursor.getInt(cursor
                                .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        initialisePreferences(true,downloadReference,getFileSize());

                        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                            downloading = false;
                            Log.i(DOWNLOAD_TAG,"Download state= complete");
                            YoungPioneerMain.mReference.endFirstRun();
                            resetPreferences();
                        }
                        final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);
                        if(YoungPioneerMain.mReference.progress!=null)
                        {
                            YoungPioneerMain.mReference.progress.setProgress(dl_progress);
                        }
                        Log.d(DOWNLOAD_TAG,"Download state= "+dl_progress);
                    }
                    catch (CursorIndexOutOfBoundsException e)
                    {
                        Log.i("DOWNLOAD","Cursor index out of bounds");
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //Looper.prepare();
                    cursor.close();
                }


    }

    private static boolean copyFile(File src,File dst)throws IOException{

        if(src.getAbsolutePath().toString().equals(dst.getAbsolutePath().toString())){

            return true;

        }
        else{
            InputStream is=new FileInputStream(src);
            OutputStream os=new FileOutputStream(dst);
            byte[] buff=new byte[1024];
            int len;
            while((len=is.read(buff))>0){
                os.write(buff,0,len);
            }
            is.close();
            os.close();
        }
        return true;
    }
}