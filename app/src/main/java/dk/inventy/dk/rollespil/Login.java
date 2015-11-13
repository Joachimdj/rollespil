package dk.inventy.dk.rollespil;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends Fragment {

    public EditText username;
    public EditText password;
    public Button loginButton;
    public Button createButton;
    public LinearLayout spinner_login_layout;
    public LinearLayout lin_login_ui;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        spinner_login_layout = (LinearLayout) view.findViewById(R.id.spinner_login_layout);
        lin_login_ui = (LinearLayout) view.findViewById(R.id.login_ui);

        getActivity().setTitle("Login");

        username = (EditText) view.findViewById(R.id.username);
        password = (EditText) view.findViewById(R.id.password);
        loginButton = (Button) view.findViewById(R.id.login_button);
        createButton = (Button) view.findViewById(R.id.create_button);

        username.setText("");
        password.setText("");

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getView().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                Activity activity = getActivity();

                if (isConnected) {
                    if (activity != null && !username.getText().toString().equals("") && !password.getText().toString().equals("")) {
                        new JSONParse().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        Toast.makeText(getActivity(), "Udfyld venligst brugernavn og kodeord", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Du har ikke forbindelse til internettet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment CreateUserFragment = new CreateUser();
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_content, CreateUserFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {

        private static final String TAG_SUCCESS = "success";
        private static final String DEBUG_PARSE = "JSONPARSE";

        private String username_input;
        private String password_input;
        private String url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            username_input = username.getText().toString();
            password_input = password.getText().toString();

            lin_login_ui.setVisibility(View.GONE);
            spinner_login_layout.setVisibility(View.VISIBLE);

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParserObject jParser = new JSONParserObject();
            url = "https://rollespil.dk/app/jsonlogin.php?username=" + username_input + "&password=" + password_input;
            return jParser.getJSONFromUrl(url);
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            spinner_login_layout.setVisibility(View.GONE);

            try {

                int checker = json.getInt(TAG_SUCCESS);
                Log.d("CheckerTag", String.valueOf(checker));

                if (checker == 1) {

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("user_logged_in", true);
                    editor.putBoolean("Launched_before", true);
                    editor.putString("user_json", json.toString());
                    editor.putString("user_id", json.getString("user_id"));
                    editor.putString("name", json.getString("name"));
                    editor.putString("profile_image", json.getString("avatar"));
                    editor.commit();

                    Fragment ProfileFragment = new Profile();
                    FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_content, ProfileFragment);
                    transaction.commit();

                } else if (checker == 0) {
                    lin_login_ui.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Forkert brugernavn eller adgangskode", Toast.LENGTH_SHORT).show();
                } else {
                    lin_login_ui.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Login fejl", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}