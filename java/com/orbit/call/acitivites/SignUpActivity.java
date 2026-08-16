package com.orbit.call.acitivites;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.orbit.call.Line;
import com.orbit.call.R;
import com.orbit.call.MyApplication;
import com.orbit.call.entities.Account;
import com.orbit.call.ui.XmppActivity;
import com.orbit.call.util.Network;
import com.portsip.PortSipEnumDefine;
import com.portsip.PortSipErrorcode;
import com.portsip.PortSipSdk;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class SignUpActivity extends XmppActivity implements MyApplication.mCallbackListener{

	private EditText	fname_et, lname_et, email_et, mobile_et, user_et,pass_et;
	//private Button		signup;
	String strfname,strlname,strmail,strmob,struser,strpass;
	SharedPreferences prefs;
	XMLParser parser;
	Element e;
	String message;
	ProgressDialog pdial;
	public static String licenseKey ="1Uh0zMTNDOEFCNTUwMUMxQzRFMkJCNjVGMUIyMjM3RDU4NEAzOEYxNTM5QjI5NzIwMkUyQzNBNTVBOUUyRUMwMjZDMUBBMUMyRThCNjdGQTcyMEFGMkM5ODg0MDQwQTRCNTA4OUBFREIxNTE3MEU2M0QwNkZFNTJFNzg5MTdDMDFBRDg3MA";
	public static PortSipSdk mSipSdk;
	MyApplication myApplication;
	private final static Handler handler = new Handler();
	private SharedPreferences mpreferences;       
	Intent intent;
    Button signup;
    TextView txt_fname,txt_lname,txt_email,txt_mobile,txt_username,txt_password;
    public static Account mAccount;
    private boolean mFetchingAvatar = false;

    public static final List<Account> accountList = new ArrayList<>();
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.signup);
        txt_fname=(TextView)findViewById(R.id.signup_fname);
        txt_lname=(TextView)findViewById(R.id.signup_lname);
        txt_email=(TextView)findViewById(R.id.signup_email);
        txt_mobile=(TextView)findViewById(R.id.signup_mobileno);
        txt_username=(TextView)findViewById(R.id.signup_username);
        txt_password=(TextView)findViewById(R.id.signup_password);
        fname_et=(EditText)findViewById(R.id.signup_fname_value);
        lname_et=(EditText)findViewById(R.id.signup_lname_value);
        email_et=(EditText)findViewById(R.id.signup_email_value);
        mobile_et=(EditText)findViewById(R.id.signup_mobile_value);
        user_et=(EditText)findViewById(R.id.signup_username_value);
        pass_et=(EditText)findViewById(R.id.signup_password_value);
        signup=(Button)findViewById(R.id.signup_btn);
        if(MyApplication.getPref().getInt("Portugusese",0)==1)
        {
            txt_fname.setText("Nome");
            txt_lname.setText("Sobrenome");
            txt_email.setText("Email");
            txt_mobile.setText("Número de Celular");
            txt_username.setText("Nome de usuário");
            txt_password.setText("Senha");
            fname_et.setHint("Nome");
            lname_et.setHint("Sobrenome");
            email_et.setHint("Email");
            mobile_et.setHint(Html.fromHtml("<font size=\"16\">" + "Mobile No. " + "</font>" + "<small>" + "International format i.e. 0012127773456" + "</small>"));
            user_et.setHint("Nome de usuário");
            pass_et.setHint("Senha");
            signup.setText("Inscrever-Se");

        }
        else if(MyApplication.getPref().getInt("Spanish",0)==1)
        {
            txt_fname.setText("Nombre ");
            txt_lname.setText("Apellido");
            txt_email.setText("Email");
            txt_mobile.setText("Teléfono Celular");
            txt_username.setText("Nombre de usuario");
            txt_password.setText("Contraseña");
            fname_et.setHint("Nombre ");
            lname_et.setHint("Apellido");
            email_et.setHint("Email");
            mobile_et.setHint(Html.fromHtml("<font size=\"16\">" + "Mobile No. " + "</font>" + "<small>" + "International format i.e. 0012127773456" + "</small>"));
            user_et.setHint("Nombre de usuario");
            pass_et.setHint("Contraseña");
            signup.setText("Contratar");
        }
        else
        {
            txt_fname.setText("First name");
            txt_lname.setText("Last name");
            txt_email.setText("Email");
            txt_mobile.setText("Mobile No");
            txt_username.setText("username");
            txt_password.setText("Password");
            fname_et.setHint("First name");
            lname_et.setHint("Last name");
            email_et.setHint("Email");
            mobile_et.setHint(Html.fromHtml("<font size=\"16\">" + "Mobile No. " + "</font>" + "<small>" + "International format i.e. 0012127773456" + "</small>"));
           //mobile_et.setHint(getResources().getString(R.string.mobile_no_hint));
            user_et.setHint("username");
            pass_et.setHint("Password");
            signup.setText("SignUp");
        }
		 prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			myApplication = ((MyApplication) getApplicationContext());
		    myApplication.setCallbackListener(this);
			mSipSdk = myApplication.getPortSIPSDK();
			  mpreferences = getSharedPreferences(
		                String.format("%s_preferences", this.getPackageName()),
		                Context.MODE_PRIVATE);
	}
	public void onSignup(View v)
	{

		strfname=fname_et.getText().toString();
		strlname=lname_et.getText().toString();
		strmail=email_et.getText().toString();
		strmob=mobile_et.getText().toString();
		struser=user_et.getText().toString();
		//strpass=pass_et.getText().toString();
		if(strfname.equals("") || strlname.equals("") || strmail.equals("") ||strmob.equals("")
				)
		{
            if(MyApplication.getPref().getInt("Portugusese",0)==1)
            {
                Toast.makeText(this, "Todos os campos devem ser preenchidos", Toast.LENGTH_SHORT).show();

            }
            else if(MyApplication.getPref().getInt("Spanish",0)==1)
            {
                Toast.makeText(this, "Todos campos deben ser completados", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "All field should be filled", Toast.LENGTH_SHORT).show();
            }

		}
		else if(!isValidEmail(strmail))
		{
            if(MyApplication.getPref().getInt("Portugusese",0)==1)
            {
                Toast.makeText(this, "Digite correta identificação do email", Toast.LENGTH_SHORT).show();

            }
            else if(MyApplication.getPref().getInt("Spanish",0)==1)
            {
                Toast.makeText(this, "Introduzca ID de correo electrónico correcta", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Please enter correct email id", Toast.LENGTH_SHORT).show();
            }

		
		}
		else if(!isValidPhoneNumber(strmob))
		{

            if(MyApplication.getPref().getInt("Portugusese",0)==1)
            {
                Toast.makeText(this, "Por favor, indique nenhum móvel correto", Toast.LENGTH_SHORT).show();

            }
            else if(MyApplication.getPref().getInt("Spanish",0)==1)
            {
                Toast.makeText(this, "Introduzca correcta sin móvil", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Please enter correct mobile no", Toast.LENGTH_SHORT).show();
            }

		
		}else if (!strmob.toString().startsWith("00")){
            Toast.makeText(SignUpActivity.this,"Enter mobile no. starting with 00",Toast.LENGTH_SHORT).show();
        }
		/*else if(struser.length()< 4)
		{
            if(MyApplication.getPref().getInt("Portugusese",0)==1)
            {
                Toast.makeText(this, "Nome de usuário deve ter pelo menos quatro caracteres", Toast.LENGTH_SHORT).show();

            }
            else if(MyApplication.getPref().getInt("Spanish",0)==1)
            {
                Toast.makeText(this, "Nombre de usuario debe tener al menos cuatro caracteres", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Username should have at least four character", Toast.LENGTH_SHORT).show();
            }


		}*/
		/*else if(strpass.length()< 4)
		{
            if(MyApplication.getPref().getInt("Portugusese",0)==1)
            {
                Toast.makeText(this, "Senha deve ter pelo menos quatro caracteres", Toast.LENGTH_SHORT).show();

            }
            else if(MyApplication.getPref().getInt("Spanish",0)==1)
            {
                Toast.makeText(this, "Contraseña debe tener al menos cuatro caracteres", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Password should have at least four character", Toast.LENGTH_SHORT).show();
            }

		}*/
		else
		{
			if (LoginActivitys.isOnline())
			{
				
				new SignUp().execute();
			}
			else {
                if(MyApplication.getPref().getInt("Portugusese",0)==1)
                {
                    Toast.makeText(this, "Sem conectividade Internet", Toast.LENGTH_SHORT).show();

                }
                else if(MyApplication.getPref().getInt("Spanish",0)==1)
                {
                    Toast.makeText(this, "Sin conexión a Internet", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "No internet connectivity", Toast.LENGTH_SHORT).show();
                }


            }
		}
	}
	private boolean isValidEmail(CharSequence email) {
	    if (!TextUtils.isEmpty(email)) {
	        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
	    }
	    return false;
	}
	private boolean isValidPhoneNumber(CharSequence phoneNumber) {
	    if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
	    }
	    return false;
	}
	
	public class SignUp extends AsyncTask<Void, Integer, String> {
		protected String getASCIIContentFromEntity(HttpEntity entity)
				throws IllegalStateException, IOException {
			InputStream in = entity.getContent();

			StringBuffer out = new StringBuffer();
			int n = 1;
			while (n > 0) {
				byte[] b = new byte[4096];
				n = in.read(b);

				if (n > 0)
					out.append(new String(b, 0, n));
			}

			return out.toString();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();		
			String url;
            //http://customer.gsm2go.com/dialerapi/signup.php?sign=1&user=
            //http://88.208.222.15/a2b/customer/dialerapi/signup.php?sign=1&user=raj&pass=raj&cur=usd&fname=Raj&lname=bhatia&email=raj1@gmail.com&phone=9180100

            url=MyApplication.getPref().getString(SplashActivity.BALANCE_URL,"http://5.196.197.75/a2billing/customer/dialerapi/")+"signup.php?sign=1"
					//+ struser
					//+"&pass="

					+"&cur="
					+"usd"
					+"&fname="
					+strfname
					+"&lname="
					+strlname
					+"&email="
					+strmail
					+"&phone="
					+strmob;
            Log.e("signup url ",url);
			HttpGet httpGet = new HttpGet(url);
			String text = null;
			try {
				HttpResponse response = httpClient.execute(httpGet,
						localContext);

				HttpEntity entity = response.getEntity();

				text = getASCIIContentFromEntity(entity);
                Log.e("response is",text);
				System.out.println("the result is "+text);

			} catch (Exception e) {
				return e.getLocalizedMessage();
			}

			return text;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try{
			 pdial.dismiss();
		}catch(Exception exception)
		{}
			if(result != null)
			{
				 parser = new XMLParser();
				Document doc = parser.getDomElement(result);
				NodeList nl = doc.getElementsByTagName("response");
				 e = (Element) nl.item(0);
					message=parser.getValue(e, "msg")+"\n"+"Your Password :"+parser.getValue(e,"pass");
				/*System.out.println("the status "+ parser.getValue(e, "status"));
				System.out.println("the user "+ parser.getValue(e, "user"));
				System.out.println("the pass "+ parser.getValue(e, "pass"));*/
				if(parser.getValue(e, "status").equals("1")){
					/*fname_et.setText("");
					lname_et.setText("");
					mobile_et.setText("");
					email_et.setText("");		*/
                   /* if(accountList.size()==1) {
                        Log.v("accountList",""+accountList.size());
                        for (int i = 0; i < accountList.size(); i++) {
                            xmppConnectionService.deleteAccount(accountList.get(i));
                        }
                    }*/

                    prefs.edit()
                            .putString("username", strmob)
                            .putString("user_password", parser.getValue(e, "pass"))
                            .putString("server_api", MyApplication.getPref().getString(SplashActivity.BALANCE_URL,getString(R.string.server_api))).commit();
					    Log.e("password stored is ", prefs.getString("user_password",""));
					/*MyApplication.getPref().edit().putString("username", strmob).commit();
					MyApplication.getPref().edit().putString("user_password", strpass).commit();
					MyApplication.getPref().edit().putString("server_api", getString(R.string.server_api)).commit();*/
                   /* final Jid jid;
                    try {
                        jid = Jid.fromString(MyApplication.getPref().getString("username","") + "@88.208.222.15");
                    } catch (final InvalidJidException e) {
                        return;
                    }
                    mAccount = new Account(jid.toBareJid(), MyApplication.getPref().getString("password",""));
                    LoginActivitys.flag_sign=1;
                    mAccount.setOption(Account.OPTION_DISABLED, false);
                    xmppConnectionService.updateAccount(mAccount);
                    xmppConnectionService.createAccount(mAccount);*/
                    openAlert();
			      
				}
				
				else
				{
					Toast.makeText(getApplicationContext(),
							message, Toast.LENGTH_LONG)
							.show();
					//startActivity(new Intent(
						//	getApplicationContext(),SignUpActivity.class));
				}
			}
			else
			{
                if(MyApplication.getPref().getInt("Portugusese",0)==1)
                {
                    Toast.makeText(getApplicationContext(), "Sem conectividade Internet", Toast.LENGTH_SHORT).show();

                }
                else if(MyApplication.getPref().getInt("Spanish",0)==1)
                {
                    Toast.makeText(getApplicationContext(), "Sin conexión a Internet", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Invalid Data", Toast.LENGTH_SHORT).show();
                }

			}


		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pdial = new ProgressDialog(SignUpActivity.this);
			pdial.setCancelable(false);
			pdial.setCanceledOnTouchOutside(false);
			pdial.setMessage("Connecting");
			pdial.show();
		}
	}
    public static void unregisterChat()
    {
        mAccount.setOption(Account.OPTION_DISABLED, true);
        xmppConnectionService.updateAccount(mAccount);
    }
	  private void openAlert() {
		  	         AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUpActivity.this);
          if(MyApplication.getPref().getInt("Portugusese",0)==1)
          {
              alertDialogBuilder.setTitle("Alert");

          }
          else if(MyApplication.getPref().getInt("Spanish",0)==1)
          {
              alertDialogBuilder.setTitle("Alert");
          }
          else
          {
              alertDialogBuilder.setTitle("Alert");
          }

		  	         alertDialogBuilder.setMessage(message);
		  	         // set positive button: Yes message
          if(MyApplication.getPref().getInt("Portugusese",0)==1)
          {
              alertDialogBuilder.setPositiveButton("Proceed to Login",new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog,int id) {
                      // go to a new activity of the app

                      intent = new Intent(getApplicationContext(), LoginActivitys.class);
                      startActivity(intent);
                    //  Login login = new Login();
                      //login.execute();
                  }
              });

          }
          else if(MyApplication.getPref().getInt("Spanish",0)==1)
          {
              alertDialogBuilder.setPositiveButton("Proceed to Login",new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog,int id) {
                      // go to a new activity of the app

                      intent = new Intent(getApplicationContext(), LoginActivitys.class);
                      startActivity(intent);
                     // Login login = new Login();
                     // login.execute();
                  }
              });
          }
          else
          {
              alertDialogBuilder.setPositiveButton("Proceed to Login",new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog,int id) {
                      // go to a new activity of the app

                      intent = new Intent(getApplicationContext(), LoginActivitys.class);
                      startActivity(intent);
                      //Login login = new Login();
                     // login.execute();
                  }
              });
          }

		  	        
		  	         AlertDialog alertDialog = alertDialogBuilder.create();
		           // show alert
		  	         alertDialog.show();
		      }
	  class Login extends AsyncTask<Void, Void, Void>
		{
			protected void onPreExecute() 
			{
			}
			
			@Override
			protected Void doInBackground(Void... arg0) 
			{
				online(strmob,strpass);
				return null;
			}
			
		}
		@Override
		public void onRegisterSuccess(String reason, int code) 
		{
            if(MyApplication.getPref().getInt("Portugusese",0)==1)
            {
                Toast.makeText(getApplicationContext(), "Login feito com sucesso", Toast.LENGTH_SHORT).show();

            }
            else if(MyApplication.getPref().getInt("Spanish",0)==1)
            {
                Toast.makeText(getApplicationContext(), "Login exitoso", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
            }

			if(intent != null)
			{
				MyApplication.getPref().edit().putBoolean("register", true).commit();
				startActivity(intent);
				finish();
                /*final Jid jid;
                try {
                    jid = Jid.fromString(MyApplication.getPref().getString("username","") + "@88.208.222.15");
                } catch (final InvalidJidException e) {
                    return;
                }
                Account mAccount = new Account(jid.toBareJid(), MyApplication.getPref().getString("password",""));
                mAccount.setOption(Account.OPTION_USETLS, true);
                mAccount.setOption(Account.OPTION_USECOMPRESSION, true);
                mAccount.setOption(Account.OPTION_REGISTER, true);
                this.xmppConnectionService.createAccount(mAccount);*/
			}
		}

	// user authentication failed
		@Override
		public void onRegisterFailure(String reason, int code) 
		{
            if(MyApplication.getPref().getInt("Portugusese",0)==1)
            {
                Toast.makeText(getApplicationContext(), "Falha registeration", Toast.LENGTH_SHORT).show();

            }
            else if(MyApplication.getPref().getInt("Spanish",0)==1)
            {
                Toast.makeText(getApplicationContext(), "Registeration Falló", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Registeration Failed", Toast.LENGTH_SHORT).show();
            }

			if(intent != null)
			{
				MyApplication.getPref().edit().putBoolean("register", false).commit();
				startActivity(intent);
				finish();
			}
		}
	  public int online(String user, String pass) 
		{
			int result = setUserInfo(user,pass);


			if (result == PortSipErrorcode.ECoreErrorNone)
			{
				result = mSipSdk.registerServer(90, 3);
				if(result!= PortSipErrorcode.ECoreErrorNone )
				{  if(MyApplication.getPref().getInt("Portugusese",0)==1)
                {
                    Toast.makeText(getApplicationContext(), "Server Registeration failed", Toast.LENGTH_LONG).show();

                }
                else if(MyApplication.getPref().getInt("Spanish",0)==1)
                {
                    Toast.makeText(getApplicationContext(), "Server Registeration failed", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Server Registeration failed", Toast.LENGTH_LONG).show();
                }



				}
			}


			return result;

		}
	  int setUserInfo(String user, String pass) {

			String userName, displayname, authName, password, localIP, userDomain, SIPServer, SIPPort, Stunsrv, Stunport;
			int localPort, istunport, isipsrvport;
		   Random random = new Random();
			Environment.getExternalStorageDirectory();
			localIP = new Network(getApplicationContext()).getLocalIP(false);// ipv4
			if(localIP == null)
			{
				handler.post(new Runnable() 
				{	
					@Override
					public void run() 
					{
						Toast.makeText(getApplicationContext(), "Server Registeration failed", Toast.LENGTH_LONG).show();
					}
				});
				
				return PortSipErrorcode.ECoreNotRegistered;
			}

			localPort = random.nextInt(4940) + Integer.parseInt(getString(R.string.server_port));
			userName = user;
			//authName = metauthName.getText().toString();

			//displayname = metdisplay.getText().toString();
			password = pass;
			//userDomain = metusrdomain.getText().toString();
			//SIPServer = metsipsrv.getText().toString();
			//SIPPort = metsipport.getText().toString();
			//Stunsrv = metStunsrv.getText().toString();
			//Stunport = metStunport.getText().toString();
			istunport = Integer.parseInt(getString(R.string.server_port));
			isipsrvport = Integer.parseInt(getString(R.string.server_port));
	        SIPPort=getString(R.string.server_port);
	        Stunsrv="";
	        Stunport="";
			if (Stunport != null && Stunport.length() > 0) 
			{
				istunport = Integer.valueOf(Stunport);
			}
			if (SIPPort != null && SIPPort.length() > 0) 
			{
				isipsrvport = Integer.valueOf(SIPPort);
			}

			/*if (displayname == null || displayname.length() <= 0)
			{
				displayname = userName;
			}*/
	        displayname = userName;
	        authName = userName;
	        userDomain="";
	        SIPServer=getString(R.string.server_url);

			if (userName != null && userName.length() > 0 && password != null
					&& password.length() > 0 && SIPPort != null
					&& SIPPort.length() > 0 && SIPServer != null
					&& SIPServer.length() > 0)// these fields are required
			{
				mSipSdk.CreateCallManager(getApplicationContext());// step 1
				int result = mSipSdk.initialize(PortSipEnumDefine.ENUM_TRANSPORT_UDP,
						PortSipEnumDefine.ENUM_LOG_LEVEL_DEBUG, null,
						Line.MAX_LINES, "MobiSnow V4.0",
						3,0);// step 2
				if (result != PortSipErrorcode.ECoreErrorNone)
				{
					handler.post(new Runnable() 
					{	
						@Override
						public void run() 
						{
							Toast.makeText(getApplicationContext(), "Initialization Failed", Toast.LENGTH_LONG).show();
						}
					});
					return result;
				}

				setPortSipLisenceKey(licenseKey);// step 3

				result = mSipSdk.setUser(userName, displayname, authName, password,
						localIP, localPort, userDomain, SIPServer, isipsrvport,
						Stunsrv, istunport, null, Integer.parseInt(getString(R.string.server_port)));// step 4

				if (result != PortSipErrorcode.ECoreErrorNone)
				{
					handler.post(new Runnable() 
					{	
						@Override
						public void run() 
						{
							Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();

						}
					});
					return result;
				}
			} 
			else 
			{
				handler.post(new Runnable() 
				{	
					@Override
					public void run() 
					{
						Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_LONG).show();

					}
				});
				return -1;
			}

			setAVArguments();
			return PortSipErrorcode.ECoreErrorNone;
		}
		public static boolean setPortSipLisenceKey(String lisence) 
		{
			int nSetKeyRet = mSipSdk.setLicenseKey(lisence);
			if (nSetKeyRet == PortSipErrorcode.ECoreTrialVersionLicenseKey)
			{
				return false;
			}
			
			else if (nSetKeyRet == PortSipErrorcode.ECoreWrongLicenseKey)
			{
				return false;
			}
			return true;
		}

	// set audio codecs
		void setAVArguments() {
			
			// audio codecs
			mSipSdk.clearAudioCodec();

			if (mpreferences.getBoolean(getString(R.string.MEDIA_G722), false)) {
				mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_G722);
			}

			if (mpreferences.getBoolean(getString(R.string.MEDIA_G729), true)) {
				mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_G729);
			}

			if (mpreferences.getBoolean(getString(R.string.MEDIA_AMR), false)) {
				mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_AMR);
			}

			if (mpreferences.getBoolean(getString(R.string.MEDIA_AMRWB), false)) {
				mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_AMRWB);
			}

			if (mpreferences.getBoolean(getString(R.string.MEDIA_GSM), true)) {
				mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_GSM);
			}

			if (mpreferences.getBoolean(getString(R.string.MEDIA_PCMA), true)) {
				mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_PCMA);
			}

			if (mpreferences.getBoolean(getString(R.string.MEDIA_PCMU), true)) {
				mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_PCMU);
			}

			if (mpreferences.getBoolean(getString(R.string.MEDIA_SPEEX), false)) {
				mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_SPEEX);
			}

			if (mpreferences.getBoolean(getString(R.string.MEDIA_SPEEXWB), false)) {
				mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_SPEEXWB);
			}
			if (mpreferences.getBoolean(getString(R.string.MEDIA_ILBC), false)) {
				mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_ILBC);
			}
			if (mpreferences.getBoolean(getString(R.string.MEDIA_ISACWB), false)) {
				mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_ISACWB);
			}
			if (mpreferences.getBoolean(getString(R.string.MEDIA_ISACSWB), false)) {
				mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_ISACSWB);
			}
			if (mpreferences.getBoolean(getString(R.string.MEDIA_OPUS), false)) {
				mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_OPUS);
			}
			if (mpreferences.getBoolean(getString(R.string.MEDIA_DTMF), false)) {
				mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_DTMF);
			}

			mSipSdk.enableVAD(mpreferences.getBoolean(
					getString(R.string.MEDIA_VAD), true));
			mSipSdk.enableAEC(mpreferences.getBoolean(
					getString(R.string.MEDIA_AEC), true)? PortSipEnumDefine.ENUM_EC_DEFAULT: PortSipEnumDefine.ENUM_EC_NONE);
			mSipSdk.enableANS(mpreferences.getBoolean(
					getString(R.string.MEDIA_ANS), false)? PortSipEnumDefine.ENUM_NS_DEFAULT: PortSipEnumDefine.ENUM_NS_NONE);
			mSipSdk.enableAGC(mpreferences.getBoolean(
					getString(R.string.MEDIA_AGC), true)? PortSipEnumDefine.ENUM_AGC_DEFAULT: PortSipEnumDefine.ENUM_AGC_NONE);
			mSipSdk.enableCNG(mpreferences.getBoolean(
					getString(R.string.MEDIA_CNG), true));

			// Video codecs
			mSipSdk.clearVideoCodec();

			if (mpreferences.getBoolean(getString(R.string.MEDIA_H263), false)) {
				mSipSdk.addVideoCodec(PortSipEnumDefine.ENUM_VIDEOCODEC_H263);
			}

			if (mpreferences.getBoolean(getString(R.string.MEDIA_H26398), false)) {
				mSipSdk.addVideoCodec(PortSipEnumDefine.ENUM_VIDEOCODEC_H263_1998);
			}

			if (mpreferences.getBoolean(getString(R.string.MEDIA_H264), true)) {
				mSipSdk.addVideoCodec(PortSipEnumDefine.ENUM_VIDEOCODEC_H264);
			}
			if (mpreferences.getBoolean(getString(R.string.MEDIA_VP8), false)) {
				mSipSdk.addVideoCodec(PortSipEnumDefine.ENUM_VIDEOCODEC_VP8);
			}
			
			mSipSdk.setVideoResolution(Integer.valueOf(mpreferences.getString(getString(R.string.str_resolution), "1")));

			setForward(mpreferences);

			// Use earphone
			mSipSdk.setLoudspeakerStatus(false);
			
			// Use Front Camera
			mSipSdk.setVideoDeviceId(1);
			mSipSdk.setVideoOrientation(PortSipEnumDefine.ENUM_ROTATE_CAPTURE_FRAME_270);
		}

	// set call forwarding
		private int setForward(SharedPreferences preferences) 
		{
			int ret = PortSipErrorcode.ECoreArgumentNull;
			boolean forwardopen = preferences.getBoolean(getString(R.string.str_fwopenkey), false);

			if (forwardopen == false) 
			{
				mSipSdk.disableCallForward();
				return ret;
			}

			String forwardTo = preferences.getString(
					getString(R.string.str_fwtokey), "");
			boolean forwardonbusy = preferences.getBoolean(
					getString(R.string.str_fwbusykey), true);

			if (forwardTo.length() <= 0
					|| !forwardTo.matches(MyApplication.SIP_ADDRRE_PATTERN)) 
			{
				mSipSdk.disableCallForward();
				return ret;
			}

			if (forwardonbusy) 
			{
				ret = mSipSdk.enableCallForward(true, forwardTo);
			} 
			else 
			{
				ret = mSipSdk.enableCallForward(false, forwardTo);
			}

			return ret;
		}
	public class XMLParser{
		
		XMLParser()
		{
		}
		public Document getDomElement(String xml){
	        Document doc = null;
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        try {
	 
	            DocumentBuilder db = dbf.newDocumentBuilder();
	 
	            InputSource is = new InputSource();
	                is.setCharacterStream(new StringReader(xml));
	                doc = db.parse(is);
	 
	            } catch (ParserConfigurationException e) {
	                Log.e("Error: ", e.getMessage());
	                return null;
	            } catch (SAXException e) {
	                Log.e("Error: ", e.getMessage());
	                return null;
	            } catch (IOException e) {
	                Log.e("Error: ", e.getMessage());
	                return null;
	            }
	                // return DOM
	            return doc;
	    }
		public String getValue(Element item, String str) {     
		    NodeList n = item.getElementsByTagName(str);       
		    return this.getElementValue(n.item(0));
		}
		 
		public final String getElementValue( Node elem ) {
		         Node child;
		         if( elem != null){
		             if (elem.hasChildNodes()){
		                 for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
		                     if( child.getNodeType() == Node.TEXT_NODE  ){
		                         return child.getNodeValue();
		                     }
		                 }
		             }
		         }
		         return "";
		  } 
	
	}
	@Override
	public void onBackPressed()
	{
	    super.onBackPressed();

		Intent intent = new Intent(SignUpActivity.this, LoginActivitys.class);
		startActivity(intent);
		SignUpActivity.this.finish();
	    // Do your things.
	}
    @Override
    public void onBackendConnected() {
        this.accountList.clear();
        this.accountList.addAll(xmppConnectionService.getAccounts());
        //  this.xmppConnectionService.getNotificationService().setIsInForeground(true);
    }

}
