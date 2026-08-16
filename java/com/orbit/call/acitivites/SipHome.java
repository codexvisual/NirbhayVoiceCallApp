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

package com.orbit.call.acitivites;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.orbit.call.CallHistory;
import com.orbit.call.MyCustomViewPager;
import com.orbit.call.R;
import com.portsip.PortSipEnumDefine;
import com.portsip.PortSipErrorcode;
import com.portsip.PortSipSdk;
import com.orbit.call.AudioSettingFragment;
import com.orbit.call.FreecallsFragment;
import com.orbit.call.Line;
import com.orbit.call.MyAccount;
import com.orbit.call.MyApplication;
import com.orbit.call.NumpadFragment;
import com.orbit.call.Remote.*;
import com.orbit.call.Remote.RemoteData.*;
import com.orbit.call.api.SipManager;
import com.orbit.call.interfaces.Messanger;
import com.orbit.call.ui.StartConversationFragment;
import com.orbit.call.util.DelayTimer;
import com.orbit.call.util.Network;
import com.orbit.call.utils.Log;
import com.orbit.call.utils.PreferencesProviderWrapper;

import android.graphics.*;
public class SipHome extends SherlockFragmentActivity implements Messanger,MyApplication.mCallbackListener,
        OnRemoteCompleated,DelayTimer.TimerCallbackInterface {
    public static String licenseKey ="1Uh0zMTNDOEFCNTUwMUMxQzRFMkJCNjVGMUIyMjM3RDU4NEAzOEYxNTM5QjI5NzIwMkUyQzNBNTVBOUUyRUMwMjZDMUBBMUMyRThCNjdGQTcyMEFGMkM5ODg0MDQwQTRCNTA4OUBFREIxNTE3MEU2M0QwNkZFNTJFNzg5MTdDMDFBRDg3MA";
    public static final int ACCOUNTS_MENU = Menu.FIRST + 1;
    public static final int PARAMS_MENU = Menu.FIRST + 2;
    public static final int CLOSE_MENU = Menu.FIRST + 3;
    public static final int HELP_MENU = Menu.FIRST + 4;
    public static final int DISTRIB_ACCOUNT_MENU = Menu.FIRST + 5;
    public static byte DID_CALL = 0x0;
    public static String bal;
    NumpadFragment numpadFragment = null;
    StartConversationFragment startConversationFragment = null;
    CallHistory callhistoryFrag;
    FreecallsFragment freecallFragment = null;
    ContactsFragment contactsFragment=null;
    FevContactsFragment fevContactsFragment=null;
    AudioSettingFragment audioSettingFragment = null;
    MyAccount settingFragment = null;
    private static final String THIS_FILE = "SIP_HOME";
    private boolean onForeground = false;
    private static final int TAB_INDEX_DIALER = 0;
    private static final int TAB_INDEX_CALL_LOG = TAB_INDEX_DIALER + 1;
	/* private static final int TAB_INDEX_MESSAGES = TAB_INDEX_CALL_LOG+1; */

    private static final int TAB_INDEX_CONTACTS = TAB_INDEX_CALL_LOG + 1;
    private static final int TAB_INDEX_FAVORITES = TAB_INDEX_CONTACTS + 1;
    private static final int TAB_INDEX_MYACCOUNT = TAB_INDEX_FAVORITES + 1;
    private static SharedPreferences		pref;
    private static SharedPreferences.Editor	editor;
    private PreferencesProviderWrapper prefProviderWrapper;
    public static String us; //
    public static String pass; //
    private static final int REQUEST_EDIT_DISTRIBUTION_ACCOUNT = 0;
    private boolean hasTriedOnceActivateAcc = false;
    // private ImageButton pickupContact;
   public static MyCustomViewPager mViewPager;
   // public static ViewPager mViewPager;
    public static TabsAdapter mTabsAdapter;
    private boolean mDualPane;
    // private Thread asyncSanityChecker;
    private TextView adminMsg;
    private LinearLayout header;
    public final static boolean USE_LIGHT_THEME = false;
    SharedPreferences prefs;
    static PortSipSdk mSipSdk;
    private MyApplication myApplication;
    private SharedPreferences mpreferences;
    ProgressDialog pd;
    int net_flag=0;
    private DelayTimer delay;
    private Thread thread;
    private static boolean canLogin;
    public static int netflag;
    private final static Handler handler = new Handler();
    public static ArrayList<String> confNumbers =new ArrayList<String>();
    public static HashMap<String,String> confNames =new HashMap<String,String>();
    public static HashMap<String,Integer> unReadMessageCounter	= new HashMap<String,Integer>();
    public static ArrayList<String> friendlist=new ArrayList<String>();
    LinearLayout layout_numpad, layout_chat, layout_contact, layout_fav, layout_more;
    Button btnchat;
    public interface ViewPagerVisibilityListener {
        void onVisibilityChanged(boolean visible);
    }

    @Override
    public void onBackPressed() {

        int h = mTabsAdapter.mCurrentPosition;
       // Toast.makeText(SipHome.this, " "+h, Toast.LENGTH_SHORT).show();
        if (mTabsAdapter.mCurrentPosition <= 0)
        {
            if(MyApplication.getPref().getInt("add_call", 0)!=0)
            {
                MyApplication.getPref().edit().putInt("add_call", (MyApplication.getPref().getInt("add_call", 0)-1)).commit();

                MyApplication.getPref().edit().putBoolean("isCallMade", false).commit();

                finish();
            }
            else
            {
                moveTaskToBack(true);
            }
            //android.os.Process.killProcess(android.os.Process.myPid());

            //	mViewPager.setCurrentItem(0, true);
        }
/*		else if (mTabsAdapter.mCurrentPosition == 4
				&& MyAccount.viewPosition == 1)
			((Messanger) getFragmentAt(4)).send((byte) 0, null);*/
        else
            mViewPager.setCurrentItem(mTabsAdapter.mCurrentPosition - 1, true);

        // super.onBackPressed();
    }

    private void setTheme() {
        header.setBackgroundColor(Color.parseColor("#FFFFFF"));


    }

    private void setAdminMsg() {
        RemoteData remoteData = new RemoteData(1, SipHome.this);
        remoteData.execute(RemoteData.RESULT_XML,
                getResources().getString(R.string.admin_msg_uri));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("siphome on create");
        prefProviderWrapper = new PreferencesProviderWrapper(this);
        if (USE_LIGHT_THEME) {
            setTheme(R.style.LightTheme_noTopActionBar);
        }
        prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sip_home);


        if (callhistoryFrag == null) {
            callhistoryFrag = new CallHistory();
        }

        if (startConversationFragment == null) {
            startConversationFragment = new StartConversationFragment();
        }
        if (numpadFragment == null) {
            numpadFragment = new NumpadFragment();
        }
        if (freecallFragment == null) {
            freecallFragment = new FreecallsFragment();
        }
        if (contactsFragment == null) {
            contactsFragment = new ContactsFragment();
        }
        if (fevContactsFragment == null) {
            fevContactsFragment = new FevContactsFragment();
        }
        if (settingFragment == null) {
            settingFragment = new MyAccount();
        }
        if (audioSettingFragment == null) {
            audioSettingFragment = new AudioSettingFragment();
        }
        header = (LinearLayout) findViewById(R.id.header);
        adminMsg = (TextView) findViewById(R.id.admin_msg);
        adminMsg.setAutoLinkMask(0x01);
        adminMsg.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        adminMsg.setLinksClickable(true);
        adminMsg.setFocusable(true);
        adminMsg.setFocusableInTouchMode(true);
        adminMsg.setHorizontallyScrolling(true);
        adminMsg.setSingleLine(true);

        adminMsg.setTextSize(15);
        adminMsg.setTypeface(Typeface.SERIF);
        adminMsg.setMarqueeRepeatLimit(-1);
        adminMsg.setPadding(15, 1, 15, 0);
        setAdminMsg();
        setTheme();
        initiateNumber();
        MyApplication.getPref();
        mpreferences = getSharedPreferences(
                String.format("%s_preferences", this.getPackageName()),
                Context.MODE_PRIVATE);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        myApplication = (MyApplication) getApplicationContext();
        if(MyApplication.Online())
        {
            mSipSdk = myApplication.getPortSIPSDK();
            myApplication.setCallbackListener(this);
            if(MyApplication.getPref().getInt("add_call", 0)==0 && !MyApplication.getPref().getBoolean("isSiphome", false))
            {
                Login login = new Login();
                login.execute();
            }
        }
        final ActionBar ab = getSupportActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ab.setDisplayShowHomeEnabled(false);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(false);

        Intent intent= getIntent();
        netflag=intent.getIntExtra("network", netflag);
        mDualPane = getResources().getBoolean(R.bool.use_dual_panes);

        Tab dialerTab = ab.newTab().setCustomView(R.layout.tabview)
                .setContentDescription(R.string.dial_tab_name_text);
        TextView textView = null;
        if (Build.VERSION.SDK_INT >= 16) {
            System.out.println("notab");
            textView = (TextView) dialerTab.getCustomView().findViewById(
                    R.id.tabview);
            textView.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    getResources().getDrawable(
                            R.drawable.ic_ab_dialer_holo_dark), null, null);
            textView.setText(getResources().getString(R.string.num_pad));
            textView.setTextSize(11);
        } else {
            System.out.println("tab");
            textView = (TextView) dialerTab.getCustomView().findViewById(
                    R.id.tabview);
            textView.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    getResources().getDrawable(
                            R.drawable.ic_ab_dialer_holo_dark), null, null);
            textView.setText(getResources().getString(R.string.num_pad));

        }

        Tab callLogTab = ab.newTab().setCustomView(R.layout.tabview)
                .setContentDescription(R.string.recent_tab_text);
        //callLogTab.setTabListener(new ListItemAdapter.OnTagClickedListener())

        if (Build.VERSION.SDK_INT >= 16) {
            textView = (TextView) callLogTab.getCustomView().findViewById(
                    R.id.tabview);
            textView.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    getResources().getDrawable(
                            R.drawable.ic_ab_history_holo_dark), null, null);
            textView.setText(getResources().getString(R.string.recent));
            textView.setTextSize(11);
        } else {
            textView = (TextView) callLogTab.getCustomView().findViewById(
                    R.id.tabview);
            textView.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    getResources().getDrawable(
                            R.drawable.ic_ab_history_holo_dark), null, null);
            textView.setText(getResources().getString(R.string.recent));

        }


        Tab favoritesTab = ab.newTab().setCustomView(R.layout.tabview)
                .setContentDescription(R.string.setting_tab_name_text);
        if (Build.VERSION.SDK_INT >= 16) {
            textView = (TextView) favoritesTab.getCustomView().findViewById(
                    R.id.tabview);
            textView.setCompoundDrawablesWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.fev), null, null);
            textView.setText(getResources().getString(R.string.favorite));
            textView.setTextSize(11);
        } else {
            textView = (TextView) favoritesTab.getCustomView().findViewById(
                    R.id.tabview);
            textView.setCompoundDrawablesWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.fev), null, null);
            textView.setText(getResources().getString(R.string.favorite));
        }

        Tab phonebook = null;
        if (!mDualPane) {
            phonebook = ab.newTab().setCustomView(R.layout.tabview)
                    .setContentDescription(R.string.calllog_tab_name_text);
            if (Build.VERSION.SDK_INT >= 16) {
                textView = (TextView) phonebook.getCustomView().findViewById(
                        R.id.tabview);
                textView.setCompoundDrawablesWithIntrinsicBounds(null,
                        getResources().getDrawable(R.drawable.contacts), null,
                        null);
                textView.setText(getResources().getString(R.string.contacts));
                textView.setTextSize(11);
            } else {
                textView = (TextView) phonebook.getCustomView().findViewById(
                        R.id.tabview);
                textView.setCompoundDrawablesWithIntrinsicBounds(null,
                        getResources().getDrawable(R.drawable.contacts), null,
                        null);
                textView.setText(getResources().getString(R.string.contacts));
            }
        }

        Tab settings = ab.newTab().setCustomView(R.layout.tabview)
                .setContentDescription(R.string.setting_tab_name_text);
        if (Build.VERSION.SDK_INT >= 16) {
            textView = (TextView) settings.getCustomView().findViewById(
                    R.id.tabview);
            textView.setCompoundDrawablesWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.myaccount), null,
                    null);
            textView.setText(getResources().getString(R.string.settings));
            textView.setTextSize(11);
        } else {
            textView = (TextView) settings.getCustomView().findViewById(
                    R.id.tabview);
            textView.setCompoundDrawablesWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.myaccount), null,
                    null);
            textView.setText(getResources().getString(R.string.settings));
        }

        mViewPager = (MyCustomViewPager) findViewById(R.id.pager);
        mTabsAdapter = new TabsAdapter(this, getSupportActionBar(), mViewPager);


        mTabsAdapter.addTab(dialerTab, NumpadFragment.class);
        mTabsAdapter.addTab(callLogTab, CallHistory.class);
        mTabsAdapter.addTab(phonebook, ContactsFragment.class);
        mTabsAdapter.addTab(favoritesTab, FevContactsFragment.class);
        mTabsAdapter.addTab(settings, MyAccount.class);

    }

    /**
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated TabHost. It relies on a
     * trick. Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show. This is not sufficient for switching
     * between pages. So instead we make the content part of the tab host 0dp
     * high (it is not shown) and the TabsAdapter supplies its own dummy view to
     * show as the tab content. It listens to changes in tabs, and takes care of
     * switch to the correct paged in the ViewPager whenever the selected tab
     * changes.
     */
    public class TabsAdapter extends FragmentPagerAdapter implements
            MyCustomViewPager.OnPageChangeListener, ActionBar.TabListener {
        private final Context mContext;
        private final ActionBar mActionBar;
        private final MyCustomViewPager mViewPager;
        private final List<String> mTabs = new ArrayList<String>();
        private boolean hasClearedDetails = false;

        public int mCurrentPosition = -1;
        /**
         * Used during page migration, to remember the next position
         * {@link #onPageSelected(int)} specified.
         */
        public int mNextPosition = -1;

        public TabsAdapter(FragmentActivity activity, ActionBar actionBar,
                           MyCustomViewPager pager) {
            super(activity.getSupportFragmentManager());
            mContext = activity;
            mActionBar = actionBar;
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(Tab tab, Class<?> clss) {
            mTabs.add(clss.getName());
            mActionBar.addTab(tab.setTabListener(this));
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            return Fragment.instantiate(mContext, mTabs.get(position), null);
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            clearDetails();

            if (mViewPager.getCurrentItem() != tab.getPosition()) {
                mViewPager.setCurrentItem(tab.getPosition(), true);

            }

            /*if (tab.getPosition() == 1){
                Intent browserIntent = new Intent(SipHome.this,CallHistory.class);
                startActivity(browserIntent);

                *//*Intent intent=new Intent(SipHome.this, StartConversationActivity.class);
                intent.putExtra("phone_number","");
                startActivity(intent);*//*
            }*/
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mViewPager.getApplicationWindowToken(),
                    0);
        }

        @Override
        public void onPageSelected(int position) {

            mActionBar.setSelectedNavigationItem(position);
            // getPrevStateIcon(mActionBar);
            // if (getIcon(position) != 0)
            // mActionBar.getTabAt(position).setIcon(getIcon(position));

            if (mCurrentPosition == position) {
                Log.w(THIS_FILE,
                        "Previous position and next position became same ("
                                + position + ")");
            }


            mNextPosition = position;
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            System.out.println("sfgsg"); // Nothing to do


        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            System.out.println("sfgsg");// Nothing to do
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            System.out.println("sfgsg");
            // Nothing to do
        }

		/*
		 * public void setCurrentPosition(int position) { mCurrentPosition =
		 * position; }
		 */

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                case MyCustomViewPager.SCROLL_STATE_IDLE: {
                    if (mCurrentPosition >= 0) {
                        sendFragmentVisibilityChange(mCurrentPosition, false);
                    }
                    if (mNextPosition >= 0) {
                        sendFragmentVisibilityChange(mNextPosition, true);
                    }
                    // invalidateOptionsMenu();

                    mCurrentPosition = mNextPosition;
                    break;
                }
                case MyCustomViewPager.SCROLL_STATE_DRAGGING:
                    clearDetails();
                    hasClearedDetails = true;
                    break;
                case MyCustomViewPager.SCROLL_STATE_SETTLING:
                    hasClearedDetails = false;
                    break;
                default:
                    break;
            }
        }

        private void clearDetails() {
            if (mDualPane && !hasClearedDetails) {
                FragmentTransaction ft = SipHome.this
                        .getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.details, new Fragment(), null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        }
    }

    private Fragment getFragmentAt(int position) {
        switch (position) {
            case TAB_INDEX_DIALER:
                return numpadFragment;
            case TAB_INDEX_CALL_LOG:
                return freecallFragment;
			/*
			 * case TAB_INDEX_MESSAGES: return mMessagesFragment;
			 */
            case TAB_INDEX_FAVORITES:
                return fevContactsFragment;
            case TAB_INDEX_CONTACTS:
                return contactsFragment;
            case TAB_INDEX_MYACCOUNT:
                return settingFragment;
            default:
                throw new IllegalStateException("Unknown fragment index: "
                        + position);
        }
    }

    public Fragment getCurrentFragment() {
        if (mViewPager != null) {
            return getFragmentAt(mViewPager.getCurrentItem());
        }
        return null;
    }

    private void sendFragmentVisibilityChange(int position, boolean visibility) {
        final Fragment fragment = getFragmentAt(position);
        if (fragment instanceof ViewPagerVisibilityListener) {
            ((ViewPagerVisibilityListener) fragment)
                    .onVisibilityChanged(visibility);
        }
    }

    /*@Override
    public void onAttachFragment(Fragment fragment) {
        // This method can be called before onCreate(), at which point we cannot
        // rely on ViewPager.
        // In that case, we will setup the "current position" soon after the
        // ViewPager is ready.
        final int currentPosition = mViewPager != null ? mViewPager
                .getCurrentItem() : -1;

        if (fragment instanceof NumpadFragment) {
            numpadFragment = (NumpadFragment) fragment;
            if (currentPosition == TAB_INDEX_DIALER) {
                numpadFragment.onVisibilityChanged(true);
            }
        } else if (fragment instanceof CallLogListFragment) {
            mCallLogFragment = (CallLogListFragment) fragment;
            if (currentPosition == TAB_INDEX_CALL_LOG) {
                mCallLogFragment.onVisibilityChanged(true);
            }
        }
        *//*
		 * else if (fragment instanceof ConversationsListFragment) {
		 * mMessagesFragment = (ConversationsListFragment) fragment; }
		 *//*
		else if (fragment instanceof FevContactsFragment) {
			mPhoneFavoriteFragment = (FevContactsFragment) fragment;
		} else if (fragment instanceof ContactsFragment) {
			mContacts = (ContactsFragment) fragment;
		} else if (fragment instanceof MyAccount) {
			mMyAccount = (MyAccount) fragment;
		}

	}*/
    @Override
    protected void onPause() {
        ((MyApplication) getApplicationContext()).setMainActivity(this);
        super.onPause();

    }

    @Override
    protected void onResume() {
        ((MyApplication) getApplicationContext()).setMainActivity(this);
        System.out.println("siphome on resume");
        if(!SplashActivity.noInternet)
        {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (!MyApplication.Online()) {
                        net_flag = 1;
                    } else if (net_flag == 1 && MyApplication.Online()) {
                        myApplication.setCallbackListener(SipHome.this);
                        net_flag = 0;
                        Login login = new Login();
                        login.execute();
                    }
                    handler.postDelayed(this, 1000);
                }
            }, 1000);
        }
        super.onResume();

    }
    public class Login extends AsyncTask<Void, Void, Void>
    {

        protected void onPreExecute()
        {
            delay = new DelayTimer();
            delay.registerInterface(SipHome.this);
            thread = new Thread(delay);
            thread.start();
            pd=new ProgressDialog(SipHome.this);
            pd.setCancelable(false);
            pd.setMessage("Loading...");
            pd.show();
            canLogin = true;
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            online(MyApplication.getPref().getString("username", ""),MyApplication.getPref().getString("user_password", ""));
            return null;
        }

    }
    @Override
    public void updateTime(long seconds)
    {
        if(seconds == (long)20)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if(canLogin==true)
                    {
                        //Toast.makeText(SipHome.this, "update time over", Toast.LENGTH_SHORT).show();
                        MyApplication.getPref().edit().putBoolean("register", false).commit();
                        pd.dismiss();
                        NumpadFragment.status.setText(getString(R.string.unregister));
                        killTasks();
                    }
                }
            });
        }
    }
    public void killTasks()
    {
        try
        {
            thread.interrupt();
        }
        catch(NullPointerException exception)
        {}
    }
    @Override
    public void onRegisterSuccess(String reason, int code)
    {
        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
        //if(intent != null)
        //{
        mSipSdk.setKeepAliveTime(50);
        MyApplication.getPref().edit().putBoolean("register", true).commit();
        pd.dismiss();
        canLogin = false;
        if(NumpadFragment.status!=null)
            NumpadFragment.status.setText(getString(R.string.registed));
        //startActivity(intent);
        //finish();
        //}
    }

    // user authentication failed
    @Override
    public void onRegisterFailure(String reason, int code)
    {
        Toast.makeText(getApplicationContext(), "Registeration Failed", Toast.LENGTH_SHORT).show();
        //if(intent != null)
        //{
        MyApplication.getPref().edit().putBoolean("register", false).commit();
        pd.dismiss();
        if(NumpadFragment.status!=null)
            NumpadFragment.status.setText(getString(R.string.unregister));
        //startActivity(intent);
        //finish();
        //}
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

        localPort = random.nextInt(4940) + Integer.parseInt(pref.getString(SplashActivity.SIPPORT,getString(R.string.server_port)));
        userName = user;
        //authName = metauthName.getText().toString();

        //displayname = metdisplay.getText().toString();
        password = pass;
        //userDomain = metusrdomain.getText().toString();
        //SIPServer = metsipsrv.getText().toString();
        //SIPPort = metsipport.getText().toString();
        //Stunsrv = metStunsrv.getText().toString();
        //Stunport = metStunport.getText().toString();


        istunport = Integer.parseInt(pref.getString(SplashActivity.SIPPORT, getString(R.string.server_port)));
        isipsrvport = Integer.parseInt(pref.getString(SplashActivity.SIPPORT, getString(R.string.server_port)));
        SIPPort=pref.getString(SplashActivity.SIPPORT, getString(R.string.server_port));
        /*istunport = 5060;
        isipsrvport = 5060;
        SIPPort = "5060";*/
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
        SIPServer = pref.getString(SplashActivity.SIPSERVER,getString(R.string.server_url));
       // SIPServer=getString(R.string.server_url);

        if (userName != null && userName.length() > 0 && password != null
                && password.length() > 0 && SIPPort != null
                && SIPPort.length() > 0 && SIPServer != null
                && SIPServer.length() > 0)// these fields are required
        {
            int trastype = 3;
            mSipSdk.CreateCallManager(SipHome.this);// step 1
            String transportType = MyApplication.getPref().getString(SplashActivity.TRANSPORT,"");
            if (transportType.equals("PERS") || transportType.equals("ABS") || transportType.equals("")){
                trastype = PortSipEnumDefine.ENUM_TRANSPORT_PERS;
            }
            if (transportType.equals("TCP")){
                trastype = PortSipEnumDefine.ENUM_TRANSPORT_TCP;
            }
            if (transportType.equals("UDP")){
                trastype = PortSipEnumDefine.ENUM_TRANSPORT_UDP;
            }
            if (transportType.equals("TLS")){
                trastype = PortSipEnumDefine.ENUM_TRANSPORT_TLS;
            }
            int result = mSipSdk.initialize(trastype,
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
                    Stunsrv, istunport, null, Integer.parseInt(pref.getString(SplashActivity.SIPPORT, getString(R.string.server_port))));// step 4

            Log.e("registration result is ",result+" username "+userName+" displayname "+displayname+" authname "+authName+" password "+password+" localip "+localIP+" localport "+localPort+" userdomain "+userDomain+" sipserver "+ SIPServer+" ispsrvport "+ isipsrvport+" stunserver "
                    +Stunsrv+" isstunport "+ istunport+" sipport "+pref.getString(SplashActivity.SIPPORT, getString(R.string.server_port)));

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
            handler.post(new Runnable() {
                @Override
                public void run() {
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
	/*@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		selectTabWithAction(intent);
	}*/

	/*private void selectTabWithAction(Intent intent) {
		if (intent != null) {
			String callAction = intent.getAction();
			if (!TextUtils.isEmpty(callAction)) {
				ActionBar ab = getSupportActionBar();
				Tab toSelectTab = null;
				if (callAction.equalsIgnoreCase(SipManager.ACTION_SIP_DIALER)
						|| callAction.equalsIgnoreCase(Intent.ACTION_DIAL)) {
					toSelectTab = ab.getTabAt(TAB_INDEX_DIALER);
					Uri data = intent.getData();
					if (data != null && numpadFragment != null) {
						String nbr = data.getSchemeSpecificPart();
						if (!TextUtils.isEmpty(nbr)) {
							mDialpadFragment.setTextDialing(true);
							mDialpadFragment.setTextFieldValue(nbr);
						}
					}
				} else if (callAction
						.equalsIgnoreCase(SipManager.ACTION_SIP_CALLLOG)) {
					toSelectTab = ab.getTabAt(TAB_INDEX_CALL_LOG);
				}
				*//*
				 * else if
				 * (callAction.equalsIgnoreCase(SipManager.ACTION_SIP_MESSAGES))
				 * { toSelectTab = ab.getTabAt(TAB_INDEX_MESSAGES); }
				 *//*
				else if (callAction
						.equalsIgnoreCase(SipManager.ACTION_SIP_CONTACTS)) {
					toSelectTab = ab.getTabAt(TAB_INDEX_CONTACTS);
				} else if (callAction
						.equalsIgnoreCase(SipManager.ACTION_SIP_MYACCOUNT)) {
					toSelectTab = ab.getTabAt(TAB_INDEX_MYACCOUNT);
				}
				if (toSelectTab != null) {
					ab.selectTab(toSelectTab);
				}
			}
		}
	}*/

    @Override
    protected void onDestroy() {
        //disconnect(false);
        super.onDestroy();
        Log.d(THIS_FILE, "---DESTROY SIP HOME END---");
    }

    /*
     * @Override public boolean onCreateOptionsMenu(Menu menu) {
     *
     * WizardInfo distribWizard =
     * CustomDistribution.getCustomDistributionWizard(); if (distribWizard !=
     * null) { menu.add(Menu.NONE, DISTRIB_ACCOUNT_MENU, Menu.NONE, "My " +
     * distribWizard.label) .setIcon(distribWizard.icon)
     * .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM); } if
     * (CustomDistribution.distributionWantsOtherAccounts()) {
     * menu.add(Menu.NONE, ACCOUNTS_MENU, Menu.NONE, (distribWizard == null) ?
     * R.string.accounts : R.string.other_accounts)
     * .setIcon(R.drawable.ic_menu_account_list) .setAlphabeticShortcut('a')
     * .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM |
     * MenuItem.SHOW_AS_ACTION_WITH_TEXT); } menu.add(Menu.NONE, PARAMS_MENU,
     * Menu.NONE, R.string.prefs)
     * .setIcon(android.R.drawable.ic_menu_preferences)
     * .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
     *
     * menu.add(Menu.NONE, HELP_MENU, Menu.NONE, R.string.help)
     * .setIcon(android.R.drawable.ic_menu_help)
     * .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER); menu.add(Menu.NONE,
     * CLOSE_MENU, Menu.NONE, R.string.menu_disconnect)
     * .setIcon(R.drawable.ic_lock_power_off)
     * .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
     *
     * return super.onCreateOptionsMenu(menu);
     *
     * // TODO -- make sure we are not in split action bar a different way
     * boolean showInActionBar = Compatibility.isCompatible(14) ||
     * Compatibility.isTabletScreen(this); int ifRoomIfSplit = showInActionBar ?
     * MenuItem.SHOW_AS_ACTION_IF_ROOM : MenuItem.SHOW_AS_ACTION_NEVER;
     *
     * WizardInfo distribWizard =
     * CustomDistribution.getCustomDistributionWizard();
     *
     * if (distribWizard != null) { menu.add(Menu.NONE, DISTRIB_ACCOUNT_MENU,
     * Menu.NONE, "My Account").setIcon(distribWizard.icon
     * ).setShowAsAction(ifRoomIfSplit);
     *
     * // ABC-VoIP Modification: make signup button/link to distribution //
     * provider // menu.add(Menu.NONE, DISTRIB_ACCOUNT_SIGNUP_MENU, Menu.NONE,
     * // "Sign Up").setIcon(distribWizard.icon).setShowAsAction(ifRoomIfSplit
     * ); }
     *
     *
     * if (CustomDistribution.distributionWantsOtherAccounts()) {
     * menu.add(Menu.NONE, ACCOUNTS_MENU, Menu.NONE, (distribWizard == null) ?
     * R.string.accounts :
     * R.string.other_accounts).setIcon(R.drawable.ic_menu_account_list
     * ).setAlphabeticShortcut('a')
     * .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVERifRoomIfSplit |
     * MenuItem.SHOW_AS_ACTION_WITH_TEXT); }
     *
     * // menu.add(Menu.NONE, PARAMS_MENU, Menu.NONE, //
     * R.string.prefs).setIcon(
     * android.R.drawable.ic_menu_preferences).setShowAsAction
     * (MenuItem.SHOW_AS_ACTION_NEVER);
     *
     * // menu.add(Menu.NONE, HELP_MENU, Menu.NONE, //
     * R.string.help).setIcon(android
     * .R.drawable.ic_menu_help).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
     * menu.add(Menu.NONE, CLOSE_MENU, Menu.NONE,
     * R.string.menu_disconnect).setIcon
     * (R.drawable.ic_lock_power_off).setShowAsAction
     * (MenuItem.SHOW_AS_ACTION_NEVER);
     *
     * return super.onCreateOptionsMenu(menu); }
     *//*
		 * @Override public boolean onOptionsItemSelected(MenuItem item) {
		 * switch (item.getItemId()) {
		 * 
		 * case ACCOUNTS_MENU: Intent intent = new Intent();
		 * intent.setClass(this, BasePrefsWizard.class);
		 * intent.putExtra(SipProfile.FIELD_WIZARD, "BASIC");
		 * intent.putExtra(SipProfile.FIELD_ID,(long)1); startActivity(intent);
		 * return true; case PARAMS_MENU: if (Compatibility.isCompatible(11)) {
		 * startActivityForResult(new Intent(this,
		 * com.phonetel.tech.ui.prefs.hc.MainPrefs.class), CHANGE_PREFS); }
		 * else { startActivityForResult(new Intent(this,
		 * com.phonetel.tech.ui.prefs.cupcake.MainPrefs.class),
		 * CHANGE_PREFS); } return true;
		 * 
		 * case CLOSE_MENU: Log.d(THIS_FILE, "CLOSE"); if
		 * (prefProviderWrapper.isValidConnectionForIncoming()) { // Alert user
		 * that we will disable for all incoming calls as // he want to quit
		 * 
		 * new AlertDialog.Builder(this).setTitle(R.string.warning).setMessage
		 * (getString(R.string.disconnect_and_incoming_explaination))
		 * .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
		 * { public void onClick(DialogInterface dialog, int which) {
		 * 
		 * // prefWrapper.disableAllForIncoming();
		 * prefProviderWrapper.setPreferenceBooleanValue
		 * (PreferencesWrapper.HAS_BEEN_QUIT, true); disconnect(true);
		 * 
		 * } }).setNegativeButton(R.string.cancel, null).show();
		 * 
		 * } else { ArrayList<String> networks =
		 * prefProviderWrapper.getAllIncomingNetworks(); if (networks.size() >
		 * 0) { String msg = getString(R.string.disconnect_and_will_restart,
		 * TextUtils.join(", ", networks)); Toast.makeText(this, msg,
		 * Toast.LENGTH_LONG).show(); } disconnect(true); } return true;
		 * 
		 * case HELP_MENU: // Create the fragment and show it as a dialog.
		 * DialogFragment newFragment = Help.newInstance();
		 * newFragment.show(getSupportFragmentManager(), "dialog"); return true;
		 * 
		 * case DISTRIB_ACCOUNT_MENU: WizardInfo distribWizard =
		 * CustomDistribution.getCustomDistributionWizard();
		 * 
		 * Cursor c = getContentResolver().query(SipProfile.ACCOUNT_URI, new
		 * String[] { SipProfile.FIELD_ID }, SipProfile.FIELD_WIZARD + "=?", new
		 * String[] { distribWizard.id }, null);
		 * 
		 * Intent it = new Intent(this, BasePrefsWizard.class);
		 * it.putExtra(SipProfile.FIELD_WIZARD, distribWizard.id); Long
		 * accountId = null; if (c != null && c.getCount() > 0) { try {
		 * c.moveToFirst(); accountId =
		 * c.getLong(c.getColumnIndex(SipProfile.FIELD_ID)); } catch (Exception
		 * e) { Log.e(THIS_FILE, "Error while getting wizard", e); } finally {
		 * c.close(); } } if (accountId != null) {
		 * it.putExtra(SipProfile.FIELD_ID, accountId); }
		 * startActivityForResult(it, REQUEST_EDIT_DISTRIBUTION_ACCOUNT);
		 * 
		 * return true; default: break; } return
		 * super.onOptionsItemSelected(item); }
		 */
    private final static int CHANGE_PREFS = 1;
    public static final byte SET_BAL = 5;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHANGE_PREFS) {
            sendBroadcast(new Intent(SipManager.ACTION_SIP_REQUEST_RESTART));
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        for (int i = 0; i < Integer.parseInt(pref.getString(
                "pause_time", "2")); i++)
            sb.append(",");
		/*for (int i = 0; i < 1; i++)
			sb.append(",");*/
        if (pref.getBoolean("auto_pin", false)) {
            sb.append(pref.getString("pin_number", ""));
            for (int i = 0; i < Integer.parseInt(pref.getString(
                    "pause_time_after_pin", "2")); i++)
                sb.append(",");
        }
        sb.append(number);
        return sb.toString();
    }

    @Override
    public boolean send(int id, Bundle bundle) {
        if (id == SET_BAL) {
            //((Messanger) mDialpadFragment).send(id, bundle);
        }
        if (id == DID_CALL) {
            String access_number;
			/*if(prefs.getBoolean("no_net", false))
			 {
				 access_number = Engin.getPref().getString("access_number1","");
			 }
			else
			{*/
            access_number = pref.getString("access_number", "");
            System.out.println("the access code when dial "+access_number);
            //}
            if (access_number != null && !access_number.equals("")) {
                String toCall = "";
                if (bundle.getBoolean("isDigit")) {
                    toCall = PhoneNumberUtils.stripSeparators(bundle
                            .getString("number"));

                } else {
                    toCall = bundle.getString("number");
                }
                if(toCall.startsWith("+"))
                {
                    toCall=toCall.replace("+", "00");
                }
                toCall = toCall;
                String str = getDIDFormatedString(access_number, toCall);
                str = str.concat("#");
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                        + str)));


                return true;
            } else {
                // LayoutInflater inflater = this.getLayoutInflater();
                // View tview = inflater.inflate(R.layout.voucher, null);
                // final EditText num = (EditText)
                // tview.findViewById(R.id.vcode);
                // AlphaDialog alpha = new AlphaDialog(this, tview);
                // alpha.show();
                // alpha.headTextView.setText("Please Enter Access Number");
                // alpha.headTextView.setTextSize(16);
                // alpha.headTextView.setTextColor(this.getResources().getColor(R.color.grayA9A9A9));
                // alpha.headTextView.setTypeface(Typeface.createFromAsset(this.getAssets(),
                // "HelveticaNeueBold.ttf"));
                // alpha.mid.setBackgroundColor(0x0000);
                // alpha.head.setPadding(12, 1, 1, 2);
                // alpha.head.setBackgroundColor(0x0000);
                // alpha.foot.setBackgroundColor(0x0000);
                // alpha.setIcon(R.drawable.callbtn);
                // alpha.setPositiveBtnBG(Engin.getGraphics().getSLTRDrawable(ScreenGraphics.XML_RES,
                // "login_edt_txt_color"));
                // alpha.setNegativeBtnBG(Engin.getGraphics().getSLTRDrawable(ScreenGraphics.XML_RES,
                // "login_edt_txt_color"));
                // alpha.setPositiveBtnText("Save");
                // alpha.setNegativeBtnText("Cancel");
                // alpha.setAlphaPositiveListner(new AlphaListner()
                // {
                // @Override
                // public void positive(View v)
                // {
                // Engin.getEditor().putString("access_number",
                // num.getText().toString().trim()).commit();
                // }
                // });
                openAlert();
                //Toast.makeText(getApplicationContext(),
                //"There is no \"Local access number\" for your  country selection.",
                //	Toast.LENGTH_SHORT).show();
            }

        }
        return false;

    }

    public void openAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SipHome.this);

        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setMessage(getString(R.string.no_local_access));
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                // go to a new activity of the app

                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }
    public void openAlert(String str) {
        final String phoneNo=str;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SipHome.this);

        alertDialogBuilder.setTitle("Call");
        alertDialogBuilder.setMessage(phoneNo);
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("Call",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                // go to a new activity of the app
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                        + phoneNo)));
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel" ,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                // go to a new activity of the app
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }
    @Override
    public void remoteCompleated(RemoteProperty remoteProperty) {
        if (remoteProperty != null) {
            NodeList nl = remoteProperty.getDocument().getElementsByTagName(
                    "response");
            Element e = (Element) nl.item(0);
            if (remoteProperty.getId() == 1) {
                adminMsg.setText(remoteProperty.getValue(e, "text"));
            }
        }
    }
    void initiateNumber()
    {
        if(MyApplication.Online() && MyApplication.getPref().getInt("add_call", 0)==0
                && !MyApplication.getPref().getBoolean("isSiphome", false))
        {
            GetNumberBackground getNumberBackground = new GetNumberBackground();

            Uri.Builder builder = new Uri.Builder();

            // auth.aawazindia.com/newjson.aspx
            // builder.scheme("http")
            // .authority("mobisnow.com")
            // .appendPath("demo.php")
            // .appendPath("newauth.aspx")
            builder.scheme("http").authority("sip.persia.com")
                    .appendPath("a2billing").appendPath("customer")
                    .appendPath("access_number_sms.php");

            /*try {
                getNumberBackground.execute(new URL(builder.toString()));
            } catch (MalformedURLException e) {

                e.printStackTrace();
            }*/

        }else {

        }
    }

    class GetNumberBackground extends AsyncTask<URL, Void, String> {
        private ProgressDialog progress;
        InputStream inputStream = null;
        String data = null;


        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(SipHome.this);

            progress.setMessage("Loading...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            if (progress != null && !progress.isShowing())
                progress.show();
        }

        @Override
        protected String doInBackground(URL... params) {
            try {
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
                showTips("Server not responding");
            } catch (IOException exception) {
                showTips("Server not responding");
            } finally {
                try {
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException e) {
                    Log.e(SipHome.this.getPackageName() + "_Aman",
                            "Input Stream not opened", e);
                } catch (NullPointerException exception) {
                    Log.e(SipHome.this.getPackageName() + "_Aman",
                            "Input Stream not opened", exception);
                }
            }
            System.out.println("********* DATA ********" + data);
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            if (progress != null && progress.isShowing())
                progress.dismiss();

            try {

                if (result != null) {

                    JSONArray jArray = new JSONArray(result);

                    JSONObject jObject = jArray.getJSONObject(0);

                    if (jObject.getString("number") != null) {
                        String phoneNo = jObject.getString("number");
                        System.out.println("the sms no is "+phoneNo);
                        MyApplication.getPref().edit().putString("number", phoneNo).commit();
                        //showTips(jObject.getString("number"));

                    } else {
                       // showTips("Didn't fetch number. please try again");
                    }

                }
            } catch (JSONException exception) {
                Log.e(SipHome.this + "_Aman", "JSON Exception", exception);
                showTips("Please try again.");
            } catch (NullPointerException exception) {
                Log.e(SipHome.this + "_Aman", "Null Pointer Exception",
                        exception);
                showTips("Server not Responding");
            }
        }
    }
    private static final Handler handler1 = new Handler();
    public void showTips(final String text) {
        handler1.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SipHome.this, text, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
