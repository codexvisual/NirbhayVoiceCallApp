package com.orbit.call;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orbit.call.Adapter.MyCustomAdapter;
import com.orbit.call.acitivites.InCallActivity;
import com.orbit.call.database.CallLog;
import com.orbit.call.database.Contact;
import com.orbit.call.database.MySqliteHelper;


public class CallHistory extends Fragment
{
    private Cursor cursor;
    private Toast toast;
    private CallLog callLog;
    ListView lv;
    TextView no_calllog;
    MyCustomAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.calllog_fragment, null);
        lv=(ListView)view.findViewById(R.id.call_list);
        return view;
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.calllog_fragment);
        lv=(ListView)findViewById(R.id.call_list);
    }*/


    void refreshCallLog()
    {
        // getApplicationContext().setTitle(getString(R.string.call_log_frag));

        callLog = new CallLog(getActivity().getApplicationContext());
        callLog.openReadableDataBase();
        cursor = callLog.getCallLog();
        System.out.println("the cuser size is "+cursor.getCount());
        adapter = new MyCustomAdapter(getActivity().getApplicationContext(), R.layout.single_line_calllog, cursor, 0);
        lv.setAdapter(adapter);
        //this.setListAdapter(adapter);
        //lv = getListView();
    }

    @Override
    public void onResume()
    {
        refreshCallLog();
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position,
                                    long arg3)
            {
                cursor.moveToPosition(position);
                Intent in = new Intent(getActivity().getApplicationContext(), InCallActivity.class);
                in.putExtra(InCallActivity.NUMBER, cursor.getString(cursor.getColumnIndex(MySqliteHelper.PHONE_NUMBER)));
                in.putExtra(InCallActivity.TYPE, Contact.OUTGOING_CALL);
                startActivity(in);
            }
        });

      /*  lv.setOnItemLongClickListener(new OnItemLongClickListener() {

            @SuppressLint("NewApi")
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                // TODO Auto-generated method stub

                *//** Instantiating PopupMenu class *//*
                PopupMenu popup = new PopupMenu(getApplicationContext(), view);

                *//** Adding menu items to the popumenu *//*
                popup.getMenuInflater().inflate(R.layout.call_log_menu, popup.getMenu());

                *//** Defining menu item click listener for the popup menu *//*
                popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId()==R.id.addnewcontect) {
                            cursor.moveToPosition(position);
                            callLog.openDatabase();

                            Intent addContactIntent = new Intent(Intent.ACTION_INSERT);
                            addContactIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                            String number=cursor.getString(cursor.getColumnIndex(MySqliteHelper.PHONE_NUMBER));
                            addContactIntent.putExtra(ContactsContract.Intents.Insert.PHONE,number);
                            startActivity(addContactIntent);
                            callLog.closeDatabase();

                        }else if (item.getItemId()==R.id.deletelog) {
                            cursor.moveToPosition(position);
                            callLog.openDatabase();
                            callLog.deleteRow(cursor.getString(cursor.getColumnIndex(MySqliteHelper.COLUMN_ID)));
                            callLog.closeDatabase();
                            refreshCallLog();
                        }

                        return true;
                    }
                });

                *//** Showing the popup menu *//*
                popup.show();
                return true;
            }
        });*/


        super.onResume();
    }



    @Override
    public void onPause()
    {
        super.onPause();
    }

    public void showTips(String text)
    {
        if(toast != null)
            toast.cancel();

        toast = Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }
}	
