package mcgyvers.mobitrip;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ornach.nobobutton.NoboButton;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by Shanto on 9/6/2017.
 */

public class Profile extends Fragment{

    ImageView profilePic;
    MaterialEditText namePT,phonePT,emailPT,addressPT;
    NoboButton savePT;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);



        Typeface regular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Regular.ttf");
        Typeface bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Bold.ttf");


        profilePic = rootView.findViewById(R.id.propic);
        namePT = rootView.findViewById(R.id.name_pro);
        phonePT = rootView.findViewById(R.id.phone_pro);
        emailPT = rootView.findViewById(R.id.email_pro);
        addressPT = rootView.findViewById(R.id.address_pro);
        savePT = rootView.findViewById(R.id.save_pro);




        namePT.setTypeface(regular);
        phonePT.setTypeface(regular);
        emailPT.setTypeface(regular);
        addressPT.setTypeface(regular);

        savePT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences currPrefs = getContext().getSharedPreferences(MainActivity.CURR_PREFS, Context.MODE_PRIVATE);
                SharedPreferences sharedPreferences = getContext().getSharedPreferences(getContext().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor currEditor = currPrefs.edit();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                currEditor.clear();
                editor.apply();
                currEditor.apply();
                Toast.makeText(getContext(), "Trip records wiped out", Toast.LENGTH_SHORT).show();


            }
        });

        return rootView;
    }
}
