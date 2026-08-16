package com.orbit.call;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.portsip.PortSipEnumDefine;
import com.portsip.PortSipErrorcode;
import com.portsip.PortSipSdk;
import com.orbit.call.util.PreferenceFragment;

public class AudioSettingFragment extends PreferenceFragment
{
	PortSipSdk mSipSdk;
	PreferenceManager mprefmamager;
	SharedPreferences mpreferences;
	boolean changed = true;
	Context context = null;
    PreferenceCategory pc;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.media_set_new);
		context = getActivity();
		mSipSdk = ((MyApplication) getActivity().getApplicationContext())
				.getPortSIPSDK();
        pc=(PreferenceCategory)findPreference("audio_feature");
        if(MyApplication.getPref().getInt("Portugusese",0)==1)
        {
            pc.setTitle("Definição de Áudio");
            getActivity().setTitle("Definições");

        }
        else if(MyApplication.getPref().getInt("Spanish",0)==1)
        {
            pc.setTitle("Característica Audio");
            getActivity().setTitle("Ajustes");
        }
        else
        {
            pc.setTitle("Audio Feature");
            getActivity().setTitle("Settings");
        }
		mprefmamager = getPreferenceManager();
		mpreferences = mprefmamager.getSharedPreferences();
		

//		MyOnChangeListen changeListen = new MyOnChangeListen();
//		findPreference(getString(R.string.str_bitrate))
//				.setOnPreferenceChangeListener(changeListen);
//		findPreference(getString(R.string.str_resolution))
//				.setOnPreferenceChangeListener(changeListen);
//		findPreference(getString(R.string.str_fwtokey))
//				.setOnPreferenceChangeListener(changeListen);

		mpreferences
				.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {

					@Override
					public void onSharedPreferenceChanged(
							SharedPreferences sharedPreferences, String key) {
						changed = true;
					}
				});

	}

	
	@Override
	public void onResume() 
	{
		// TODO Auto-generated method stub
		super.onResume();
		//getActivity().setTitle(getString(R.string.settings_frag));
	}
	
	
	@Override
	public void onPause() {
		super.onPause();
		//getActivity().findViewById(R.id.row).setVisibility(View.GONE);
		if (changed == false)
			return;

		// audio codecs
		mSipSdk.clearAudioCodec();

		if (mpreferences.getBoolean(getString(R.string.MEDIA_G722), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_G722);
		}

		if (mpreferences.getBoolean(getString(R.string.MEDIA_G729), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_G729);
		}

		if (mpreferences.getBoolean(getString(R.string.MEDIA_AMR), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_AMR);
		}

		if (mpreferences.getBoolean(getString(R.string.MEDIA_AMRWB), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_AMRWB);
		}

		if (mpreferences.getBoolean(getString(R.string.MEDIA_GSM), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_GSM);
		}

		if (mpreferences.getBoolean(getString(R.string.MEDIA_PCMA), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_PCMA);
		}

		if (mpreferences.getBoolean(getString(R.string.MEDIA_PCMU), false)) {
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
		
		// audio codecs
		mSipSdk.clearVideoCodec();

		if (mpreferences.getBoolean(getString(R.string.MEDIA_H263), false)) {
			mSipSdk.addVideoCodec(PortSipEnumDefine.ENUM_VIDEOCODEC_H263);
		}

		if (mpreferences.getBoolean(getString(R.string.MEDIA_H26398), false)) {
			mSipSdk.addVideoCodec(PortSipEnumDefine.ENUM_VIDEOCODEC_H263_1998);
		}

		if (mpreferences.getBoolean(getString(R.string.MEDIA_H264), false)) {
			mSipSdk.addVideoCodec(PortSipEnumDefine.ENUM_VIDEOCODEC_H264);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_VP8), false)) {
			mSipSdk.addVideoCodec(PortSipEnumDefine.ENUM_VIDEOCODEC_VP8);
		}

		// sdk.setAudioSamples(20,0);

		mSipSdk.setRtpPortRange(2000, 3000, 3002, 4000);

		setForward();
	}

	class MyOnChangeListen implements OnPreferenceChangeListener {

		@Override
		public boolean onPreferenceChange(Preference arg0, Object arg1) {
			if (arg0.getKey().equals(getString(R.string.str_bitrate))) {
				mSipSdk.setVideoBitrate((Integer) arg1);
			} else if (arg0.getKey().equals(getString(R.string.str_resolution))) {
				mSipSdk.setVideoResolution(Integer.valueOf((String) arg1));

			} else if (arg0.getKey().equals(getString(R.string.str_fwtokey))) {
				String forwardto = (String) arg1;
				if (!forwardto.matches(MyApplication.SIP_ADDRRE_PATTERN)) {
					Toast.makeText(
							context,
							"The forward address must likes sip:xxxx@sip.portsip.com.",
							Toast.LENGTH_LONG).show();
				}
			}
			return true;
		}
	}

	private int setForward() {
		int ret = PortSipErrorcode.ECoreArgumentNull;
		boolean forwardopen = mpreferences.getBoolean(
				getString(R.string.str_fwopenkey), false);

		if (forwardopen == false) {
			mSipSdk.disableCallForward();
			return ret;
		}

		String forwardTo = mpreferences.getString(
				getString(R.string.str_fwtokey), "");
		boolean forwardonbusy = mpreferences.getBoolean(
				getString(R.string.str_fwbusykey), true);

		if (forwardTo.length() <= 0
				|| !forwardTo.matches(MyApplication.SIP_ADDRRE_PATTERN)) {
			// Toast.makeText(context,"The forward address must likes sip:xxxx@sip.portsip.com.",
			// Toast.LENGTH_LONG).show();
			mSipSdk.disableCallForward();
			return ret;
		}

		if (forwardonbusy) {
			ret = mSipSdk.enableCallForward(true, forwardTo);
		} else {
			ret = mSipSdk.enableCallForward(false, forwardTo);
		}

		return ret;
	}
}
