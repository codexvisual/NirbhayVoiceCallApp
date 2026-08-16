package com.orbit.call.acitivites;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.orbit.call.MyAccount;
import com.orbit.call.NumpadFragment;
import com.orbit.call.R;


public class ActivityTopHistoryListInSettingOptions extends Activity {

	ListView lv;
	SimpleAdapter adapter;
	LinearLayout contentPlaceHolder;
	String requestFor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topup_history_list_in_setting_option);

		contentPlaceHolder = (LinearLayout) findViewById(R.id.placeholderInTopupHistoryActivity);

		try {
			requestFor = getIntent().getExtras().getString("requestFor");
			if (requestFor.equals("topUpHistory")) {

				LayoutInflater inflater = ActivityTopHistoryListInSettingOptions.this
						.getLayoutInflater();
				View vi = inflater.inflate(R.layout.refillhisto, null);

				lv = (ListView) vi.findViewById(R.id.history);
				TextView tv = (TextView) vi.findViewById(R.id.empty);
				if (MyAccount.list.isEmpty()) {
					lv.setVisibility(View.GONE);
					tv.setVisibility(View.VISIBLE);
				} else {
					tv.setVisibility(View.GONE);
					lv.setVisibility(View.VISIBLE);
				}
				adapter = new SimpleAdapter(
						ActivityTopHistoryListInSettingOptions.this,
						MyAccount.list, R.layout.refillhistolist, new String[] {
								"cr", /* "vo", */
								"dt" }, new int[] { R.id.cr,/* R.id.vo, */
						R.id.dt });
				lv.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				contentPlaceHolder.removeAllViews();
				contentPlaceHolder.addView(vi);
			} else if (requestFor.equals("callHistory")) {
				HistoryAdapter historyAdapter;
				LayoutInflater inflater = ActivityTopHistoryListInSettingOptions.this
						.getLayoutInflater();
				View vi = inflater.inflate(R.layout.refillhisto, null);
				lv = (ListView) vi.findViewById(R.id.history);
				historyAdapter = new HistoryAdapter(
						ActivityTopHistoryListInSettingOptions.this,
						MyAccount.list, R.layout.call_histo_list_item,
						new String[] { "calledto", /* "destination", */
								"duration", "date", "cost" }, new int[] {
								R.id.calledto, /*
												 * R. id . destination ,
												 */
								R.id.duration, R.id.date, R.id.cost });
				lv.setAdapter(historyAdapter);
				historyAdapter.notifyDataSetChanged();
				contentPlaceHolder.removeAllViews();
				contentPlaceHolder.addView(vi);
			} else if (requestFor.equals("smsHistory")) {
				HistoryAdapter historyAdapter;
				LayoutInflater inflater = ActivityTopHistoryListInSettingOptions.this
						.getLayoutInflater();
				View vi = inflater.inflate(R.layout.refillhisto, null);
				lv = (ListView) vi.findViewById(R.id.history);
				historyAdapter = new HistoryAdapter(
						ActivityTopHistoryListInSettingOptions.this,
						MyAccount.list, R.layout.call_histo_list_item,
						new String[] { "calledto", /* "destination", */
								"duration", "date", "cost", "text" },
						new int[] { R.id.calledto, /*
													 * R. id . destination ,
													 */
						R.id.duration, R.id.date, R.id.cost, R.id.textmsg });
				lv.setAdapter(historyAdapter);
				historyAdapter.notifyDataSetChanged();
				contentPlaceHolder.removeAllViews();
				contentPlaceHolder.addView(vi);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

			}
		});

	}

	public class HistoryAdapter extends SimpleAdapter {
		Context context;

		public HistoryAdapter(Context context,
				List<HashMap<String, String>> items, int resource,
				String[] from, int[] to) {

			super(context, items, resource, from, to);
			this.context = context;
		}



		@SuppressWarnings("deprecation")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			Button button = (Button) view.findViewById(R.id.call);
			RelativeLayout rlHistItem = (RelativeLayout) view
					.findViewById(R.id.rlHistItem);
			button.setBackgroundResource(R.color.light_orange);
			final TextView textmsg = (TextView) view.findViewById(R.id.textmsg);
			rlHistItem.setTag(view);
			/*rlHistItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					*//*Toast.makeText(getApplicationContext(),
							textmsg.getText().toString(), Toast.LENGTH_SHORT)
							.show();*//*
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							ActivityTopHistoryListInSettingOptions.this);

					alertDialogBuilder.setTitle(getString(R.string.msg));
					alertDialogBuilder
							.setMessage(textmsg.getText().toString());
					// set positive button: Yes message
					alertDialogBuilder.setPositiveButton(
							getString(R.string.close),
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialog,
										int id) {
									// go to a new activity of the
									// app
									
								}
							});
					

					AlertDialog alertDialog = alertDialogBuilder
							.create();
					// show alert
					alertDialog.show();
					
				}
			});*/

			button.setTag(view);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//Toast.makeText(getApplicationContext(),"clicked",Toast.LENGTH_SHORT).show();
					v = (View) v.getTag();
					TextView calledto = (TextView) v
							.findViewById(R.id.calledto);
					if (!(SipHome.mTabsAdapter.mCurrentPosition <= 0))
						SipHome.mViewPager.setCurrentItem(0, true);
					NumpadFragment.etSipNum.getText().clear();
					NumpadFragment.etSipNum.append(PhoneNumberUtils
							.stripSeparators(calledto.getText().toString()));
					((ActivityTopHistoryListInSettingOptions)context).finish();
					startActivity(new Intent(ActivityTopHistoryListInSettingOptions.this, SipHome.class));
				}
			});

			if (requestFor.equals("smsHistory")) {
				button.setVisibility(View.GONE);
				textmsg.setVisibility(View.GONE);
			} else {

			}
			return view;
		}

	}

}
