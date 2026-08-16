package com.orbit.call;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AlphaDialog extends Dialog {

	EditText edtx;
	AlphaListner pos, neg;
	Context context;
	public TextView headTextView;
	ImageView img;
	private SharedPreferences pref;
	TextView msg;
	private Button yes, no;
	public RelativeLayout head, mid;
	public LinearLayout container, foot;
	View view;
	boolean notext = false;

	public Button getPosButon() {
		return yes;
	}

	public Button getNegButon() {
		return no;
	}

	public interface AlphaListner {
		public void positive(View v);
	}

	public AlphaDialog(Context context) {
		super(context);

		this.context = context;
		notext = false;

	}

	public AlphaDialog(Context context, View v) {
		super(context);

		this.context = context;
		this.headTextView = new TextView(context);
		view = v;
		notext = true;

	}

	public boolean isNotext() {
		return notext;
	}

	public void setNotext(boolean notext) {
		this.notext = notext;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// LinearLayout.LayoutParams params = new
		// LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
		// LinearLayout.LayoutParams.WRAP_CONTENT);
		// LinearLayout.LayoutParams fparams = new
		// LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
		// LinearLayout.LayoutParams.WRAP_CONTENT);
		//

		pref = PreferenceManager.getDefaultSharedPreferences(context);
		container = new LinearLayout(context);
		container.setOrientation(LinearLayout.VERTICAL);

		head = new RelativeLayout(context);
		head.setPadding(4, 10, 4, 10);
		// head.setId(1000);
		RelativeLayout.LayoutParams headImageParam = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		// headInnerParam.addRule(RelativeLayout.CENTER_IN_PARENT);
		headImageParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		img = new ImageView(context);
		img.setId(1);
		RelativeLayout.LayoutParams hprmr = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		// hprmr.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		hprmr.addRule(RelativeLayout.RIGHT_OF, img.getId());
		RelativeLayout.LayoutParams headhelpParam = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		headhelpParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		headhelpParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		TextView help = new TextView(context);
		help.setVisibility(View.GONE);
		help.setPadding(8, 8, 8, 8);
		help.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
						.parse(context.getResources().getString(
								R.string.help_url)
								+ "?username="
								+ pref.getString("userid", "")
								+ "&pasword=" + pref.getString("userpass", "")));
				context.startActivity(browserIntent);

			}
		});
		help.setCompoundDrawablesWithIntrinsicBounds(null, context
				.getResources().getDrawable(R.drawable.hold), null, null);

		if (headTextView != null) {
			headTextView.setText(" ");

			head.addView(img, headImageParam);
			head.addView(help, headhelpParam);
			hprmr.addRule(RelativeLayout.CENTER_VERTICAL);
			head.addView(headTextView, hprmr);
		}
		mid = new RelativeLayout(context);
		// mid.setId(1001);
		if (!notext) {

			RelativeLayout.LayoutParams msprm = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);

			msg = new TextView(context);
			msg.setText("Message");
			msg.setTextColor(0xffff);
			msg.setGravity(Gravity.CENTER_HORIZONTAL);
			mid.addView(msg, msprm);
		} else
			mid.addView(view);
		foot = new LinearLayout(context);
		foot.setPadding(0, 0, 0, 0);
		yes = new Button(context);
		// yes.setBackgroundResource(R.drawable.btn_dial_no_corner);
		yes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (view != null)
					v.setTag(view);
				if (pos != null)
					pos.positive(v);
				AlphaDialog.this.dismiss();

			}
		});
		yes.setText("OK");
		yes.setTextAppearance(context, android.R.attr.textAppearanceLarge);
		yes.setTextColor(0xffffffff);

		yes.setTypeface(Typeface.createFromAsset(context.getAssets(),
				"HelveticaNeueBold.ttf"));
		no = new Button(context);
		no.setText("Cancel");
		no.setVisibility(View.GONE);
		no.setTextAppearance(context, android.R.attr.textAppearanceLarge);
		no.setTypeface(Typeface.createFromAsset(context.getAssets(),
				"HelveticaNeueBold.ttf"));
		//no.setTextColor(color.grayA9A9A9);

		// no.setBackgroundResource(R.drawable.btn_dial_no_corner);
		no.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (view != null)
					v.setTag(view);
				if (neg != null)
					neg.positive(v);
				AlphaDialog.this.dismiss();

			}
		});
		// foot.setId(1002);
		LinearLayout.LayoutParams btnprm = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, (float) .5);
		foot.addView(yes, btnprm);
		foot.addView(no, btnprm);
		head.getLayoutParams();
		LinearLayout.LayoutParams headParam = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		// headParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		container.addView(head, headParam);
		head.requestLayout();
		// LinearLayout.LayoutParams midParam = new
		// LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
		// LinearLayout.LayoutParams.WRAP_CONTENT);
		// midParam.addRule(RelativeLayout.BELOW,1000);
		container.addView(mid, headParam);
		/*
		 * RelativeLayout.LayoutParams footParam = new
		 * RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
		 * RelativeLayout.LayoutParams.WRAP_CONTENT);
		 * footParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		 */
		container.addView(foot, headParam);

		container.setBackgroundColor(0xff888888);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setBackgroundDrawable(
				context.getResources().getDrawable(R.drawable.shp_dialog_bg));

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		RelativeLayout.LayoutParams mainParam = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				(int) (display.getHeight() * 0.8));
		mainParam.addRule(RelativeLayout.CENTER_IN_PARENT);
		setContentView(container, headParam);
	}

	public void setAlphaPositiveListner(AlphaListner pos) {
		this.pos = pos;
	}

	public void setAlphaNegativeListner(AlphaListner neg) {
		this.neg = neg;
	}

	public void setIcon(int id) {
		img.setImageDrawable(context.getResources().getDrawable(id));
	}

	void setWinBG(Drawable d)

	{
		getWindow().setBackgroundDrawable(d);
	}

	public void setPositiveBtnBG(Drawable d)

	{
		yes.setBackgroundDrawable(d);
	}

	public void setNegativeBtnBG(int id)

	{
		no.setBackgroundResource(id);
	}

	public void setNegativeBtnText(String ss)

	{
		no.setText(ss);
	}

	public void setPositiveBtnBG(int id)

	{
		yes.setBackgroundResource(id);
	}

	public void setPositiveBtnText(String ss)

	{
		yes.setText(ss);
	}

	public void setNegativeBtnBG(Drawable d)

	{
		 no.setBackgroundDrawable(d);
	}

	public Button getNegative() {
		return this.no;
	}
	public Button getPositive() {
		return this.yes;
	}

	public void setMessage(String ms) {
		msg.setText(ms);
	}

	public TextView getMessage() {
		return msg;
	}
}
