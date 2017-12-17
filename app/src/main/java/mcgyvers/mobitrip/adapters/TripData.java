package mcgyvers.mobitrip.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mcgyvers.mobitrip.CompletedTrip;
import mcgyvers.mobitrip.Current_trip_member_information;
import mcgyvers.mobitrip.MainActivity;
import mcgyvers.mobitrip.NewTrip;
import mcgyvers.mobitrip.R;
import mcgyvers.mobitrip.User;
import mcgyvers.mobitrip.dataModels.Member;
import mcgyvers.mobitrip.dataModels.Trip;

/**
 * Created by edson on 13/09/17.
 * adapter for YourTrips recyclerView
 */

public class TripData extends RecyclerView.Adapter<TripData.MyViewHolder> {

    private ArrayList<Trip> trips;
    private boolean onBind;

    User mUser = User.getInstance();
    private Context context;
    FragmentManager fm;

    public TripData(ArrayList<Trip> trips, Context context){
        this.trips = trips;
        this.context = context;
        System.out.println("creating adapter...");
        System.out.println("adapter trip origin: ");
//        System.out.println(trips.get(0).getOrigin());
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.my_trips_model, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Integer pos = position;
        final Trip trip = trips.get(pos);
        System.out.println("adapter trip origin: ");
        System.out.println(trip.getOrigin());
        System.out.println("trip date: ");
        System.out.println(trip.getDate());

        if(trip.getOriginPlace() != null){
            holder.trip_route.setText("From "+trip.getOriginPlace().getName().toUpperCase() + " to " + trip.getDestPlace().getName().toUpperCase());
        }else holder.trip_route.setText(trip.getOrigin() + " - " + trip.getDestination());

        holder.trip_dateStart.setText(trip.getDate());
        
        String s = "Avg. expense per person: $"+String.valueOf(trip.getAmount());
        holder.trip_expense.setText(s);
        holder.trip_members.setText("Team size: "+String.valueOf(trip.getMembers().size()));

        holder.trip_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //context.startActivity(new Intent(context, CompletedTrip.class));
                //context.startActivity(new Intent(context, Current_trip_member_information.class));
                editTrip(pos, trip);

            }
        });
        //holder.trip_bg.setImageResource();//

        if(trip.getName() != ""){
            holder.tour_name.setText(trip.getName());
        }else{
            holder.tour_name.setText("From "+trip.getOriginPlace().getName().toUpperCase() + " to " + trip.getDestPlace().getName().toUpperCase());
        }



    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public ArrayList<Trip> getTrips(){
        return this.trips;
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder {
        ImageView trip_bg;
        TextView trip_route;
        TextView trip_dateStart,trip_dateEnd;
        TextView trip_members;
        TextView trip_expense;
        TextView date_txt;
        TextView tour_name;


        public MyViewHolder(View view) {
            super(view);

            System.out.println("creating objects");
            trip_bg = view.findViewById(R.id.bg_trip);
            trip_dateStart = view.findViewById(R.id.tour_date_start);
            trip_dateEnd = view.findViewById(R.id.tour_date_end);
            trip_route = view.findViewById(R.id.tour_route);
            trip_members = view.findViewById(R.id.tour_team_size);
            trip_expense = view.findViewById(R.id.tour_avg_expense);
            date_txt = view.findViewById(R.id.tour_date_text);
            tour_name = view.findViewById(R.id.tour_name);

            Typeface regular = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Regular.ttf");
            Typeface firaSans_medium = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/FiraSans-Medium.ttf");
            Typeface firaSans_semiBold = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/FiraSans-Medium.ttf");

            trip_route.setTypeface(firaSans_medium);
            trip_members.setTypeface(firaSans_medium);
            trip_expense.setTypeface(firaSans_medium);
            date_txt.setTypeface(firaSans_medium);
            tour_name.setTypeface(firaSans_semiBold);






        }
    }


    /**
     * EDITING TRIPS
     *
     * This method assings the clicked trip to the 'TRIP_EDIT' edit field of
     * the TMP_PREFS local storage field for temporary assingment on NewTrip.class
     * and brings the app to the NewTrip.class with the informations of the
     * trip in question to be edited.
     *
     * @param pos position of the trip in the original list of trips
     * @param trip Trip object to be edited
     */
    public void editTrip(int pos, Trip trip){

        SharedPreferences tmpShared = this.context.getSharedPreferences(MainActivity.TMP_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor tmpEditor = tmpShared.edit();

        Gson gson = new Gson();
        try {
            JSONObject editTrip = new JSONObject(gson.toJson(trip));
            tmpEditor.putString(MainActivity.TRIP_EDIT, editTrip.toString());
            tmpEditor.putInt(MainActivity.TRIP_EDIT_POS, pos);
            String membersArray = gson.toJson(trip.getMembers(), new TypeToken<ArrayList<Member>>(){}.getType());
            tmpEditor.putString(MainActivity.MEMBERS, membersArray);
            tmpEditor.apply();

            Intent intent = new Intent(context, MainActivity.class);
            Bundle b = new Bundle();
            b.putInt("fragToLoad", R.id.nav_new_trips);
            intent.putExtras(b);
            context.startActivity(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
