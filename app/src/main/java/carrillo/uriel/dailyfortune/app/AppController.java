package carrillo.uriel.dailyfortune.app;

import android.app.Application;
import android.text.TextUtils;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by jose.canseco on 08/12/2015.
 */
public class AppController extends Application{
    private static final String TAG ="AppController";
    private static AppController mInstance;
    public static synchronized AppController getInstance() {
        return mInstance;
    }

    private RequestQueue mRequestQueue;
    @Override
    public void onCreate(){
        super.onCreate();
        mInstance=this;
    }

    public RequestQueue getRequestQueue(){
        if(mRequestQueue==null){
            mRequestQueue=Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req, String tag){
        getRequestQueue().add(req);
    }
    public <T> void addToRequestQueue(Request<T> req){
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequest(Object tag){
        if(mRequestQueue!=null){
            mRequestQueue.cancelAll(tag);
        }
    }

    public AppController() {
    }
}
