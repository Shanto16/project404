package mcgyvers.mobitrip.dataModels;

import java.util.ArrayList;

/**
 * Created by edson on 09/09/17.
 * base object for the trips
 */

public class Trip {

    //TODO: add in the commented fields on the other structures
    private String tripId;
    private String origin;
    private String destination;
    private String coverPic;
    private Integer amount;
    private Integer commonExp;
    private String date;
    private boolean completed;

    private AtPlace destPlace;
    private AtPlace originPlace;


    private ArrayList<Member> members;

    public Trip(){}

    public Trip(String origin, String destination, Integer amount, Integer commonExp, ArrayList<Member> Members, String date, String tripId){
        //programatically generate tripId
        this.origin = origin;
        this.destination = destination;
        this.amount = amount;
        this.commonExp = commonExp;
        this.members = Members;
        this.date = date;
        this.setCompleted(false);
        this.members = new ArrayList<>();
        this.tripId = tripId; //later on generate them upon checking the local storage
    }

    public void setDestPlace(AtPlace destPlace){ this.destPlace = destPlace;}
    public void setOriginPlace(AtPlace originPlace){ this.originPlace = originPlace;}

    public AtPlace getDestPlace(){return this.destPlace;}
    public AtPlace getOriginPlace(){return this.originPlace;}

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getCommonExp() {
        return commonExp;
    }

    public void setCommonExp(Integer commonExp) {
        this.commonExp = commonExp;
    }

    public ArrayList<Member> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<Member> Members) {
        this.members = Members;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCoverPic() {
        return coverPic;
    }

    public void setCoverPic(String coverPic) {
        this.coverPic = coverPic;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
