package mcgyvers.mobitrip.Receivers;

import android.net.wifi.p2p.WifiP2pDeviceList;

/**
 * Created by edson on 30/12/17.
 */

public class OnReceiverDevicesEvent {

    private WifiP2pDeviceList devices;


    public OnReceiverDevicesEvent(WifiP2pDeviceList devices){
        this.devices = devices;
    }

    public WifiP2pDeviceList getDevices() {
        return devices;
    }
}
