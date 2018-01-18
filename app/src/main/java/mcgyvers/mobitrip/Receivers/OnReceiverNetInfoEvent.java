package mcgyvers.mobitrip.Receivers;

import android.net.wifi.p2p.WifiP2pInfo;

/**
 * Created by edson on 18/01/18.
 */

public class OnReceiverNetInfoEvent {

    private WifiP2pInfo info;


    public OnReceiverNetInfoEvent(WifiP2pInfo info){
        this.info = info;
    }

    public WifiP2pInfo getInfo() {
        return info;
    }
}
