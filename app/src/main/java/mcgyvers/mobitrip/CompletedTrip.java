package mcgyvers.mobitrip;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

public class CompletedTrip extends AppCompatActivity {


    android.support.v7.widget.Toolbar completeToolbar;
    TextView tooltext;
    TextView tour_name,tour_date,tour_avg,tour_route,tour_common,tour_team,tour_expense_banner,tour_from,tour_to,tour_distance,pictures_title;
    RecyclerView trip_pictures;
    Button see_all_pics,more_expense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_trip);
        completeToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(completeToolbar);
        getSupportActionBar().setTitle("");
        tooltext = findViewById(R.id.toolbarTXT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tooltext.setText("Trip Details");


        tour_avg = findViewById(R.id.det_tour_avg);
        tour_name = findViewById(R.id.det_tour_name);
        tour_date = findViewById(R.id.det_tour_date);
        tour_route = findViewById(R.id.det_tour_route);
        tour_common = findViewById(R.id.det_tour_common);
        tour_team = findViewById(R.id.det_tour_member);
        tour_expense_banner = findViewById(R.id.det_expenseTitle);
        tour_from = findViewById(R.id.from_map);
        tour_to = findViewById(R.id.to_map);
        tour_distance = findViewById(R.id.distance_map);
        pictures_title=findViewById(R.id.det_pictures_title);
        see_all_pics = findViewById(R.id.det_show_all_pictures);
        more_expense = findViewById(R.id.det_more_expense);

        trip_pictures = findViewById(R.id.det_pictures_recyclerview);


        Typeface firaSans_medium = Typeface.createFromAsset(getAssets(),"fonts/FiraSans-Medium.ttf");
        Typeface firaSans_semiBold = Typeface.createFromAsset(getAssets(),"fonts/FiraSans-Medium.ttf");
        Typeface amaranth = Typeface.createFromAsset(getAssets(),"fonts/Amaranth-Bold.ttf");
        tooltext.setTypeface(amaranth);


        tour_name.setTypeface(firaSans_semiBold);
        tour_expense_banner.setTypeface(firaSans_semiBold);
        pictures_title.setTypeface(firaSans_semiBold);
        see_all_pics.setTypeface(firaSans_medium);
        more_expense.setTypeface(firaSans_semiBold);


        tour_avg.setTypeface(firaSans_medium);
        tour_date.setTypeface(firaSans_medium);
        tour_route.setTypeface(firaSans_medium);
        tour_common.setTypeface(firaSans_medium);
        tour_team.setTypeface(firaSans_medium);
        tour_from.setTypeface(firaSans_medium);
        tour_to.setTypeface(firaSans_medium);
        tour_distance.setTypeface(firaSans_medium);




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
