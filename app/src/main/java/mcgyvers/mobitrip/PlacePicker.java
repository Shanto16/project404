package mcgyvers.mobitrip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

public class PlacePicker extends AppCompatActivity {

    EditText placePicker;
    RecyclerView places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);


        placePicker = (EditText) findViewById(R.id.placePickerEditText);
        places = (RecyclerView) findViewById(R.id.placePickerRecyclerView);




    }
}
