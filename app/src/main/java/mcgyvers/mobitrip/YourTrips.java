package mcgyvers.mobitrip;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import mcgyvers.mobitrip.adapters.TripData;
import mcgyvers.mobitrip.dataModels.Trip;

/**
 * Created by Shanto on 9/6/2017.
 */

public class YourTrips extends Fragment {

    RecyclerView my_trips,my_upcoming_trips;
    TextView upcoming,completed;


    //****FOR MODEL****
    ImageView trip_bg;
    TextView trip_name;
    TextView trip_dateStart,trip_dateEnd;
    TextView trip_members;
    TextView trip_expense;
    TextView date_txt,avg_txt;

    private ArrayList<Trip> tripList = new ArrayList<>();
    private TripData mAdaper;
    LinearLayoutManager layoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View rootView = inflater.inflate(R.layout.fragment_your_trips, container, false);


        my_trips = rootView.findViewById(R.id.my_trips_recyclerView);
        my_upcoming_trips = rootView.findViewById(R.id.my_upcoming_trips_recyclerView);
        upcoming = rootView.findViewById(R.id.upcomingTXT);
        completed = rootView.findViewById(R.id.completedTXT);

        trip_bg = rootView.findViewById(R.id.bg_trip);
        trip_dateStart = rootView.findViewById(R.id.tour_date_start);
        trip_dateEnd = rootView.findViewById(R.id.tour_date_end);
        trip_name = rootView.findViewById(R.id.tour_route);
        trip_members = rootView.findViewById(R.id.tour_team_size);
        trip_expense = rootView.findViewById(R.id.tour_avg_expense);
        date_txt = rootView.findViewById(R.id.tour_date_text);


        Typeface firaSans_medium = Typeface.createFromAsset(getActivity().getAssets(),"fonts/FiraSans-Medium.ttf");
        Typeface firaSans_semiBold = Typeface.createFromAsset(getActivity().getAssets(),"fonts/FiraSans-Medium.ttf");
        Typeface amaranth = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Amaranth-Bold.ttf");
        upcoming.setTypeface(firaSans_semiBold);
        completed.setTypeface(firaSans_semiBold);


        mAdaper = new TripData(tripList, getContext());
        layoutManager = new LinearLayoutManager(getContext());
        my_trips.setLayoutManager(layoutManager);
        my_trips.setItemAnimator(new DefaultItemAnimator());
        my_trips.setAdapter(mAdaper);

        getData();

        return rootView;
    }

    private void getData() {
        SharedPreferences sharedPreferences =getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String data = sharedPreferences.getString(getString(R.string.trips_array), "[]");
        Gson gson = new Gson();
        ArrayList<Trip> getAllTrips = gson.fromJson(data, new TypeToken<ArrayList<Trip>>(){}.getType());
        tripList.addAll(getAllTrips);
        //System.out.println("Total trips: "+tripList.size());
        //System.out.println(tripList.toString());
        //System.out.println(tripList.get(0).getOrigin());
        mAdaper.notifyDataSetChanged();
        System.out.println("adapter has: " + mAdaper.getItemCount() + " objects");

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sort, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sort_asc:
                Toast.makeText(getActivity(), "Order by ascending", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.sort_desc:
                Toast.makeText(getActivity(), "Order by descending", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
