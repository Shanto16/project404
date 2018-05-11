package mcgyvers.mobitrip;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.Date;
import java.util.Locale;

import io.blackbox_vision.datetimepickeredittext.internal.fragment.DatePickerFragment;
import io.blackbox_vision.datetimepickeredittext.view.DatePickerEditText;
import mcgyvers.mobitrip.dataModels.AtPlace;
import mcgyvers.mobitrip.dataModels.Expense;
import mcgyvers.mobitrip.dataModels.Member;
import mcgyvers.mobitrip.dataModels.Trip;

/**
 * Created by Shanto on 9/5/2017.
 * class handling the creation new trips
 */

public class NewTrip extends Fragment {

    //SimpleDateFormat date;
    TextView startp,endP,Dmap,Sdate,Stime,ApP,Cexpense,memberInfoTXT,DmapHelper;
    Button save;
    CardView members;
    MaterialEditText departureTime;
    EditText from,destination,amount,commonexpense,tripName;

    static final int DIALOG_ID = 0;
    int hour,minute;

    DatePickerEditText trip_date;
    //Integer personalAmount;
    //Integer commonExpediture;

    SharedPreferences tmpShared;
    SharedPreferences.Editor tmpEditor;

    AtPlace or = null;
    AtPlace desti = null;


    Trip editTrip = null; // in case we're editing a trip.


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_new_trip, container, false);
        final Context context = getActivity();

        members = rootView.findViewById(R.id.membersDetails);
        trip_date = rootView.findViewById(R.id.et_trip_date);
        save = rootView.findViewById(R.id.start_trip_button);
        from = rootView.findViewById(R.id.from_journey);
        destination = rootView.findViewById(R.id.destination);
        amount = rootView.findViewById(R.id.amount_per);
        commonexpense = rootView.findViewById(R.id.common_expense);
        tripName = rootView.findViewById(R.id.trip_name);
        departureTime = rootView.findViewById(R.id.et_trip_time);

        amount.setText("0");
        commonexpense.setText("0");

        startp = rootView.findViewById(R.id.start_point);
        endP = rootView.findViewById(R.id.end_point);
        Dmap = rootView.findViewById(R.id.download_mapTXT);
        Sdate = rootView.findViewById(R.id.sDate);
        Stime = rootView.findViewById(R.id.sTime);
        ApP = rootView.findViewById(R.id.perpersonTXT);
        Cexpense = rootView.findViewById(R.id.commonTXT);
        memberInfoTXT = rootView.findViewById(R.id.member_infoTXT);
        DmapHelper = rootView.findViewById(R.id.download_map_helper);




        from.setFocusable(false);
        from.setKeyListener(null);

        destination.setFocusable(false);
        destination.setKeyListener(null);

        departureTime.setFocusable(false);
        departureTime.setKeyListener(null);

        Typeface firaSans_medium = Typeface.createFromAsset(getActivity().getAssets(),"fonts/FiraSans-Medium.ttf");
        Typeface firaSans_semiBold = Typeface.createFromAsset(getActivity().getAssets(),"fonts/FiraSans-Medium.ttf");
        Typeface amaranth = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Amaranth-Bold.ttf");
        startp.setTypeface(firaSans_semiBold);
        endP.setTypeface(firaSans_semiBold);
        Dmap.setTypeface(firaSans_semiBold);
        Sdate.setTypeface(firaSans_semiBold);
        Stime.setTypeface(firaSans_semiBold);
        ApP.setTypeface(firaSans_semiBold);
        Cexpense.setTypeface(firaSans_semiBold);
        memberInfoTXT.setTypeface(firaSans_semiBold);
        save.setTypeface(firaSans_semiBold);
        tripName.setTypeface(amaranth);

        trip_date.setTypeface(firaSans_medium);
        from.setTypeface(firaSans_medium);
        destination.setTypeface(firaSans_medium);
        amount.setTypeface(firaSans_medium);
        commonexpense.setTypeface(firaSans_medium);
        departureTime.setTypeface(firaSans_medium);






        tmpShared = getActivity().getSharedPreferences(MainActivity.TMP_PREFS, Context.MODE_PRIVATE);
        if(tmpShared != null){
            Gson gson = new Gson();
            if(tmpShared.getString(MainActivity.TRIP_EDIT, "") != ""){
                // if we're editting something
                String trip = tmpShared.getString(MainActivity.TRIP_EDIT, "");
                
                editTrip = gson.fromJson(trip, Trip.class);
                save.setText("Start Trip");
                if(editTrip.getDestPlace() != null){
                    desti = editTrip.getDestPlace();
                    destination.setText(desti.getName() + ", " + desti.getAdress());
                }

                if(editTrip.getOriginPlace() != null){
                    or = editTrip.getOriginPlace();
                    from.setText(or.getName() + ", " + or.getAdress());
                }


                if(editTrip.getName() != "") tripName.setText(editTrip.getName());
                if(editTrip.getCommonExp() != null) commonexpense.setText(String.valueOf(editTrip.getCommonExp()));
                if(editTrip.getAmount() != null) amount.setText(String.valueOf(editTrip.getAmount()));
                //TODO: fill the date fields.



                tripName.setKeyListener(null);
                commonexpense.setKeyListener(null);
                amount.setKeyListener(null);
                from.setKeyListener(null);
                from.setClickable(false);
                destination.setKeyListener(null);
                destination.setClickable(false);



            }else {

                String dest = tmpShared.getString(MainActivity.DESTINATION, "");
                String origin = tmpShared.getString(MainActivity.ORIGIN,"");
                String tripNm = tmpShared.getString(MainActivity.TRIPNAME, "");
                String commonexp = tmpShared.getString(MainActivity.COMMONEXP, "");
                String amnt = tmpShared.getString(MainActivity.AMOUNT, "");

                save.setText("Save Trip");

                if(!dest.equals("")){
                    desti = gson.fromJson(dest, AtPlace.class);
                    destination.setText(desti.getName() + ", " + desti.getAdress());
                }

                if(!origin.equals("")){
                    or = gson.fromJson(origin, AtPlace.class);
                    from.setText(or.getName() + ", " + or.getAdress());
                }

                if (!tripNm.equals("")) {
                    tripName.setText(tripNm);
                }

                if(!commonexp.equals("")){
                    commonexpense.setText(commonexp);
                }

                if(!amnt.equals("")){
                    amount.setText(amnt);
                }
            }
        }

        departureTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getFragmentManager();
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),timeSet,hour,minute,true);
                timePickerDialog.show();
            }
        });


        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTrip == null){
                    saveCurrentConfigs();
                    Intent i = new Intent(getActivity(), PlacePicker.class);
                    i.putExtra("way", "origin");
                    startActivity(i);
                }

            }
        });


        destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTrip == null){
                    saveCurrentConfigs();
                    Intent i = new Intent(getActivity(), PlacePicker.class);
                    i.putExtra("way", "destination");
                    startActivity(i);
                }
            }
        });




        trip_date.setManager(getFragmentManager());

        members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCurrentConfigs();
                startActivity(new Intent(getActivity(),Current_trip_member_information.class));

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTrip != null){
                    SharedPreferences currShared = getContext().getSharedPreferences(MainActivity.CURR_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor currEditor = currShared.edit();

                    // if there isnt any ongoing trip
                    if(currShared.getString(MainActivity.CURR_TRIP,"") == ""){
                        //now we're gonna start this goddamn trip
                        // put the trip that's being edited here into the current trip local storage file
                        String currTrip = tmpShared.getString(MainActivity.TRIP_EDIT, "");
                        currEditor.putString(MainActivity.CURR_TRIP, currTrip);
                        currEditor.apply();

                        Intent intent = new Intent(context, MainActivity.class);
                        Bundle b = new Bundle();
                        b.putInt("fragToLoad", R.id.nav_current_trip);
                        intent.putExtras(b);

                        clearTmp();
                        startActivity(intent);
                    } else{
                       //if there's already a trip ongoing
                       Toast.makeText(getContext(), "There is already an ongoing trip", Toast.LENGTH_LONG).show();

                    }





                }else{

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

                    clearTmp();



                }


            }
        });


        return rootView;
    }

    /**
     * the temporary file must be cleared after we edit a trip
     * as well as after we're creating a brand new one
     */
    void clearTmp(){
        tmpEditor = tmpShared.edit();
        tmpEditor.clear();
        tmpEditor.apply();
    }


    /**
     * saves current configurations of trip to the temporary trip file
     * to not lose them when user changes context
     */
    void saveCurrentConfigs(){

        System.out.println("saving current configs:");
        tmpShared = getActivity().getSharedPreferences(MainActivity.TMP_PREFS, Context.MODE_PRIVATE);
        tmpEditor = tmpShared.edit();

        tmpEditor.putString(MainActivity.AMOUNT, amount.getText().toString());
        tmpEditor.putString(MainActivity.COMMONEXP, commonexpense.getText().toString());
        tmpEditor.putString(MainActivity.TRIPDATE, trip_date.toString());
        tmpEditor.putString(MainActivity.TRIPNAME, tripName.getText().toString());





        tmpEditor.apply();


    }

    protected TimePickerDialog.OnTimeSetListener timeSet  = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour_x, int minute_x) {
          hour = hour_x;
          minute = minute_x;

          if(hour<=12)
          departureTime.setText(hour+":"+minute + " AM");
          else
              departureTime.setText(hour-12 + ":" +minute + " PM");
        }
    };


    void createTrip(String date, String origin, String destination_s, Integer amount_s, Integer common_s, ArrayList<Member> members){



        Context context = getActivity();

        //getting the handle of sharedpreferences to store the due values
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        Date date1 = new Date();
        long tripId = date1.getTime();
        //getting the number of trips currently in store in order to creat an id for the new trip
        long tripsN = sharedPreferences.getLong(getString(R.string.trip_count), 0);

        Trip trip = new Trip(origin, destination_s, amount_s, common_s, null, date, String.valueOf(tripId), null);

        trip.isHost = true;
        trip.setCompleted(false);
        // setting the name of the trip
        if(tripName.getText().toString() != ""){
            trip.setName(tripName.getText().toString());
        }


        //getting the list of members currently on the temporary trip file
        ArrayList<Member> m = Current_trip_member_information.getMembers(getContext());
        trip.setMembers(m);




        if(desti != null && or != null){
            trip.setDestPlace(desti);
            trip.setOriginPlace(or);
        } else {
            Toast.makeText(getContext(), "Origin and destination must be specified", Toast.LENGTH_SHORT).show();
        }


        Editor editor = sharedPreferences.edit();

        try {
            //
            //TODO: organize and debug the trips' ids
            Gson gson = new Gson();
            String tripArray = sharedPreferences.getString(getString(R.string.trips_array), "[]");
            ArrayList<Trip> getAllTrips = gson.fromJson(tripArray, new TypeToken<ArrayList<Trip>>(){}.getType());
            for(int i = 0; i < getAllTrips.size(); i++){
                if(!getAllTrips.get(i).isCompleted()){
                    //Toast.makeText(getContext(), "you currently have an ongoing trip", Toast.LENGTH_LONG).show();
                    //return;
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
        tripName.setText("");
        departureTime.setText("");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveCurrentConfigs();

        if(editTrip != null){
            tmpEditor = tmpShared.edit();
            tmpEditor.clear();
            tmpEditor.apply();

        }
    }
}
