package mcgyvers.mobitrip;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.timqi.sectorprogressview.ColorfulRingProgressView;

import static mcgyvers.mobitrip.R.id.toolbar;

/**
 * Created by Shanto on 9/7/2017.
 */

public class Current_trip extends Fragment {

    float max = 5000, min = 2045;

    float pcnt = (min / max) * 100;

    TextView _expense,spent,currentLocation, temp,weather;
    ColorfulRingProgressView expenseProgress;
    Toolbar mToolbar;


    Button team_expense, my_expense,hospitals,policeStation,maps,camera,restaurants,hotels,fuel,spots,dismiss,finish;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View rootView = inflater.inflate(R.layout.fragment_current_trip, container, false);
        final Context context = getActivity();
        mToolbar = rootView.findViewById(R.id.toolbar);

        getActivity().supportInvalidateOptionsMenu();




        Typeface regular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Regular.ttf");
        Typeface bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Bold.ttf");



        team_expense = rootView.findViewById(R.id.team_expense_button);
        my_expense = rootView.findViewById(R.id.my_expense_button);
        expenseProgress = rootView.findViewById(R.id.expense_progressbar);
        _expense = rootView.findViewById(R.id.pcnt_expense);
        spent = rootView.findViewById(R.id.spent_card);
        currentLocation = rootView.findViewById(R.id.current_location_text);
        temp = rootView.findViewById(R.id.temp_text);
        weather = rootView.findViewById(R.id.weather_text);

        hospitals = rootView.findViewById(R.id.nav_hospitals);
        policeStation = rootView.findViewById(R.id.nav_police);
        maps = rootView.findViewById(R.id.nav_maps);
        camera = rootView.findViewById(R.id.cam_current);
        restaurants = rootView.findViewById(R.id.nav_restaurants);
        hotels = rootView.findViewById(R.id.nav_hotels);
        fuel = rootView.findViewById(R.id.nav_fuel);
        spots = rootView.findViewById(R.id.nav_spots);
        dismiss = rootView.findViewById(R.id.nav_dismiss);
        finish = rootView.findViewById(R.id.nav_finish);






        hospitals.setTransformationMethod(null);
        policeStation.setTransformationMethod(null);
        maps.setTransformationMethod(null);
        camera.setTransformationMethod(null);
        restaurants.setTransformationMethod(null);
        hotels.setTransformationMethod(null);
        fuel.setTransformationMethod(null);
        spots.setTransformationMethod(null);
        dismiss.setTransformationMethod(null);
        finish.setTransformationMethod(null);
        team_expense.setTransformationMethod(null);
        my_expense.setTransformationMethod(null);
        camera.setTransformationMethod(null);








        hospitals.setTypeface(regular);
        policeStation.setTypeface(regular);
        maps.setTypeface(regular);
        camera.setTypeface(regular);
        restaurants.setTypeface(regular);
        hotels.setTypeface(regular);
        fuel.setTypeface(regular);
        spots.setTypeface(regular);
        dismiss.setTypeface(regular);
        finish.setTypeface(regular);
        team_expense.setTypeface(regular);
        my_expense.setTypeface(regular);
        weather.setTypeface(regular);
        temp.setTypeface(regular);
        currentLocation.setTypeface(regular);
        camera.setTypeface(regular);


        _expense.setTypeface(bold);
        spent.setTypeface(bold);





        hospitals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("POI", "Hospital");
                startActivity(intent);
            }
        });

        policeStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("POI", "Police");
                startActivity(intent);
            }
        });

        restaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("POI", "Restaurant");
                startActivity(intent);
            }
        });

        hotels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("POI", "Hotel");
                startActivity(intent);
            }
        });

        fuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("POI", "Fuel");
                startActivity(intent);
            }
        });

        spots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("POI", "Tourism");
                startActivity(intent);
            }
        });


        expenseProgress.setPercent(pcnt);
        _expense.setText(Float.toString(pcnt)+"%");

        my_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.my_expense_dialog);
                dialog.setTitle("My Expense");


                Button save_button = dialog.findViewById(R.id.my_expense_save);

                save_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


                ImageView addExpense = dialog.findViewById(R.id.add_new_expense);
                addExpense.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "A new 'my_expense_model.xml' will be added in this listView here. ", Toast.LENGTH_LONG).show();
                    }
                });


                dialog.show();

            }
        });


        team_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.model_team_expense);
                dialog.setTitle("Team Expense");

                ListView team_expense = dialog.findViewById(R.id.team_expense_listview);
                Button ok_button = dialog.findViewById(R.id.team_expense_dismiss);

                ok_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();
                    }
                });


                dialog.show();


            }
        });

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("POI", "");
                startActivity(intent);
            }
        });


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_current_trip, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.sos);
        item.setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sos:
                Toast.makeText(getActivity(), "a pop up screen will be opened here asking for an emergency number", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
