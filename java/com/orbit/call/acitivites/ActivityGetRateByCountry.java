package com.orbit.call.acitivites;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.orbit.call.R;

public class ActivityGetRateByCountry extends Activity {
	private WebView wb;

	String url = "http://www.persia.co.uk/rates_cc.html";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);

		if (savedInstanceState == null) {
			wb = (WebView) findViewById(R.id.webVilkkkw1);
			WebSettings ws = wb.getSettings();
			ws.setJavaScriptEnabled(true);
			ws.setBuiltInZoomControls(true);
			wb.loadUrl(url);

		}

	}

}
