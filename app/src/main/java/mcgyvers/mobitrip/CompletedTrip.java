package mcgyvers.mobitrip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Button;
import android.widget.TextView;

public class CompletedTrip extends AppCompatActivity {


    TextView fromTV,toTV, fromSHOW,toSHOW,dateTV,dateSHOW,teamsizeTV,teamsizeSHOW,totalexpenseTV,totalexpenseSHOW,avgTXT,avgSHOW;
    Button memberDetails;
    RecyclerView takenPics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_trip);

        fromTV = (TextView) findViewById(R.id.origin);
        toTV = (TextView) findViewById(R.id.destination_);
        fromSHOW = (TextView) findViewById(R.id.originTXT);
        toSHOW = (TextView) findViewById(R.id.destination_TXT);
        dateTV = (TextView) findViewById(R.id.dateTXT);
        dateSHOW = (TextView) findViewById(R.id.show_trip_date);
        teamsizeTV = (TextView) findViewById(R.id.total_membersTXT);
        teamsizeSHOW = (TextView) findViewById(R.id.show_trip_total_member);
        totalexpenseTV = (TextView) findViewById(R.id.total_expenseTXT);
        totalexpenseSHOW = (TextView) findViewById(R.id.show_trip_total_expense);
        avgTXT = (TextView) findViewById(R.id.avg_expense);
        avgSHOW = (TextView) findViewById(R.id.show_avg_expense);

        memberDetails = (Button) findViewById(R.id.membersDetails);
        takenPics = (RecyclerView) findViewById(R.id.trip_pictures);


        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        takenPics.setLayoutManager(staggeredGridLayoutManager);





    }
}
