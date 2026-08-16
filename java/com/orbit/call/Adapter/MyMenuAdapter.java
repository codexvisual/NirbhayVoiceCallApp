package com.orbit.call.Adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.orbit.call.R;


public class MyMenuAdapter extends SimpleAdapter {
    Context context;
    public MyMenuAdapter(Context context, List<HashMap<String, String>> items,
                         int resource, String[] from, int[] to) {

        super(context, items, resource, from, to);
        this.context = context;
    }

    int getResIco(String ss) {
        if (ss.equals(context.getString(R.string.help)))
            return R.drawable.help;
        else if (ss.equals(context.getString(R.string.refer_friend)))
            return R.drawable.ic_customer;
        else if (ss.equals(context.getString(R.string.refill_via_paypal)))
            return R.drawable.paypal_rech;
        else if (ss.equals(context.getString(R.string.feedback)))
            return R.drawable.sltr_feedback;
        else if (ss.equals(context.getString(R.string.recent)))
            return R.drawable.sltr_recent;
        else if (ss.equals(context.getString(R.string.sms_hist)))
            return R.drawable.sms;
        else if (ss.equals(context.getString(R.string.manage_account)))
            return R.drawable.arrow;
        else if (ss.equals(context.getString(R.string.voucherr_recharge)))
            return R.drawable.ic_refil_voucher;
        else if (ss.equals(context.getString(R.string.call_history)))
            return R.drawable.ic_call_his;
        else if (ss.equals("Recharge"))
            return R.drawable.sltr_recharge;
        else if (ss.equals("Find Local Reseller"))
            return R.drawable.sltr_reseller;
        else if (ss.equals("About Us"))
            return R.drawable.sltr_aboutus;
        else if (ss.equals("Account"))
            return R.drawable.sltr_account;
        else if (ss.equals(context.getString(R.string.advance_setting)))
            return R.drawable.arrow;
        else if (ss.equals(context.getString(R.string.rate_check)))
            return R.drawable.sltr_rats;
        else if (ss.equals(context.getString(R.string.call_back)))
            return R.drawable.sltr_callback;
        else if (ss.equals("Buy Voucher"))
            return R.drawable.sltr_refilvoucher;
        else if (ss.equals("Default Country"))
            return R.drawable.sltr_default_country;
        else if (ss.equals("Refill via Voucher"))
            return R.drawable.sltr_refil;
        else if (ss.equals("Refill History"))
            return R.drawable.sltr_refill_his;
        else if (ss.equals("Call History"))
            return R.drawable.sltr_call_his;

        else if (ss.equals(context.getString(R.string.topup)))
            return R.drawable.arrow;
        else if (ss.equals(context.getString(R.string.manage_account)))
            return R.drawable.sltr_call_his;
        else if (ss.equals(context.getString(R.string.Buy_forwarding_num)))
            return R.drawable.sltr_refill_his;

        else if(ss.equals(context.getString(R.string.access_no_setting))){
            return  R.drawable.arrow;
        }
		/*else if (ss.equals(context.getString(R.string.sms_hist)))
			return R.drawable.arrow;*/
        else
            return R.drawable.sltr_logout;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView tv = (TextView) view.findViewById(R.id.acc_item);
        ((RelativeLayout) view.findViewById(R.id.menu_item)).setBackgroundResource(R.drawable.menu_background);
        //.setBackgroundDrawable(MyApplication.getGraphics().getSLTRDrawable(
        //	ScreenGraphics.XML_RES, "menu_item_color"));
        if (tv != null) {
            ImageView img = (ImageView) view.findViewById(R.id.imageView1);
            img.setImageResource(getResIco(tv.getText().toString()));
        }
        return view;
    }

}
