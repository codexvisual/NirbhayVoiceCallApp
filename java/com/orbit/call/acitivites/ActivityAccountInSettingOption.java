package com.orbit.call.acitivites;

import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.orbit.call.CustomAlertDialog;
import com.orbit.call.MyAccount;
import com.orbit.call.MyApplication;
import com.orbit.call.R;
import com.orbit.call.Remote.RemoteData;
import com.orbit.call.Remote.RemoteData.OnRemoteCompleated;
import com.orbit.call.Remote.RemoteData.RemoteProperty;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;


public class ActivityAccountInSettingOption  extends Activity implements OnRemoteCompleated{

	ViewGroup layoutManageAccount,layoutCallHistory,layoutSmsHistorytAtActivityAccount;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_in_setting_activity);
		
		layoutManageAccount=(ViewGroup)findViewById(R.id.layoutManageAccountAtActivityAccount);
		layoutCallHistory=(ViewGroup)findViewById(R.id.layoutCallHistorytAtActivityAccount);
		layoutSmsHistorytAtActivityAccount=(ViewGroup)findViewById(R.id.layoutSmsHistorytAtActivityAccount);
		
		layoutManageAccount.setBackgroundResource(R.drawable.menu_background);
		layoutCallHistory.setBackgroundResource(R.drawable.menu_background);
		layoutSmsHistorytAtActivityAccount.setBackgroundResource(R.drawable.menu_background);
		
		layoutManageAccount.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String url = "http://www.persia.co.uk/skipRoaming/login?username="
						+ MyApplication.getPref().getString("username", "")
						+ "&password="
								+ MyApplication.getPref().getString("password", "");
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(browserIntent);
			//	Intent intent = new Intent(ActivityAccountInSettingOption.this, Mange_account.class);
				//startActivity(intent);
				
			}
		});
		layoutCallHistory.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RemoteData remoteData = new RemoteData(
						5, ActivityAccountInSettingOption.this);
				remoteData.setProgressDialog(ActivityAccountInSettingOption.this);
				remoteData.execute(
						RemoteData.RESULT_XML,
						getString(R.string.server_api)
								+ "?action=call-history&type=sip&username="
								+ MyApplication.getPref().getString("username", "")
								+ "&password="
								+ MyApplication.getPref().getString("password", ""));
                      /*    System.out.println("the callhistroy url "+Engin.getPrefProviderWrapper()
								.getPreferenceStringValue("api")
								+ "?action=call-history&type=sip&username="
								+ Engin.getPrefProviderWrapper()
										.getPreferenceStringValue(
												"username")
								+ "&password="
								+ Engin.getPrefProviderWrapper()
										.getPreferenceStringValue(
												"password"));*/
				MyAccount.viewPosition = 1;
			}
		});
		
		layoutSmsHistorytAtActivityAccount.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RemoteData remoteData = new RemoteData(
						3, ActivityAccountInSettingOption.this);
				//http://sip.skiproaming.com/a2billing/customer/androida2billing/index.php?type=sip&action=smscdr&username=7600256308&password=3217862
				remoteData.setProgressDialog(ActivityAccountInSettingOption.this);
				remoteData.execute(
						RemoteData.RESULT_XML,
						getString(R.string.server_api)
								+ "?type=sip&action=smscdr&username="
								+ MyApplication.getPref().getString("username", "")
								+ "&password="
								+ MyApplication.getPref().getString("password", ""));
				/*remoteData.execute(
						RemoteData.RESULT_XML,
						"http://sip.skiproaming.com/a2billing/customer/androida2billing/index.php?type=sip&action=smscdr&username=7600256308&password=3217862");*/
				MyAccount.viewPosition = 1;
			}
		});
		
		
	}
	@Override
	public void remoteCompleated(RemoteProperty result) {
		int i2=result.getId();
		System.out.println("result=== ="+result+"fdsf"+i2);
		if (result != null) {
				if(result.getId() == 5)
				{
			MyAccount.list.clear();
			NodeList nl = result.getDocument().getElementsByTagName(
					"response");
			Element e;
			int i = 0;
			while (i >= 0) {
				try {
					e = (Element) nl.item(i);
					HashMap<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("calledto", result.getValue(e, "called"));
					// hashMap.put("destination", result.getValue(e, "id"));
					hashMap.put("duration", getString(R.string.duration)+result.getValue(e, "duration")/*.split(":")[1]*/);
					hashMap.put("date", result.getValue(e, "starttime"));

					hashMap.put("cost",
							getString(R.string.cost)+" " + result.getValue(e, "bill"));
					/*System.out.println("the cost is "+result.getValue(e, "bill"));
					System.out.println("the duration is "+result.getValue(e, "duration"));*/
					MyAccount.list.add(hashMap);
					i++;
				} catch (Exception ex) {
					i = -1;
				}

			}
			Intent intent=new Intent(ActivityAccountInSettingOption.this,ActivityTopHistoryListInSettingOptions.class);
			intent.putExtra("requestFor","callHistory");
			startActivity(intent);
				}
				else if(result.getId() == 3)
				{
					MyAccount.list.clear();
					NodeList nl = result.getDocument().getElementsByTagName(
							"response");
					System.out.println("the node is "+nl);
					Element e;
					int i = 0;
					while (i >= 0) {
						try {
							System.out.println("the cost is ");
							e = (Element) nl.item(i);
							HashMap<String, String> hashMap = new HashMap<String, String>();
							hashMap.put("calledto", result.getValue(e, "phone"));
							// hashMap.put("destination", result.getValue(e, "id"));
							hashMap.put("duration", getString(R.string.cost)+" " + result.getValue(e, "bill"));
							hashMap.put("date", result.getValue(e, "date"));

							hashMap.put("cost",
									getString(R.string.status)+" " + result.getValue(e, "status"));
							hashMap.put("text",result.getValue(e, "text"));
							//System.out.println("the cost is "+result.getValue(e, "bill"));
							//System.out.println("the duration is "+result.getValue(e, "duration"));
							MyAccount.list.add(hashMap);
							i++;
						} catch (Exception ex) {
							i = -1;
						}

					}
					Intent intent=new Intent(ActivityAccountInSettingOption.this,ActivityTopHistoryListInSettingOptions.class);
					intent.putExtra("requestFor","smsHistory");
					startActivity(intent);
				}
				
		}
		else {
			CustomAlertDialog.showAlert(ActivityAccountInSettingOption.this, null, null, getString(R.string.error),
					getString(R.string.communication_error), false, getString(R.string.ok), true, getString(R.string.back), true,
					null);
		}
		
	}
	
	

}
