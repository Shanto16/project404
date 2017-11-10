package mcgyvers.mobitrip.adapters;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import mcgyvers.mobitrip.NewTrip;
import mcgyvers.mobitrip.R;
import mcgyvers.mobitrip.dataModels.AtPlace;


/**
 * Created by edson on 21/10/17.
 */

public class ATAdapter extends RecyclerView.Adapter<ATAdapter.PredictionHolder> implements Filterable{

    private ArrayList<AtPlace> myResultList;
    private GoogleApiClient myApiClient;
    private LatLngBounds myBounds;
    private AutocompleteFilter myACFilter;
    private final onItemClickListener clickListener;
    private Context context;

    public ATAdapter(Context context,  GoogleApiClient googleApiClient, LatLngBounds latLngBounds, AutocompleteFilter autocompleteFilter, onItemClickListener listener){


        this.context = context;
        this.myBounds = latLngBounds;
        this.myACFilter = autocompleteFilter;
        this.myApiClient = googleApiClient;
        this.clickListener = listener;
        this.myResultList = new ArrayList<>();

    };


    public void setMyBounds(LatLngBounds bounds){ this.myBounds = bounds;}


    public android.widget.Filter getFilter(){
        android.widget.Filter filter = new android.widget.Filter(){

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                if(charSequence != null){
                    myResultList = getAutoComplete(charSequence);
                    if(myResultList != null){
                        results.values = myResultList;
                        results.count = myResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                if(filterResults != null && filterResults.count > 0){
                    notifyDataSetChanged();
                } else {

                }

            }
        };
        return filter;
    }


    private ArrayList<AtPlace> getAutoComplete(CharSequence constraint){
        if(myApiClient.isConnected()){



            PendingResult<AutocompletePredictionBuffer> results = Places.GeoDataApi.getAutocompletePredictions(myApiClient, constraint.toString(), myBounds, myACFilter);

            AutocompletePredictionBuffer autocompletePredictions = results.await(60, TimeUnit.SECONDS);
            final Status status = autocompletePredictions.getStatus();

            if(!status.isSuccess()){
                Toast.makeText(this.context, "Error contacting API: " + status.toString(), Toast.LENGTH_SHORT).show();
                autocompletePredictions.release();
                return null;
            }

            Log.i("", "Query completed. Received " + autocompletePredictions.getCount() + " predictions");

            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());
            while (iterator.hasNext()){
                AutocompletePrediction prediction = iterator.next();
                resultList.add(new AtPlace(prediction.getPlaceId(), prediction.getPrimaryText(null).toString(), prediction.getSecondaryText(null).toString()));
            }

            autocompletePredictions.release();
            return resultList;
        }

        Log.i("", "Google API client is not connected for autocomplete query");
        return null;
    }

    @Override
    public ATAdapter.PredictionHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_place_picker, parent, false);
        return new PredictionHolder(itemView);
    }

    public class PredictionHolder extends RecyclerView.ViewHolder {

        TextView placeName;
        TextView placeAddress;
        RelativeLayout placeItem;

        public PredictionHolder(View itemView) {
            super(itemView);

            placeName = itemView.findViewById(R.id.TV_place_name);
            placeAddress = itemView.findViewById(R.id.TV_place_distance);
            placeItem = itemView.findViewById(R.id.placeItem);
        }
    }

    @Override
    public void onBindViewHolder(ATAdapter.PredictionHolder holder, int position) {

        final AtPlace place = myResultList.get(position);

        holder.placeName.setText(place.getName());
        holder.placeAddress.setText(place.getAdress());
        holder.placeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, "Fetching data...", Toast.LENGTH_SHORT);
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(myApiClient, place.getPlaceId());

                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        if(places.getStatus().isSuccess()){

                            Place mPlace = places.get(0);
                            LatLng queriedLocation = mPlace.getLatLng();
                            place.setLatitude(queriedLocation.latitude);
                            place.setLongitude(queriedLocation.longitude);


                            /*
                            Toast.makeText(context, "place latitude: " + queriedLocation.latitude + "\n" +
                                    "place longitude: " + queriedLocation.longitude + "\n" +
                                    "place name: " + place.toString() + "\n" +
                                    "address: " + place.getAdress(), Toast.LENGTH_SHORT).show();

                            */
                            places.release();
                            clickListener.callback(place);




                        }
                        places.release();
                    }
                });




            }
        });



    }



    @Override
    public int getItemCount() {
        return myResultList.size();
    }




    public interface onItemClickListener{

        public void callback(AtPlace place);
    }





}
