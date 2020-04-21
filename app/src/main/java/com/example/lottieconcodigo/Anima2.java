package com.example.lottieconcodigo;

import android.content.Context;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;

public class Anima2 {
    private String nombreRecurso;
    private String rawJson;

    public int x;
    public int y;
    private int ancho;
    private int largo;

    private int dx;
    private int dy;

    private int MaximaPosicionX;
    private int MaximaPosicionY;

    private LottieAnimationView animacion;

    public Anima2(Context context, String recurso, int x, int y, int ancho, int largo, int MaximoX, int MaximoY){
        this.nombreRecurso = recurso;
        this.x = x;
        this.y = y;
        this.dx = -1;
        this.dy = -1;
        this.ancho = ancho;
        this.largo = largo;
        this.MaximaPosicionX = MaximoX;
        this.MaximaPosicionY = MaximoY;

        animacion = new LottieAnimationView(context);
        animacion.setX((this.x));
        animacion.setY(this.y);
        animacion.setLayoutParams( new ViewGroup.LayoutParams(this.ancho,this.largo));
        animacion.setAnimation(this.nombreRecurso);
    }

    public Anima2(Context context, String recurso, int x, int y, int ancho, int largo){
        this.nombreRecurso = recurso;
        this.x = x;
        this.y = y;
        this.dx = -1;
        this.dy = -1;
        this.ancho = ancho;
        this.largo = largo;

        animacion = new LottieAnimationView(context);
        animacion.setX((this.x));
        animacion.setY(this.y);
        animacion.setLayoutParams( new ViewGroup.LayoutParams(this.ancho,this.largo));
        animacion.setAnimation(this.nombreRecurso);
    }

    public Anima2(Context context, int x, int y, int ancho, int largo, String contenidoJson){
        this.rawJson = contenidoJson;
        this.x = x;
        this.y = y;
        this.dx = -1;
        this.dy = -1;
        this.ancho = ancho;
        this.largo = largo;

        animacion = new LottieAnimationView(context);
        animacion.setX(this.x);
        animacion.setY(this.y);
        animacion.setLayoutParams( new ViewGroup.LayoutParams(this.ancho,this.largo));
        animacion.setAnimation(this.rawJson);
    }
    public void changeCharacter(String recurso){
        animacion.setAnimation(recurso);
    }

    public void changeSize(Boolean up){
        if(up){
            this.ancho = this.ancho + 1;
            this.largo = this.largo +1 ;
        } else {
            this.largo = this.largo -1 ;
            this.ancho = this.ancho - 1;
        }
        this.animacion.setLayoutParams(new ViewGroup.LayoutParams(this.ancho, this.largo));
    }

    public LottieAnimationView ReproducirAnimacion(boolean loop, float velocidad){
        animacion.loop(loop);
        animacion.playAnimation();
        animacion.setSpeed(velocidad);
        return animacion;
    }

    public void DesplazarAnimacion(){
        if(this.x + this.ancho >= this.MaximaPosicionX || this.x <= 0)this.dx*=-1;
        if(this.y + this.largo >= this.MaximaPosicionY || this.y <= 0)this.dy*=-1;
        this.x+=this.dx;
        this.y+=this.dy;
        animacion.setX(this.x);
        animacion.setY(this.y);
    }
    public void hide(){
        animacion.setAnimation("");
    }

    public void CortarAnimacionPorFrame(int minFrame, int maxFrame){
        animacion.setMinAndMaxFrame(minFrame,maxFrame);
       /* animacion.setMinFrame(minFrame);
        animacion.setMaxFrame(maxFrame);*/
    }

    public void CortarAnimacionPorMarcador(String minMarcador, String maxMarcador, Boolean repetir){
        //animacion.set

    }
    public void setPosition(){
        animacion.setX(this.x);
        animacion.setY(this.y);
    }
    public void ConfigurarVelocidad(int velocidad){
        this.animacion.setSpeed(velocidad);
    }


}
