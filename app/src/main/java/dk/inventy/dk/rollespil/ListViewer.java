package dk.inventy.dk.rollespil;


import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListViewer extends ListFragment {

    private static final String DEBUG_LISTVIEWER = "PROFILE";

    String DATATYPE;
    String JSONDATAIMPORT;
    JSONArray JSONDATAARRAY;
    ArrayList myStringArray;
    double longitude;
    double latitude;

    List<Map<String, String>> data = new ArrayList<Map<String, String>>();

    public ListViewer() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location!=null){
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        myStringArray = new ArrayList();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            DATATYPE = bundle.getString("type", null);
            JSONDATAIMPORT = bundle.getString("bundle", null);
        }

        if (DATATYPE.equalsIgnoreCase("Events")) {
            getActivity().setTitle("Events");

            try {
                JSONDATAARRAY = new JSONArray(JSONDATAIMPORT);

                for (int i = 0; i < JSONDATAARRAY.length(); i++) {

                    JSONObject item = JSONDATAARRAY.getJSONObject(i);
                    String title = item.getString("title");
                    String date = item.getString("start_date");

                    Map<String, String> datum = new HashMap<String, String>(2);
                    datum.put("First Line", title);
                    datum.put("Second Line", date);
                    data.add(datum);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (DATATYPE.equalsIgnoreCase("Foreninger")) {
            getActivity().setTitle("Foregninger");

            try {
                JSONDATAARRAY = new JSONArray(JSONDATAIMPORT);

                Log.d(DEBUG_LISTVIEWER, "Anders" + JSONDATAARRAY.toString());

                for (int i = 0; i < JSONDATAARRAY.length(); i++) {

                    JSONObject item = JSONDATAARRAY.getJSONObject(i);

                    try {
                        String title = item.getString("title");
                        double latItem = item.getDouble("lat");
                        double longItem = item.getDouble("long");
                        String distance = calculateDistance(latitude, longitude, latItem, longItem);

                        Map<String, String> datum = new HashMap<String, String>(2);
                        datum.put("First Line", title);
                        datum.put("Second Line", "Afstand " + distance + " km");
                        data.add(datum);
                    }catch (Exception e){
                        String title = item.getString("title");

                        Map<String, String> datum = new HashMap<String, String>(2);
                        datum.put("First Line", title);
                        datum.put("Second Line", "Afstand ukendt");
                        data.add(datum);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //something
        }

        if (data.isEmpty()) {

            if (isNetworkAvailable()) {

                if (DATATYPE.equalsIgnoreCase("Events")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Vi kunne desværre ikke finde nogen events tilknyttet denne konto")
                            .setTitle("Listen er tom!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    getFragmentManager().popBackStack();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else if (DATATYPE.equalsIgnoreCase("Foregninger")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Vi kunne desværre ikke finde nogen foregninger")
                            .setTitle("Listen er tom!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    getFragmentManager().popBackStack();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    //something
                }
            } else {
                if (DATATYPE.equalsIgnoreCase("Events")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Det var desværre ikke muligt at vise listen med events. For at kunne tilgå listen i offline tilstand skal listen have været åbnet i online tilstand på forhånd.")
                            .setTitle("Listen er tom!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    getFragmentManager().popBackStack();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else if (DATATYPE.equalsIgnoreCase("Foreninger")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Det var desværre ikke muligt at vise listen med foreninger. For at kunne tilgå listen i offline tilstand skal listen have været åbnet i online tilstand på forhånd.")
                            .setTitle("Listen er tom!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    getFragmentManager().popBackStack();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    //something
                }
            }

        }

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), data,
                android.R.layout.simple_list_item_2,
                new String[]{"First Line", "Second Line"},
                new int[]{android.R.id.text1, android.R.id.text2});
        setListAdapter(adapter);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        try {
            JSONObject item = JSONDATAARRAY.getJSONObject(position);
            String link = item.getString("id");


            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (isConnected) {
                Uri uriUrl = Uri.parse("https://rollespil.dk/block.php?id=" + link);
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            } else {
                Toast.makeText(getActivity(), "Du har ikke forbindelse til internettet", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String calculateDistance(Double latA, Double lngA, Double latB, Double lngB) {

        Location locationA = new Location("point A");

        locationA.setLatitude(latA);
        locationA.setLongitude(lngA);

        Location locationB = new Location("point B");

        locationB.setLatitude(latB);
        locationB.setLongitude(lngB);

        float distance = locationA.distanceTo(locationB)/1000;

        Log.d(DEBUG_LISTVIEWER, "Peter" + distance);

        String distanceString = String.format("%.1f", distance);

        return distanceString;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

}
