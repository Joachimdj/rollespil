package dk.inventy.dk.rollespil;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateUser extends Fragment {

    public String status;
    public EditText name;
    public EditText username;
    public EditText email;
    public EditText mobile;
    public EditText password;
    public Button create_account;
    public LinearLayout create_user_layout;
    public LinearLayout create_user_spinner;

    public CreateUser() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_user, container, false);

        create_user_layout = (LinearLayout) view.findViewById(R.id.createUser_layout);
        create_user_spinner = (LinearLayout) view.findViewById(R.id.spinner_create_layout);

        name = (EditText) view.findViewById(R.id.create_name);
        username = (EditText) view.findViewById(R.id.create_username);
        email = (EditText) view.findViewById(R.id.create_email);
        mobile = (EditText) view.findViewById(R.id.create_mobile);
        password = (EditText) view.findViewById(R.id.create_password);
        create_account = (Button) view.findViewById(R.id.create_user_button);

        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if (isConnected) {

                    if (name.getText().toString().equalsIgnoreCase("") ||
                            username.getText().toString().equalsIgnoreCase("") ||
                            email.getText().toString().equalsIgnoreCase("") ||
                            mobile.getText().toString().equalsIgnoreCase("") ||
                            password.getText().toString().equalsIgnoreCase("")) {

                        Toast.makeText(getActivity(), "Du skal udfylde alle felterne for at oprette din bruger profil", Toast.LENGTH_SHORT).show();

                    } else {
                        new PostData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }else{
                    Toast.makeText(getActivity(), "Du har ikke forbindelse til internettet", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return view;
    }

    public class PostData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            create_user_layout.setVisibility(View.GONE);
            create_user_spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://rollespil.dk/app/createUser.php");

            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("name", name.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("username", username.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("email", email.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("mobil", mobile.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("code", password.getText().toString()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);

            } catch (ClientProtocolException e) {
                create_user_spinner.setVisibility(View.GONE);
                create_user_layout.setVisibility(View.VISIBLE);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Fejl under oprettelse af bruger")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getFragmentManager().popBackStack();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            } catch (IOException e) {
                create_user_spinner.setVisibility(View.GONE);
                create_user_layout.setVisibility(View.VISIBLE);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Fejl under oprettelse af bruger")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getFragmentManager().popBackStack();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            create_user_spinner.setVisibility(View.GONE);
            create_user_layout.setVisibility(View.VISIBLE);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Din brugerprofil er nu oprettet")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getFragmentManager().popBackStack();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }


}
