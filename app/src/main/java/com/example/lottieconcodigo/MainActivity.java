package com.example.lottieconcodigo;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.github.nisrulz.sensey.FlipDetector;
import com.github.nisrulz.sensey.LightDetector;
import com.github.nisrulz.sensey.PinchScaleDetector;
import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.ShakeDetector;
import com.github.nisrulz.sensey.TouchTypeDetector;
import com.newtronlabs.easybluetooth.BluetoothClient;
import com.newtronlabs.easybluetooth.BluetoothServer;
import com.newtronlabs.easybluetooth.IBluetoothClient;
import com.newtronlabs.easybluetooth.IBluetoothConnectionCallback;
import com.newtronlabs.easybluetooth.IBluetoothConnectionFailedListener;
import com.newtronlabs.easybluetooth.IBluetoothDataReceivedCallback;
import com.newtronlabs.easybluetooth.IBluetoothDataSentCallback;
import com.newtronlabs.easybluetooth.IBluetoothMessageEvent;
import com.newtronlabs.easybluetooth.IBluetoothServer;
import com.sirvar.bluetoothkit.BluetoothKit;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
   private RelativeLayout viewGroup;
    Display display;
    Point size = new Point();
    Timer _t;
    Anima2 ciclista;
    int ciclistaX;
    int ciclistaY;
    boolean semueve = false;
    boolean caracter = false;
    int etapaCaja = 1;
    String data;
    String datareceived = "n";
    IBluetoothClient btClient;
    BluetoothKit bluetoothKit = new BluetoothKit();
    @Override public boolean dispatchTouchEvent(MotionEvent event) {
        // Setup onTouchEvent for detecting type of touch gesture
        Sensey.getInstance().setupDispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try{
            bluetoothKit.enable();
            setContentView(R.layout.activity_main);
            BluetoothDevice device = bluetoothKit.getDeviceByName("Nombre del otro dispositivo");
            Sensey.getInstance().init(this);
            super.onCreate(savedInstanceState);
            _t = new Timer();
            viewGroup = new RelativeLayout(this);
            display = getWindowManager().getDefaultDisplay();
            display.getSize(size);
            setContentView(viewGroup);
            ciclista = new Anima2(this, "bicicleta.json",0,0, 250,250,size.x,size.y);

            ciclista.CortarAnimacionPorFrame(70,100);

            LottieAnimationView vistaCiclista = ciclista.ReproducirAnimacion(true,0.5f);
            final IBluetoothServer btServer = new BluetoothServer.Builder(this.getApplicationContext(),
                    "EasyBtService", ParcelUuid.fromString("00001101-0000-1000-8000-00805F9B34FB"))
                    .build();

            if(btServer == null)
            {
                // Server could not be created.
            }
            else
            {
                // Block until a client connects.
                btClient = btServer.accept();
                // Set a data callback to receive data from the remote device.
                btClient.setDataCallback(new SampleDataCallback());
                // Set a connection callback to be notified of connection changes.
                btClient.setConnectionCallback(new SampleConnectionCallback());
                // Set a data send callback to be notified when data is sent of fails to send.
                btClient.setDataSentCallback(new SampleDataSentCallback());

                btServer.disconnect();
            }
            IBluetoothClient client = new BluetoothClient.Builder(this.getApplication(), device, ParcelUuid.fromString("00001101-0000-1000-8000-00805F9B34FB"))
                    // We want to be notified when connection completes.
                    .setConnectionCallback(new SampleConnectionCallback())
                    // Let's also get notified if it fails
                    .setConnectionFailedListener(new SampleConnectionFailedListener())
                    // Receive data from the server
                    .setDataCallback(new SampleDataCallback())
                    // Be notified when the data is sent to the server or fails to send.
                    .setDataSentCallback(new SampleDataSentCallback())
                    .build();
            client.connect();
            // Connect to server




            ShakeDetector.ShakeListener shakeListener=new ShakeDetector.ShakeListener() {
                @Override public void onShakeDetected() {
                   semueve = true;
                }

                @Override public void onShakeStopped() {
                  semueve = false;
                }
            };
            FlipDetector.FlipListener flipListener=new FlipDetector.FlipListener() {
                @Override public void onFaceUp() {
                    ciclista.changeCharacter("bicicleta.json");
                }

                @Override public void onFaceDown() {
                    ciclista.changeCharacter("cajas.json");
                }
            };


            TouchTypeDetector.TouchTypListener touchTypListener=new TouchTypeDetector.TouchTypListener() {
                @Override public void onTwoFingerSingleTap() {
                    // Two fingers single tap
                }

                @Override public void onThreeFingerSingleTap() {
                    // Three fingers single tap
                }

                @Override public void onDoubleTap() {
                    caracter = !caracter;
                    if(caracter){
                        ciclista.changeCharacter("cajas.json");
                        ciclista.ReproducirAnimacion(true, 1f);
                    }else{
                        ciclista.changeCharacter("bicicleta.json");
                        ciclista.ReproducirAnimacion(true, 1f);
                    }
                }

                @Override public void onScroll(int scrollDirection) {
                    switch (scrollDirection) {
                        case TouchTypeDetector.SCROLL_DIR_UP:
                            ciclista.y = ciclista.y - 3;
                            break;
                        case TouchTypeDetector.SCROLL_DIR_DOWN:
                            ciclista.y = ciclista.y + 3;
                            break;
                        case TouchTypeDetector.SCROLL_DIR_LEFT:
                            ciclista.x = ciclista.x - 3;
                            break;
                        case TouchTypeDetector.SCROLL_DIR_RIGHT:
                            ciclista.x = ciclista.x + 3;
                            break;
                        default:
                            // Do nothing
                            break;
                    }
                }

                @Override public void onSingleTap() {
                    // Single tap
                }

                @Override public void onSwipe(int swipeDirection) {
                    switch (swipeDirection) {
                        case TouchTypeDetector.SWIPE_DIR_UP:
                            ciclista.y = ciclista.y - 200;
                            break;
                        case TouchTypeDetector.SWIPE_DIR_DOWN:
                            ciclista.y = ciclista.y + 200;
                            break;
                        case TouchTypeDetector.SWIPE_DIR_LEFT:
                            ciclista.x = ciclista.x - 200;
                            break;
                        case TouchTypeDetector.SWIPE_DIR_RIGHT:
                            ciclista.x = ciclista.x + 200;
                            break;
                        default:
                            //do nothing
                            break;
                    }
                }

                @Override public void onLongPress() {
                    // Long press
                }
            };



            viewGroup.addView(ciclista.ReproducirAnimacion(true, 1f));
            Sensey.getInstance().startShakeDetection(shakeListener);
            Sensey.getInstance().startFlipDetection(flipListener);

            Sensey.getInstance().startTouchTypeDetection(this, touchTypListener);
            _t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if(ciclista.y < 0 ){
                        data = "u";
                        semueve = false;
                        ciclista.x = size.x + 100;
                        ciclista.y = size.y + 100;
                        btClient.sendData("data", data.getBytes());
                    } else if(ciclista.y > size.y){
                        data = "d";
                        semueve = false;
                        ciclista.x = size.x + 100;
                        ciclista.y = size.y + 100;
                        btClient.sendData("data", data.getBytes());
                    } else if(ciclista.x < 0){
                        data = "l";
                        semueve = false;
                        ciclista.x = size.x + 100;
                        ciclista.y = size.y + 100;
                        btClient.sendData("data", data.getBytes());
                    } else if(ciclista.x > size.x){
                        data = "r";
                        semueve = false;
                        ciclista.x = size.x + 100;
                        ciclista.y = size.y + 100;
                        btClient.sendData("data", data.getBytes());
                    } else {
                        data = "n";
                    }
                    if(datareceived != "n"){
                        switch (datareceived) {
                            case "u": ciclista.y = size.y; ciclista.x = size.x/2; break;
                            case "d": ciclista.y = 0; ciclista.x = size.x/2; break;
                            case "l": ciclista.x = size.x;  ciclista.y = size.y/2; break;
                            case "r": ciclista.x = 0; ciclista.y = size.y/2; break;
                            default:
                                break;
                        }
                    }

                   if(semueve) {
                     ciclista.DesplazarAnimacion();
                   }
                    ciclista.setPosition();

                }
            },0,5);
        }catch (Exception ex){
            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
            throw ex;
        }


    }
    public class SampleConnectionCallback implements IBluetoothConnectionCallback
    {
        public static final String TAG = "easyBt";
        @Override
        public void onConnected(IBluetoothClient bluetoothClient)
        {
            // Connection successful.
            Log.d(TAG, "Connected to: " + bluetoothClient.getNodeId());

            // We can start sending data now.
            bluetoothClient.sendData("ClientGreeting", "Hello Server!!".getBytes());

        }

        @Override
        public void onConnectionSuspended(IBluetoothClient bluetoothClient, int reason)
        {
            // Connection lost.
            if(reason == REASON_CONNECTION_CLOSED)
            {
                Log.d(TAG, "Connection to :" +bluetoothClient.getNodeId() + " ended.");
            }
        }
    }

    public class SampleConnectionFailedListener implements IBluetoothConnectionFailedListener
    {
        public static final String TAG = "easyBt";
        @Override
        public void onConnectionFailed(IBluetoothClient bluetoothClient, int i)
        {
            // Connection attempt failed.
            Log.d(TAG, "Connection Failed!");
        }
    }

    public class SampleDataCallback implements IBluetoothDataReceivedCallback
    {
        public static final String TAG = "easyBt";

        @Override
        public void onDataReceived(IBluetoothMessageEvent messageEvent)
        {
            // Data was received.
            datareceived = messageEvent.getData().toString();
        }
    }
    public class SampleDataSentCallback implements IBluetoothDataSentCallback
    {
        public static final String TAG = "easyBt";

        @Override
        public void onDataSent(IBluetoothClient bluetoothClient, IBluetoothMessageEvent messageEvent)
        {
            Log.d(TAG, "Data Sent: " + messageEvent.getTag() + " Data: " + new String(messageEvent.getData()));
        }

        @Override
        public void onDataSendFailed(IBluetoothClient bluetoothClient, @SendFailureReason int failureReason)
        {
            if(failureReason == REASON_DATA_FORMAT_INVALID)
            {
                Log.d(TAG, "Failed to send data. Invalid Format");
            }
            else if(failureReason == REASON_REMOTE_CONNECTION_CLOSED)
            {
                Log.d(TAG, "Failed to send data. Connection Lost.");
            }
        }
    }
}
