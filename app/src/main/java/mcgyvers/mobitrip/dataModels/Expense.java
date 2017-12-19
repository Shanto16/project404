package mcgyvers.mobitrip.dataModels;

import android.graphics.Typeface;

/**
 * Created by edson on 26/11/17.
 *
 * Object that describes an expense
 */

public class Expense {

    private String name;
    private String cost;

    public Expense(){}

    public Expense(String name, String cost){
        this.name = name;
        this.cost = cost;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
