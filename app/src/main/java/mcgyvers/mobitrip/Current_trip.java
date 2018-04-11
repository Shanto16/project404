package mcgyvers.mobitrip;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.timqi.sectorprogressview.ColorfulRingProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import mcgyvers.mobitrip.Receivers.OnReceiverDevicesEvent;
import mcgyvers.mobitrip.Receivers.OnReceiverNetInfoEvent;
import mcgyvers.mobitrip.Receivers.WiFiDirectBroadcastReceiver;
import mcgyvers.mobitrip.adapters.MemberData;
import mcgyvers.mobitrip.dataModels.Expense;
import mcgyvers.mobitrip.dataModels.Member;
import mcgyvers.mobitrip.dataModels.Trip;
import mcgyvers.mobitrip.interfaces.P2pConstants;


/**
 * Created by Shanto on 9/7/2017.
 * Class for displaying data about the latest trip
 */

public class Current_trip extends Fragment implements MemberData.onItemClickListener{

    float max = 5000, min = 3800;

    float pcnt = (min / max) * 100;

    TextView _expense,spent,currentLocation, temp,weather;
    ColorfulRingProgressView expenseProgress,animated_progress_show;
    Toolbar mToolbar;

    Trip currentTrip = null;
    int currentPos = 0;
    int totalmExpenses = 0;

    //-----------wifi p2p----------//
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    BroadcastReceiver receiver;
    IntentFilter mIntentFilter;
    PeerListListener peerListListener;
    ArrayAdapter<String> peersNames;
    DevicesListAdapter devicesListAdapter;

    AlertDialog.Builder builder;
    AlertDialog peersDialog;
    ArrayList<WifiP2pDevice> devsList = new ArrayList<>();

    String port = "8888";
    String groupOwnerAddress = "";

    ArrayList<Wifip2pService> connectionThreads = new ArrayList<>();
    ArrayList<String> devicesConnected = new ArrayList<>();
    ArrayList<String> devAddrConnected = new ArrayList<>();
    //-----------------------------//

    String TAG = "current_trip";
    // adapter for handling expenses
    final ArrayList<Expense> expenses = new ArrayList<>();
    final ExpensesAdapter expensesAdapter = new ExpensesAdapter(expenses, this);

    // get a handle of the current trip local storage file


    private static final int MY_PERMISSION_REQUEST_LOCATION=1;

    Button team_expense, my_expense,hospitals,policeStation,maps,camera,restaurants,hotels,fuel,spots,finish;

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
        Typeface firaSans_medium = Typeface.createFromAsset(getActivity().getAssets(),"fonts/FiraSans-Medium.ttf");
        final Typeface firaSans_semiBold = Typeface.createFromAsset(getActivity().getAssets(),"fonts/FiraSans-Medium.ttf");
        final Typeface amaranth = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Amaranth-Bold.ttf");



        team_expense = rootView.findViewById(R.id.team_expense_button);
        my_expense = rootView.findViewById(R.id.my_expense_button);
        expenseProgress = rootView.findViewById(R.id.expense_progressbar);
        animated_progress_show = rootView.findViewById(R.id.animated_progress);
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
        finish = rootView.findViewById(R.id.nav_finish);






        if (ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.ACCESS_COARSE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)){
                ActivityCompat.requestPermissions(getActivity(),new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },MY_PERMISSION_REQUEST_LOCATION);
            }else{
                ActivityCompat.requestPermissions(getActivity(),new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },MY_PERMISSION_REQUEST_LOCATION);
            }
        } else{
            LocationManager locationManager  = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            try{
                currentLocation.setText(myLocation(location.getLatitude(),location.getLongitude()));
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getActivity(), "Location not found", Toast.LENGTH_SHORT).show();
                currentLocation.setText("N/A");
            }
        }




        hospitals.setTransformationMethod(null);
        policeStation.setTransformationMethod(null);
        maps.setTransformationMethod(null);
        camera.setTransformationMethod(null);
        restaurants.setTransformationMethod(null);
        hotels.setTransformationMethod(null);
        fuel.setTransformationMethod(null);
        spots.setTransformationMethod(null);
        finish.setTransformationMethod(null);
        team_expense.setTransformationMethod(null);
        my_expense.setTransformationMethod(null);
        camera.setTransformationMethod(null);



        hospitals.setTypeface(firaSans_semiBold);
        policeStation.setTypeface(firaSans_semiBold);
        maps.setTypeface(firaSans_semiBold);
        camera.setTypeface(firaSans_semiBold);
        restaurants.setTypeface(firaSans_semiBold);
        hotels.setTypeface(firaSans_semiBold);
        fuel.setTypeface(firaSans_semiBold);
        spots.setTypeface(firaSans_semiBold);
        finish.setTypeface(firaSans_semiBold);
        team_expense.setTypeface(firaSans_semiBold);
        my_expense.setTypeface(firaSans_semiBold);
        weather.setTypeface(firaSans_semiBold);
        temp.setTypeface(firaSans_semiBold);
        currentLocation.setTypeface(firaSans_semiBold);
        camera.setTypeface(firaSans_semiBold);


        _expense.setTypeface(amaranth);
        spent.setTypeface(amaranth);

        setPercentage();


        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // for lack of a better something to do here
                // we just clean the current trip file and
                // bring the user back to the 'your trips' list
                SharedPreferences currShared = getContext().getSharedPreferences(MainActivity.CURR_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor currEditor = currShared.edit();
                currEditor.clear();
                currEditor.apply();
                Toast.makeText(getContext(), "We should build a post trip page", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getContext(), MainActivity.class));

            }
        });




        hospitals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getContext(), MapsActivity.class);
                //intent.putExtra("POI", "Hospital");
                //startActivity(intent);

                sendMessage("Holla!",null);
            }
        });

        policeStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getContext(), MapsActivity.class);
                //intent.putExtra("POI", "Police");
                //startActivity(intent);

                discoverPs(channel);
                peersDialog = builder.create();
                peersDialog.show();
            }
        });

        restaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getContext(), MapsActivity.class);
                //intent.putExtra("POI", "Restaurant");
                //startActivity(intent);

                if(connectionThreads.size() > 0){
                    System.out.println(devicesConnected);
                    System.out.println(connectionThreads.get(0).deviceName);
                    System.out.println(connectionThreads.get(0).getState());
                }

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

                /*Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("POI", "Fuel");
                startActivity(intent); */

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

        my_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.my_expense_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setTitle("My Expense");




                ListView myExpenseList = dialog.findViewById(R.id.my_expense_listView);
                Button save_button = dialog.findViewById(R.id.my_expense_save);
                TextView mytxt = dialog.findViewById(R.id.myExpense_txt);
                mytxt.setTypeface(amaranth);
                save_button.setTypeface(firaSans_semiBold);
                save_button.setTransformationMethod(null);

                TextView mText = dialog.findViewById(R.id.totalTEXT);
                final TextView mTotal = dialog.findViewById(R.id.totalAMOUNT);
                mText.setText("Total: ");
                mText.setTypeface(firaSans_semiBold);
                mTotal.setTypeface(firaSans_semiBold);
                mTotal.setText(String.valueOf(expensesAdapter.totalExp()));



                myExpenseList.setAdapter(expensesAdapter);
                expensesAdapter.notifyDataSetChanged();


                if(currentTrip != null &&  currentTrip.getExpenses() != null){
                    if(expenses.isEmpty()) expenses.addAll(currentTrip.getExpenses());
                } else{
                    Toast.makeText(getContext(), "This trip has no expenses", Toast.LENGTH_SHORT).show();
                }




                save_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(currentTrip != null){
                            currentPos = getCurrentPos(getContext());
                            if(currentPos > -1){

                                // removing the blank objects
                                for(int i = expenses.size()-1; i >= 0; i--){
                                    if(expenses.get(i).getName().equals("") || expenses.get(i).getCost().equals("")){
                                        expenses.remove(i);
                                    }
                                }

                                expensesAdapter.notifyDataSetChanged();
                                currentTrip.setExpenses(expenses);
                                UpdateTripList(getContext(),currentTrip, currentPos);
                            }else{
                                Toast.makeText(getContext(), "Error adding expenses", Toast.LENGTH_LONG).show();
                            }
                        }

                        dialog.dismiss();
                    }
                });


                ImageView addExpense = dialog.findViewById(R.id.add_new_expense);
                addExpense.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mTotal.setText(String.valueOf(totalmExpenses));
                        Expense expense = new Expense("", "");

                        expenses.add(0, expense);
                        expensesAdapter.notifyDataSetChanged();



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
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setTitle("Team Expense");



                ListView team_expense = dialog.findViewById(R.id.team_expense_listview);
                Button ok_button = dialog.findViewById(R.id.team_expense_dismiss);
                TextView teamTXT = dialog.findViewById(R.id.teamExpense_txt);
                TextView nameTXT = dialog.findViewById(R.id.team_name);
                TextView expenseTXT = dialog.findViewById(R.id.team_expense);

                teamTXT.setTypeface(amaranth);
                ok_button.setTransformationMethod(null);
                ok_button.setTypeface(firaSans_semiBold);
               // nameTXT.setTypeface(firaSans_semiBold);
//                expenseTXT.setTypeface(firaSans_semiBold);

                final ArrayList<Member> members = new ArrayList<>();
                if(currentTrip != null && currentTrip.getMembers() != null){
                    members.addAll(currentTrip.getMembers());
                } else Toast.makeText(getContext(), "This trip has no members", Toast.LENGTH_SHORT).show();

                final MemberExpAdapter memberExpAdapter = new MemberExpAdapter(members);
                team_expense.setAdapter(memberExpAdapter);



                ok_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(currentTrip != null){
                            currentPos = getCurrentPos(getContext());
                            memberExpAdapter.notifyDataSetChanged();
                            currentTrip.setMembers(members);
                            UpdateTripList(getContext(),currentTrip, currentPos);
                        }


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


        currentTrip = getCurrentTrip(getContext());

        setupWifip2p();
        discoverPs(channel);





        return rootView;
    }

    /**
     * sets the visual percentage of the spent resources
     */
    private void setPercentage() {
        float exp = 0;

        if(currentTrip != null){
            exp =  (float) currentTrip.getAmount() / currentTrip.getCommonExp();
        }

        expenseProgress.setPercent(exp);
        _expense.setText(Float.toString(exp)+"%");
        animated_progress_show.setPercent(exp);
        animated_progress_show.animateIndeterminate(2500,null);

    }

    /**
     * initializes the components of the wifi p2p and sets up the broadcast receiver actions
     * as well as the dialog in which the peers are gonna be shown
     */
    private void setupWifip2p() {

        manager = (WifiP2pManager) getContext().getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(getContext(), Looper.getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this, peerListListener);



        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);



        peersNames = new ArrayAdapter<String>(getContext(), android.R.layout.simple_selectable_list_item);
        devicesListAdapter = new DevicesListAdapter(devsList, getContext(), new MemberData.onItemClickListener() {
            @Override
            public void callback(int pos) {
                connect(devsList.get(pos));
            }
        });



        builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Select Peers")
                .setAdapter(devicesListAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        peersDialog.dismiss();
                    }
                });

    }

    // search for peers
    private void discoverPs(WifiP2pManager.Channel channel){

        if(manager != null){
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {


                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(getContext(), "didnt find peers, try again later", Toast.LENGTH_LONG).show();

                }
            });
        }else{
            Toast.makeText(getContext(), "your device does not support wifi direct", Toast.LENGTH_LONG).show();
        }




    }



    /**
     * the app handles the "device discovered" event triggered by the
     * WiFiDirectBroadcastReceiver(PubSub).
     * @param event device discovered event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDevicesReceived(OnReceiverDevicesEvent event){
        //if(!devicesListAdapter.isEmpty()) devicesListAdapter.clear(); // clear old names


        devicesListAdapter.removeAll();
        Log.d("P2P", "Found something on events!");
        //Toast.makeText(getContext(), "Found something", Toast.LENGTH_LONG).show();
        Collection<WifiP2pDevice> devs = event.getDevices().getDeviceList();
        devsList.addAll(devs);



        for(int i = 0; i < devsList.size(); i++){

            if(!devicesListAdapter.hasItem(devsList.get(i))){
                Log.d("P2P", "Device Found: " + devsList.get(0).deviceName);
                devicesListAdapter.add(devsList.get(i).deviceName);
                devicesListAdapter.notifyDataSetChanged();
            }

        }


    }

    /**
     * Connects to a selected p2p device
     * @param device to be connected to
     */

    public void connect(final WifiP2pDevice device){
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // keep the name of the latest device connected to keep track
                devicesConnected.add(device.deviceName);
                devAddrConnected.add(device.deviceAddress);

            }


            @Override
            public void onFailure(int reason) {

                if(reason == WifiP2pManager.P2P_UNSUPPORTED){
                    Toast.makeText(getContext(), "P2P isn't supported on this device.",
                            Toast.LENGTH_SHORT).show();

                } else{
                    Toast.makeText(getContext(), "Connect failed. Retry.",
                            Toast.LENGTH_SHORT).show();
                }



            }
        });

    }


    /**
     * Disconnects from all connected peers, if the current phone is the group owner
     */
    public void disconnect() {
        if (manager != null && channel != null) {
            manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group != null && manager != null && channel != null
                            && group.isGroupOwner()) {
                        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "removeGroup onSuccess -");
                            }

                            @Override
                            public void onFailure(int reason) {
                                Log.d(TAG, "removeGroup onFailure -" + reason);
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * Handles the client-server data thread and
     * group negotiation once the peers are connected
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkInfoReceived(OnReceiverNetInfoEvent event){

        WifiP2pInfo info = event.getInfo();

        // InetAddress from WifiP2pInfo struc.
        groupOwnerAddress = info.groupOwnerAddress.getHostAddress();
        // String port = "8888";


        // After the group negotiation, we can determine the group owner
        // (server).
        if(info.groupFormed && info.isGroupOwner){
            Toast.makeText(getContext(), "Group formed. Host", Toast.LENGTH_SHORT).show();
            if(peersDialog.isShowing())peersDialog.dismiss();

            // starting a data thread with the latest connected device
            Wifip2pService mService = new Wifip2pService(getContext(), mHandler,port, true, groupOwnerAddress);
            // assign the name of the latest connect device to this thread so we keep track
            int i = devicesConnected.size() - 1;
            mService.deviceName = devicesConnected.get(i);
            mService.devAddr = devAddrConnected.get(i);
            connectionThreads.add(mService);
            mService.start();
            devicesListAdapter.notifyDataSetChanged();


            // One common case is creating a group owner thread and accepting
            // incoming connections.
        } else if(info.groupFormed){
            Toast.makeText(getContext(), "Connected as Peer", Toast.LENGTH_SHORT).show();
            if(peersDialog.isShowing())peersDialog.dismiss();

            // starting a data thread with the owner
            Wifip2pService mService = new Wifip2pService(getContext(), mHandler, port, false, groupOwnerAddress);
            mService.deviceName = devicesConnected.get(devicesConnected.size() - 1);
            connectionThreads.add(mService);
            mService.start();

        }

    }

    /**
     * method for sending a message to a particular device that is connected
     * to our user, or to broastcast to all the users in the network, if we are
     * the host.
     * @param msg String message to be send to the user(s).
     * @param targetDevice name of the recipient device. If null, a broadcast
     *                     will be sent to all connected peers.
     */
    private void sendMessage(String msg, String targetDevice){

        if(msg.length()  > 0){

            if(connectionThreads.size() == 0 ){
                // check if there are any threads before continuing
                Toast.makeText(getContext(), "No devices connected", Toast.LENGTH_SHORT).show();
                return;
            }

            if (targetDevice == null){
                // if no target device is specified, broadcast message to all
                // connected devices, if there are any

                for(int i = 0; i < connectionThreads.size(); i++){


                    if (connectionThreads.get(i).getState() == Wifip2pService.STATE_CONNECTED){
                        // Get the message bytes and tell the WifiP2pService to write
                        byte[] send = msg.getBytes();
                        connectionThreads.get(i).write(send);
                    }

                    else return;
                }
            }

            else{
                // send message to a specific device
                for(int i = 0; i < connectionThreads.size(); i++){

                    if (targetDevice.equals(connectionThreads.get(i).deviceName)){
                        if (connectionThreads.get(i).getState() == Wifip2pService.STATE_CONNECTED){
                            // Get the message bytes and tell the WifiP2pService to write
                            byte[] send = msg.getBytes();
                            connectionThreads.get(i).write(send);
                        }

                        else return;

                    } else Toast.makeText(getContext(), "No device connected",Toast.LENGTH_SHORT).show();


                }
            }
        }

    }



    /**
     * method for returning the ArrayList of trip objects containing all of the
     * trips already added by the user from the local storage
     * mainly for use in other functions.
     * @param context current activity context
     * @return Array list of trip objects
     */
    public static ArrayList<Trip> getTripList(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String tripArray = sharedPreferences.getString(context.getString(R.string.trips_array), "[]");

        ArrayList<Trip> allTrips = gson.fromJson(tripArray, new TypeToken<ArrayList<Trip>>(){}.getType());
        if(allTrips.size() > 0){
            return allTrips;
        } else return null;

    }

    /**
     * Method for returning a trip object containing the latest created trip.
     * It fetches the array list of all the trips on the device, and returns only
     * the first element on the list, if it exists. It also updates currentPos int
     * variable with the position of the current trip in the list
     *
     * Update(Feb/2018): it fetches only trips under the CURR_TRIP tag, no longer from the list of trips
     *
     * @param context current activity context
     * @return Trip object
     */
    public static Trip getCurrentTrip(Context context) {

        SharedPreferences currPrefs = context.getSharedPreferences(MainActivity.CURR_PREFS, Context.MODE_PRIVATE);
        String cur = currPrefs.getString(MainActivity.CURR_TRIP, "");

        Gson gson = new Gson();
        if(cur != ""){
            return gson.fromJson(cur, Trip.class);
        } else return null;

    }

    /**
     * Method for updating the list of trips, will be used when expenses are added on the
     * list of members.
     *
     * @param context current activity context
     * @param trip object to be modified in the list
     * @param pos position of the object in the list
     */
    public static void UpdateTripList(Context context, Trip trip, int pos){


        ArrayList<Trip> allTrips = getTripList(context);
        allTrips.set(pos, trip);

        String tripsArray = new Gson().toJson(allTrips);

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.trips_array), tripsArray);
        editor.apply();
        System.out.println(tripsArray);


    }

    public int getCurrentPos(Context context){
        ArrayList e = getTripList(context);
        if(e != null)
            return e.size() - 1;
        return -1;
    }

    @Override
    public void callback(int pos) {
        //currentTrip.getExpenses().remove(pos);
        expenses.remove(pos);
        expensesAdapter.notifyDataSetChanged();


    }





    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults){
        switch (requestCode){
            case MY_PERMISSION_REQUEST_LOCATION : {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        LocationManager locationManager  = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        try{
                            currentLocation.setText(myLocation(location.getLatitude(),location.getLongitude()));
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Location not found", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getActivity(), "Location permission is not granted!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
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

    //GETTING CURRENT CITY NAME
    public String myLocation(double latitude, double longitude){
        String myCity = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addressList ;
        try{
            addressList = geocoder.getFromLocation(latitude,longitude,1);

            if(addressList.size()>0){
                myCity = addressList.get(0).getLocality();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return myCity;
    }



    private static class ViewHolder {
        private EditText exp;
        private EditText cost;
        private TextWatcher costTextWatcher, expTextWatcher;
        private ImageView remove;

        private TextView memberName;
        private EditText memberExpense;
        private TextWatcher memberExpenseWatcher;
    }

    /**
     * Adapter for showing the users and their expenses on a dialog box
     */
    public class MemberExpAdapter extends BaseAdapter{

        private final ArrayList<Member> members;

        public MemberExpAdapter(ArrayList<Member> members){
            this.members = members;
        }

        @Override
        public int getCount() {
            return members.size();
        }

        @Override
        public Object getItem(int position) {
            return members.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Member member = members.get(position);

            View view = convertView;
            if(view == null){
                // not recycled, inflate a new view

                view = getLayoutInflater().inflate(R.layout.team_expense_model, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.memberExpense = view.findViewById(R.id.expense_expense_edit);
                viewHolder.memberName = view.findViewById(R.id.expense_name_text);
                view.setTag(viewHolder);



            }

            ViewHolder holder = (ViewHolder) view.getTag();

            holder.memberName.setText(member.getName());

            if(member.getExpense() == null){
                holder.memberExpense.setText("0");
            }else holder.memberExpense.setText(member.getExpense());

            // Remove any existing TextWatcher that will be keyed to the wrong ListItem
            if(holder.memberExpenseWatcher != null)
                holder.memberExpense.removeTextChangedListener(holder.memberExpenseWatcher);

            holder.memberExpenseWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    member.setExpense(s.toString());

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };

            holder.memberExpense.addTextChangedListener(holder.memberExpenseWatcher);




            return view;
        }
    }


    /**
     * Adapter for showing and editing the expenses of each user on a dialog box
     */
    public class ExpensesAdapter extends BaseAdapter{

        private ArrayList<Expense> expenses;
        private final MemberData.onItemClickListener listener;



        public ExpensesAdapter(ArrayList<Expense> expenses, MemberData.onItemClickListener listener){
            this.expenses = expenses;
            this.listener = listener;
        }

        @Override
        public int getCount() {
            return expenses.size();
        }

        @Override
        public Object getItem(int position) {
            return expenses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public Integer totalExp(){
            Integer exps = 0;

            for(int i = 0; i < expenses.size(); i++){
                if(!expenses.get(i).getName().equals("")){
                    Integer thisExps = Integer.valueOf(expenses.get(i).getCost());
                    exps += thisExps;
                }

            }
            System.out.println(String.valueOf(exps));

            return exps;

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            totalmExpenses = totalExp();
            currentTrip.setCommonExp(totalmExpenses);
            setPercentage();

            View view = convertView;
            if(view == null){
                // not recycled, inflate a new view

                view = getLayoutInflater().inflate(R.layout.my_expense_model, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.exp = view.findViewById(R.id.usedON);
                viewHolder.cost = view.findViewById(R.id.cost);
                viewHolder.remove = view.findViewById(R.id.remove_item_expense);
                view.setTag(viewHolder);



            }

            ViewHolder holder = (ViewHolder) view.getTag();
            // Remove any existing TextWatcher that will be keyed to the wrong ListItem
            if(holder.costTextWatcher != null)
                holder.cost.removeTextChangedListener(holder.costTextWatcher);

            if(holder.expTextWatcher != null)
                holder.exp.removeTextChangedListener(holder.expTextWatcher);




            final Expense expense = expenses.get(position);
            // Keep a reference to the TextWatcher so that we can remove it later
            holder.costTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    expense.setCost(s.toString());
                    totalmExpenses = totalExp();
                    currentTrip.setCommonExp(totalmExpenses);

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };

            holder.expTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    expense.setName(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };

            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.callback(position);

                }
            });


            holder.exp.addTextChangedListener(holder.expTextWatcher);
            holder.exp.setText(expense.getName());
            holder.cost.addTextChangedListener(holder.costTextWatcher);
            holder.cost.setText(expense.getCost());




            return view;

        }
    }

    /**
     * adapter for the devices list. A custom adapter had to be made
     * so we can have a click listener interface with the the main thread
     * so we can perform peer connection
     */
    class DevicesListAdapter extends ArrayAdapter<String>{

        ArrayList<WifiP2pDevice> devslist;
        private final MemberData.onItemClickListener listener;


        DevicesListAdapter(ArrayList<WifiP2pDevice> list, Context context, MemberData.onItemClickListener listener){
            super(context, R.layout.get_expense_model);
            this.devslist = list;
            this.listener = listener;
        }



        @Override
        public int getCount() {
            return devslist.size();
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            View row;

            if(convertView == null){
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.get_expense_model, parent, false);
            } else {
                row = convertView;
            }

            TextView dev = row.findViewById(R.id.device_name);
            TextView mc = row.findViewById(R.id.device_mac);
            Button gt = row.findViewById(R.id.get_expense_bt);
            gt.setText("Invite");


            dev.setText(devslist.get(position).deviceName);
            mc.setText(devslist.get(position).deviceAddress);
            gt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.callback(position);

                }
            });

            if(devicesConnected.size() > 0){
                for(int i = 0; i < devAddrConnected.size(); i++){
                    if(devslist.get(position).deviceAddress.equals(devAddrConnected.get(i))){
                        dev.setText(devicesConnected.get(i));
                        gt.setText("Connected");
                        gt.setTextColor(Color.BLUE);
                        gt.setEnabled(false);
                    }
                }
            }





            return row;
        }

        public boolean hasItem(WifiP2pDevice dev){
            for(int i = devslist.size(); i < 0; i--){
                if(dev.deviceName == devslist.get(i).deviceName) return true;
            }

            return false;
        }

        public void removeAll(){
            devslist.clear();
        }

    }



    @Override
    public void onResume() {
        super.onResume();
        //making this activity a subscriber of the wifi broadcast receiver
        EventBus.getDefault().register(this);
        if(receiver != null && mIntentFilter != null){
            getContext().registerReceiver(receiver, mIntentFilter);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        // unsubscribing
        EventBus.getDefault().unregister(this);
        if(receiver != null && mIntentFilter != null){
            getContext().unregisterReceiver(receiver);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // stop all threads when app is closed
        if(connectionThreads.size() > 0){
            for(int i = 0; i < connectionThreads.size(); i++){
                connectionThreads.get(i).stop();
            }

            devicesConnected.clear();
            devAddrConnected.clear();

        }
        disconnect();


    }

    /**
     * The Handler that gets information back from the Wifip2pService
     *
     */
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case P2pConstants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case Wifip2pService.STATE_CONNECTED:
                            // do something here
                            Toast.makeText(getContext(), "Device Connected", Toast.LENGTH_SHORT).show();
                        case Wifip2pService.STATE_CONNECTING:
                            // do something here
                        case Wifip2pService.STATE_NONE:
                            // do something here
                            break;
                    }
                    break;
                case P2pConstants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer

                    String writeMessage = new String(writeBuf);
                    System.out.println("Message sent to device: " + writeMessage);
                    break;
                case P2pConstants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Toast.makeText(getContext(), readMessage, Toast.LENGTH_SHORT).show();
                    break;

                case P2pConstants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(P2pConstants.TOAST),
                                Toast.LENGTH_SHORT).show();

                        // update our list of connected devices
                        String devAddr = msg.getData().getString("devAddr");
                        int i = devAddrConnected.indexOf(devAddr);
                        if(i > -1){
                            devAddrConnected.remove(i);
                            devicesConnected.remove(i);
                            devicesListAdapter.notifyDataSetChanged();
                        }

                    }
                    break;
            }
        }
    };





}



