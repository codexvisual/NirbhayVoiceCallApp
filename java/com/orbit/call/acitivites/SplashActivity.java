package com.orbit.call.acitivites;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.AsyncTask;
import android.view.Window;
import android.widget.Toast;

import com.orbit.call.MyApplication;
import com.orbit.call.R;
import com.orbit.call.util.PopulatePreferenceWithCodecs;
import com.orbit.call.utils.Log;

import org.json.JSONException;
import org.json.JSONObject;


public class SplashActivity extends Activity
{
    public static final String USER = "user", PASSWORD = "password",
            CURRENCY = "currency", PIN = "pin", BALURL = "balurl";
    public static final String BRAND = "brand", SIPSERVER = "sipserver",
            SIPPORT = "sipport", TRANSPORT = "transport",STATUS = "status",
            ENCRYPTION = "encryption", BALANCE_URL = "balurl",IVR = "ivr",
            CONST1 = "const1", CONST2 = "const2", BALANCE_PARMS = "balprm";
    private static ConnectivityManager cm;
    private final static Handler handler = new Handler();
    private static final long TIMEOUT = 3000;
    private SharedPreferences prefs,mpreferences;
    public static boolean noInternet=true;
    private String[] allCodecs;
    private  String[] allAudios;
    private Uri.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        prefs = MyApplication.getPref();
        mpreferences = getSharedPreferences(
                String.format("%s_preferences", this.getPackageName()),
                Context.MODE_PRIVATE);
        MyApplication.getPref().edit().putInt("add_call", 0).commit();
        MyApplication.getPref().edit().putBoolean("isSiphome", false).commit();
        MyApplication.getPref().edit().putString("numbers", "").commit();
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        allCodecs = new String[]{"CODEC",getString(R.string.MEDIA_G729),getString(R.string.MEDIA_PCMA),getString(R.string.MEDIA_PCMU),getString(R.string.MEDIA_GSM),getString(R.string.MEDIA_G722),getString(R.string.MEDIA_ILBC),getString(R.string.MEDIA_AMR),getString(R.string.MEDIA_AMRWB),getString(R.string.MEDIA_SPEEX),getString(R.string.MEDIA_SPEEXWB),getString(R.string.MEDIA_ISACWB),getString(R.string.MEDIA_ISACSWB),getString(R.string.MEDIA_OPUS)};
        allAudios = new String[]{getString(R.string.MEDIA_VAD),getString(R.string.MEDIA_AEC),getString(R.string.MEDIA_CNG),getString(R.string.MEDIA_AGC),getString(R.string.MEDIA_ANS)};
        if (!isTaskRoot()) {
            finish();
            return;
        }




        doOpcodeThings();
    }

    private void doOpcodeThings() {
        try {


            builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("lic.phonixdialer.com")
                    .appendPath("index.php")
                    .appendQueryParameter("pin", "9548");
            System.out.println("query url is " + builder.toString());

            OpcodeBackgorund opcodeBackgorund = new OpcodeBackgorund();
            opcodeBackgorund.execute(new URL(builder.toString()));

        }

        catch (MalformedURLException e) {
            Log.e(getPackageName() + "Krishna", "Malformed URL", e);
            showTips("malformed url exception. Please try again.");
            //finish();
        }

        catch (NullPointerException exception) {
            Log.e(getPackageName() + "Krishna",
                    "Null Pointer exception", exception);
            showTips("Invalid Opcode. Please try again.");
            //finish();
        }

        catch (NumberFormatException e) {
            Log.e(getPackageName() + "Krishna",
                    "Number Format exception", e);
            showTips("Invalid Opcode. Please try again.");
            //finish();
        }

    }
    class OpcodeBackgorund extends AsyncTask<URL, Void, String> {

        InputStream inputStream = null;
        String data = null;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(URL... params) {
            try {
                System.out.println("the opcode url is " + params[0]);
                StringBuilder builder = new StringBuilder();
                URL url = params[0];
                URLConnection connection = url.openConnection();
                inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));

                String singleLine;

                while ((singleLine = reader.readLine()) != null)
                    builder.append(singleLine);

                data = builder.toString();
            } catch (NullPointerException e) {
                System.out.println("null pointer exception "+e.toString());
                showTips("Server not responding");
                //finish();
            } catch (IOException exception) {
                System.out.println("io exception exception "+exception.toString());
                showTips("Server not responding");
                //finish();
            } finally {
                try {
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException e) {
                    Log.e(getApplication().getPackageName() + "Krishna",
                            "Input Stream not opened", e);
                    finish();
                } catch (NullPointerException exception) {
                    Log.e(getApplication().getPackageName() + "Krishna",
                            "Input Stream not opened", exception);
                    finish();
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                System.out.println("the JSON result is " + result);
                if (result != null) {
                    JSONObject jobj = new JSONObject(result);
                    if (jobj != null) {


                        mpreferences.edit()

                                .putString(SplashActivity.USER, jobj.getString(SplashActivity.USER))
                                .putString(SplashActivity.PASSWORD, jobj.getString(SplashActivity.PASSWORD))
                                .putString(SplashActivity.CURRENCY, jobj.getString(SplashActivity.CURRENCY))

                                .putString(SplashActivity.BRAND,jobj.getString(SplashActivity.BRAND))
                                .putString(SplashActivity.SIPSERVER, jobj.getString(SplashActivity.SIPSERVER))
                                .putString(SplashActivity.SIPPORT, jobj.getString(SplashActivity.SIPPORT))
                                .putString(SplashActivity.TRANSPORT,jobj.getString(SplashActivity.TRANSPORT))
                                .putString(SplashActivity.STATUS, jobj.getString(SplashActivity.STATUS))

                                .putString(SplashActivity.BALANCE_URL, jobj.getString(SplashActivity.BALURL))
                                .putString(SplashActivity.CONST1,jobj.getString(SplashActivity.CONST1))
                                .putString(SplashActivity.CONST2, jobj.getString(SplashActivity.CONST2))
                                .putString(SplashActivity.IVR,jobj.getString(SplashActivity.IVR))

                                .commit();

                        if (jobj.getString(SplashActivity.TRANSPORT)
                                .equals("ABS")) {
                            mpreferences.edit()
                                    .putString(SplashActivity.TRANSPORT,
                                            "PERS").commit();
                        } else {
                            mpreferences.edit()
                                    .putString(
                                            SplashActivity.TRANSPORT,
                                            jobj.getString(SplashActivity.TRANSPORT))
                                    .commit();
                        }


                    }

                }else{
                    mpreferences.edit()

                            .putString(SplashActivity.BRAND,"ORBITCALL")
                            .putString(SplashActivity.SIPSERVER,"192.99.7.88")
                            .putString(SplashActivity.SIPPORT, "5060")
                            .putString(SplashActivity.TRANSPORT,"UDP")
                            .putString(SplashActivity.STATUS, "1")
                            .putString(SplashActivity.BALANCE_URL, "http://192.99.7.88/demo/customer/dialerapi/")
                            .putString(SplashActivity.USER, "username")
                            .putString(SplashActivity.PASSWORD, "password")
                            .putString(SplashActivity.CURRENCY, "")
                            .putString(SplashActivity.CONST1,"action=login&type=sip")
                            .putString(SplashActivity.CONST2, "")
                            .putString(SplashActivity.IVR,"")
                            .commit();

                    //showTips("Didn't fetch data. please try again");

                }
            } catch (JSONException exception) {
                Log.e(getPackageName() + "_Krishna", "JSON Exception",
                        exception);
                showTips("Invalid Opcode. Please try again.");
                //finish();
            } catch (NullPointerException exception) {
                Log.e(getPackageName() + "_Krishna", "Null Pointer Exception",
                        exception);
                System.out.println("null pointer exception "+exception.toString());
                showTips("Server not Responding");
                //finish();
            }
            PopulatePreferenceWithCodecs.populatePreference(allCodecs, allAudios, mpreferences, SplashActivity.this);

            if(isOnline())
            {
                Intent intent = new Intent(SplashActivity.this, LoginActivitys.class);
                intent.putExtra(Intent.EXTRA_TEXT, true);
                startActivity(intent);
                finish();
            }
            else {
                noInternet=true;
                Intent intent = new Intent(SplashActivity.this, SipHome.class);
                intent.putExtra("network", 2);
                startActivity(intent);
                finish();
            }


        }
    }

    public void showTips(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static boolean isOnline() {

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


   /* @Override
    protected void onResume()
    {
        super.onResume();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run()
            {
                if(isNetworkAvailable())
                {
                    Intent intent = new Intent(SplashActivity.this, LoginActivitys.class);
                    intent.putExtra(Intent.EXTRA_TEXT, true);
                    startActivity(intent);
                    finish();
                }
                else {
                    noInternet=true;
                    Intent intent = new Intent(SplashActivity.this, SipHome.class);
                    intent.putExtra("network", 2);
                    startActivity(intent);
                    finish();
                }
            }
        }, TIMEOUT);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
*/
}


