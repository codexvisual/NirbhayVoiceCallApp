package com.orbit.call.acitivites;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.orbit.call.MyApplication;
import com.orbit.call.R;
import com.orbit.call.utils.XMLParserSendsms;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SendSmsActivity extends Activity implements OnClickListener {

	private ImageView ivAddContact;
	private Button btnsendsms, btnDone;
	private EditText etMessage, etTosms;
	private ProgressDialog pDialog;
	XMLParserSendsms parser;
	Uri.Builder builder;
	NodeList nodelist;
	private static final int SELECT_CONTACT = 10;
	String newString;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_sendsms);
		
		
		if (savedInstanceState == null) {
		    Bundle extras = getIntent().getExtras();
		    if(extras == null) {
		        newString= null;
		    } else {
		        newString= extras.getString("number");
		    }
		} else {
		    newString= (String) savedInstanceState.getSerializable("number");
		}
		
		ivAddContact = (ImageView) findViewById(R.id.ivAddContact);
		btnsendsms = (Button) findViewById(R.id.btnsendsms);
		btnDone = (Button) findViewById(R.id.btnDone);
		etMessage = (EditText) findViewById(R.id.etMessage);
		etTosms = (EditText) findViewById(R.id.etTosms);
		btnsendsms.setOnClickListener(this);
		ivAddContact.setOnClickListener(this);
		btnDone.setOnClickListener(this);
		etTosms.setText(newString);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnsendsms:
			sendSms();
			break;
		case R.id.ivAddContact:
			Intent pickContactIntent = new Intent(Intent.ACTION_PICK,
					ContactsContract.Contacts.CONTENT_URI);
			pickContactIntent.setType(Phone.CONTENT_TYPE);
			startActivityForResult(pickContactIntent, SELECT_CONTACT);
			break;
		case R.id.btnDone:
			SendSmsActivity.this.finish();
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request it is that we're responding to
		if (requestCode == SELECT_CONTACT) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				// Get the URI that points to the selected contact
				Uri contactUri = data.getData();

				String[] projection = { Phone.NUMBER };

				Cursor cursor = getContentResolver().query(contactUri,
						projection, null, null, null);
				cursor.moveToFirst();

				// Retrieve the phone number from the NUMBER column
				int column = cursor.getColumnIndex(Phone.NUMBER);
				String number = cursor.getString(column).replaceAll("\\s", "");
				etTosms.setText(number);
				cursor.close();

			}
		}
	}

	private void sendSms() {
		String sTosms = null, sMessage = null, from = null, usr = null, pass = null;

		sTosms = etTosms.getText().toString().replaceAll("\\s", "");
		sMessage = etMessage.getText().toString().trim();
		from =MyApplication.getPref().getString("callbacknum", "");
		usr = MyApplication.getPref().getString("username", "");
		pass = MyApplication.getPref().getString("password", "");
		if (sTosms == "") {
			Toast.makeText(SendSmsActivity.this, getString(R.string.not_save_destination_number), Toast.LENGTH_LONG).show();
		}
		if (from == "") {
			Toast.makeText(SendSmsActivity.this, getString(R.string.not_save_callback_number), Toast.LENGTH_LONG).show();
		}
		if (usr == "") {
			//utils.showTips("You have not saved username", SendSmsActivity.this);
		}
		if (pass == "") {
			//utils.showTips("You have not saved password", SendSmsActivity.this);
		}
		if (sMessage.isEmpty()) {
			Toast.makeText(SendSmsActivity.this, getString(R.string.can_not_send_blank), Toast.LENGTH_LONG).show();
		}
		
		// auth.aawazindia.com/newjson.aspx
		// builder.scheme("http")
		// .authority("mobisnow.com")
		// .appendPath("demo.php")
		// .appendPath("newauth.aspx")
		builder = new Uri.Builder();
		builder.scheme("http").authority("sip.persia.com")
				.appendPath("a2billing").appendPath("customer").appendPath("androida2billing").appendPath("index.php")
				.appendQueryParameter("type", "sip")
				.appendQueryParameter("action", "sendsms")
				.appendQueryParameter("username", String.valueOf(usr))
				.appendQueryParameter("password", String.valueOf(pass))
				.appendQueryParameter("from", String.valueOf(from))
				.appendQueryParameter("to", String.valueOf(sTosms))
				.appendQueryParameter("text", String.valueOf(sMessage));
		/*String URL = "http://sip.skiproaming.com/a2billing/customer/androida2billing/index.php?type=sip&action=sendsms&username="
				+ usr
				+ "&password="
				+ pass
				+ "&from="
				+ from
				+ "&to="
				+ sTosms
				+ "&text=" + sMessage;*/
		
		String URL = builder.toString();
		if (sTosms != "" && from != "" && usr != "" && pass != ""
				&& sMessage.length() != 0) {
			if (MyApplication.Online()) {

				new Authenticate().execute(URL
						.toString());

			} else {
				Toast.makeText(SendSmsActivity.this, getString(R.string.internet_connection_msg), Toast.LENGTH_LONG).show();
			}
		}

	}

	private class Authenticate extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Create a progressbar
			pDialog = new ProgressDialog(SendSmsActivity.this);
			// Set progressbar title

			// Set progressbar message
			pDialog.setMessage("Authenticating..");
			pDialog.setIndeterminate(false);
			// Show progressbar
			pDialog.show();
		}

		@Override
		protected Void doInBackground(String... Url) {
			try {
				String urlstring = (Url[0]);
				parser = new XMLParserSendsms();
				String xml = parser.getXmlFromUrl(urlstring); // getting XML
				Document doc = parser.getDomElement(xml);
				nodelist = doc.getElementsByTagName("response");
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void args) {
			if (nodelist != null) {
				for (int temp = 0; temp < nodelist.getLength(); temp++) 
				{
					Node nNode = nodelist.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) 
					{
						Element eElement = (Element) nNode;

						if (parser.getValue(eElement, "status").toString().equals("1")) 
						{
							Toast.makeText(SendSmsActivity.this, getString(R.string.sms_sent), Toast.LENGTH_LONG).show();
							finish();
						}
						else 
						{
							Toast.makeText(SendSmsActivity.this, getString(R.string.sms_failed), Toast.LENGTH_LONG).show();
							
						}
					}
				}
			} 
			else 
			{
				Toast.makeText(SendSmsActivity.this, getString(R.string.server_not_response), Toast.LENGTH_LONG).show();
			}
			pDialog.dismiss();
		}
	}
}
