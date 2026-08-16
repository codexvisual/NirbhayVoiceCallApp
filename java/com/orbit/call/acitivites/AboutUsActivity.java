package com.orbit.call.acitivites;

import android.app.Activity;
import android.os.Bundle;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.orbit.call.R;

public class AboutUsActivity extends Activity {
	TextView aboutusContent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.about_page);
		String aboutushtml = "<body><h1>OrbitCall Version : 2.0</h1><p>Copyright " +

				"<strong>neema Software LLC</strong></p>" +
				"<p>This software is protected by number of laws.<br/>" +
				"Copyright 2015 <strong>neema Software LLC</strong></p>"+
				"<p>Licensed under the Apache License, Version 2.0 .<br/>" +
				"you may not use this file except in compliance with the License.<br/>" +
				"You may obtain a copy of the License at<br/><a href=\"http://www.apache.org/licenses/LICENSE-2.0\">" +
				"http://www.apache.org/licenses/LICENSE-2.0</a></p>"+
				"<p>Unless required by applicable law or agreed to in writing, software" +
				"distributed under the License is distributed on an \"AS IS\" BASIS," +
				"WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.</p>"+
				"<p>See the License for the specific language governing permissions and" +
				"limitations under the License.</p>"+
				"<p>For any concerns and more information please contact<a href = \"info@neema-software.com\">info@neema-software.com</a></p>"+
				"</body>";
		aboutusContent = (TextView)findViewById(R.id.about_txt);
		aboutusContent.setText(Html.fromHtml(aboutushtml));
		aboutusContent. setMovementMethod(LinkMovementMethod.getInstance());
		/*WebView webView = (WebView)findViewById(R.id.web_view);

		//webView.setWebChromeClient(new WebChromeClient(){});
		webView.clearCache(true);
		webView.clearHistory();
	    webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.loadUrl(getString(R.string.about_us_link));*/
		
	}
	


}
