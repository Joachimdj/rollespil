package dk.inventy.dk.rollespil;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JSONParserArray {

    Profile profile;

    private static final String DEBUG_JSONPARSEARRAY = "JSONPARSE";

    public JSONArray getJSONFromUrl(String url) {

        profile = new Profile();

        DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
        HttpPost httppost = new HttpPost(url);

        httppost.setHeader("Content-type", "application/json");

        Log.d(DEBUG_JSONPARSEARRAY, url);

        InputStream inputStream = null;
        String result = null;

        try {
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            inputStream = entity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            if (url.contains("memberships")) {
                sb.delete(0, 18);
                sb.delete(sb.length() - 2, sb.length());
            }

            if (url.contains("tickets")) {
                sb.delete(0, 17);
            }

            Log.d(DEBUG_JSONPARSEARRAY, sb.toString());
            result = sb.toString();

        } catch (Exception e) {
            // Oops
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (Exception squish) {
            }
        }

        JSONArray jArray = null;
        try {
            jArray = new JSONArray(result);
            Log.d(DEBUG_JSONPARSEARRAY, jArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jArray;
    }

}
