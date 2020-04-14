package com.example.lottieconcodigo;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
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
            ciclista = new Anima2(this, "bicicleta.json",0,0, 250,250,size.x,size.y);

            ciclista.CortarAnimacionPorFrame(70,100);

            LottieAnimationView vistaCiclista = ciclista.ReproducirAnimacion(true,0.5f);

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
            PinchScaleDetector.PinchScaleListener hola =new PinchScaleDetector.PinchScaleListener() {
                @Override public void onScale(ScaleGestureDetector scaleGestureDetector, boolean isScalingOut) {
                    if (isScalingOut) {
                       ciclista.changeSize(true);
                    } else {
                        ciclista.changeSize(false);
                    }
                }

                @Override public void onScaleStart(ScaleGestureDetector scaleGestureDetector) {
                    // Scaling Started
                }

                @Override public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
                    // Scaling Stopped
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
           Sensey.getInstance().startPinchScaleDetection(this, hola);
            Sensey.getInstance().startTouchTypeDetection(this, touchTypListener);
            _t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
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


}
