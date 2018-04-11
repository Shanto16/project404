package mcgyvers.mobitrip.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import mcgyvers.mobitrip.Current_trip;
import mcgyvers.mobitrip.Receivers.OnReceiverDevicesEvent;
import mcgyvers.mobitrip.Receivers.OnReceiverNetInfoEvent;

/**
 * Created by edson on 30/12/17.
 * A BroadcastReceiver that notifies of important Wi-Fi P2P events
 */

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver{

    private WifiP2pManager mManager;
    private Channel mChannel;
    private Current_trip mActivity;

    private PeerListListener mPeerListener;
    public WifiP2pDeviceList devices;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, Current_trip activity, PeerListListener peerListListener){
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
        this.mPeerListener = peerListListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
            } else {
                // Wi-Fi P2P is not enabled
                Toast.makeText(context, "Your wifi p2p is not enabled", Toast.LENGTH_LONG).show();
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers.
            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()



            //Toast.makeText(context, "something happened", Toast.LENGTH_LONG).show();
            if(mManager != null){
                mManager.requestPeers(mChannel, mPeerListener);
            }


            mPeerListener = new PeerListListener() {
                @Override
                public void onPeersAvailable(WifiP2pDeviceList peers) {
                    devices = peers;
                    Log.d("P2P", "found someone!");
                    EventBus.getDefault().post(new OnReceiverDevicesEvent(peers));


                }
            };
            // handle for when peers disappear



        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            if(mManager == null){
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()){
                // We are connected with the other device, request connection
                // info to find group owner IP

                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        EventBus.getDefault().post(new OnReceiverNetInfoEvent(info));
                    }
                });


            }


        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing

        }


    }

    public WifiP2pDeviceList getDevices() {
        return devices;
    }
}
