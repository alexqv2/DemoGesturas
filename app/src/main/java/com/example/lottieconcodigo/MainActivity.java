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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    FirebaseDatabase database = FirebaseDatabase.getInstance();
   private RelativeLayout viewGroup;
    Display display;
    Point size = new Point();
    Timer _t;
    Anima2 ciclista;
    static final String pcicli = "bicicleta.json";
    static final String pcajas = "cajas.json";
    int ciclistaX;
    int ciclistaY;
    boolean semueve = false;
    boolean caracter = false;
    int etapaCaja = 1;
    String id = "n";
    boolean espera = false;


    @Override public boolean dispatchTouchEvent(MotionEvent event) {
        // Setup onTouchEvent for detecting type of touch gesture
        Sensey.getInstance().setupDispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            setContentView(R.layout.activity_main);
            Sensey.getInstance().init(this);
            super.onCreate(savedInstanceState);
            _t = new Timer();
            viewGroup = new RelativeLayout(this);
            display = getWindowManager().getDefaultDisplay();
            display.getSize(size);
            setContentView(viewGroup);
            ciclista = new Anima2(this, pcicli,0,0, 250,250,size.x,size.y);
            ciclista.CortarAnimacionPorFrame(70,100);
            LottieAnimationView vistaCiclista = ciclista.ReproducirAnimacion(true,0.5f);
            if( readAest() == 0){
                id = "a";
                espera = false;
                writeAest(1);
            } else if( readBest() == 0 ){
                id ="a2";
                espera = true;
                writeBest(1);
            } ///else cerrar aplicación porque solo permite 2 la base de datos
            ShakeDetector.ShakeListener shakeListener=new ShakeDetector.ShakeListener() {
                @Override public void onShakeDetected(){ if(id=="a"){
                   semueve = true;}
                }
                @Override public void onShakeStopped() {
                  semueve = false;
                }
            };
            FlipDetector.FlipListener flipListener=new FlipDetector.FlipListener() {
                @Override public void onFaceUp() { if(id == "a"){
                    ciclista.changeCharacter(pcicli);}
                }
                @Override public void onFaceDown() { if (id =="a"){
                    ciclista.changeCharacter(pcajas);}
                }
            };
            TouchTypeDetector.TouchTypListener touchTypListener=new TouchTypeDetector.TouchTypListener() {
                @Override public void onTwoFingerSingleTap() {
                }
                @Override public void onThreeFingerSingleTap() {
                }
                @Override public void onDoubleTap() {
                    if(id=="a"){
                    caracter = !caracter;
                    if(caracter){
                        ciclista.changeCharacter(pcajas);
                        ciclista.ReproducirAnimacion(true, 1f);
                    }else {
                        ciclista.changeCharacter(pcicli);
                        ciclista.ReproducirAnimacion(true, 1f);
                    }                   }
                }
                @Override public void onScroll(int scrollDirection) {
                    if(id=="a"){
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
                    }}
                }
                @Override public void onSingleTap() {
                }
                @Override public void onSwipe(int swipeDirection) {
                    if(id=="a"){
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
                            break;
                    }}
                }
                @Override public void onLongPress() {
                }
            };
            viewGroup.addView(ciclista.ReproducirAnimacion(true, 1f));
            Sensey.getInstance().startShakeDetection(shakeListener);
            Sensey.getInstance().startFlipDetection(flipListener);
            Sensey.getInstance().startTouchTypeDetection(this, touchTypListener);
            _t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if(id=="a") {
                        if(!espera){
                            if (semueve) {
                            ciclista.DesplazarAnimacion();
                            }
                            ciclista.setPosition();
                            if(ciclista.x < 0 || ciclista.x > size.x || ciclista.y < 0 || ciclista.y > size.y){
                                writeAx(ciclista.x);
                                writeAy(ciclista.y);
                                writeBx(0);
                                writeBy(0);
                                espera = true;
                            }
                        }else {
                            int bx = readBx();
                            if (bx != 0){
                               ciclista.x =  bx < 0 ? bx + size.x : bx - size.x;
                               int by = readBy();
                               ciclista.y = by < 0 ? by + size.y : by - size.y;
                               ciclista.setPosition();
                               espera = false;
                            }
                        }
                    }else if(id =="a2") {
                        if(!espera){
                            if (semueve) {
                                ciclista.DesplazarAnimacion();
                            }
                            ciclista.setPosition();
                            if(ciclista.x < 0 || ciclista.x > size.x || ciclista.y < 0 || ciclista.y > size.y){
                                writeBx(ciclista.x);
                                writeBy(ciclista.y);
                                writeAx(0);
                                writeAy(0);
                                espera = true;
                            }
                        }else {
                            int ax = readAx();
                            if (ax != 0){
                                ciclista.x =  ax < 0 ? ax + size.x : ax - size.x;
                                int ay = readAy();
                                ciclista.y = ay < 0 ? ay + size.y : ay - size.y;
                                ciclista.setPosition();
                                espera = false;
                            }
                        }
                    }
                }
            },0,5);
        }catch (Exception ex){
            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
            throw ex;
        }
    }
    public void writeAest(int est){
        database.getReference("a/est").setValue(est);
    }
    public void writeAx(int x){
        database.getReference("a/x").setValue(x);
    }
    public void writeAy(int y){
        database.getReference("a/y").setValue(y);
    }
    public void writeBest(int est){
        database.getReference("b/est").setValue(est);
    }
    public void writeBx(int x){
        database.getReference("b/x").setValue(x);
    }
    public void writeBy(int y){
        database.getReference("b/y").setValue(y);
    }
    ///----------------------------------
    public int readAest(){
        final int[] val = new int[1];
        database.getReference("a/est").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                 val[0] = dataSnapshot.getValue(int.class);

            }
            @Override
            public void onCancelled(DatabaseError error) {
                val[0] = 0;
            }
        });
        return val[0];
    }
    public int readAx(){
        final int[] val = new int[1];
        database.getReference("a/x").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                val[0] = dataSnapshot.getValue(int.class);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                val[0] = 0;
            }
        });
        return val[0];
    }
    public int readAy( ){
        final int[] val = new int[1];
        database.getReference("a/y").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                val[0] = dataSnapshot.getValue(int.class);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                val[0] = 0;
            }
        });
        return val[0];
    }
    public int readBest(){
        final int[] val = new int[1];
        database.getReference("b/est").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                val[0] = dataSnapshot.getValue(int.class);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                val[0] = 0;
            }
        });
        return val[0];
    }
    public int readBx(){
        final int[] val = new int[1];
        database.getReference("b/x").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                val[0] = dataSnapshot.getValue(int.class);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                val[0] = 0;
            }
        });
        return val[0];
    }
    public int readBy(){
        final int[] val = new int[1];
        database.getReference("b/y").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                val[0] = dataSnapshot.getValue(int.class);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                val[0] = 0;
            }
        });
        return val[0];
    }
    @Override
    protected void onStop() {
        // call the superclass method first
        super.onStop();
        if(id == "a"){
            writeAest(0);
            writeAx(0);
            writeAy(0);
        }
        if(id == "a2"){
            writeBest(0);
            writeBx(0);
            writeBy(0);
        }

    }
}
