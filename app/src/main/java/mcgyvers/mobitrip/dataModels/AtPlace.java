package mcgyvers.mobitrip.dataModels;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by edson on 28/10/17.
 */

public class AtPlace {
    private CharSequence placeId;
    private CharSequence description;
    private CharSequence address;

    private LatLng coords;



    public AtPlace(CharSequence placeId, CharSequence description, CharSequence address) {
        this.placeId = placeId;
        this.description = description;
        this.address = address;

    }

    public void setCoords(LatLng coords){
        this.coords = coords;
    }

    public double getLatitude(){ return coords.latitude; }

    public double getLongitude(){ return coords.longitude; }

    public LatLng getCoords(){ return coords; }

    public String getPlaceId(){ return placeId.toString(); }

    public String getName(){
        return description.toString();
    }

    public String getAdress(){
        return address.toString();
    }

}
