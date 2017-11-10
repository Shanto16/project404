package mcgyvers.mobitrip.dataModels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import static java.io.FileDescriptor.in;

/**
 * Created by edson on 28/10/17.
 */

public class AtPlace implements Parcelable {

    private String placeId;
    private String description;
    private String address;

    private Double longitude;
    private Double latitude;



    public AtPlace(String placeId, String description, String address) {
        this.placeId = placeId;
        this.description = description;
        this.address = address;

    }

    public AtPlace(){}

    public double getLatitude(){ return latitude; }

    public double getLongitude(){ return longitude; }

    public void setLatitude(Double latitude){ this.latitude = latitude;}

    public void setLongitude(Double longitude) { this.longitude = longitude;}

    public String getPlaceId(){ return placeId.toString(); }

    public String getName(){
        return description.toString();
    }

    public String getAdress(){
        return address.toString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placeId);
        dest.writeString(description);
        dest.writeString(address);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);

    }

    public static final  Parcelable.Creator<AtPlace> CREATOR = new Creator<AtPlace>() {
        @Override
        public AtPlace createFromParcel(Parcel source) {
            AtPlace atPlace = new AtPlace();
            atPlace.placeId = source.readString();
            atPlace.description = source.readString();
            atPlace.address = source.readString();
            atPlace.longitude = source.readDouble();
            atPlace.latitude = source.readDouble();
            return atPlace;
        }

        @Override
        public AtPlace[] newArray(int size) {
            return new AtPlace[0];
        }
    };
}
