package mcgyvers.mobitrip;

import android.content.Context;

/**
 * Created by edson on 12/09/17.
 * user class
 */

public class User {

    private static User instance = null;

    private String phoneNumber;

    //a private constructor so no instances can be made outside this class
    private User(){}

    //Everytime you need an instance, call this
    //synchronized to make the call thread-safe
    public static synchronized User getInstance(){
        if(instance == null)
            instance = new User();

        return instance;
    }

    //Initialize this or any other variables in probably the Application class
    public void initUser(Context context){
        //TODO: create local storage for user data

    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
