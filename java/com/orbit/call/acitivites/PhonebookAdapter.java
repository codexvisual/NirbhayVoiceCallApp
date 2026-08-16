package com.orbit.call.acitivites;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Contacts;
import android.support.v4.widget.SimpleCursorAdapter;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.support.v4.app.FragmentActivity;
import com.orbit.call.R;


import com.orbit.call.NumpadFragment;
import com.orbit.call.Remote.ScreenGraphics;
import com.orbit.call.interfaces.Messanger;
import com.orbit.call.models.CallerInfo;
import com.orbit.call.util.ContactInfo;
import com.orbit.call.utils.ContactsAsyncHelper;
import com.orbit.call.utils.PreferencesWrapper;
import com.orbit.call.utils.CustomDialogBox.DialogCallbackInterface;
import com.orbit.call.widgets.contactbadge.QuickContactBadge;

@TargetApi(5)
public class PhonebookAdapter extends SimpleCursorAdapter implements DialogCallbackInterface
{
	Context					mContext;
	private  Bitmap mNoPictureBitmap;
	private ContactInfo info1;
	private InputStream is = null;
	private Uri uri;
	private static String numbertodial;
	 private static SharedPreferences			pref;
	    Messanger									messanger;
	    private PreferencesWrapper	prefsWrapper;
	    private SharedPreferences.Editor			editor;
    private static ScreenGraphics graphics;

 public static abstract class Row {}
	    
	    public static final class Section extends Row {
	        public final String text;

	        public Section(String text) {
	            this.text = text;
	        }
	    }
	    
	    public static final class Item extends Row {
	        public final String text;

	        public Item(String text) {
	            this.text = text;
	        }
	    }
	    
	    public List<Row> rows;
	    
	    public void setRows(List<Row> rows) {
	        this.rows = rows;
	    }

	    
	    
	@SuppressWarnings("deprecation")
	public PhonebookAdapter(Context context, int layout, Cursor c, String[] from, int[] to)
	{
		super(context, layout, c, from, to);
		this.mContext = context;
		mNoPictureBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_contact_picture_holo_light);
        graphics = ScreenGraphics.getInstance(mContext, false);
		
		
		
		
		 pref = PreferenceManager.getDefaultSharedPreferences((FragmentActivity)mContext);
  		editor = pref.edit();
     if (messanger == null) messanger = (Messanger) ((FragmentActivity)mContext);
	}
	 /* @Override
	    public int getCount() {
	        return rows.size();
	    }

	    @Override
	    public Row getItem(int position) {
	        return rows.get(position);
	    }

	    @Override
	    public long getItemId(int position) {
	        return position;
	    }
	    
	    @Override
	    public int getViewTypeCount() {
	        return 2;
	    }
	    
	    @Override
	    public int getItemViewType(int position) {
	        if (getItem(position) instanceof Section) {
	            return 1;
	        } else {
	            return 0;
	        }
	    }*/
	@Override
	public void bindView(View view, final Context context, Cursor cursor)
	{
		super.bindView(view, context, cursor);
		final PhoneBookItemInfo bookItemInfo = new PhoneBookItemInfo();
		CallerInfo info;
		try
		{
			bookItemInfo.setIsStred(cursor.getString(cursor.getColumnIndex("starred")));
			bookItemInfo.setContactID(cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.CONTACT_ID)));
			bookItemInfo.setName(cursor.getString(cursor.getColumnIndex("display_name")));
			String n = cursor.getString(cursor.getColumnIndex("data1"));
			// if (n.trim().startsWith("+")) n = n.substring(1);
			bookItemInfo.setNumber(n);
			info = CallerInfo.getCallerInfoFromSipUri(mContext, n);
			bookItemInfo.setUserData(cursor.getPosition());
			ImageView imageView = (ImageView)view.findViewById(R.id.img1);
			info1 = new ContactInfo(n, mContext);
			uri = info1.getPhotoUri();
			if(uri != null)
			{
				try
				{
					is = context.getContentResolver().openInputStream(uri);
					int height = mNoPictureBitmap.getHeight();
					int width = mNoPictureBitmap.getWidth();

					imageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(is), width, height, false));
				}
				catch(FileNotFoundException exception)
				{
					imageView.setImageBitmap(mNoPictureBitmap);
				}
				catch(NullPointerException exception)
				{
					imageView.setImageBitmap(mNoPictureBitmap);
				}
			} else{

				imageView.setImageBitmap(mNoPictureBitmap);
			}
			QuickContactBadge img = (QuickContactBadge) view.findViewById(R.id.img);
			setPhoto(img, info);
		}
		catch (Exception e)
		{}

		ImageButton imb = (ImageButton) view.findViewById(R.id.imb);
		imb.setBackgroundResource(R.color.application_orange);
		View v;
		v = view.findViewById(R.id.condisp);
		((RelativeLayout) v).setBackgroundResource(R.drawable.menu_background);
		v.setTag(bookItemInfo);
		v.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View view)
			{
				PhoneBookItemInfo pi = (PhoneBookItemInfo) view.getTag();
                if (!(SipHome.mTabsAdapter.mCurrentPosition <= 0)) SipHome.mViewPager.setCurrentItem(0, true);
                NumpadFragment.etSipNum.getText().clear();

                NumpadFragment.etSipNum.append(PhoneNumberUtils.stripSeparators(pi.getNumber()));
			}
		});
		v = view.findViewById(R.id.fav);
		v.setTag(bookItemInfo);
		if (bookItemInfo.getIsStred().equals("1")) ((ImageView) v).setImageDrawable(context.getResources().getDrawable(R.drawable.fev_list_item));
		else ((ImageView) v).setImageDrawable(context.getResources().getDrawable(R.drawable.fev_inv));
		v.setOnClickListener(new OnClickListener()
		{

			@TargetApi(5)
			@Override
			public void onClick(View v)
			{
				PhoneBookItemInfo pi = (PhoneBookItemInfo) v.getTag();
				ContentValues values = new ContentValues();
				ImageView imv = (ImageView) v;
				if (pi.getIsStred().equals("0"))
				{
					values.put(Contacts.STARRED, 1);
					mContext.getContentResolver().update(Contacts.CONTENT_URI, values, Contacts._ID + "= ?", new String[]
					{
						pi.getContactID()
					});
					pi.setIsStred("1");
					imv.setImageDrawable(context.getResources().getDrawable(R.drawable.fev_list_item));

				}
				else
				{
					values.put(Contacts.STARRED, 0);
					context.getContentResolver().update(Contacts.CONTENT_URI, values, Contacts._ID + "= ?", new String[]
					{
						pi.getContactID()
					});
					pi.setIsStred("0");
					imv.setImageDrawable(context.getResources().getDrawable(R.drawable.fev_inv));

				}
				v.setTag(pi);

			}
		});

		imb.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				numbertodial = PhoneNumberUtils.stripSeparators(bookItemInfo.getNumber());
                if (!(SipHome.mTabsAdapter.mCurrentPosition <= 0)) SipHome.mViewPager.setCurrentItem(0, true);
                NumpadFragment.etSipNum.getText().clear();
                //if(numbertodial.startsWith("+"))
                //	numbertodial=numbertodial.replace("+", "00");
                NumpadFragment.etSipNum.append(PhoneNumberUtils.stripSeparators(numbertodial));

			}
		});
	}

	private void setPhoto(QuickContactBadge views, CallerInfo ci)
	{

		views.assignContactUri(ci.contactContentUri);
		ContactsAsyncHelper.updateImageViewWithContactPhotoAsync(mContext, views.getImageView(), ci, SipHome.USE_LIGHT_THEME ? R.drawable.ic_contact_picture_holo_dark : R.drawable.ic_contact_picture_holo_dark);
	}


	@Override
	public void firstButtonClicked() {
		// TODO Auto-generated method stub
		editor.putBoolean("balrec", true);
		editor.commit();
		Bundle bundle = new Bundle();
		bundle.putString("number", NumpadFragment.formatNumber(numbertodial));
		//bundle.putBoolean("isDigit", NumpadFragment.isDigit);
		messanger.send(SipHome.DID_CALL, bundle);
	}


	@Override
	public void secondButtonClicked() {
		// TODO Auto-generated method stub

	}


}
