package mcgyvers.mobitrip;

import android.*;
import android.Manifest;
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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mcgyvers.mobitrip.dataModels.Trip;

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


                Button save_button = dialog.findViewById(R.id.my_expense_save);

                save_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


                ImageView addExpense = dialog.findViewById(R.id.add_new_expense);
                addExpense.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "A new 'my_expense_model.xml' will be added in this listView here. ", Toast.LENGTH_LONG).show();
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


        currentTrip = getCurrentTrip(getContext(), getString(R.string.preference_file_key), getString(R.string.trips_array));


        return rootView;
    }

    /**
     * Method for returning a trip object containing the latest created trip.
     * It fetches the array list of all the trips on the device, and returns only
     * the first element on the list, if it exists.
     * @return Trip object
     */
    public static Trip getCurrentTrip(Context context, String prefs, String tripsArray) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(prefs, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String tripArray = sharedPreferences.getString(tripsArray, "[]");
        if(!tripArray.isEmpty()){
            ArrayList<Trip> allTrips = gson.fromJson(tripArray, new TypeToken<ArrayList<Trip>>(){}.getType());
            Trip ret = allTrips.get(allTrips.size() - 1);
            return ret;
        } else return null;


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
}
