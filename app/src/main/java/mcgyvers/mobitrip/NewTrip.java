package mcgyvers.mobitrip;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;

import io.blackbox_vision.datetimepickeredittext.view.DatePickerEditText;
import mcgyvers.mobitrip.dataModels.AtPlace;
import mcgyvers.mobitrip.dataModels.Member;
import mcgyvers.mobitrip.dataModels.Trip;

/**
 * Created by Shanto on 9/5/2017.
 * class handling the creation new trips
 */

public class NewTrip extends Fragment {

    //SimpleDateFormat date;
    Button members,start;
    MaterialEditText from,destination,amount,commonexpense;


    DatePickerEditText trip_date;
    //Integer personalAmount;
    //Integer commonExpediture;

    SharedPreferences tmpShared;
    SharedPreferences.Editor tmpEditor;

    AtPlace or = null;
    AtPlace desti = null;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_new_trip, container, false);
        final Context context = getActivity();

        members = rootView.findViewById(R.id.membersDetails);
        trip_date = rootView.findViewById(R.id.et_trip_date);
        start = rootView.findViewById(R.id.start_trip_button);
        from = rootView.findViewById(R.id.from_journey);
        destination = rootView.findViewById(R.id.destination);
        amount = rootView.findViewById(R.id.amount_per);
        commonexpense = rootView.findViewById(R.id.common_expense);


        from.setFocusable(false);
        from.setKeyListener(null);

        destination.setFocusable(false);
        destination.setKeyListener(null);


        tmpShared = getActivity().getSharedPreferences(MainActivity.TMP_PREFS, Context.MODE_PRIVATE);
        if(tmpShared != null){
            Gson gson = new Gson();
            String dest = tmpShared.getString(MainActivity.DESTINATION, "");
            String origin = tmpShared.getString(MainActivity.ORIGIN,"");
            if(!dest.equals("")){
                desti = gson.fromJson(dest, AtPlace.class);
                destination.setText(desti.getName() + ", " + desti.getAdress());
            }

            if(!origin.equals("")){
                or = gson.fromJson(origin, AtPlace.class);
                from.setText(or.getName() + ", " + or.getAdress());
            }

        }




        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), PlacePicker.class);
                i.putExtra("way", "origin");
                startActivity(i);
            }
        });


        destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), PlacePicker.class);
                i.putExtra("way", "destination");
                startActivity(i);
            }
        });




        trip_date.setManager(getFragmentManager());

        members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: encapsulate this code
                String tdate = "";
                String fr = "";
                String dest = "";

                fr = from.getText().toString();
                tdate = trip_date.getText().toString();
                dest = destination.getText().toString();

                if(tdate.equals("") || fr.equals("") || dest.equals("")){
                    Toast.makeText(getContext(), "Locations and date fields must be filled", Toast.LENGTH_LONG).show();
                }else{
                    createTrip(tdate, fr, dest ,Integer.parseInt(amount.getText().toString()),
                            Integer.parseInt(commonexpense.getText().toString()), null);
                }

                startActivity(new Intent(getActivity(),Current_trip_member_information.class));

            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:check if amounts are numbers
                String tdate = "";
                String fr = "";
                String dest = "";

                fr = from.getText().toString();
                tdate = trip_date.getText().toString();
                dest = destination.getText().toString();

                if(tdate.equals("") || fr.equals("") || dest.equals("")){
                    Toast.makeText(getContext(), "Locations and date fields must be filled", Toast.LENGTH_LONG).show();
                }else{
                    createTrip(tdate, fr, dest ,Integer.parseInt(amount.getText().toString()),
                            Integer.parseInt(commonexpense.getText().toString()), null);
                }



                tmpEditor = tmpShared.edit();
                tmpEditor.clear();
                tmpEditor.apply();

                //---------------------delete
                // send message to mainActivity to clean the savedInstance
                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                intent.putExtra("MESSAGE","CLEAN");
                startActivity(intent);
                //--------------------------

            }
        });

        //getLocations();



        return rootView;
    }





    void createTrip(String date, String origin, String destination_s, Integer amount_s, Integer common_s, ArrayList<Member> members){

        Context context = getActivity();
        //getting the handle of sharedpreferences to store the due values
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        //getting the number of trips currently in store in order to creat an id for the new trip
        long tripsN = sharedPreferences.getLong(getString(R.string.trip_count), 0);
        Trip trip = new Trip(origin, destination_s, amount_s, common_s, null, date, String.valueOf(++tripsN));



        Editor editor = sharedPreferences.edit();

        try {
            //
            //TODO: organize and debug the trips' ids
            Gson gson = new Gson();
            String tripArray = sharedPreferences.getString(getString(R.string.trips_array), "[]");
            ArrayList<Trip> getAllTrips = gson.fromJson(tripArray, new TypeToken<ArrayList<Trip>>(){}.getType());
            for(int i = 0; i < getAllTrips.size(); i++){
                if(!getAllTrips.get(i).isCompleted()){
                    Toast.makeText(getContext(), "you currently have an ongoing trip", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            //getting the array with the previous trips from storage
            JSONArray trips = new JSONArray(tripArray);
            //creates json object with the new trip and puts it into the array of trips
            JSONObject newTrip =  new JSONObject(gson.toJson(trip));
            trips.put(newTrip);

            //add everything to storage and save
            editor.putString(getString(R.string.trips_array), trips.toString());
            editor.putLong(getString(R.string.trip_count), ++tripsN);
            editor.apply();
            System.out.println(trips.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Toast.makeText(getContext(), "Trip succesfully added!", Toast.LENGTH_LONG).show();
        from.setText("");
        destination.setText("");
        amount.setText("");
        commonexpense.setText("");
        trip_date.setText("");

    }




}
