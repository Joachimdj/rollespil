package dk.inventy.dk.rollespil;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONArray;

import java.io.InputStream;
import java.net.URL;


public class Profile extends Fragment {

    public static final String DEFAULT = "N/A";

    public TextView user_id_textview;
    public TextView name_textview;
    public ImageView profile_picture;

    public Button events_button;
    public Button foreninger_button;
    public Button medlemskaber_button;
    public Button billetter_button;

    public String events_url;
    public String foreninger_url;
    public String medlemskaber_url;
    public String billetter_url;

    public String selectedUrl;

    public boolean connected;

    public LinearLayout spinner_layout;
    public LinearLayout profile_layout;

    Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        spinner_layout = (LinearLayout) view.findViewById(R.id.spinner_layout);
        profile_layout = (LinearLayout) view.findViewById(R.id.profile_layout);

        getActivity().setTitle("Profil");

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);

        String user_id = sharedPreferences.getString("user_id", DEFAULT);
        String name = sharedPreferences.getString("name", DEFAULT);
        String profileImage = sharedPreferences.getString("profile_image", DEFAULT);

        user_id_textview = (TextView) view.findViewById(R.id.user_id_textview);
        name_textview = (TextView) view.findViewById(R.id.name_textview);
        profile_picture = (ImageView) view.findViewById(R.id.profile_image);

        user_id_textview.setText("ID: " + user_id);
        name_textview.setText(name);

        events_button = (Button) view.findViewById(R.id.events_button);
        foreninger_button = (Button) view.findViewById(R.id.foreninger_button);
        medlemskaber_button = (Button) view.findViewById(R.id.medlemskaber_button);
        billetter_button = (Button) view.findViewById(R.id.billetter_button);

        events_url = "https://rollespil.dk/app/blocks.php?type=event";
        foreninger_url = "https://rollespil.dk/app/blocks.php?type=com";
        medlemskaber_url = "https://rollespil.dk/app/memberships.php?id=" + user_id;
        billetter_url = "https://rollespil.dk/app/tickets.php?id=" + user_id;

        //new ProfileImageLoader().execute(profileImage);

        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getActivity())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP

        String url = "http://javatechig.com/wp-content/uploads/2014/05/UniversalImageLoader-620x405.png";

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.avatar)
                .showImageOnFail(R.drawable.avatar)
                .showImageOnLoading(R.drawable.avatar).build();

//initialize image view
        ImageView imageView = (ImageView) view.findViewById(R.id.profile_image);

//download and display image from url
        imageLoader.displayImage(profileImage, imageView, options);

        events_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                connected = isNetworkAvailable();

                Activity activity = getActivity();

                selectedUrl = events_url;

                if (connected) {
                    if (activity != null) {
                        new ArrayParse().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                } else {
                    offlineLoader(selectedUrl);
                }

            }
        });

        foreninger_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                connected = isNetworkAvailable();

                Activity activity = getActivity();

                selectedUrl = foreninger_url;

                if (connected) {
                    if (activity != null) {
                        new ArrayParse().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                } else {
                    offlineLoader(selectedUrl);
                }
            }
        });

        medlemskaber_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                connected = isNetworkAvailable();

                Activity activity = getActivity();

                selectedUrl = medlemskaber_url;

                if (connected) {
                    if (activity != null) {
                        new ArrayParse().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                } else {
                    offlineLoader(selectedUrl);
                }
            }
        });

        billetter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                connected = isNetworkAvailable();

                Activity activity = getActivity();

                selectedUrl = billetter_url;

                if (connected) {
                    if (activity != null) {
                        new ArrayParse().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                } else {
                    offlineLoader(selectedUrl);
                }
            }
        });

        return view;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private class ProfileImageLoader extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            if (image != null) {

                Bitmap output = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(output);

                final int color = 0xff424242;
                final Paint paint = new Paint();
                final Rect rect = new Rect(0, 0, image.getWidth(), image.getHeight());

                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                paint.setColor(color);

                canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(bitmap, rect, rect, paint);

                profile_picture.setImageBitmap(output);
            } else {

                bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.avatar);

                Bitmap avatar = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.avatar);

                Bitmap output = Bitmap.createBitmap(avatar.getWidth(), avatar.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(output);

                final int color = 0xff424242;
                final Paint paint = new Paint();
                final Rect rect = new Rect(0, 0, avatar.getWidth(), avatar.getHeight());

                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                paint.setColor(color);

                canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(bitmap, rect, rect, paint);

                profile_picture.setImageBitmap(output);
            }
        }
    }

    private class ArrayParse extends AsyncTask<String, String, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            profile_layout.setVisibility(View.GONE);
            spinner_layout.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            JSONParserArray jParser = new JSONParserArray();
            return jParser.getJSONFromUrl(selectedUrl);
        }

        @Override
        protected void onPostExecute(JSONArray json) {

            if (selectedUrl.equalsIgnoreCase(events_url)) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("events_json", json.toString());
                editor.commit();

                Fragment ListViewerFragment = new ListViewer();

                Bundle bundle = new Bundle();
                bundle.putString("type", "Events");
                bundle.putString("bundle", json.toString());
                ListViewerFragment.setArguments(bundle);

                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_content, ListViewerFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            } else if (selectedUrl.equalsIgnoreCase(foreninger_url)) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("foreninger_json", json.toString());
                editor.commit();

                Fragment ListViewerFragment = new ListViewer();

                Bundle bundle = new Bundle();
                bundle.putString("choice", "Foreninger");
                bundle.putString("type", "Foreninger");
                bundle.putString("bundle", json.toString());
                ListViewerFragment.setArguments(bundle);

                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_content, ListViewerFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            } else if (selectedUrl.equalsIgnoreCase(medlemskaber_url)) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("medlemskaber_json", json.toString());
                editor.commit();

                Fragment ListViewerIconFragment = new ListViewerIcon();

                Bundle bundle = new Bundle();
                bundle.putString("type", "Membership");
                bundle.putString("bundle", json.toString());
                ListViewerIconFragment.setArguments(bundle);

                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_content, ListViewerIconFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            } else if (selectedUrl.equalsIgnoreCase(billetter_url)) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("billetter_json", json.toString());
                editor.commit();

                Fragment ListViewerIconFragment = new ListViewerIcon();

                Bundle bundle = new Bundle();
                bundle.putString("type", "Ticket");
                bundle.putString("bundle", json.toString());
                ListViewerIconFragment.setArguments(bundle);

                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_content, ListViewerIconFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            } else {
                //Toast.makeText(getActivity(), "Something went wrong while trying to inflate layout in the profile class", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ArrayBackgroundParse extends AsyncTask<String, String, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            JSONParserArray jParser = new JSONParserArray();
            return jParser.getJSONFromUrl(selectedUrl);
        }

        @Override
        protected void onPostExecute(JSONArray json) {

            if (selectedUrl.equalsIgnoreCase(events_url)) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("events_json", json.toString());
                editor.commit();

            } else if (selectedUrl.equalsIgnoreCase(foreninger_url)) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("foreninger_json", json.toString());
                editor.commit();

            } else if (selectedUrl.equalsIgnoreCase(medlemskaber_url)) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("medlemskaber_json", json.toString());
                editor.commit();

            } else if (selectedUrl.equalsIgnoreCase(billetter_url)) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("billetter_json", json.toString());
                editor.commit();

            } else {

            }
        }
    }

    public void offlineLoader(String url) {
        if (url.equalsIgnoreCase(events_url)) {

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
            String json = sharedPreferences.getString("events_json", DEFAULT);

            Fragment ListViewerFragment = new ListViewer();

            Bundle bundle = new Bundle();
            bundle.putString("type", "Events");
            bundle.putString("bundle", json);
            ListViewerFragment.setArguments(bundle);

            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_content, ListViewerFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        } else if (url.equalsIgnoreCase(foreninger_url)) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
            String json = sharedPreferences.getString("foreninger_json", DEFAULT);

            Fragment ListViewerFragment = new ListViewer();

            Bundle bundle = new Bundle();
            bundle.putString("choice", "Foreninger");
            bundle.putString("type", "Foreninger");
            bundle.putString("bundle", json);
            ListViewerFragment.setArguments(bundle);

            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_content, ListViewerFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        } else if (url.equalsIgnoreCase(medlemskaber_url)) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
            String json = sharedPreferences.getString("medlemskaber_json", DEFAULT);

            Fragment ListViewerIconFragment = new ListViewerIcon();

            Bundle bundle = new Bundle();
            bundle.putString("type", "Membership");
            bundle.putString("bundle", json);
            ListViewerIconFragment.setArguments(bundle);

            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_content, ListViewerIconFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        } else if (url.equalsIgnoreCase(billetter_url)) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
            String json = sharedPreferences.getString("billetter_json", DEFAULT);

            Fragment ListViewerIconFragment = new ListViewerIcon();

            Bundle bundle = new Bundle();
            bundle.putString("type", "Ticket");
            bundle.putString("bundle", json);
            ListViewerIconFragment.setArguments(bundle);

            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_content, ListViewerIconFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        } else {
            Toast.makeText(getActivity(), "Ingen tilgængelige data", Toast.LENGTH_SHORT).show();
        }
    }
}