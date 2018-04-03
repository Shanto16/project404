package mcgyvers.mobitrip;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import mcgyvers.mobitrip.interfaces.P2pConstants;

public class Wifip2pService {

    private static final String TAG = "wifip2pService";
    private final Handler mHandler;
    private final WifiP2pManager manager;
    private ServerSocket mServerSocker;
    private Socket mSocket;
    Context mContext;
    private int mState;
    private int mNewState;
    private int mPort;
    private boolean isHost;
    private String hostAdress = "";
    public String deviceName = "";
    public String devAddr = "";

    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private ClientConnectThread mClientConnectThread;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 6;         // we're doing nothing
    public static final int STATE_CONNECTING = 8;   // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 9;    // now connected to a remote device

    /**
     * Constructor, prepares a wifi p2p service
     *
     * @param context The UI Activity context
     * @param handler A handler to send messages back to the UI Activity
     */
    public Wifip2pService(Context context, Handler handler, String port, boolean host, String addr) {
        mHandler = handler;
        mContext = context;
        manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        mState = STATE_NONE;
        mNewState = mState;
        mPort =  Integer.parseInt(port);
        isHost = host;
        hostAdress = addr;
    }

    /**
     * to make toast messages more easily
     * @param txt string message
     */
    public void mssg(String txt){
        Toast.makeText(mContext, txt, Toast.LENGTH_SHORT).show();
    }

    /**
     * @return the current connection state
     */
    public synchronized int getState(){
        return mState;
    }

    /**
     * start the service
     */
    public void start(){

        // end all threads
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if(mClientConnectThread != null){
            mClientConnectThread.cancel();
            mClientConnectThread = null;
        }

        // if our device is host, create a host server
        if(isHost){
            mConnectThread = new ConnectThread();
            mConnectThread.start();

        } else {
            mClientConnectThread = new ClientConnectThread();
            mClientConnectThread.start();
        }


    }

    /**
     * Stop all threads
     */
    public void stop() {
        Log.d(TAG, "stop");
        mState = STATE_NONE;
        Message readMsg = mHandler.obtainMessage(P2pConstants.MESSAGE_STATE_CHANGE, Wifip2pService.STATE_NONE);
        readMsg.sendToTarget();

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if(mClientConnectThread != null){
            mClientConnectThread.cancel();
            mClientConnectThread = null;
        }





    }



    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     *
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }


    /**
     * Start the ConnectedThread to begin managing a p2p connection
     *
     * @param socket The Socket on which the connection was made
     */
    public void connected(Socket socket){
        Log.d(TAG, "Connected");


        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

    }


    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class ClientConnectThread extends Thread {
        private Socket mmSocket;

        public ClientConnectThread() {
            mmSocket = null;
            mState = STATE_CONNECTING;
            Message readMsg = mHandler.obtainMessage(P2pConstants.MESSAGE_STATE_CHANGE, mState);
            readMsg.sendToTarget();
            Log.e(TAG, "state connecting");

        }

        public void run() {

            try {
                /**
                 * client method to send data to other devices or groupOwner
                 * through p2p data thread
                 */
                mmSocket.bind(null);
                mmSocket.connect((new InetSocketAddress(hostAdress, mPort)), 500);



            } catch (IOException e) {
                e.printStackTrace();
                // Unable to connect; close the socket and return.
                Log.e(TAG, "connect failed", e);
                mState = STATE_NONE;
                Message readMsg = mHandler.obtainMessage(P2pConstants.MESSAGE_STATE_CHANGE, mState);
                readMsg.sendToTarget();
                try {
                    mmSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connected(mmSocket);



        }

        public void cancel() {
            try {
                mState = STATE_NONE;
                Message readMsg = mHandler.obtainMessage(P2pConstants.MESSAGE_STATE_CHANGE, mState);
                readMsg.sendToTarget();
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }

        }
    }





    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    public class ConnectThread extends Thread{
        private Socket mmSocket;
        private ServerSocket mmServerSocket;


        public ConnectThread(){


            mmServerSocket = null;
            mState = STATE_CONNECTING;
            Message readMsg = mHandler.obtainMessage(P2pConstants.MESSAGE_STATE_CHANGE, mState);
            readMsg.sendToTarget();
            Log.e(TAG, "state connecting");

        }

        public void run(){

            /*
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            try {
                mmServerSocket = new ServerSocket(mPort);
                mmSocket = mmServerSocket.accept();

                /*
                 * If this code is reached, a client has connected
                 */




            } catch (IOException e) {
                e.printStackTrace();
                // Unable to connect; close the socket and return.
                Log.e(TAG, "connect failed", e);
                mState = STATE_NONE;
                Message readMsg = mHandler.obtainMessage(P2pConstants.MESSAGE_STATE_CHANGE, mState);
                readMsg.sendToTarget();
                try {
                    mmSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connected(mmSocket);



        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mState = STATE_NONE;
                Message readMsg = mHandler.obtainMessage(P2pConstants.MESSAGE_STATE_CHANGE, mState);
                readMsg.sendToTarget();
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }




    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    public class ConnectedThread extends Thread{
        private final Socket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(Socket socket){
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            mState = STATE_CONNECTED;
            Message readMsg = mHandler.obtainMessage(P2pConstants.MESSAGE_STATE_CHANGE, mState);
            readMsg.sendToTarget();
            Log.e(TAG, "state connected");

        }

        public void run(){
            mmBuffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (mState == STATE_CONNECTED){
                try{
                    // Read from InputStream while connected
                    bytes = mmInStream.read(mmBuffer);

                    // send the obtainend bytes to the UI activity
                    mHandler.obtainMessage(P2pConstants.MESSAGE_READ, bytes, -1, mmBuffer).sendToTarget();

                } catch (IOException e){
                    Log.e(TAG, "disconnected");
                    break;
                }
            }
        }
        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer){
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(P2pConstants.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }




}
