package dk.inventy.dk.rollespil;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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


public class ListViewerIcon extends ListFragment {

    private static final String DEBUG_LISTVIEWER = "PROFILE";

    String DATATYPE;
    String JSONDATAIMPORT;
    JSONArray JSONDATAARRAY;
    ArrayList myStringArray;

    List<Map<String, String>> data = new ArrayList<Map<String, String>>();

    public ListViewerIcon() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        data.clear();

        myStringArray = new ArrayList();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            DATATYPE = bundle.getString("type", null);
            JSONDATAIMPORT = bundle.getString("bundle", null);
        }

        if (DATATYPE.equalsIgnoreCase("membership")) {
            getActivity().setTitle("Medlemskaber");
        } else if (DATATYPE.equalsIgnoreCase("Ticket")) {
            getActivity().setTitle("Billetter");
        } else {
            //something
        }

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

        if (data.isEmpty()) {

            if (isNetworkAvailable()) {


                if (DATATYPE.equalsIgnoreCase("membership")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Det var ikke muligt at finde nogen medlemskaber tilknyttet til din brugerprofil")
                            .setTitle("Listen er tom!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    getFragmentManager().popBackStack();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else if (DATATYPE.equalsIgnoreCase("Ticket")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Det var ikke muligt at finde nogen billetter tilknyttet til din brugerprofil")
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
                if (DATATYPE.equalsIgnoreCase("membership")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Det var desværre ikke muligt at vise listen med medlemskaber. For at kunne tilgå listen i offline tilstand skal listen have været åbnet i online tilstand på forhånd.")
                            .setTitle("Listen er tom!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    getFragmentManager().popBackStack();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else if (DATATYPE.equalsIgnoreCase("Ticket")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Det var desværre ikke muligt at vise listen med billetter. For at kunne tilgå listen i offline tilstand skal listen have været åbnet i online tilstand på forhånd.")
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
                R.layout.fragment_list_viewer_icon,
                new String[]{"First Line", "Second Line"},
                new int[]{R.id.main_item_icon, R.id.sub_item_icon});
        setListAdapter(adapter);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (DATATYPE.equalsIgnoreCase("Ticket")) {

            Fragment TicketFragment = new Ticket();

            try {
                JSONObject item = JSONDATAARRAY.getJSONObject(position);

                Bundle bundle = new Bundle();
                bundle.putString("bundle", item.toString());
                TicketFragment.setArguments(bundle);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_content, TicketFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        } else if (DATATYPE.equalsIgnoreCase("Membership")) {

            Fragment MembershipFragment = new Membership();

            try {
                JSONObject item = JSONDATAARRAY.getJSONObject(position);

                Bundle bundle = new Bundle();
                bundle.putString("bundle", item.toString());
                MembershipFragment.setArguments(bundle);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_content, MembershipFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        } else {
            //Toast.makeText(getActivity(), "Something went wrong in ListviwerIcon OnClickListener", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}