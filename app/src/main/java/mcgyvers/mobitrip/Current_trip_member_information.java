package mcgyvers.mobitrip;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.lang.reflect.Type;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import mcgyvers.mobitrip.adapters.MemberData;
import mcgyvers.mobitrip.dataModels.Member;
import mcgyvers.mobitrip.dataModels.Trip;

public class Current_trip_member_information extends AppCompatActivity implements MemberData.onItemClickListener{

    Button save,cancel;
    RecyclerView membersInformation;



    MaterialEditText name,contact_num,amount;
    ImageButton add_now;


    //**********************************************
     // MODEL ITEMS TO BE USED IN ADAPTER

    MaterialEditText memberName,memberAmount,memberPhone;
    ImageView deleteMember;

    RelativeLayout memberCard;
    //********************************************

    MemberData mAdapter;
    ArrayList<Member> memberList = new ArrayList<>();
    LinearLayoutManager layoutManager;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    //Trip currentTrip;
    //ArrayList<Trip> cTrips = new ArrayList<>();
    //int tripId;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_trip_member_information);
        ActionBar actionBar = getActionBar();


        membersInformation = (RecyclerView) findViewById(R.id.member_information_recycler);
        membersInformation.setItemAnimator(new SlideInDownAnimator());
        membersInformation.getItemAnimator().setAddDuration(4000);

        save = (Button) findViewById(R.id.save_members);
        cancel = (Button) findViewById(R.id.cancel_members);

        memberCard = (RelativeLayout) findViewById(R.id.membercard);

        //*****************Recyclerview Model**************************
        memberName = (MaterialEditText) findViewById(R.id.model_member_name);
        memberAmount = (MaterialEditText) findViewById(R.id.model_member_amount);
        memberPhone = (MaterialEditText) findViewById(R.id.model_member_phone);
        deleteMember = (ImageView) findViewById(R.id.delete_model);




        //*****************FIXED FIELDS**************************
        name = (MaterialEditText) findViewById(R.id.fixed_model_member_name);
        contact_num = (MaterialEditText) findViewById(R.id.fixed_model_member_phone);
        amount = (MaterialEditText) findViewById(R.id.fixed_model_member_amount);
        add_now = (ImageButton) findViewById(R.id.fixed_add_model);






        mAdapter = new MemberData(memberList, getApplicationContext(), this);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        membersInformation.setLayoutManager(layoutManager);
        membersInformation.setItemAnimator(new DefaultItemAnimator());
        membersInformation.setAdapter(mAdapter);

         sharedPreferences = getApplicationContext().getSharedPreferences(MainActivity.TMP_PREFS, Context.MODE_PRIVATE);
         //editor = sharedPreferences.edit();

        //getCurrentTrip();
        memberCard.setVisibility(View.VISIBLE);//


        add_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //here we add members to the temporary list of members

                String nameS = name.getText().toString();
                String phoneS = contact_num.getText().toString();
                String amountS = amount.getText().toString();


                if(nameS.isEmpty() || amountS.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please fill all the member information fields", Toast.LENGTH_LONG).show();
                }else{
                    addNewMember(nameS, phoneS, amountS);
                }
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //here we save the added members on the memory
                saveItAll();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //here we just go back to the previous activity
                finish();
            }
        });


        if(sharedPreferences.getString(MainActivity.TRIP_EDIT, "") != ""){
            memberCard.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
        }

        setData();


    }

    /**
     * save all the data referring to members of the trip to the TMP_PREFS temporary file
     * for posterior handling
     */
    private void saveItAll() {
        // we'll write the data to the temporary file
        editor = sharedPreferences.edit();


        Gson gson = new Gson();

        String membersArray = gson.toJson(memberList, new TypeToken<ArrayList<Member>>(){}.getType());
        editor.putString(MainActivity.MEMBERS, membersArray);
        editor.apply();

        System.out.println(membersArray);
        Toast.makeText(getApplicationContext(), "Users saved!", Toast.LENGTH_LONG).show();
        finish();


    }

    /**
     * Creates a member object and add it to the recyclerView adapter
     * erasing the data fields afterwards
     * @param nameS name of the user
     * @param phoneS phone number of user
     * @param amountS amount of money
     */
    private void addNewMember(String nameS, String phoneS, String amountS ) {
        //TODO: check for empty variables and make sure they cont cause crashes
        Member member = new Member(nameS, phoneS, amountS);
        int pos = memberList.size();
        memberList.add(member);
        mAdapter.notifyItemInserted(pos); // does this work?
        name.setText("");
        contact_num.setText("");
        amount.setText("");
        Toast.makeText(getApplicationContext(), "Member added", Toast.LENGTH_LONG).show();
        name.setFocusable(true);
        name.requestFocus();
        System.out.println(memberList);
    }


    /**
     * gets the list of members currently registered on the TEMP_PREFS temporary local
     * storage file for the trip being set
     */
    private void setData() {

        String data = sharedPreferences.getString(MainActivity.MEMBERS, "[]");
        memberList.addAll(getMembers(getApplicationContext()));
        //Gson gson = new Gson();
        //ArrayList<Member> m = gson.fromJson(data,new TypeToken<ArrayList<Member>>(){}.getType());
        //memberList.addAll(m);


        //memberList.addAll(currentTrip.getMembers());
        mAdapter.notifyDataSetChanged();
        System.out.println("list of members are " + memberList.size());
        System.out.println("members are now " +  mAdapter.getItemCount());


    }

    /**
     * retrieves the member array from the TMP_PREFS temporary local storage file
     * @param context current application context
     * @return array list of member objects
     */
    public static ArrayList<Member> getMembers(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.TMP_PREFS, Context.MODE_PRIVATE);
        String data = sharedPreferences.getString(MainActivity.MEMBERS, "[]");
        Gson gson = new Gson();
        return gson.fromJson(data,new TypeToken<ArrayList<Member>>(){}.getType());



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

    //callback to handle the deletion of members
    @Override
    public void callback(int pos) {
        memberList.remove(pos);
        mAdapter.notifyItemRemoved(pos);
    }
}
