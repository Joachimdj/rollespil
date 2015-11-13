package dk.inventy.dk.rollespil;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class Membership extends Fragment {

    public static final String DEFAULT = "N/A";

    TextView lokalforegning;
    TextView slut_dato;
    TextView adresse;
    TextView ejer;
    TextView pris;
    TextView kobsdato;
    TextView tilmeldings_id;
    TextView transkations_id;
    TextView order_id;
    ImageView qr_image;

    String JSONDATAIMPORT;
    JSONObject DATA;

    public Membership() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Medlemskaber");

        View view = inflater.inflate(R.layout.fragment_membership, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
        String user_id = sharedPreferences.getString("user_id", DEFAULT);
        String name = sharedPreferences.getString("name", DEFAULT);

        qr_image = (ImageView) view.findViewById(R.id.qr_membership_image);

        try {
            generateQRCode(user_id);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        order_id = (TextView) view.findViewById(R.id.order_id_membership_label);
        lokalforegning = (TextView) view.findViewById(R.id.lokalforegning_label);
        slut_dato = (TextView) view.findViewById(R.id.udlob_label);
        adresse = (TextView) view.findViewById(R.id.adresse_membership_label);
        ejer = (TextView) view.findViewById(R.id.ejer_membership_label);
        pris = (TextView) view.findViewById(R.id.pris_membership_label);
        kobsdato = (TextView) view.findViewById(R.id.kob_dato_membership_label);
        tilmeldings_id = (TextView) view.findViewById(R.id.tilmelding_membership_label);
        transkations_id = (TextView) view.findViewById(R.id.tansaktion_membership_label);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            JSONDATAIMPORT = bundle.getString("bundle", null);
        }

        try {
            DATA = new JSONObject(JSONDATAIMPORT);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            order_id.setText(DATA.getString("signupID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            lokalforegning.setText("Lokalforegning: " + DATA.getString("place"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            slut_dato.setText("Slut dato: " + DATA.getString("lastDate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            adresse.setText("Adresse:\n" + DATA.getString("address"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            adresse.setText("Adresse:\n" + DATA.getString("address"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ejer.setText("Ejer: " + name);

        try {
            pris.setText("Pris: DKKR " + DATA.getString("price"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            kobsdato.setText("Købsdato: " + DATA.getString("paymentDate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            tilmeldings_id.setText("Tilmeldings ID: " + DATA.getString("signupID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            transkations_id.setText("Transaktions ID: " + DATA.getString("transID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    public void generateQRCode(String data) throws WriterException {
        com.google.zxing.Writer writer = new QRCodeWriter();
        String finaldata = Uri.encode(data, "utf-8");

        BitMatrix bm = writer.encode(finaldata, BarcodeFormat.QR_CODE, 150, 150);
        Bitmap ImageBitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < 150; i++) {//width
            for (int j = 0; j < 150; j++) {//height
                ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.TRANSPARENT);
            }
        }

        if (ImageBitmap != null) {
            qr_image.setImageBitmap(ImageBitmap);
        } else {
            Toast.makeText(getActivity(), "Kunne ikke generer QR kode", Toast.LENGTH_SHORT).show();
        }
    }


}
