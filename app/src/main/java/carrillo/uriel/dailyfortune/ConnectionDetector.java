package carrillo.uriel.dailyfortune;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {

    private Context context;
    ConnectionDetector(Context context){
        this.context=context;
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activenetworkInfo= connectivityManager.getActiveNetworkInfo();
        return activenetworkInfo!=null && activenetworkInfo.isConnected();
    }

}
