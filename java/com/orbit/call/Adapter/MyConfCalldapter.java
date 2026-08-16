package com.orbit.call.Adapter;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orbit.call.R;
import com.orbit.call.database.MySqliteHelper;
import com.orbit.call.util.ContactInfo;
import com.orbit.call.util.FontStyle;
import com.orbit.call.util.RoundImage;

public class MyConfCalldapter extends ResourceCursorAdapter {
	private Bitmap mNoPictureBitmap;
	private String mNoName;
	private String mNoDuration;
	private InputStream is = null;
	private Uri uri = null;
	private ContactInfo info;
	RoundImage roundedImage, roundedImage1;
	FontStyle fontStyle;

	public MyConfCalldapter(Context context, int layout, Cursor c, int flags) {
		super(context, layout, c, flags);
		// default values if data not found
		mNoPictureBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_contact_picture_holo_dark);
		mNoName = "(Unknown)";
		mNoDuration = "00:00:00";
		fontStyle = new FontStyle(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView name = (TextView) view.findViewById(R.id.contact_name);
		//name.setTypeface(fontStyle.getCaviarDreamsBoldFont());
	
		String n = cursor.getString(cursor.getColumnIndex(MySqliteHelper.NAME));
		if (n == null)
			name.setText(mNoName);
		else
			name.setText(n);

		

		TextView number = (TextView) view.findViewById(R.id.contact_number);
		String num = cursor.getString(cursor
				.getColumnIndex(MySqliteHelper.PHONE_NUMBER));
		number.setText(num);

		ImageView contactImage = (ImageView) view
				.findViewById(R.id.contact_image);

		info = new ContactInfo(num, context);
		uri = info.getPhotoUri();
		//String type = cursor.getString(cursor
				//.getColumnIndex(MySqliteHelper.TYPE));
	
		if(uri != null)
        {
            try
            {
                is = context.getContentResolver().openInputStream(uri);
                int height = mNoPictureBitmap.getHeight();
                int width = mNoPictureBitmap.getWidth();
                
                contactImage.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(is), width, height, false));
            }
            catch(FileNotFoundException exception)
            {
                contactImage.setImageBitmap(mNoPictureBitmap);
            }
            catch(NullPointerException exception)
            {
                contactImage.setImageBitmap(mNoPictureBitmap);            
            }
		} else
			
			 // contactImage.setImageBitmap(this.mNoPictureBitmap); Bitmap bm =
			// BitmapFactory
			// .decodeResource(context.getResources(),R.drawable.no_contact_image
		//	 );
			 
			//roundedImage = new RoundImage(mNoPictureBitmap);
		//contactImage.setImageDrawable(roundedImage);
			contactImage.setImageBitmap(mNoPictureBitmap);
	}
}