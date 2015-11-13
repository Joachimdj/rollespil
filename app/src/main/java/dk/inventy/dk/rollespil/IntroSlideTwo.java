package dk.inventy.dk.rollespil;



import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import static android.view.View.*;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class IntroSlideTwo extends Fragment {

    public Button start;

    public IntroSlideTwo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_intro_slide_two, container, false);

        start = (Button) rootView.findViewById(R.id.start);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), FragmentHandler.class);
                startActivity(i);
            }
        });

        return rootView;
    }

}
