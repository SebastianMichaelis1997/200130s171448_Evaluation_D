package com.sebisoftworks.s171448_evaluation_d;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, Button.OnClickListener {
    static MainActivity mThis;
    ArrayAdapter<String> mArrayAdapter;
    ArrayList<String> mData;
    String rec = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mThis = this;
        mData = new ArrayList<>();

        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mData);
        ListView list = findViewById(R.id.liste);
        list.setAdapter(mArrayAdapter);
        list.setOnItemClickListener(this);
        new CloudAccesStore(mData, CloudAccesStore.MODE_GET_KEYS).execute("https://webtechlecture.appspot.com/cloudstore/listkeys?owner=shortchat");

    }

    public void dataSetChanged() {
        mArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView tv = findViewById(R.id.textView_to);
        tv.setText("Message to " + mData.get(position));
        rec = mData.get(position);
    }

    @Override
    public void onClick(View v) {
        if (rec.equals("")) {
            Toast.makeText(this, "Choose a receioent", Toast.LENGTH_SHORT).show();
            return;
        }
        EditText et_message = findViewById(R.id.editText_mes);
        try {
            Message m = new Message("Sebastian", new Date(), URLEncoder.encode(et_message.getText().toString(), "utf-8"));

            ArrayList<Message> messages = new ArrayList<>();
            messages.add(m);

            new CloudAccesStore(messages, CloudAccesStore.MODE_RETRIEVE_RECIPIENT_MESSAGE_LIST).execute("https://webtechlecture.appspot.com/cloudstore/get?owner=shortchat&key=" + rec);
        } catch (Exception e) {
        }
    }

    public void sendMessages(ArrayList<Message> allMessages) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Message current : allMessages) {
                jsonArray.put(current.toJSONObject());
            }
            JSONObject jo = new JSONObject();
            jo.put("messages", jsonArray);
            new CloudAccesStore(CloudAccesStore.MODE_SEND_MESSAGE).execute("https://webtechlecture.appspot.com/cloudstore/add?owner=shortchat&key=" + rec + "&jsonstring=" + jo.toString());
        } catch (Exception e) {
        }
    }

    public void messageSuccess(Boolean su) {
        if (su) {
            Toast.makeText(this, "Message send succesfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Message send failed", Toast.LENGTH_SHORT).show();
        }
    }
}
