package carrillo.uriel.dailyfortune;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import carrillo.uriel.dailyfortune.app.AppController;
import carrillo.uriel.dailyfortune.app.MyPreferences;

public class FortuneActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fortune);

        MyPreferences pref = new MyPreferences(FortuneActivity.this);
        if(pref.isFirstTime())
        {
            Toast.makeText(FortuneActivity.this, "Hi " + pref.getUserName(), Toast.LENGTH_LONG).show();
            pref.setOld(true);
        }else
        {
            Toast.makeText(FortuneActivity.this,"Welcome back "+pref.getUserName(),Toast.LENGTH_LONG).show();
        }
        ConnectionDetector cd = new ConnectionDetector(this);
        if(cd.isConnectingToInternet())
            getFortuneOnline();
        else
            readFortuneFromFile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fortune, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void getFortuneOnline()
    {
        //set the fortune text to Loadding
        final TextView fortuneText =(TextView) findViewById(R.id.fortune);
        fortuneText.setText("Loading...");
        //create an instance of the request
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.GET,"http://api.theysaidso.com/qod.json",
                (String)null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("Respone",response.toString());
                        String fortune;
                        //parse tje quote
                        try
                        {
                            JSONObject obj = response.getJSONObject("contents");
                            JSONArray params = obj.getJSONArray("quotes");
                            JSONObject param1 = params.getJSONObject(0);
                            String par = param1.getString("quote");


                            fortune = par.toString();
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Error:"+e.getMessage(),Toast.LENGTH_LONG).show();
                            fortune="Error";
                        }
                        //set the fortune text to the parsed quote
                        fortuneText.setText(fortune);
                        WriteToFile(fortune);
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error)
            {
                VolleyLog.d("Response", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        //Add request to request queue
        AppController.getInstance().addToRequestQueue(request);
    }

    private void WriteToFile(String data)
    {
        try
        {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("Fortune.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }catch (IOException e)
        {
            Log.e("Message: ", "File write failed: "+e.toString());
        }
    }

    private void readFortuneFromFile()
    {
        String fortune=" ";
        try{
            InputStream inputStream = openFileInput("Fortune.json");
            if(inputStream!=null)
            {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString="";
                StringBuilder stringBuilder = new StringBuilder();
                Log.v("Message:", "reading...");
                while((receiveString=bufferedReader.readLine())!=null)
                {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                fortune=stringBuilder.toString();
            }
        }catch(FileNotFoundException e)
        {
            Log.e("Message:","File not found: "+e.toString());
        }catch (IOException e)
        {
            Log.e("Message:","Can not read file: "+e.toString());
        }
        TextView fortuneTxt= (TextView)findViewById(R.id.fortune);
        fortuneTxt.setText(fortune);
    }
}
