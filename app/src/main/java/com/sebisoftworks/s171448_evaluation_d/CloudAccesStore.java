package com.sebisoftworks.s171448_evaluation_d;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class CloudAccesStore extends AsyncTask<String, Integer, String> {
    public static final int MODE_GET_KEYS = 0;
    public static final int MODE_RETRIEVE_MESSAGE_LIST = 1;
    public static final int MODE_SEND_MESSAGE = 2;
    public static final int MODE_RETRIEVE_RECIPIENT_MESSAGE_LIST = 3;
    ArrayList mData;
    int mode;

    public CloudAccesStore(ArrayList aData, int aMode) {
        mData = aData;
        mode = aMode;
    }

    public CloudAccesStore(int aMode) {
        mode = aMode;
    }

    @Override
    protected String doInBackground(String... strings) {
        String response = "";
        try {
            URL url = new URL(strings[0]);
            InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = null;
            do {
                line = reader.readLine();
                if (line != null) {
                    response = response + line;
                }
            } while (line != null);
        } catch (IOException e) {

        }
        return response;
    }

    protected void onPostExecute(String aResponse) {
        try {
            switch (mode) {
                case MODE_GET_KEYS: {
                    JSONArray jsonArray = new JSONArray(aResponse);
                    if (jsonArray.length() == 0) {
                        mData.add("Invalid User Key");
                        MainActivity.mThis.dataSetChanged();
                        return;
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mData.add(jsonArray.getJSONObject(i).getString("key"));
                    }
                    MainActivity.mThis.dataSetChanged();
                    break;
                }
                case MODE_SEND_MESSAGE: {

                    JSONObject jo = new JSONObject(aResponse);
                    if (jo.has("status")) {
                        if (jo.get("status").toString().equals("ok")) {
                            MainActivity.mThis.messageSuccess(true);
                        } else {
                            MainActivity.mThis.messageSuccess(false);

                        }
                    } else {
                        MainActivity.mThis.messageSuccess(false);
                    }
                    break;
                }
                case MODE_RETRIEVE_RECIPIENT_MESSAGE_LIST: {
                    if (!new JSONObject(aResponse).has("messages")) {
                        MainActivity.mThis.sendMessages(mData);
                        return;
                    }
                    JSONArray jsonArray = new JSONObject(aResponse).getJSONArray("messages");
                    if (jsonArray.length() == 0) {
                        MainActivity.mThis.sendMessages(mData);
                        return;
                    }
                    Message newMessage = (Message) mData.get(0);
                    mData = new ArrayList<Message>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject current = jsonArray.getJSONObject(i);
                        if (current.has("sender") && current.has("timestamp") && current.has("text")) {
                            String from = current.getString("sender");
                            String text = current.getString("text");
                            String date = current.getString("timestamp");
                            mData.add(new Message(from, new Date(new Long(date)), text));
                        }
                    }
                    mData.add(newMessage);
                    MainActivity.mThis.sendMessages(mData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
