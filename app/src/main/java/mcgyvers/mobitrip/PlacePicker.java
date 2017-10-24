package mcgyvers.mobitrip;

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

public class PlacePicker extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

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

        mAtAdapter = new ATAdapter(getApplicationContext(), googleApiClient, myBounds, null);

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
        if(!googleApiClient.isConnected() && !googleApiClient.isConnecting()){
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
    }
}
