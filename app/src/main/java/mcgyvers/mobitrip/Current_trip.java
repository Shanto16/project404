package mcgyvers.mobitrip;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.timqi.sectorprogressview.ColorfulRingProgressView;

import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mcgyvers.mobitrip.dataModels.Expense;
import mcgyvers.mobitrip.dataModels.Trip;

import static mcgyvers.mobitrip.R.id.all;
import static mcgyvers.mobitrip.R.id.place_autocomplete_prediction_primary_text;
import static mcgyvers.mobitrip.R.id.toolbar;

/**
 * Created by Shanto on 9/7/2017.
 * Class for displaying data about the latest trip
 */

public class Current_trip extends Fragment {

    float max = 5000, min = 1200;

    float pcnt = (min / max) * 100;

    TextView _expense,spent,currentLocation, temp,weather;
    ColorfulRingProgressView expenseProgress;
    Toolbar mToolbar;

    Trip currentTrip = null;
    int currentPos = 0;

    private static final int MY_PERMISSION_REQUEST_LOCATION=1;

    Button team_expense, my_expense,hospitals,policeStation,maps,camera,restaurants,hotels,fuel,spots,dismiss,finish;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View rootView = inflater.inflate(R.layout.fragment_current_trip, container, false);
        final Context context = getActivity();
        mToolbar = rootView.findViewById(R.id.toolbar);
        getActivity().supportInvalidateOptionsMenu();


        Typeface regular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Regular.ttf");
        Typeface bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Bold.ttf");



        team_expense = rootView.findViewById(R.id.team_expense_button);
        my_expense = rootView.findViewById(R.id.my_expense_button);
        expenseProgress = rootView.findViewById(R.id.expense_progressbar);
        _expense = rootView.findViewById(R.id.pcnt_expense);
        spent = rootView.findViewById(R.id.spent_card);
        currentLocation = rootView.findViewById(R.id.current_location_text);
        temp = rootView.findViewById(R.id.temp_text);
        weather = rootView.findViewById(R.id.weather_text);

        hospitals = rootView.findViewById(R.id.nav_hospitals);
        policeStation = rootView.findViewById(R.id.nav_police);
        maps = rootView.findViewById(R.id.nav_maps);
        camera = rootView.findViewById(R.id.cam_current);
        restaurants = rootView.findViewById(R.id.nav_restaurants);
        hotels = rootView.findViewById(R.id.nav_hotels);
        fuel = rootView.findViewById(R.id.nav_fuel);
        spots = rootView.findViewById(R.id.nav_spots);
        dismiss = rootView.findViewById(R.id.nav_dismiss);
        finish = rootView.findViewById(R.id.nav_finish);






        if (ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.ACCESS_COARSE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)){
                ActivityCompat.requestPermissions(getActivity(),new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },MY_PERMISSION_REQUEST_LOCATION);
            }else{
                ActivityCompat.requestPermissions(getActivity(),new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },MY_PERMISSION_REQUEST_LOCATION);
            }
        } else{
            LocationManager locationManager  = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            try{
                currentLocation.setText(myLocation(location.getLatitude(),location.getLongitude()));
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getActivity(), "Location not found", Toast.LENGTH_SHORT).show();
            }
        }


        hospitals.setTransformationMethod(null);
        policeStation.setTransformationMethod(null);
        maps.setTransformationMethod(null);
        camera.setTransformationMethod(null);
        restaurants.setTransformationMethod(null);
        hotels.setTransformationMethod(null);
        fuel.setTransformationMethod(null);
        spots.setTransformationMethod(null);
        dismiss.setTransformationMethod(null);
        finish.setTransformationMethod(null);
        team_expense.setTransformationMethod(null);
        my_expense.setTransformationMethod(null);
        camera.setTransformationMethod(null);








        hospitals.setTypeface(regular);
        policeStation.setTypeface(regular);
        maps.setTypeface(regular);
        camera.setTypeface(regular);
        restaurants.setTypeface(regular);
        hotels.setTypeface(regular);
        fuel.setTypeface(regular);
        spots.setTypeface(regular);
        dismiss.setTypeface(regular);
        finish.setTypeface(regular);
        team_expense.setTypeface(regular);
        my_expense.setTypeface(regular);
        weather.setTypeface(regular);
        temp.setTypeface(regular);
        currentLocation.setTypeface(regular);
        camera.setTypeface(regular);


        _expense.setTypeface(bold);
        spent.setTypeface(bold);





        hospitals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("POI", "Hospital");
                startActivity(intent);
            }
        });

        policeStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("POI", "Police");
                startActivity(intent);
            }
        });

        restaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("POI", "Restaurant");
                startActivity(intent);
            }
        });

        hotels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("POI", "Hotel");
                startActivity(intent);
            }
        });

        fuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("POI", "Fuel");
                startActivity(intent);
            }
        });

        spots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("POI", "Tourism");
                startActivity(intent);
            }
        });


        expenseProgress.setPercent(pcnt);
        _expense.setText(Float.toString(pcnt)+"%");

        my_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.my_expense_dialog);
                dialog.setTitle("My Expense");



                ListView myExpenseList = dialog.findViewById(R.id.my_expense_listView);
                TextView myTotal = dialog.findViewById(R.id.totalAMOUNT);
                Button save_button = dialog.findViewById(R.id.my_expense_save);

                final ArrayList<Expense> expenses = new ArrayList<>();
                final ExpensesAdapter expensesAdapter = new ExpensesAdapter(expenses);
                myExpenseList.setAdapter(expensesAdapter);
                myTotal.setText(String.valueOf(expensesAdapter.totalExp()));

                if(currentTrip.getExpenses() != null){
                    expenses.addAll(currentTrip.getExpenses());
                }



                save_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentPos = getCurrentPos(getContext());
                        if(currentPos > -1){
                            expensesAdapter.notifyDataSetChanged();
                            currentTrip.setExpenses(expenses);
                            UpdateTripList(getContext(),currentTrip, currentPos);
                        }else{
                            Toast.makeText(getContext(), "Error adding expenses", Toast.LENGTH_LONG).show();
                        }

                        dialog.dismiss();
                    }
                });


                ImageView addExpense = dialog.findViewById(R.id.add_new_expense);
                addExpense.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Expense expense = new Expense("", "");

                        expenses.add(expense);
                        expensesAdapter.notifyDataSetChanged();

                    }
                });


                dialog.show();

            }
        });


        team_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.model_team_expense);
                dialog.setTitle("Team Expense");

                ListView team_expense = dialog.findViewById(R.id.team_expense_listview);
                Button ok_button = dialog.findViewById(R.id.team_expense_dismiss);

                ok_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();
                    }
                });


                dialog.show();


            }
        });

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("POI", "");
                startActivity(intent);
            }
        });


        currentTrip = getCurrentTrip(getContext());


        return rootView;
    }

    /**
     * method for returning the ArrayList of trip objects containing all of the
     * trips already added by the user from the local storage
     * mainly for use in other functions.
     * @param context current activity context
     * @return Array list of trip objects
     */
    public static ArrayList<Trip> getTripList(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String tripArray = sharedPreferences.getString(context.getString(R.string.trips_array), "[]");

        ArrayList<Trip> allTrips = gson.fromJson(tripArray, new TypeToken<ArrayList<Trip>>(){}.getType());
        if(allTrips.size() > 0){
            return allTrips;
        } else return null;

    }

    /**
     * Method for returning a trip object containing the latest created trip.
     * It fetches the array list of all the trips on the device, and returns only
     * the first element on the list, if it exists. It also updates currentPos int
     * variable with the position of the current trip in the list
     *
     * @param context current activity context
     * @return Trip object
     */
    public static Trip getCurrentTrip(Context context) {

        ArrayList<Trip> allTrips = getTripList(context);

        if(allTrips != null){

            return allTrips.get(allTrips.size() - 1);
        } else return null;



    }

    /**
     * Method for updating the list of trips, will be used when expenses are added on the
     * list of members.
     *
     * @param context current activity context
     * @param trip object to be modified in the list
     * @param pos position of the object in the list
     */
    public static void UpdateTripList(Context context, Trip trip, int pos){


        ArrayList<Trip> allTrips = getTripList(context);
        allTrips.set(pos, trip);

        String tripsArray = new Gson().toJson(allTrips);

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.trips_array), tripsArray);
        editor.apply();
        System.out.println(tripsArray);


    }

    public int getCurrentPos(Context context){
        ArrayList e = getTripList(context);
        if(e != null)
            return e.size() - 1;
        return -1;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults){
        switch (requestCode){
            case MY_PERMISSION_REQUEST_LOCATION : {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        LocationManager locationManager  = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        try{
                            currentLocation.setText(myLocation(location.getLatitude(),location.getLongitude()));
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Location not found", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getActivity(), "Location permission is not granted!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_current_trip, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.sos);
        item.setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sos:
                Toast.makeText(getActivity(), "a pop up screen will be opened here asking for an emergency number", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //GETTING CURRENT CITY NAME
    public String myLocation(double latitude, double longitude){
        String myCity = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addressList ;
        try{
            addressList = geocoder.getFromLocation(latitude,longitude,1);

            if(addressList.size()>0){
                myCity = addressList.get(0).getLocality();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return myCity;
    }


    private static class ViewHolder {
        private EditText exp;
        private EditText cost;
        private TextWatcher costTextWatcher, expTextWatcher;
    }


    public class ExpensesAdapter extends BaseAdapter{

        private ArrayList<Expense> expenses;

        public ExpensesAdapter(ArrayList<Expense> expenses){
            this.expenses = expenses;
        }

        @Override
        public int getCount() {
            return expenses.size();
        }

        @Override
        public Object getItem(int position) {
            return expenses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public int totalExp(){
            int exps = 0;

            for(int i = 0; i < expenses.size(); i++){
                Integer thisExps = Integer.valueOf(expenses.get(i).getCost());
                exps += thisExps;
            }
            System.out.println(String.valueOf(exps));

            return exps;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            View view = convertView;
            if(view == null){
                // not recycled, inflate a new view

                view = getLayoutInflater().inflate(R.layout.my_expense_model, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.exp = view.findViewById(R.id.usedON);
                viewHolder.cost = view.findViewById(R.id.cost);
                view.setTag(viewHolder);



            }

            ViewHolder holder = (ViewHolder) view.getTag();
            // Remove any existing TextWatcher that will be keyed to the wrong ListItem
            if(holder.costTextWatcher != null)
                holder.cost.removeTextChangedListener(holder.costTextWatcher);

            if(holder.expTextWatcher != null)
                holder.exp.removeTextChangedListener(holder.expTextWatcher);



            final Expense expense = expenses.get(position);
            // Keep a reference to the TextWatcher so that we can remove it later
            holder.costTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    expense.setCost(s.toString());

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };

            holder.expTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    expense.setName(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };

            holder.exp.addTextChangedListener(holder.expTextWatcher);
            holder.exp.setText(expense.getName());
            holder.cost.addTextChangedListener(holder.costTextWatcher);
            holder.cost.setText(expense.getCost());




            return view;

        }
    }
}
