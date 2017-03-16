package in.youngpioneer.dps.notificationMessages.FileIO;

import com.artifex.mupdfdemo.YoungPioneerMain;

import in.youngpioneer.dps.R;

/**
 * Created by vyomkeshjha on 24/08/16.
 */
public class LoadStrings {
    public static StringTemplate RuntimeStrings;
    public LoadStrings()
    {
        RuntimeStrings = new StringTemplate();
        initialize();
    }
    public void initialize()
    {
        String BaseURL=YoungPioneerMain.mReference.getString(R.string.DownloadBaseURL);
        RuntimeStrings.setBaseURL(BaseURL);
        String InfoMessage=YoungPioneerMain.mReference.getString(R.string.infoMesage);
        RuntimeStrings.setInfoMessage(InfoMessage);
        String ReviewURL=YoungPioneerMain.mReference.getString(R.string.ReviewURL);
        RuntimeStrings.setReviewURL(ReviewURL);
        String SiteForWebView=YoungPioneerMain.mReference.getString(R.string.WebviewSiteURL);
        RuntimeStrings.setSiteForWebView(SiteForWebView);
    }
}
