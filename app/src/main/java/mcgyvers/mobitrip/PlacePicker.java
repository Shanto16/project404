package mcgyvers.mobitrip;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import mcgyvers.mobitrip.adapters.ATAdapter;
import mcgyvers.mobitrip.dataModels.AtPlace;

public class PlacePicker extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, ATAdapter.onItemClickListener{

    EditText placePicker;
    RecyclerView places;

    protected GoogleApiClient googleApiClient;

    private static final LatLngBounds myBounds = new LatLngBounds(new LatLng(-0,0), new LatLng(0, 0));

    private ATAdapter mAtAdapter;
    private LinearLayoutManager layoutManager;
    public AutocompleteFilter filter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);


        placePicker = (EditText) findViewById(R.id.placePickerEditText);
        places = (RecyclerView) findViewById(R.id.placePickerRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        buildGoogleAPIClient();

        mAtAdapter = new ATAdapter(getApplicationContext(), googleApiClient, myBounds, null, this);

        places.setLayoutManager(layoutManager);
        places.setItemAnimator(new DefaultItemAnimator());
        places.setAdapter(mAtAdapter);



        placePicker.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().equals("") && googleApiClient.isConnected()){
                    mAtAdapter.getFilter().filter(charSequence.toString());

                } else if(!googleApiClient.isConnected()){
                    Toast.makeText(getApplicationContext(), "Can't fetch new locations", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



    }

    protected synchronized void buildGoogleAPIClient(){
        // initilizing google API client
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // when activity comes back from pause and resumes the API client from google
        // must reconnect
        if(!googleApiClient.isConnected() && !googleApiClient.isConnecting()){
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // if activity goes out of focus (pauses or suspends), the API is disconnected
        if(googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
    }




    /**
     * callback from the click listener on the ATAdapter adapter, returns an AtPlace object
     * containing name, address and coordinates for the selected item,
     * @param place
     */
    @Override
    public void callback(AtPlace place) {
        //WHATS HAPPENING HEEEERE?!
        Intent i = getIntent();
        Intent intent = new Intent(getApplicationContext(), NewTrip.class);
        //intent.putExtra("way", i.getStringExtra("way"));
        //intent.putExtra("place latitude", place.getLatitude());
        //intent.putExtra("place longitude", place.getLongitude());
        //intent.putExtra("place name", place.toString());
        //intent.putExtra("place address", place.getAdress());
        if(googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }


        Toast.makeText(getApplicationContext(), "place latitude: " + place.getLatitude() + "\n" +
                "place longitude: " + place.getLongitude() + "\n" +
                "place name: " + place.getName() + "\n" +
                "address: " + place.getAdress(), Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}
