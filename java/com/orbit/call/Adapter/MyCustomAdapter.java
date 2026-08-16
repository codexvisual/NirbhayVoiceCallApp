/* Author Krishnadev Yadav
 * 
 * This Adapter is used to inflate Call Log tab.
 * 
 * */

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

import com.orbit.call.MyApplication;
import com.orbit.call.R;
import com.orbit.call.database.Contact;
import com.orbit.call.database.MySqliteHelper;
import com.orbit.call.util.ContactInfo;
import com.orbit.call.util.FontStyle;
import com.orbit.call.util.FormatTime;
import com.orbit.call.util.RoundImage;

public class MyCustomAdapter extends ResourceCursorAdapter {
	private Bitmap mNoPictureBitmap;
	private String mNoName;
	private String mNoDuration;
	private InputStream is = null;
	private Uri uri = null;
	private ContactInfo info;
	RoundImage roundedImage, roundedImage1;
	FontStyle fontStyle;

	public MyCustomAdapter(Context context, int layout, Cursor c, int flags) {
		super(context, layout, c, flags);
		// default values if data not found
		mNoPictureBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_contact_picture_holo_light);
        if(MyApplication.getPref().getInt("Portugusese",0)==1)
        {
            mNoName = "(Desconhecido)";

        }
        else if(MyApplication.getPref().getInt("Spanish",0)==1)
        {
            mNoName = "(Desconocido)";
        }
        else
        {
            mNoName = "(Unknown)";
        }

		mNoDuration = "00:00:00";
		fontStyle = new FontStyle(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView name = (TextView) view.findViewById(R.id.contact_name);
        TextView time_of_call = (TextView) view.findViewById(R.id.call_time);
        TextView duration = (TextView) view.findViewById(R.id.call_duration);
        TextView number = (TextView) view.findViewById(R.id.contact_number);
		//name.setTypeface(fontStyle.getCaviarDreamsBoldFont());
        if(MyApplication.getPref().getInt("Portugusese",0)==1)
        {
            name.setText("Nome");
            time_of_call.setText("Tempo");
            duration.setText("Duração");
            number.setText("Número");

        }
        else if(MyApplication.getPref().getInt("Spanish",0)==1)
        {
            name.setText("Nombre");
            time_of_call.setText("Tiempo");
            duration.setText("Duración");
            number.setText("Número");
        }
        else
        {
            name.setText("Name");
            time_of_call.setText("Time");
            duration.setText("Duration");
            number.setText("Number");
        }
		String n = cursor.getString(cursor.getColumnIndex(MySqliteHelper.NAME));
		if (n == null)
			name.setText(mNoName);
		else
			name.setText(n);

		long start_time = cursor.getLong(cursor
				.getColumnIndex(MySqliteHelper.START_TIME));


		//time_of_call.setTypeface(fontStyle.getCaviarDreamsFont());
		time_of_call.setText(new FormatTime(start_time)
				.getFormattedDateAndTime());


		//duration.setTypeface(fontStyle.getCaviarDreamsFont());
		long end_time = cursor.getLong(cursor
				.getColumnIndex(MySqliteHelper.END_TIME));

		if (end_time == 0)
			duration.setText(mNoDuration);
		else
			duration.setText(new FormatTime(end_time / 1000 - start_time / 1000)
					.getFormatedTime());


		number.setText(new FormatTime(start_time).getFormattedDateAndTime());
		//number.setTypeface(fontStyle.getCaviarDreamsBoldFont());
		String num = cursor.getString(cursor
				.getColumnIndex(MySqliteHelper.PHONE_NUMBER));
		number.setText(num);

		ImageView contactImage = (ImageView) view
				.findViewById(R.id.contact_image);

		ImageView iv_callType = (ImageView) view.findViewById(R.id.iv_callType);
		info = new ContactInfo(num, context);
		uri = info.getPhotoUri();
		String type = cursor.getString(cursor
				.getColumnIndex(MySqliteHelper.TYPE));
		if (type.equals(Contact.INCOMING_CALL)) {
			iv_callType.setImageResource(R.drawable.incom);
		}
		if (type.equals(Contact.OUTGOING_CALL)) {
			iv_callType.setImageResource(R.drawable.out);
		} else if (type.equals(Contact.MISSED_CALL)) {
			iv_callType.setImageResource(R.drawable.miss);
		}
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
	/*@Override
    public void bindView(View view, Context context, Cursor cursor) 
    {
        TextView name = (TextView) view.findViewById(R.id.contact_name);
        String n = cursor.getString(cursor.getColumnIndex(MySqliteHelper.NAME));
        if(n == null)
            name.setText(mNoName);
        else
            name.setText(n);

        long start_time = cursor.getLong(cursor.getColumnIndex(MySqliteHelper.START_TIME));

        TextView time_of_call = (TextView) view.findViewById(R.id.call_time);
        time_of_call.setText(new FormatTime(start_time).getFormattedDateAndTime());
        
        TextView duration = (TextView) view.findViewById(R.id.call_duration);
        long end_time = cursor.getLong(cursor.getColumnIndex(MySqliteHelper.END_TIME));
        
        if(end_time == 0)
            duration.setText(mNoDuration);
        else
            duration.setText(new FormatTime(end_time/1000 - start_time/1000).getFormatedTime());
        
        TextView number = (TextView) view.findViewById(R.id.contact_number);
        String num = cursor.getString(cursor.getColumnIndex(MySqliteHelper.PHONE_NUMBER));
        number.setText(num);
        
        ImageView type = (ImageView) view.findViewById(R.id.iv_callType);
        String call_type = cursor.getString(cursor.getColumnIndex(MySqliteHelper.TYPE));
        
        if(call_type.equals(Contact.INCOMING_CALL))
            type.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.incom));
        if(call_type.equals(Contact.OUTGOING_CALL))
            type.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.out));
        else
            type.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.miss));
        
        ImageView contactImage = (ImageView) view.findViewById(R.id.contact_image);
        info = new ContactInfo(num, context);
        uri = info.getPhotoUri();
        
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
        }
        else
            
            contactImage.setImageBitmap(this.mNoPictureBitmap);
    }
}*/
