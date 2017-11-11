package mcgyvers.mobitrip.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import mcgyvers.mobitrip.CompletedTrip;
import mcgyvers.mobitrip.Current_trip_member_information;
import mcgyvers.mobitrip.R;
import mcgyvers.mobitrip.User;
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

        if(trip.getOriginPlace() != null){
            holder.trip_name.setText(trip.getOriginPlace().getName() + " - " + trip.getDestPlace().getName());
        }else holder.trip_name.setText(trip.getOrigin() + " - " + trip.getDestination());

        holder.trip_date.setText(trip.getDate());
        
        String s = "$"+String.valueOf(trip.getAmount());
        holder.trip_expense.setText(s);
        holder.trip_members.setText(String.valueOf(trip.getMembers().size()));

        holder.trip_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, CompletedTrip.class));
                //context.startActivity(new Intent(context, Current_trip_member_information.class));
            }
        });
        //holder.trip_bg.setImageResource();//


    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public ArrayList<Trip> getTrips(){
        return this.trips;
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder {
        public ImageView trip_bg;
        public TextView trip_name;
        public TextView trip_date;
        public TextView trip_members;
        public TextView trip_expense;
        public TextView date_txt;
        public TextView avg_txt;


        public MyViewHolder(View view) {
            super(view);

            System.out.println("creating objects");
            trip_bg = view.findViewById(R.id.bg_trip);
            trip_date = view.findViewById(R.id.tour_date);
            trip_name = view.findViewById(R.id.tour_name);
            trip_members = view.findViewById(R.id.trip_total_member);
            trip_expense = view.findViewById(R.id.trip_expense_per_head);
            date_txt = view.findViewById(R.id.tour_date_txt);
            avg_txt = view.findViewById(R.id.ic_expense_per_head);

            Typeface regular = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Regular.ttf");
            trip_date.setTypeface(regular);
            trip_name.setTypeface(regular);
            trip_members.setTypeface(regular);
            trip_expense.setTypeface(regular);
            date_txt.setTypeface(regular);
            avg_txt.setTypeface(regular);






        }
    }
}
