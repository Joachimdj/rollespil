package dk.inventy.dk.rollespil;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class FragmentHandler extends Activity {

    private boolean login_status;
    public static final String DEFAULT = "N/A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_handler);

        SharedPreferences prefs = this.getSharedPreferences("userdata", Context.MODE_PRIVATE);

        login_status = prefs.getBoolean("user_logged_in", false);

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_content);

        if (fragment == null) {

            if (login_status == false) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.fragment_content, new Login());
                ft.commit();
            }else{
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.fragment_content, new Profile());
                ft.commit();
            }
        }

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return  isConnected;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fragment_handler, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.logout_button_menu:

                FragmentManager fm = this.getFragmentManager();
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }

                SharedPreferences sharedPreferences = this.getSharedPreferences("userdata", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("user_logged_in", false);
                editor.commit();

                Fragment LoginFragment = new Login();
                FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_content, LoginFragment);
                transaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
