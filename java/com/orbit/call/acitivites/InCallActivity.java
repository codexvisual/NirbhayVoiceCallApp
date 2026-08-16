/* Author Krishnadev Yadav 
 * 
 * This class in used to display in call window.
 * 
 * */

package com.orbit.call.acitivites;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.portsip.PortSipErrorcode;
import com.orbit.call.R;
import com.portsip.PortSipEnumDefine;
import com.portsip.PortSipSdk;
import com.orbit.call.Line;
import com.orbit.call.MyApplication;
import com.orbit.call.NumpadFragment;
import com.orbit.call.Session;
import com.orbit.call.Adapter.MyConfCalldapter;
import com.orbit.call.database.CallLog;
import com.orbit.call.database.Contact;
import com.orbit.call.util.ContactInfo;
import com.orbit.call.util.FontStyle;
import com.orbit.call.util.FormatTime;
import com.orbit.call.util.RoundImage;
import com.orbit.call.utils.WakeLocker;

public class InCallActivity extends Activity implements OnClickListener {
    public static final String NUMBER = "number", TYPE = "type";
    private PortSipSdk mPortSipSdk;
    private Button hangup, hold, btn_accept, btn_reject;
    private ImageButton one_incall, two_incall, three_incall, four_incall,
            five_incall, six_incall, seven_incall, eight_incall, nine_incall,
            star_incall, sharp_incall;
    private LinearLayout ll_callAccept;
    private ImageButton zero_incall;
    private ImageView mute, speaker, dtmf, addCall, mergeCall, viewConference;
    private MyApplication myApp;
    private static final int _CurrentlyLine = 0;
    private TextView status, timer, name_textview, phone_textview,
            tv_zero_incall, tv_incallHeder, conferencePersons;
    public static InCallProgress inProgress;
    LinearLayout conf_layout, name_number_layout;
    ListView callList;
    private Toast toast;
    private CallLog callLog;
    private static String in_call_timer, status_strings = MyApplication.TRYING;
    private static String number, type;
    private ContactInfo contactInfo;
    private Contact contact;
    private Line[] lines;
    private ImageView image;
    private static final Handler handler = new Handler();
    private BroadcastReceiver bReceiver, callState;
    @SuppressWarnings("unused")
    private String str_mute, str_speaker, str_hold, str_dtmf;
    private LinearLayout incall_keypad;
    private EditText et_incall;
    private SharedPreferences pref;
    FontStyle fontStyle;
    static PortSipSdk mSipSdk;
    Ringtone r;
    Uri notification;
    RoundImage roundedImage, roundedImage1;
    Bitmap mNoPictureBitmap;
    MyConfCalldapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_in_call);
        fontStyle = new FontStyle(InCallActivity.this);
        MyApplication.getPref().edit().putBoolean("isCallMade", true).commit();
        initiate();
        pref = getSharedPreferences(
                String.format("%s_preferences", this.getPackageName()),
                Context.MODE_PRIVATE);
        notification = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);

        // tv_zero_incall.setTypeface(fontStyle.getCaviarDreamsFont());
		/*
		 * one_incall.setTypeface(fontStyle.getCaviarDreamsFont());
		 * two_incall.setTypeface(fontStyle.getCaviarDreamsFont());
		 * three_incall.setTypeface(fontStyle.getCaviarDreamsFont());
		 * four_incall.setTypeface(fontStyle.getCaviarDreamsFont());
		 * five_incall.setTypeface(fontStyle.getCaviarDreamsFont());
		 * six_incall.setTypeface(fontStyle.getCaviarDreamsFont());
		 * seven_incall.setTypeface(fontStyle.getCaviarDreamsFont());
		 * eight_incall.setTypeface(fontStyle.getCaviarDreamsFont());
		 * nine_incall.setTypeface(fontStyle.getCaviarDreamsFont());
		 * star_incall.setTypeface(fontStyle.getCaviarDreamsFont());
		 * sharp_incall.setTypeface(fontStyle.getCaviarDreamsFont());
		 */
		/*
		 * et_incall.setTypeface(fontStyle.getCaviarDreamsFont());
		 * status.setTypeface(fontStyle.getCaviarDreamsFont());
		 * timer.setTypeface(fontStyle.getCaviarDreamsFont());
		 * phone_textview.setTypeface(fontStyle.getCaviarDreamsBoldFont());
		 * name_textview.setTypeface(fontStyle.getCaviarDreamsBoldFont());
		 * tv_incallHeder.setTypeface(fontStyle.getCaviarDreamsBoldFont());
		 * hangup.setTypeface(fontStyle.getCaviarDreamsBoldFont());
		 * btn_accept.setTypeface(fontStyle.getCaviarDreamsBoldFont());
		 * btn_accept.setTypeface(fontStyle.getCaviarDreamsBoldFont());
		 */
        // tv_incallHeder.setText(pref.getString(OpcodeActivity.BRAND, ""));
        zero_incall.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String txt = et_incall.getText().toString();
                StringBuffer txtbfr = new StringBuffer(txt);
                txtbfr.append("0");
                txt = new String(txtbfr);
                et_incall.setText(txt);
            }
        });

        myApp = (MyApplication) getApplicationContext();
        mPortSipSdk = myApp.getPortSIPSDK();
        lines = myApp.getLines();

        in_call_timer = "00:00:00";
        status_strings = MyApplication.TRYING;

        Intent intent = getIntent();
        number = intent.getStringExtra(InCallActivity.NUMBER);
        type = intent.getStringExtra(InCallActivity.TYPE);

        System.out.println("oncreate");
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(MyApplication.SESSION_CHANG);
        registerReceiver(mReceiver, mIntentFilter);
        bReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                final ConnectivityManager connMgr = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                final android.net.NetworkInfo info = connMgr
                        .getActiveNetworkInfo();

                if (info != null && info.isAvailable()) {
                    connected();
                } else {
                    disconnected();
                }
            }
        };

        callState = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                try {

                    String state = intent
                            .getStringExtra(TelephonyManager.EXTRA_STATE);

                    if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                    }

                    if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                        if (hold()) {
                            showTips("Sip call on hold");
                        }
                    }

                    if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {

                        if (unhold()) {
                            showTips("Sip call resumed");
                        }

                    }
                } catch (Exception e) {
                    Log.e(getPackageName() + "_Krishnadev",
                            "Call state not handled", e);
                }
            }
        };
        android.util.Log.v("addcall",""+MyApplication.getPref().getInt("add_call", 0));
        if (!SipHome.confNumbers.contains(number)) {
            SipHome.confNumbers.add(number);
        }

		/*
		 * if(MyApplication.getPref().getInt("add_call", 0)==0) {
		 * 
		 * 
		 * String str=MyApplication.getPref().getString("numbers", ""); String[]
		 * numbersArray = str.split(","); Boolean isExist=false; for(int
		 * i=0;i<numbersArray.length;i++) { if(number.equals(numbersArray[i])) {
		 * isExist=true; break; }
		 * 
		 * } if(!isExist) {
		 * MyApplication.getPref().edit().putString("numbers",number).commit();
		 * } } else { String str=MyApplication.getPref().getString("numbers",
		 * ""); String[] numbersArray = str.split(","); Boolean isExist=false;
		 * for(int i=0;i<numbersArray.length;i++) {
		 * if(number.equals(numbersArray[i])) { isExist=true; break; }
		 * 
		 * } if(!isExist) { MyApplication.getPref().edit().putString("numbers",
		 * MyApplication.getPref().getString("numbers",
		 * "")+","+number).commit(); }
		 * 
		 * }
		 */

    }

    void initiate() {

        btn_accept = (Button) findViewById(R.id.btn_accept);
        btn_reject = (Button) findViewById(R.id.btn_reject);
        ll_callAccept = (LinearLayout) findViewById(R.id.ll_callAccept);
        conf_layout = (LinearLayout) findViewById(R.id.confernce_layout);
        conferencePersons = (TextView) findViewById(R.id.conference_person);
        viewConference = (ImageView) findViewById(R.id.view_conf);
        callList = (ListView) findViewById(R.id.conf_list);

        name_number_layout = (LinearLayout) findViewById(R.id.name_number_layout);
        tv_zero_incall = (TextView) findViewById(R.id.tv_zero_incall);
        incall_keypad = (LinearLayout) findViewById(R.id.incall_keypad);
        mute = (ImageView) findViewById(R.id.mute);
        speaker = (ImageView) findViewById(R.id.speaker);
        addCall = (ImageView) findViewById(R.id.add_call);
        mergeCall = (ImageView) findViewById(R.id.merge_call);
        hangup = (Button) findViewById(R.id.hang);
        hold = (Button) findViewById(R.id.hold);
        dtmf = (ImageView) findViewById(R.id.dtmf);
        str_mute = getString(R.string.str_muteOff);
        str_speaker = getString(R.string.str_speekoff);
        str_hold = getString(R.string.hold);
        str_dtmf = getString(R.string.dtmf);
        et_incall = (EditText) findViewById(R.id.et_incall);
        one_incall = (ImageButton) findViewById(R.id.one);
        two_incall = (ImageButton) findViewById(R.id.two);
        three_incall = (ImageButton) findViewById(R.id.three);
        four_incall = (ImageButton) findViewById(R.id.four);
        five_incall = (ImageButton) findViewById(R.id.five);
        six_incall = (ImageButton) findViewById(R.id.six);
        seven_incall = (ImageButton) findViewById(R.id.seven);
        eight_incall = (ImageButton) findViewById(R.id.eight);
        nine_incall = (ImageButton) findViewById(R.id.nine);
        star_incall = (ImageButton) findViewById(R.id.star);
        sharp_incall = (ImageButton) findViewById(R.id.sharp);
        zero_incall = (ImageButton) findViewById(R.id.zero);
        timer = (TextView) findViewById(R.id.timer);
        status = (TextView) findViewById(R.id.status);
        name_textview = (TextView) findViewById(R.id.c_name);
        phone_textview = (TextView) findViewById(R.id.ph_number);
        image = (ImageView) findViewById(R.id.timer_window_image);
        tv_incallHeder = (TextView) findViewById(R.id.tv_incallHeder);
        et_incall.setInputType(InputType.TYPE_NULL);
        mute.setOnClickListener(this);
        speaker.setOnClickListener(this);
        hangup.setOnClickListener(this);
        hold.setOnClickListener(this);
        dtmf.setOnClickListener(this);
        addCall.setOnClickListener(this);
        mergeCall.setOnClickListener(this);
        one_incall.setOnClickListener(this);
        two_incall.setOnClickListener(this);
        three_incall.setOnClickListener(this);
        four_incall.setOnClickListener(this);
        five_incall.setOnClickListener(this);
        six_incall.setOnClickListener(this);
        seven_incall.setOnClickListener(this);
        eight_incall.setOnClickListener(this);
        nine_incall.setOnClickListener(this);
        star_incall.setOnClickListener(this);
        sharp_incall.setOnClickListener(this);
        zero_incall.setOnClickListener(this);
        btn_accept.setOnClickListener(this);
        btn_reject.setOnClickListener(this);
        viewConference.setOnClickListener(this);
        btn_accept.setVisibility(View.GONE);
        btn_reject.setVisibility(View.GONE);
        ll_callAccept.setVisibility(View.GONE);
        if (MyApplication.getPref().getInt("add_call", 0) != 0) {

            addCall.setVisibility(View.GONE);
            mergeCall.setVisibility(View.VISIBLE);
            mergeCall.setEnabled(false);
        } else {

            addCall.setVisibility(View.GONE);
            mergeCall.setVisibility(View.GONE);
            addCall.setEnabled(false);
        }
        mute.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("the Call made "
                + MyApplication.getPref().getBoolean("isCallMade", false));
        if (MyApplication.getPref().getBoolean("isCallMade", false)) {
            if (type.equals(Contact.OUTGOING_CALL)) {

                String callTo = number.replaceAll("[^0-9]", "");
                // String callTo = number;
                if (myApp.isOnline() == false) {
                    showTips(getString(R.string.not_logged));
                    this.finish();
                    return;
                }
                System.out.println("line no "+NumpadFragment._CurrentlyLine);
                Session currentline = myApp
                        .findSessionByIndex(NumpadFragment._CurrentlyLine);
                if (currentline.getSessionState() == true
                        || currentline.getRecvCallState() == true) {
                    // showTips("Line is busy");
                    // this.finish();
                    return;
                }

                // Ensure that we have been added one audio codec at least
                if (mPortSipSdk.isAudioCodecEmpty() == true) {
                    setAVArguments();
                    //showTips("Audio Codec Empty,add audio codec at first");
                    //this.finish();
                   // return;
                }


                // Usually for 3PCC need to make call without SDP
                long sessionId = mPortSipSdk.call(callTo, true, true);
                if (sessionId <= 0) {
                    showTips("Call failure");
                    this.finish();
                    return;
                }

                currentline.setSessionId(sessionId);
                currentline.setSessionState(true);
                myApp.setCurrentLine(lines[NumpadFragment._CurrentlyLine]);
            } else if (type.equals(Contact.INCOMING_CALL)) {
                // String callTo = number.replaceAll("[^0-9]", "");
                // ll_callAccept.setOnClickListener(this);
                PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                boolean isScreenOn = powerManager.isScreenOn();

                if (!isScreenOn) {
                    WakeLocker.acquire(getApplicationContext());
                    System.out.println("the wake up is " + WakeLocker.wakeLock);
                    generateNotification(getApplicationContext(),
                            "Call from SkipRoaming " + number);
                    // The screen has been locked
                    // do stuff...
                }

                status_strings = MyApplication.INCOMING;
                btn_accept.setVisibility(View.VISIBLE);
                btn_reject.setVisibility(View.VISIBLE);
                ll_callAccept.setVisibility(View.VISIBLE);
                r.play();
                hangup.setVisibility(View.GONE);
                timer.setVisibility(View.GONE);
                dtmf.setEnabled(false);
                WakeLocker.release();
            }
        }
    }

    // Receive Call Status
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String description = intent.getStringExtra("description");
            if (description.equals(MyApplication.TRYING)){
                if (type.equals(Contact.OUTGOING_CALL)) {
                    callLog = new CallLog(context);
                    callLog.openDatabase();
                    contact = new Contact(contactInfo.getName(),
                            contactInfo.getNumber(), type);
                    callLog.insertRow(contact);
                    callLog.closeDatabase();
                }

            }

            if (description.equals(MyApplication.CONNECTED)) {
                hold.setEnabled(true);
                dtmf.setEnabled(true);
                if (type.equals(Contact.OUTGOING_CALL)) {
                    /*callLog = new CallLog(context);
                    callLog.openDatabase();
                    contact = new Contact(contactInfo.getName(),
                            contactInfo.getNumber(), type);
                    callLog.insertRow(contact);
                    callLog.closeDatabase();*/
                }
                inProgress = new InCallProgress();
                inProgress.execute();
                hold.setEnabled(true);
                addCall.setEnabled(true);
                mergeCall.setEnabled(true);
            }
            if (description.startsWith(MyApplication.CLOSED)
                    || description.startsWith(MyApplication.FAILED)) {
                if (callLog != null && contact != null) {
                    hold.setEnabled(false);
                    dtmf.setEnabled(false);
                    callLog.openDatabase();
                    contact.update_Entry();
                    callLog.update_Row(contact, callLog.getLastRowID());
                    callLog.closeDatabase();
                    hold.setEnabled(false);
                    for (int i = 0; i < SipHome.confNumbers.size(); i++) {

                        Line currentLine = myApp.findSessionByIndex(i);
                        if (!currentLine.getSessionState()) {
                            SipHome.confNumbers.remove(i);
                        }
                    }
                    if (SipHome.confNumbers.size() == 1) {
                        conf_layout.setVisibility(View.GONE);
                        image.setVisibility(View.VISIBLE);
                        name_number_layout.setVisibility(View.VISIBLE);
                        phone_textview.setText(SipHome.confNumbers.get(0));
                        name_textview.setText(SipHome.confNames
                                .get(SipHome.confNumbers.get(0)));
                        mute.setVisibility(View.VISIBLE);
                    }
                }
                if (type.equals(Contact.INCOMING_CALL)) {
                    if (callLog == null && contact == null) {
                        callLog = new CallLog(context);
                        callLog.openDatabase();
                        contact = new Contact(contactInfo.getName(),
                                contactInfo.getNumber(), Contact.MISSED_CALL);
                        callLog.insertRow(contact);
                        callLog.closeDatabase();
                        addCall.setVisibility(View.GONE);
                    }
                }
                if (MyApplication.getPref().getInt("add_call", 0) == 0) {
                    if (inProgress != null)
                        inProgress.cancel(true);
                    r.stop();
                    NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notifManager.cancel(0);
                    InCallActivity.this.finish();
                }

            }
            if (!description.equals("Closed"))
                InCallActivity.this.setStatus(description);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onresume");
        if (MyApplication.getPref().getBoolean("isCallMade", false)) {
            IntentFilter filter = new IntentFilter(
                    ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(bReceiver, filter);

            this.hold.setEnabled(false);
            this.setTimer(in_call_timer); // In Call timer
            this.setStatus(status_strings); // Show Call Status

            contactInfo = new ContactInfo(number, this);
            if (contactInfo.getName() == null)
                name_textview.setText("<Unknown Number>"); // No phone number
            else
                name_textview.setText(contactInfo.getName());

            SipHome.confNames.put(number, name_textview.getText().toString());
            phone_textview.setText(number);

            IntentFilter intentFilter = new IntentFilter(
                    TelephonyManager.ACTION_PHONE_STATE_CHANGED);
            this.registerReceiver(callState, intentFilter);

            this.loadContactImage();
            MyApplication.getPref().edit().putBoolean("isCallMade", false)
                    .commit();
        }

        else {
            System.out.println("the current line is "
                    + (MyApplication.getPref().getInt("add_call", 0)));
            Session currentline = myApp.findSessionByIndex((MyApplication
                    .getPref().getInt("add_call", 0)));
            if (currentline.getSessionState() && currentline.getHoldState()) {
                mPortSipSdk.unHold(currentline.getSessionId());
                currentline.setHoldState(false);
            }

        }
    }

    // show and kill toasts
    public void showTips(String tips) {
        toast = Toast.makeText(this, tips, Toast.LENGTH_SHORT);
        toast.show();
    }

    // kill Toasts
    public void killToast() {
        if (toast != null)
            toast.cancel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.speaker:
                if (str_speaker == getString(R.string.str_speekon)) {
                    mPortSipSdk.setLoudspeakerStatus(false);
                    str_speaker = getString(R.string.str_speekoff);
                    ((ImageView) v).setImageResource(R.drawable.speakeroff);
                } else {
                    mPortSipSdk.setLoudspeakerStatus(true);
                    str_speaker = getString(R.string.str_speekon);
                    ((ImageView) v).setImageResource(R.drawable.speakeron);
                }
                break;

            case R.id.mute: {
                Session currentline = myApp.findSessionByIndex(0);
                if (str_mute == getString(R.string.str_muteOff)) {
                    mPortSipSdk.muteSession(currentline.getSessionId(), false,
                            true, true, true);
                    str_mute = getString(R.string.str_muteOn);
                    ((ImageView) v).setImageResource(R.drawable.muteon);
                } else {
                    mPortSipSdk.muteSession(currentline.getSessionId(), false,
                            false, false, false);
                    str_mute = getString(R.string.str_muteOff);
                    ((ImageView) v).setImageResource(R.drawable.muteoff);
                }
		/*	if (MyApplication.getPref().getInt("add_call", 0) != 0) {

				if (str_mute == getString(R.string.str_muteOff)) {
					for (int i = 0; i <= MyApplication.getPref().getInt(
							"add_call", 0); i++) {
						Session currentline = myApp.findSessionByIndex(i);
						mPortSipSdk.muteSession(currentline.getSessionId(),
								false, true, true, true);
					}
					str_mute = getString(R.string.str_muteOn);
					((ImageView) v).setImageResource(R.drawable.muteon);
				} else {
					for (int i = 0; i <= MyApplication.getPref().getInt(
							"add_call", 0); i++) {
						Session currentline = myApp.findSessionByIndex(i);
						mPortSipSdk.muteSession(currentline.getSessionId(),
								false, false, false, false);
					}
					str_mute = getString(R.string.str_muteOff);
					((ImageView) v).setImageResource(R.drawable.muteoff);

				}
			} else {
				Session currentline = myApp.findSessionByIndex(0);
				if (str_mute == getString(R.string.str_muteOff)) {
					mPortSipSdk.muteSession(currentline.getSessionId(), false,
							true, true, true);
					str_mute = getString(R.string.str_muteOn);
					((ImageView) v).setImageResource(R.drawable.muteon);
				} else {
					mPortSipSdk.muteSession(currentline.getSessionId(), false,
							false, false, false);
					str_mute = getString(R.string.str_muteOff);
					((ImageView) v).setImageResource(R.drawable.muteoff);
				}
			}*/
            }
            break;
            case R.id.dtmf: {

                if (str_dtmf == getString(R.string.undtmf)) {

                    str_dtmf = getString(R.string.dtmf);
                    incall_keypad.setVisibility(View.VISIBLE);
                    ((ImageView) v).setImageResource(R.drawable.dtmf);
                } else {

                    str_dtmf = getString(R.string.undtmf);
                    incall_keypad.setVisibility(View.GONE);
                    ((ImageView) v).setImageResource(R.drawable.dtmf);
                }
            }
            break;
            case R.id.add_call:
                if (MyApplication.getPref().getInt("add_call", 0) > 5) {
                    Toast.makeText(getApplicationContext(),
                            "Now You are not allowed to add call",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                MyApplication
                        .getPref()
                        .edit()
                        .putInt("add_call",
                                MyApplication.getPref().getInt("add_call", 0) + 1).commit();

                MyApplication.getPref().edit().putBoolean("isSiphome", true).commit();

                Intent i = new Intent(getApplicationContext(), SipHome.class);
                startActivity(i);
                // finish();
                break;
            case R.id.merge_call:
                mute.setVisibility(View.GONE);
                NumpadFragment.conference();
                addCall.setVisibility(View.VISIBLE);
                mergeCall.setVisibility(View.GONE);
                conf_layout.setVisibility(View.VISIBLE);
                image.setVisibility(View.GONE);
                name_number_layout.setVisibility(View.GONE);
                conferencePersons.setText("(" + SipHome.confNumbers.size() + ")");
                break;
            case R.id.view_conf:
                if (callList.getVisibility() == View.VISIBLE) {
                    callList.setVisibility(View.GONE);
                } else {
                    callList.setVisibility(View.VISIBLE);
                }
                callLog = new CallLog(getApplicationContext());
                callLog.openReadableDataBase();
                // String str=MyApplication.getPref().getString("numbers", "");
                // String[] numbersArray = str.split(",");
                Cursor cursor = null;
                if (SipHome.confNumbers.size() == 2) {
                    cursor = callLog.getCallsTwo(SipHome.confNumbers.get(0),
                            SipHome.confNumbers.get(1));
                    System.out.println("the cursor size " + cursor.getCount());
                    adapter = new MyConfCalldapter(getApplicationContext(),
                            R.layout.conf_calls_row, cursor, 0);
                    callList.setAdapter(adapter);
                } else if (SipHome.confNumbers.size() == 3) {
                    cursor = callLog.getCallsThree(SipHome.confNumbers.get(0),
                            SipHome.confNumbers.get(1), SipHome.confNumbers.get(2));
                    System.out.println("the cursor size " + cursor.getCount());
                    adapter = new MyConfCalldapter(getApplicationContext(),
                            R.layout.conf_calls_row, cursor, 0);
                    callList.setAdapter(adapter);
                } else if (SipHome.confNumbers.size() == 4) {
                    cursor = callLog.getCallsFour(SipHome.confNumbers.get(0),
                            SipHome.confNumbers.get(1), SipHome.confNumbers.get(2),
                            SipHome.confNumbers.get(3));
                    System.out.println("the cursor size " + cursor.getCount());
                    adapter = new MyConfCalldapter(getApplicationContext(),
                            R.layout.conf_calls_row, cursor, 0);
                    callList.setAdapter(adapter);
                } else if (SipHome.confNumbers.size() == 5) {
                    cursor = callLog.getCallsFive(SipHome.confNumbers.get(0),
                            SipHome.confNumbers.get(1), SipHome.confNumbers.get(2),
                            SipHome.confNumbers.get(3), SipHome.confNumbers.get(4));
                    System.out.println("the cursor size " + cursor.getCount());
                    adapter = new MyConfCalldapter(getApplicationContext(),
                            R.layout.conf_calls_row, cursor, 0);
                    callList.setAdapter(adapter);
                }

                break;
            case R.id.hang:
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        hangup();
                    }
                });
                this.finish();
                break;

            case R.id.hold:
                if (((Button) v).getText().equals(getString(R.string.hold))) {
                    if (hold()) {
                        ((Button) v).setText(getString(R.string.unhold));
                        ((Button) v).setCompoundDrawablesWithIntrinsicBounds(null,
                                getResources().getDrawable(R.drawable.unhold),
                                null, null);
                        showTips("Call on Hold");
                    } else {
                        showTips("Hold operation failed");
                    }
                }

                else {
                    if (unhold()) {

                        ((Button) v).setText(getString(R.string.hold));
                        ((Button) v).setCompoundDrawablesWithIntrinsicBounds(null,
                                getResources().getDrawable(R.drawable.hold), null,
                                null);
                        showTips("Call Resumed");
                    } else {
                        showTips("Un-Hold operation failed");
                    }
                }

                break;
            case R.id.one: {
                String txt = et_incall.getText().toString();
                StringBuffer txtbfr = new StringBuffer(txt);
                txtbfr.append("1");
                txt = new String(txtbfr);
                et_incall.setText(txt);
                Session currentline = myApp.findSessionByIndex(_CurrentlyLine);
                if (myApp.isOnline() == true
                        && currentline.getSessionState() == true) {

                    mPortSipSdk.sendDtmf(currentline.getSessionId(),
                            PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, 1, 160,
                            true);
                }
            }
            break;
            case R.id.two: {
                String txt = et_incall.getText().toString();
                StringBuffer txtbfr = new StringBuffer(txt);
                txtbfr.append("2");
                txt = new String(txtbfr);
                et_incall.setText(txt);
                Session currentline = myApp.findSessionByIndex(_CurrentlyLine);
                if (myApp.isOnline() == true
                        && currentline.getSessionState() == true) {

                    mPortSipSdk.sendDtmf(currentline.getSessionId(),
                            PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, 1, 160,
                            true);
                }
            }
            break;
            case R.id.three: {
                String txt = et_incall.getText().toString();
                StringBuffer txtbfr = new StringBuffer(txt);
                txtbfr.append("3");
                txt = new String(txtbfr);
                et_incall.setText(txt);
                Session currentline = myApp.findSessionByIndex(_CurrentlyLine);
                if (myApp.isOnline() == true
                        && currentline.getSessionState() == true) {

                    mPortSipSdk.sendDtmf(currentline.getSessionId(),
                            PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, 1, 160,
                            true);
                }
            }
            break;
            case R.id.four: {
                String txt = et_incall.getText().toString();
                StringBuffer txtbfr = new StringBuffer(txt);
                txtbfr.append("4");
                txt = new String(txtbfr);
                et_incall.setText(txt);
                Session currentline = myApp.findSessionByIndex(_CurrentlyLine);
                if (myApp.isOnline() == true
                        && currentline.getSessionState() == true) {

                    mPortSipSdk.sendDtmf(currentline.getSessionId(),
                            PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, 1, 160,
                            true);
                }
            }
            break;
            case R.id.five: {
                String txt = et_incall.getText().toString();
                StringBuffer txtbfr = new StringBuffer(txt);
                txtbfr.append("5");
                txt = new String(txtbfr);
                et_incall.setText(txt);
                Session currentline = myApp.findSessionByIndex(_CurrentlyLine);
                if (myApp.isOnline() == true
                        && currentline.getSessionState() == true) {

                    mPortSipSdk.sendDtmf(currentline.getSessionId(),
                            PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, 1, 160,
                            true);
                }
            }
            break;
            case R.id.six: {
                String txt = et_incall.getText().toString();
                StringBuffer txtbfr = new StringBuffer(txt);
                txtbfr.append("6");
                txt = new String(txtbfr);
                et_incall.setText(txt);
                Session currentline = myApp.findSessionByIndex(_CurrentlyLine);
                if (myApp.isOnline() == true
                        && currentline.getSessionState() == true) {

                    mPortSipSdk.sendDtmf(currentline.getSessionId(),
                            PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, 1, 160,
                            true);
                }
            }
            break;
            case R.id.seven: {
                String txt = et_incall.getText().toString();
                StringBuffer txtbfr = new StringBuffer(txt);
                txtbfr.append("7");
                txt = new String(txtbfr);
                et_incall.setText(txt);
                Session currentline = myApp.findSessionByIndex(_CurrentlyLine);
                if (myApp.isOnline() == true
                        && currentline.getSessionState() == true) {

                    mPortSipSdk.sendDtmf(currentline.getSessionId(),
                            PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, 1, 160,
                            true);
                }
            }
            break;
            case R.id.eight: {
                String txt = et_incall.getText().toString();
                StringBuffer txtbfr = new StringBuffer(txt);
                txtbfr.append("8");
                txt = new String(txtbfr);
                et_incall.setText(txt);
                Session currentline = myApp.findSessionByIndex(_CurrentlyLine);
                if (myApp.isOnline() == true
                        && currentline.getSessionState() == true) {

                    mPortSipSdk.sendDtmf(currentline.getSessionId(),
                            PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, 1, 160,
                            true);
                }
            }
            break;
            case R.id.nine: {
                String txt = et_incall.getText().toString();
                StringBuffer txtbfr = new StringBuffer(txt);
                txtbfr.append("9");
                txt = new String(txtbfr);
                et_incall.setText(txt);
                Session currentline = myApp.findSessionByIndex(_CurrentlyLine);
                if (myApp.isOnline() == true
                        && currentline.getSessionState() == true) {

                    mPortSipSdk.sendDtmf(currentline.getSessionId(),
                            PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, 1, 160,
                            true);
                }
            }
            break;
            case R.id.star: {
                String txt = et_incall.getText().toString();
                StringBuffer txtbfr = new StringBuffer(txt);
                txtbfr.append("*");
                txt = new String(txtbfr);
                et_incall.setText(txt);
                Session currentline = myApp.findSessionByIndex(_CurrentlyLine);
                if (myApp.isOnline() == true
                        && currentline.getSessionState() == true) {

                    mPortSipSdk.sendDtmf(currentline.getSessionId(),
                            PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, 10, 160,
                            true);

                }
            }
            break;
            case R.id.sharp: {
                String txt = et_incall.getText().toString();
                StringBuffer txtbfr = new StringBuffer(txt);
                txtbfr.append("#");
                txt = new String(txtbfr);
                et_incall.setText(txt);
                Session currentline = myApp.findSessionByIndex(_CurrentlyLine);
                if (myApp.isOnline() == true
                        && currentline.getSessionState() == true) {

                    mPortSipSdk.sendDtmf(currentline.getSessionId(),
                            PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, 10, 160,
                            true);

                }
            }
            break;

            case R.id.btn_reject: {
                NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notifManager.cancel(0);
                if (myApp.isOnline() == false) {
                    showTips("Not register, please regist at first.");
                    InCallActivity.this.finish();
                    return;
                }

                Session currentline = myApp.findSessionByIndex(_CurrentlyLine);
                if (currentline.getRecvCallState() == true) {
                    mPortSipSdk.rejectCall(currentline.getSessionId(), 486);
                    currentline.reset();
                    // showTips(myApp.getCurrentSession()
                    // + ": Rejected call");
                    if (type.equals(Contact.INCOMING_CALL)) {
                        callLog = new CallLog(InCallActivity.this);
                        callLog.openDatabase();
                        contact = new Contact(contactInfo.getName(),
                                contactInfo.getNumber(), type);
                        callLog.insertRow(contact);
                        callLog.closeDatabase();
                    }
                    r.stop();
                    InCallActivity.this.finish();
                    return;
                }

            }
            break;
            case R.id.btn_accept: {
                NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notifManager.cancel(0);
                if (myApp.isOnline() == false) {
                    showTips("Not register, please regist at first.");
                    InCallActivity.this.finish();
                    return;
                }
                Line currentline = myApp.findSessionByIndex(_CurrentlyLine);
                if (currentline.getRecvCallState() == false) {
                    showTips("No incoming call on current line, please switch a line.");
                    InCallActivity.this.finish();
                    return;
                }

                currentline.setRecvCallState(false);
                currentline.setSessionState(true);

                int rt = mPortSipSdk.answerCall(currentline.getSessionId(), false);
                if (rt == 0) {

				/*
				 * showTips(myApp.findSessionByIndex(_CurrentlyLine) +
				 * ": Call established");
				 */
                    myApp.setCurrentLine(currentline);

                    if (type.equals(Contact.INCOMING_CALL)) {
                        callLog = new CallLog(InCallActivity.this);
                        callLog.openDatabase();
                        contact = new Contact(contactInfo.getName(),
                                contactInfo.getNumber(), type);
                        callLog.insertRow(contact);
                        callLog.closeDatabase();
                    }
                    r.stop();
                    btn_accept.setVisibility(View.GONE);
                    btn_reject.setVisibility(View.GONE);
                    ll_callAccept.setVisibility(View.GONE);
                    hangup.setVisibility(View.VISIBLE);
                    timer.setVisibility(View.VISIBLE);
                    dtmf.setEnabled(true);
				/*
				 * if (cbrecVideo.isChecked()) {
				 * mPortSipSdk.sendVideo(currentline.getSessionId(), true); }
				 */
				/*
				 * if (cbConfrence.isChecked() == true) { mPortSipSdk
				 * .joinToConference(currentline.getSessionId());
				 * currentline.setHoldState(false); }
				 */
                } else {
                    currentline.reset();
                    showTips(myApp.findSessionByIndex(_CurrentlyLine)
                            + ": failed to answer call !");
                    r.stop();
                    InCallActivity.this.finish();

                }
            }
            break;
        }
    }

    private boolean hold() {
        Session currentline = myApp.findSessionByIndex(_CurrentlyLine);
        if (myApp.isOnline() == false) {
            return false;
        }

        if (currentline.getSessionState() == false
                || currentline.getHoldState() == true) {
            return false;
        }

        int rt = mPortSipSdk.hold(currentline.getSessionId());
        if (rt != 0) {
            return false;
        }
        currentline.setHoldState(true);
        return true;
    }

    private boolean unhold() {
        Session currentline = myApp.findSessionByIndex(_CurrentlyLine);
        if (myApp.isOnline() == false) {
            return false;
        }

        if (currentline.getSessionState() == false
                || currentline.getHoldState() == false) {
            return false;
        }

        int rt = mPortSipSdk.unHold(currentline.getSessionId());
        if (rt != 0) {
            currentline.setHoldState(false);
            return false;
        }

        currentline.setHoldState(false);
        return true;
    }

    // put call on hold
    private void hangup() {
        if (myApp.isOnline() == false) {
            return;
        }
        Log.v("hangup ",""+MyApplication.getPref().getInt("add_call", 0));
        if (MyApplication.getPref().getInt("add_call", 0) != 0) {
            for (int i = 0; i <= MyApplication.getPref().getInt("add_call", 0); i++) {
                System.out.println("hanpup " + i);
                Session currentline = myApp.findSessionByIndex(i);

                if (currentline.getRecvCallState() == true) {
                    mPortSipSdk.rejectCall(currentline.getSessionId(), 486);
                    currentline.reset();
                    this.setStatus("Call Rejected");
                    return;
                }

                if (currentline.getSessionState() == true) {
                    mPortSipSdk.hangUp(currentline.getSessionId());
                    currentline.reset();
                    this.setStatus("Call hanged up");
                }
            }
        } else {
            Session currentline = myApp.findSessionByIndex(0);

            if (currentline.getRecvCallState() == true) {
                mPortSipSdk.rejectCall(currentline.getSessionId(), 486);
                currentline.reset();
                this.setStatus("Call Rejected");
                return;
            }

            if (currentline.getSessionState() == true) {
                mPortSipSdk.hangUp(currentline.getSessionId());
                currentline.reset();
                this.setStatus("Call hanged up");
            }
        }

        if (callLog != null) {
            callLog.openDatabase();
            contact.update_Entry();
            callLog.update_Row(contact, callLog.getLastRowID());
            callLog.closeDatabase();
        }

        if (inProgress != null)
            inProgress.cancel(true);
        SipHome.confNumbers.clear();
        MyApplication.getPref().edit().putInt("add_call", 0).commit();
        // SipHome.mViewPager.setCurrentItem(0, true);
        // InputMethodManager imm = (InputMethodManager)
        // getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.hideSoftInputFromWindow(SipHome.mViewPager.getApplicationWindowToken(),
        // 0);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void finish() {

       // MyApplication.getPref().edit().putInt("add_call", 0).commit();
        MyApplication.getPref().edit().putBoolean("isSiphome", true).commit();
        Intent i = new Intent(getApplicationContext(), SipHome.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        if (MyApplication.getPref().getInt("add_call", 0) != 0) {
            super.finish();
        } else {
            try {
                unregisterReceiver(bReceiver);
                unregisterReceiver(callState);
            } catch (IllegalArgumentException exception) {
                Log.e(getApplicationContext().getPackageName() + "_Krishnadev",
                        "Receiver not registered", exception);
            }
            super.finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (MyApplication.getPref().getInt("add_call", 0) != 0) {
            super.onDestroy();
        } else {
            try {
                unregisterReceiver(mReceiver);
            } catch (IllegalArgumentException exception) {
                Log.e(getApplicationContext().getPackageName() + "_Krishnadev",
                        "Receiver not registered", exception);
            }
            mPortSipSdk.setLoudspeakerStatus(false);
            // Intent i=new Intent(getApplicationContext(),SipHome.class);
            // startActivity(i);
            MyApplication.getPref().edit().putInt("add_call", 0).commit();
            // SipHome.mViewPager.setCurrentItem(4, true);
            super.onDestroy();
        }

    }

    private void setStatus(String status) {
        status_strings = status;
        this.status.setText(status);
    }

    private void setTimer(String timer) {
        in_call_timer = timer;
        this.timer.setText(timer);
    }

    public class InCallProgress extends AsyncTask<Void, Long, Void> {

        private long seconds;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                while (true) {
                    Thread.sleep(1000);
                    this.publishProgress(++seconds);
                }
            } catch (InterruptedException exception) {
            }
            return null;

        }

        @Override
        protected void onProgressUpdate(Long... values) {
            try {
                InCallActivity.this.setTimer(new FormatTime(values[0])
                        .getFormatedTime());
            } catch (NullPointerException exception) {
            }
        }

        @Override
        protected void onCancelled() {
            this.seconds = 0;
            InCallActivity.this.setTimer(new FormatTime(this.seconds)
                    .getFormatedTime());
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            this.seconds = 0;
            InCallActivity.this.setTimer(new FormatTime(this.seconds)
                    .getFormatedTime());

        }
    }

    @Override
    public void onBackPressed() {
        // this.hangup();
        //	super.onBackPressed();
    }

    public void loadContactImage() {
        Uri uri = this.contactInfo.getPhotoUri();
        InputStream is = null;
        if (uri != null) {
            try {
                is = getApplicationContext().getContentResolver()
                        .openInputStream(uri);
                // image.setImageBitmap(BitmapFactory.decodeStream(is));
                mNoPictureBitmap = BitmapFactory.decodeStream(is);
                // roundedImage = new RoundImage(mNoPictureBitmap);
                // image.setImageDrawable(roundedImage);
                image.setImageBitmap(mNoPictureBitmap);
            } catch (FileNotFoundException e) {
				/*
				 * image.setImageBitmap(BitmapFactory.decodeResource(
				 * getResources(), R.drawable.no_contact_image));
				 */
                // mNoPictureBitmap=BitmapFactory.decodeResource(
                // getResources(), R.drawable.ic_contact_picture_holo_dark);
                // roundedImage = new RoundImage(mNoPictureBitmap);
                // image.setImageDrawable(roundedImage);
                image.setBackgroundResource(R.drawable.picture_unknown);
            }
        } else
            // mNoPictureBitmap=BitmapFactory.decodeResource(
            // getResources(), R.drawable.ic_contact_picture_holo_dark);
            // roundedImage = new RoundImage(mNoPictureBitmap);
            // image.setImageDrawable(roundedImage);
            image.setBackgroundResource(R.drawable.picture_unknown);
    }

    public void connected() {

    }

    public void disconnected() {
        handler.post(new Runnable() {

            @Override
            public void run() {
                hangup();
            }

        });
        finish();
    }

    @SuppressWarnings({ "deprecation", "deprecation" })
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);

        String title = context.getString(R.string.app_name);

        Intent notificationIntent = new Intent(context, InCallActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        // notification.sound = Uri.parse("android.resource://" +
        // context.getPackageName() + "your_sound_file_name.mp3");

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);

    }


    void setAVArguments() {

        // audio codecs
        mSipSdk.clearAudioCodec();

        if (pref.getBoolean(getString(R.string.MEDIA_G722), false)) {
            mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_G722);
        }

        if (pref.getBoolean(getString(R.string.MEDIA_G729), true)) {
            mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_G729);
        }

        if (pref.getBoolean(getString(R.string.MEDIA_AMR), false)) {
            mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_AMR);
        }

        if (pref.getBoolean(getString(R.string.MEDIA_AMRWB), false)) {
            mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_AMRWB);
        }

        if (pref.getBoolean(getString(R.string.MEDIA_GSM), true)) {
            mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_GSM);
        }

        if (pref.getBoolean(getString(R.string.MEDIA_PCMA), true)) {
            mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_PCMA);
        }

        if (pref.getBoolean(getString(R.string.MEDIA_PCMU), true)) {
            mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_PCMU);
        }

        if (pref.getBoolean(getString(R.string.MEDIA_SPEEX), false)) {
            mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_SPEEX);
        }

        if (pref.getBoolean(getString(R.string.MEDIA_SPEEXWB), false)) {
            mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_SPEEXWB);
        }
        if (pref.getBoolean(getString(R.string.MEDIA_ILBC), false)) {
            mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_ILBC);
        }
        if (pref.getBoolean(getString(R.string.MEDIA_ISACWB), false)) {
            mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_ISACWB);
        }
        if (pref.getBoolean(getString(R.string.MEDIA_ISACSWB), false)) {
            mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_ISACSWB);
        }
        if (pref.getBoolean(getString(R.string.MEDIA_OPUS), false)) {
            mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_OPUS);
        }
        if (pref.getBoolean(getString(R.string.MEDIA_DTMF), false)) {
            mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_DTMF);
        }

        mSipSdk.enableVAD(pref.getBoolean(
                getString(R.string.MEDIA_VAD), true));
        mSipSdk.enableAEC(pref.getBoolean(
                getString(R.string.MEDIA_AEC), true)?PortSipEnumDefine.ENUM_EC_DEFAULT:PortSipEnumDefine.ENUM_EC_NONE);
        mSipSdk.enableANS(pref.getBoolean(
                getString(R.string.MEDIA_ANS), false)?PortSipEnumDefine.ENUM_NS_DEFAULT:PortSipEnumDefine.ENUM_NS_NONE);
        mSipSdk.enableAGC(pref.getBoolean(
                getString(R.string.MEDIA_AGC), true)?PortSipEnumDefine.ENUM_AGC_DEFAULT:PortSipEnumDefine.ENUM_AGC_NONE);
        mSipSdk.enableCNG(pref.getBoolean(
                getString(R.string.MEDIA_CNG), true));

        // Video codecs
        mSipSdk.clearVideoCodec();

        if (pref.getBoolean(getString(R.string.MEDIA_H263), false)) {
            mSipSdk.addVideoCodec(PortSipEnumDefine.ENUM_VIDEOCODEC_H263);
        }

        if (pref.getBoolean(getString(R.string.MEDIA_H26398), false)) {
            mSipSdk.addVideoCodec(PortSipEnumDefine.ENUM_VIDEOCODEC_H263_1998);
        }

        if (pref.getBoolean(getString(R.string.MEDIA_H264), true)) {
            mSipSdk.addVideoCodec(PortSipEnumDefine.ENUM_VIDEOCODEC_H264);
        }
        if (pref.getBoolean(getString(R.string.MEDIA_VP8), false)) {
            mSipSdk.addVideoCodec(PortSipEnumDefine.ENUM_VIDEOCODEC_VP8);
        }

        mSipSdk.setVideoResolution(Integer.valueOf(pref.getString(getString(R.string.str_resolution), "1")));

        setForward(pref);

        // Use earphone
        mSipSdk.setLoudspeakerStatus(false);

        // Use Front Camera
        mSipSdk.setVideoDeviceId(1);
        mSipSdk.setVideoOrientation(PortSipEnumDefine.ENUM_ROTATE_CAPTURE_FRAME_270);
    }
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
}