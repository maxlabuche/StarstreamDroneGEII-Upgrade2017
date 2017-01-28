package com.example.admin.pilotage;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 *This class give the pitch and the roll of the Phone
 * with getPitch and getRoll
 *@author Mathieu
 *@version 2.0
 */
public class Inclinaison implements SensorEventListener {
    Context context;
    private  SensorManager mSensorManager;
    private  Sensor mAccelerometer;
    private  Sensor mMagnetic;
    //vector
    float[] accelerometerVector=new float[3];
    float[] magneticVector=new float[3];
    float[] resultMatrix=new float[9];
    float[] values=new float[3];
    float fy;
    float fz;

    public Inclinaison(Context rContext){
        this.context= rContext;
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetic=mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, mAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetic,SensorManager.SENSOR_DELAY_NORMAL);
        accelerometerVector[0]=0;
        accelerometerVector[1]=0;
        accelerometerVector[2]=0;
    }

    /**
     * Get the Roll
     * @return the Roll with a float [-10..10]
     */
    public float getRoll (){
        float fRoll=0;
        if( fy>= 45 ){
            fRoll=10;
        }else if(fy>=40){
            fRoll=8;
        }else if(fy>=35){
            fRoll=7;
        }else if(fy>=30){
            fRoll=6;
        }else if(fy>=25){
            fRoll=5;
        }else if(fy>=20){
            fRoll=4;
        }else if(fy>=15){
            fRoll=3;
        }else if(fy>=10){
            fRoll=2;
        }else if(fy>=5){
            fRoll=1;
        }else if(fy<=-45){
            fRoll=-10;
        }else if(fy<=-35){
            fRoll=-7;
        }else if(fy<=-30){
            fRoll=-6;
        }else if(fy<=-25){
            fRoll=-5;
        }else if(fy<=-20){
            fRoll=-4;
        }else if(fy<=-15){
            fRoll=-3;
        }else if(fy<=-10){
            fRoll=-2;
        }else if(fy<=-5){
            fRoll=-1;
        }
        if(fy > -5 && fy < 5){
            fRoll=0;
        }
        if(fy > 85 && fy < -85){
            fRoll=0;
        }
        return -fRoll;
    }

    /**
     * Get the Pitch
     * @return the Pitcj with a float [-10..10]
     */
    public float getPitch (){
        float fPitch=0;
        if( fz>= 45 ){
            fPitch=-10;
        }else if(fz>=40){
            fPitch=-8;
        }else if(fz>=35){
            fPitch=-7;
        }else if(fz>=30){
            fPitch=-6;
        }else if(fz>=25){
            fPitch=-5;
        }else if(fz>=20){
            fPitch=-4;
        }else if(fz>=15){
            fPitch=-3;
        }else if(fz>=10){
            fPitch=-2;
        }else if(fz>=5){
            fPitch=-1;
        }else if(fz<=-45){
            fPitch=10;
        }else if(fz<=-35){
            fPitch=7;
        }else if(fz<=-30){
            fPitch=6;
        }else if(fz<=-25){
            fPitch=5;
        }else if(fz<=-20){
            fPitch=4;
        }else if(fz<=-15){
            fPitch=3;
        }else if(fz<=-10){
            fPitch=2;
        }else if(fz<=-5){
            fPitch=1;
        }
        if(fz > -5 && fz < 5){
            fPitch=0;
        }
        if(fy > 85 && fz < -85){
            fPitch=0;
        }

        return -fPitch;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent se) {
        if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerVector=se.values;
        }
        if(se.sensor.getType()== Sensor.TYPE_MAGNETIC_FIELD){
            magneticVector=se.values;
        }
        // ask the RotationMatrix
        SensorManager.getRotationMatrix(resultMatrix, null, accelerometerVector, magneticVector);
        // get the orientation vector
        SensorManager.getOrientation(resultMatrix, values);
        // pitch
        fy = (float) Math.toDegrees(values[1]);
        // roll
        fz = (float) Math.toDegrees(values[2]);
    }
}