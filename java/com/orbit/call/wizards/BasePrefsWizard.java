/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr) This file is
 * part of CSipSimple.
 *
 * CSipSimple is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version. If you own a pjsip commercial license you can also redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * an android library.
 *
 * CSipSimple is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * CSipSimple. If not, see <http://www.gnu.org/licenses/>.
 */

package com.orbit.call.wizards;

import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.orbit.call.R;

import com.portsip.PortSipEnumDefine;
import com.portsip.PortSipErrorcode;
import com.portsip.PortSipSdk;
import com.orbit.call.Line;
import com.orbit.call.MyApplication;
import com.orbit.call.acitivites.LoginActivitys;
import com.orbit.call.acitivites.SipHome;
import com.orbit.call.api.SipProfile;
import com.orbit.call.prefs.GenericPrefs;
import com.orbit.call.util.Network;
import com.orbit.call.utils.Log;
import com.orbit.call.utils.PreferencesProviderWrapper;
import com.orbit.call.utils.PreferencesWrapper;
import com.orbit.call.wizards.WizardUtils.WizardInfo;

public class BasePrefsWizard extends GenericPrefs implements MyApplication.mCallbackListener
{
    /*
     * public static final int SAVE_MENU = Menu.FIRST + 1; public static final
     * int TRANSFORM_MENU = Menu.FIRST + 2; public static final int FILTERS_MENU
     * = Menu.FIRST + 3; public static final int DELETE_MENU = Menu.FIRST + 4;
     */
    public static String licenseKey ="1Uh0zMTNDOEFCNTUwMUMxQzRFMkJCNjVGMUIyMjM3RDU4NEAzOEYxNTM5QjI5NzIwMkUyQzNBNTVBOUUyRUMwMjZDMUBBMUMyRThCNjdGQTcyMEFGMkM5ODg0MDQwQTRCNTA4OUBFREIxNTE3MEU2M0QwNkZFNTJFNzg5MTdDMDFBRDg3MA";
    private static final String	THIS_FILE	= "Base Prefs wizard";
    static PortSipSdk mSipSdk;
    MyApplication myApplication;
    private final static Handler handler = new Handler();
    protected SipProfile		account		= null;
    private Button				saveButton;
    private String				wizardId	= "";
    private WizardIface			wizard		= null;
    private boolean				fl;
    private PreferencesWrapper	prefsWrapper;
    private PreferencesProviderWrapper	prefProviderWrapper;
    private SipHome sipHome,sh;
    private static SharedPreferences		pref;
    private SharedPreferences mpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Get back the concerned account and if any set the current (if not a
        // new account is created)
        getActionBar().setDisplayHomeAsUpEnabled(true);
        myApplication = ((MyApplication) getApplicationContext());
        myApplication.setCallbackListener(this);
        mSipSdk = myApplication.getPortSIPSDK();
        Intent intent = getIntent();
        System.out.println("oncreate");
        long accountId = intent.getLongExtra(SipProfile.FIELD_ID, SipProfile.INVALID_ID);

        // TODO : ensure this is not null...
        setWizardId(intent.getStringExtra(SipProfile.FIELD_WIZARD));
        //account = SipProfile.getProfileFromDbId(this, accountId, DBProvider.ACCOUNT_FULL_PROJECTION);
        fl = intent.getBooleanExtra("fl", false);
        System.out.println("fl is "+fl);
        super.onCreate(savedInstanceState);
        Button bt = (Button) findViewById(R.id.cancel_bt);
        saveButton = (Button) findViewById(R.id.save_bt);
        System.out.println("fl is11 "+fl);
        mpreferences = getSharedPreferences(
                String.format("%s_preferences", this.getPackageName()),
                Context.MODE_PRIVATE);
        // Bind buttons to their actions
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        System.out.println("fl is 22"+fl);
        bt.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setResult(RESULT_CANCELED, getIntent());
                BasePrefsWizard.this.finish();

            }
        });
        System.out.println("fl is 33"+fl);
		/*if (prefsWrapper == null)
		{
			prefsWrapper = new PreferencesWrapper(getApplicationContext());
		}*/
        System.out.println("fl is 44"+fl);

        saveButton.setEnabled(false);
        saveButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                saveAndFinish();
                MyApplication.getPref().edit().putBoolean("get_val", true);


            }
        });
        System.out.println("fl is 55"+fl);
        if (fl)
        {
            System.out.println("inside if ");
            //MyApplication.getPref().edit().putString("username", intent.getStringExtra("usrid")).commit();
            //MyApplication.getPref().edit().putString("password", intent.getStringExtra("pass").trim()).commit();
            //MyApplication.getPref().edit().putString("server_api", intent.getStringExtra("server").trim()).commit();
			/*account.username = intent.getStringExtra("usrid");
			if (intent.getStringExtra("api") != null && !intent.getStringExtra("api").trim().equals("")) 
				account.api = intent.getStringExtra("api").trim();
			else account.api = intent.getStringExtra("server").trim();
			String[] serverParts = intent.getStringExtra("server").trim().split(":");
			account.acc_id = "<sip:" + SipUri.encodeUser(intent.getStringExtra("usrid")) + "@" + serverParts[0].trim() + ">";
			account.data = intent.getStringExtra("pass");
			account.display_name = "PhoneTel";
			account.allow_contact_rewrite = false;

			String regUri = "sip:" + intent.getStringExtra("server").trim();
			account.reg_uri = regUri;
			account.proxies = new String[]
			{
				regUri
			};*/

        }

        wizard.fillLayout(account);
        hidepref("account_setting","api");
        //hidepref("account_setting","country");
        hidepref("account_setting","server");
        hidepref("account_setting","server");
        //hidepref("account_setting","access_number");
        //hidepref("account_setting","pause_time");

    }
    public void disconnect(boolean quit) {
        offline();
        finish();
        Intent intent = new Intent(this, LoginActivitys.class);
        //intent.putExtra(Intent.EXTRA_TEXT, quit);
        startActivity(intent);

    }
    private void offline() {
        Line[] mLines = myApplication.getLines();
        for (int i = Line.LINE_BASE; i < Line.MAX_LINES; ++i) {
            if (mLines[i].getRecvCallState() == true) {
                mSipSdk.rejectCall(mLines[i].getSessionId(), 486);
            } else if (mLines[i].getSessionState() == true) {
                mSipSdk.hangUp(mLines[i].getSessionId());
            }

            mLines[i].reset();
        }
        myApplication.setOnlineState(false);
        mSipSdk.unRegisterServer();
        mSipSdk.DeleteCallManager();
    }
    private String getDIDFormatedString(String access_number, String number) {
        StringBuilder sb = new StringBuilder();
        sb.append(access_number);
		/*for (int i = 0; i < Integer.parseInt(Engin.getPref().getString(
				"pause_time", "2")); i++)*/
        for (int i = 0; i < 1; i++)
            sb.append(",");
        if (pref.getBoolean("auto_pin", false)) {
            sb.append(pref.getString("pin_number", ""));
            for (int i = 0; i < Integer.parseInt(pref.getString(
                    "pause_time_after_pin", "2")); i++)
                sb.append(",");
        }
        sb.append(number);
        return sb.toString();
    }
    private void hidepref(String p)
    {
        PreferenceScreen pfs = getPreferenceScreen();
        PreferenceGroup parentPref = pfs;

        Preference toRemovePref = pfs.findPreference(p);

        if (toRemovePref != null && parentPref != null)
        {
            parentPref.removePreference(toRemovePref);
        }
    }
    public void hidepref(String parent, String fieldName) {
        PreferenceScreen pfs = getPreferenceScreen();
        PreferenceGroup parentPref = pfs;
        if (parent != null) {
            parentPref = (PreferenceGroup) pfs.findPreference(parent);
        }

        Preference toRemovePref = pfs.findPreference(fieldName);

        if (toRemovePref != null && parentPref != null) {
            parentPref.removePreference(toRemovePref);
        } else {
            Log.w("Generic prefs", "Not able to find" + parent + " " + fieldName);
        }
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        updateDescriptions();
        updateValidation();
        if (fl) saveAndFinish();
    }
    private boolean setWizardId(String wId)
    {
        if (wizardId == null) { return setWizardId(WizardUtils.EXPERT_WIZARD_TAG); }

        WizardInfo wizardInfo = WizardUtils.getWizardClass(wId);
        if (wizardInfo == null)
        {
            if (!wizardId.equals(WizardUtils.EXPERT_WIZARD_TAG)) { return setWizardId(WizardUtils.EXPERT_WIZARD_TAG); }
            return false;
        }

        try
        {
            wizard = (WizardIface) wizardInfo.classObject.newInstance();
        }
        catch (IllegalAccessException e)
        {
            Log.e(THIS_FILE, "Can't access wizard class", e);
            if (!wizardId.equals(WizardUtils.EXPERT_WIZARD_TAG)) { return setWizardId(WizardUtils.EXPERT_WIZARD_TAG); }
            return false;
        }
        catch (InstantiationException e)
        {
            Log.e(THIS_FILE, "Can't access wizard class", e);
            if (!wizardId.equals(WizardUtils.EXPERT_WIZARD_TAG)) { return setWizardId(WizardUtils.EXPERT_WIZARD_TAG); }
            return false;
        }
        wizardId = wId;
        wizard.setParent(this);

        return true;
    }

    @Override
    protected void beforeBuildPrefs()
    {
        // Use our custom wizard view
        setContentView(R.layout.wizard_prefs_base);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        System.out.println("onshared preference");
        updateDescriptions();
        updateValidation();
    }

    /**
     * Update validation state of the current activity. It will check if wizard
     * can be saved and if so will enable button
     */
    public void updateValidation()
    {
        System.out.println("the wizard can save ");
        saveButton.setEnabled(wizard.canSave());
    }

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * menu.add(Menu.NONE, SAVE_MENU, Menu.NONE,
	 * R.string.save).setIcon(android.R.drawable.ic_menu_save); if (account.id
	 * != SipProfile.INVALID_ID) { menu.add(Menu.NONE, TRANSFORM_MENU,
	 * Menu.NONE,
	 * R.string.choose_wizard).setIcon(android.R.drawable.ic_menu_edit);
	 * menu.add(Menu.NONE, FILTERS_MENU, Menu.NONE,
	 * R.string.filters).setIcon(R.drawable.ic_menu_filter); menu.add(Menu.NONE,
	 * DELETE_MENU, Menu.NONE,
	 * R.string.delete_account).setIcon(android.R.drawable.ic_menu_delete); }
	 * return super.onCreateOptionsMenu(menu); }
	 */
	/*
	 * @Override public boolean onPrepareOptionsMenu(Menu menu) {
	 * menu.findItem(SAVE_MENU).setVisible(wizard.canSave());
	 * 
	 * return super.onPrepareOptionsMenu(menu); }
	 */

    private static final int	CHOOSE_WIZARD		= 0;
    private static final int	MODIFY_FILTERS		= CHOOSE_WIZARD + 1;

    private static final int	FINAL_ACTIVITY_CODE	= MODIFY_FILTERS;

    private int					currentActivityCode	= FINAL_ACTIVITY_CODE;

    public int getFreeSubActivityCode()
    {
        currentActivityCode++;
        return currentActivityCode;
    }

	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case SAVE_MENU: saveAndFinish(); return true; case
	 * TRANSFORM_MENU: startActivityForResult(new Intent(this,
	 * WizardChooser.class), CHOOSE_WIZARD); return true; case DELETE_MENU: if
	 * (account.id != SipProfile.INVALID_ID) {
	 * getContentResolver().delete(ContentUris
	 * .withAppendedId(SipProfile.ACCOUNT_ID_URI_BASE, account.id), null, null);
	 * setResult(RESULT_OK, getIntent()); finish(); } return true; case
	 * FILTERS_MENU: if (account.id != SipProfile.INVALID_ID) { Intent it = new
	 * Intent(this, AccountFilters.class); it.putExtra(SipProfile.FIELD_ID,
	 * account.id); startActivityForResult(it, MODIFY_FILTERS); return true; }
	 * break; default: break; } return super.onOptionsItemSelected(item); }
	 */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_WIZARD && resultCode == RESULT_OK && data != null && data.getExtras() != null)
        {
            String wizardId = data.getStringExtra(WizardUtils.ID);
            if (wizardId != null)
            {
                saveAccount(wizardId);
                setResult(RESULT_OK, getIntent());
                finish();
            }
        }

        if (requestCode > FINAL_ACTIVITY_CODE)
        {
            wizard.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Save account and end the activity
     */
    public void saveAndFinish()
    {
        saveAccount();
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy()
    {
//		Intent dialpad = new Intent(this, SipHome.class);
//		startActivity(dialpad);
        super.onDestroy();
		/*boolean flag= MyApplication.getPref().getBoolean("create_account", false);
		System.out.println("the flaggggg "+flag);
		if(flag)
		{
			Intent dialpad = new Intent(getApplicationContext(), SipHome.class);
			startActivity(dialpad);
			MyApplication.getPref().edit().putBoolean("create_account", false);
			super.onDestroy();
		}
   else
	{
		super.onDestroy();   // shekhar
     	}*/
    }

    /*
     * Save the account with current wizard id
     */
    private void saveAccount()
    {
        saveAccount(wizardId);

    }

    /**
     * Save the account with given wizard id
     *
     * @param wizardId
     *            the wizard to use for account entry
     */
    @Override
    public void onRegisterSuccess(String reason, int code)
    {
        MyApplication.getPref().edit().putBoolean("register", true).commit();
        //Toast.makeText(getApplicationContext(), "Registered", Toast.LENGTH_LONG).show();
    }

    // user authentication failed
    @Override
    public void onRegisterFailure(String reason, int code)
    {
        MyApplication.getPref().edit().putBoolean("register", false).commit();
        //Toast.makeText(getApplicationContext(), "Unregistered", Toast.LENGTH_LONG).show();
    }
    private void saveAccount(String wizardId)
    {
        if(myApplication.isOnline())
        {
            wizard.buildAccount(account);
            online(MyApplication.getPref().getString("username", ""),MyApplication.getPref().getString("password", ""));
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Internet is found", Toast.LENGTH_LONG).show();
        }
    }
    public int online(String user, String pass)
    {
        int result = setUserInfo(user,pass);


        if (result == PortSipErrorcode.ECoreErrorNone)
        {
            result = mSipSdk.registerServer(90, 3);
            if(result!=PortSipErrorcode.ECoreErrorNone )
            {
                Toast.makeText(getApplicationContext(), "Server Registeration failed", Toast.LENGTH_LONG).show();
                //killTasks();
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
                    Toast.makeText(getApplicationContext(), "No Network Available", Toast.LENGTH_LONG).show();
                    //killTasks();
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
            int result = mSipSdk.initialize(LoginActivitys.transtype,
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
                        //killTasks();
                    }
                });
                return result;
            }

            LoginActivitys.setPortSipLisenceKey(licenseKey);// step 3

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
                        //killTasks();
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
                    //killTasks();
                }
            });
            return -1;
        }

        setAVArguments();
        return PortSipErrorcode.ECoreErrorNone;
    }
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
                getString(R.string.MEDIA_AEC), true)?PortSipEnumDefine.ENUM_EC_DEFAULT:PortSipEnumDefine.ENUM_EC_NONE);
        mSipSdk.enableANS(mpreferences.getBoolean(
                getString(R.string.MEDIA_ANS), false)?PortSipEnumDefine.ENUM_NS_DEFAULT:PortSipEnumDefine.ENUM_NS_NONE);
        mSipSdk.enableAGC(mpreferences.getBoolean(
                getString(R.string.MEDIA_AGC), true)?PortSipEnumDefine.ENUM_AGC_DEFAULT:PortSipEnumDefine.ENUM_AGC_NONE);
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

    @Override
    protected int getXmlPreferences()
    {
        return wizard.getBasePreferenceResource();
    }

    @Override
    protected void updateDescriptions()
    {
        wizard.updateDescriptions();
    }

    @Override
    protected String getDefaultFieldSummary(String fieldName)
    {
        return wizard.getDefaultFieldSummary(fieldName);
    }

}