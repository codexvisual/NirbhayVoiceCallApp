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

package com.orbit.call.wizards.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;

import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;

import com.orbit.call.R;
import com.orbit.call.MyApplication;
import com.orbit.call.api.SipProfile;
import com.orbit.call.api.SipUri.ParsedSipContactInfos;

public class Basic extends BaseImplementation implements OnPreferenceChangeListener
{
    protected static final String	THIS_FILE		= "Basic W";

    private EditTextPreference		accountDisplayName;
    private EditTextPreference		accountUserName;
    private EditTextPreference		accountServer;
    private EditTextPreference		accountPassword;
    //private int						COUNTRY_LIST	= 1;
   // private int						NUMBER_LIST		= 1;
    ParsedSipContactInfos parsedInfo;
    String username, password;
    public static boolean ischeck=false;
    /*
     * private EditTextPreference accountMidPassword; private EditTextPreference
     * accountMidId;
     */
    //private EditTextPreference		accountApi;

   // private EditTextPreference		pinNumber;
    //private EditTextPreference		pauseTime;
    //private EditTextPreference		pauseTimeAfterPin;
    //public static  ListPreference	accessNumber;
    //public static ListPreference			country;
    CharSequence[]					countries		=
            {
                    "No Country found"
            };
    CharSequence[] numbers =
            {
			/*"Access Number not set"*/
                    MyApplication.getResource().getString(R.string.access_empty)
            };
    /*private boolean setAccessNumber(String country)
    {

        try
        {

            JSONArray array = new JSONArray(MyApplication.getPref().getString("access_number_lst",""));

            int length = array.length();
            numbers = new CharSequence[length];
            HashSet<CharSequence> hashSet=new HashSet<CharSequence>();
            for (int i = 0; i < length; i++)
            {
                if(array.getJSONObject(i).getString("country").equals(country))
                    hashSet.add(array.getJSONObject(i).getString("number"));
            }
            numbers=new CharSequence[hashSet.size()];
            int i=0;
            Iterator<CharSequence> iterator=hashSet.iterator();

            while(iterator.hasNext())
            {
                numbers[i++]=iterator.next();
            }



            accessNumber.setEntries(numbers);
            accessNumber.setEntryValues(numbers);
            MyApplication.getPref().edit().putString("access_code",numbers.toString()).commit();
            if(accessNumber.getValue()==null||accessNumber.getValue().equals(""))
            {
                accessNumber.setValue("Access Number not set");
                if(country!=null)
                {
                    accessNumber.setValueIndex(0);
                }
            }

            return true;
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }*/
    private void bindFields()
    {
        // accountDisplayName = (EditTextPreference)
        // findPreference("display_name");
        accountUserName = (EditTextPreference) findPreference("username");
        accountServer = (EditTextPreference) findPreference("server");
        accountPassword = (EditTextPreference) findPreference("password");
		/*
		 * accountMidPassword = (EditTextPreference) findPreference("mid_pass");
		 * accountMidId = (EditTextPreference) findPreference("mid_id");
		 */
       /* accountApi = (EditTextPreference) findPreference("api");
        country = (ListPreference) findPreference("country");
        country.setLayoutResource(R.layout.custom_listpreferece);*/
//		pinNumber = (EditTextPreference) findPreference("pin_number");
	/*	pauseTime = (EditTextPreference) findPreference("pause_time");
		pauseTimeAfterPin = (EditTextPreference) findPreference("pause_time_after_pin");*/
        //accessNumber = (ListPreference) findPreference("access_number");

//		pauseTime.setOnPreferenceChangeListener(this);
//		pauseTimeAfterPin.setOnPreferenceChangeListener(this);
//		setAccessNumber(country.getValue());
		
		/*System.out.println("the country value is "+(MyApplication.getPref().getString("country_selected","0")));
		if(MyApplication.getPref().getString("country_selected","0").equals("1"))
		{
			System.out.println("the country is "+MyApplication.getPref().getString("Country_value",""));
			//country.setDefaultValue((Engin.getPref().getString("Country_value","")));
			country.setValue(MyApplication.getPref().getString("Country_value",""));
			MyApplication.getPref().edit().putString("country_selected","0").commit();
		}*/


        /*country.setEntries(countries);
        country.setEntryValues(countries);
        accessNumber.setEntries(numbers);
        accessNumber.setEntryValues(numbers);
        country.setValue(MyApplication.getPref().getString("access_country", ""));
        accessNumber.setValue(MyApplication.getPref().getString("access_number", ""));*/


        try
        {

            JSONArray array = new JSONArray(MyApplication.getPref().getString("access_number_lst",""));
            int length = array.length();
            HashSet<CharSequence> hashSet=new HashSet<CharSequence>();


            for (int i = 0; i < length; i++)
            {
                hashSet.add(array.getJSONObject(i).getString("country"));
            }
            countries=new CharSequence[hashSet.size()];
            int i=0;
            Iterator<CharSequence> iterator=hashSet.iterator();
            while(iterator.hasNext())
            {
                countries[i++]=iterator.next();
            }

           /* country.setEntries(countries);
            country.setEntryValues(countries);*/

        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
/*
        country.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                accessNumber.setValue("");
                MyApplication.getPref().edit().putString("access_country",newValue.toString()).commit();
                return	setAccessNumber(newValue.toString());
            }
        });*/

    }
//		String cn_pref =Engin.getPref().getString("access_country","");
//		String co_pref =Engin.getPref().getString("access_code","");
//		
//		country.setValue(cn_pref);
//		accessNumber.setValue(co_pref);


    public void fillLayout(SipProfile account)
    {
        System.out.println("fill layout11");
        bindFields();
        accountUserName.setText(MyApplication.getPref().getString("username", ""));
        //accountServer.setText(serverFull);
        accountPassword.setText(MyApplication.getPref().getString("password", ""));
//		country.setText(account.data);
		/*
		 * accountMidId.setText(account.mid_id);
		 * accountMidPassword.setText(account.mid_pass);
		 */
        //System.out.println("the account api "+account.api);
        System.out.println("the access no "+MyApplication.getPref().getString("access_country", ""));

        //accountApi.setText(MyApplication.getPref().getString("server_api", ""));
//		if (pinNumber.getText() == null || pinNumber.getText().equals("")) pinNumber.setText(parsedInfo.userName);
    }

    public void updateDescriptions()
    {
        //setListFieldSummary("country");
        //setStringFieldSummary("display_name");
		/*
		 * setStringFieldSummary("mid_id"); setPasswordFieldSummary("mid_pass");
		 */
        setStringFieldSummary("username");
        setStringFieldSummary("server");
        setStringFieldSummary("api");
        setPasswordFieldSummary("password");

        //setStringFieldSummary("pin_number");
        //setStringFieldSummary("pause_time");
        //setStringFieldSummary("pause_time_after_pin");
       // setStringFieldSummary("access_number");
    }

    private static HashMap<String, Integer>	SUMMARIES	= new HashMap<String, Integer>()
    {
        private static final long	serialVersionUID	= -5743705263738203615L;

        {
            put("display_name", R.string.w_common_display_name_desc);
																/*
																 * put("mid_id",
																 * R.string.
																 * w_common_display_name_desc
																 * );
																 * put("mid_pass"
																 * ,R.string.
																 * w_basic_password_desc
																 * );
																 */
            put("username", R.string.w_basic_username_desc);
            put("server", R.string.w_common_server_desc);
            put("password", R.string.w_basic_password_desc);
            put("api", R.string.w_common_api_desc);

            put("pin_number", R.string.w_common_pin);
            put("pause_time", R.string.w_common_time_before);
            put("pause_time_after_pin", R.string.w_common_time_after);
            put("access_number", R.string.w_common_accessnumber);
            put("country", R.string.w_common_accesscountry);
//																put("country", "ankur");
            //		country.setTitle("ankur");
        }
    };

    @Override
    public String getDefaultFieldSummary(String fieldName)
    {
        Integer res = SUMMARIES.get(fieldName);
        if (res != null) { return parent.getString(res); }
        return "";
    }

    public boolean canSave()
    {
        boolean isValid = true;
        // isValid &= checkField(accountDisplayName,
        // isEmpty(accountDisplayName));
		/*
		 * isValid &= checkField(accountMidId, isEmpty(accountMidId)); isValid
		 * &= checkField(accountMidPassword, isEmpty(accountMidPassword));
		 */
        isValid &= checkField(accountPassword, isEmpty(accountPassword));
        isValid &= checkField(accountUserName, isEmpty(accountUserName));
       // isValid &= checkField(accountApi, isEmpty(accountApi));
        return isValid;
    }

    public void buildAccount(SipProfile	account)
    {
		/*if(MyApplication.getPref().getString("username", "").equals(getText(accountUserName).trim())&&
				MyApplication.getPref().getString("password", "").equals(getText(accountPassword).trim()))
			MyApplication.getPref().edit().putBoolean("account_change",false).commit();
		else
			MyApplication.getPref().edit().putBoolean("account_change",true).commit();*/
        MyApplication.getPref().edit().putString("username", getText(accountUserName).trim()).commit();
        MyApplication.getPref().edit().putString("password", getText(accountPassword).trim()).commit();
       // MyApplication.getPref().edit().putString("server_api", getText(accountApi).trim()).commit();

    }

    @Override
    public int getBasePreferenceResource()
    {
        return R.xml.w_basic_preferences;
    }

    @Override
    public boolean needRestart()
    {
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference arg0, Object newValue)
    {
        return Integer.parseInt(newValue.toString()) >= 1;
    }




}
