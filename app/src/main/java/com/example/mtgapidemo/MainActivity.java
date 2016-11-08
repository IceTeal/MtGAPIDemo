package com.example.mtgapidemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class MainActivity extends AppCompatActivity {

    EditText cardNameText;
    ProgressBar progressBar;
    ImageView cardImage;

    String cardName;

    static final String API_KEY = null;
    static final String API_URL = "https://api.deckbrew.com/mtg/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardNameText = (EditText) findViewById(R.id.cardNameText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        cardImage = (ImageView) findViewById(R.id.cardImage);

        Button queryButton = (Button) findViewById(R.id.queryButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetrieveFeedTask().execute();
            }
        });
    }

    private class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            cardName = cardNameText.getText().toString();
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here

            try {
                String encodedCardName = URLEncoder.encode(cardName, "UTF-8");
                URL url = new URL(API_URL + "cards?name=\"" + encodedCardName + "\"");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            // TODO: check this.exception
            // TODO: do something with the feed

            try {
                JSONArray cards = (JSONArray) new JSONTokener(response).nextValue();

                JSONObject firstCard = cards.getJSONObject(0);
                JSONArray editions = firstCard.getJSONArray("editions");
                JSONObject recentEdition = editions.getJSONObject(0);

                String imageURL = recentEdition.getString("image_url");

                Picasso.with(MainActivity.this)
                        .load(imageURL)
                        .into(cardImage);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}