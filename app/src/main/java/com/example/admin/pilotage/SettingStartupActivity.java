package com.example.admin.pilotage;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class SettingStartupActivity extends AppCompatActivity {

    DroneManager mDrone;
    Pilotage mPilot;

    //Variables;
    private boolean bIndicBatterie;
    private boolean bIndicAltitude;
    private boolean bIndicVitesse;
    private boolean bIndicAvArr;
    private boolean bIndicGD;
    private boolean bIndicRotation;
    private boolean bAffJoy;

    private boolean bActivationVideo;

    private boolean bDebugMode;

    //Drone config:
    Spinner VideoSpinner;
    SeekBar AltitudeSeekBar;
    SeekBar AngleSeekBar;
    SeekBar YawSpeedSeekBar;
    SeekBar VerticalSpeedSeekBar;
    TextView CurrentAngle;
    TextView CurrentAltitude;
    TextView CurrentYawSpeed;
    TextView CurrentVerticalSpeed;

    //Checkbox:
    CheckBox AltitudeMaxOn;
    CheckBox cbBatterie;
    CheckBox cbAltitude;
    CheckBox cbVitesse;
    CheckBox cbAvArr;
    CheckBox cbGD;
    CheckBox cbRotation;
    CheckBox cbAcceptUse;
    CheckBox cbActivationVideo;
    CheckBox cbDebugMode;
    CheckBox cbAffJoy;

    //Spinner piloting interface choice
    Spinner spinInterfacePilotage;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_startup);

        //XML/Code association (objects):
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        cbBatterie = (CheckBox) findViewById(R.id.chk_battery);
        cbAltitude = (CheckBox) findViewById(R.id.chk_altitude);
        cbVitesse = (CheckBox) findViewById(R.id.chk_vitesse);
        cbAvArr = (CheckBox) findViewById(R.id.chk_av_arr);
        cbGD = (CheckBox) findViewById(R.id.chk_gd);
        cbRotation = (CheckBox) findViewById(R.id.chk_rotation);
        cbAffJoy= (CheckBox) findViewById(R.id.AffJoy);

        cbActivationVideo = (CheckBox) findViewById(R.id.chk_video_mode);
        cbAffJoy = (CheckBox) findViewById(R.id.AffJoy);
        cbDebugMode = (CheckBox) findViewById(R.id.chk_debug);

        spinInterfacePilotage = (Spinner)findViewById(R.id.spinner_type_pilotage);

        VideoSpinner = (Spinner)findViewById(R.id.cameraspinner);
        AltitudeSeekBar= (SeekBar)findViewById(R.id.seekbar_altitude_max);
        AngleSeekBar = (SeekBar)findViewById(R.id.seekbar_pitch_roll_max);
        YawSpeedSeekBar = (SeekBar)findViewById(R.id.seekbar_yaw_speed);
        VerticalSpeedSeekBar = (SeekBar)findViewById(R.id.seekbar_vertical_speed_max);
        CurrentAltitude= (TextView)findViewById(R.id.txt_alt_max_settings);
        CurrentAngle= (TextView)findViewById(R.id.txt_pitch_roll_max_settings);
        AltitudeMaxOn = (CheckBox)findViewById(R.id.chk_altitude_max);
        CurrentYawSpeed = (TextView)findViewById(R.id.txt_yaw_max_settings);
        CurrentVerticalSpeed = (TextView)findViewById(R.id.txt_vert_max_settings);


        cbAcceptUse =  (CheckBox) findViewById(R.id.chk_accept_use);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        setSupportActionBar(toolbar);

        //We hide the button at start
        fab.hide();

        getSupportActionBar().setTitle("Configuration de démarrage du drone");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Drone instantiation
        mDrone = new DroneManager();
        mPilot = new Pilotage();

        /*Every seekbars have their own listener and textview,
        when the user move a seekbar it change the value of his texview.*/

        //Max Altitude seekbar
        final int stepAlt = 100;
        final int maxAlt = 5000;
        final int minAlt = 500;
        CurrentAltitude.setText("500");
        AltitudeSeekBar.setMax((maxAlt - (int) minAlt) / stepAlt);
        AltitudeSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(AltitudeMaxOn.isChecked()){
                            int value = minAlt + (progress * stepAlt);
                            CurrentAltitude.setText(Integer.toString(value));
                        }else{
                            //If the Altitude CheckBox isn't checked, the value can't be changed
                            AltitudeSeekBar.setProgress(0);
                            CurrentAltitude.setText("500");
                        }
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

        //Max Angle seekbar
        final int stepAgl = 2;
        final int maxAgl = 52;
        AngleSeekBar.setMax((maxAgl) / stepAgl);
        AngleSeekBar.setProgress(52);
        CurrentAngle.setText(Double.toString(30));
        AngleSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        float value = (progress * stepAgl)*30/52;
                        CurrentAngle.setText(Float.toString(value));
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

        //Max Yaw speed seekbar
        final int stepYaw = 1;
        final int maxYaw = 6;
        final int minYaw = 1;
        YawSpeedSeekBar.setMax((maxYaw - (int) minYaw) / stepYaw);
        YawSpeedSeekBar.setProgress(6);
        CurrentYawSpeed.setText(Integer.toString(6));
        YawSpeedSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        int value = minYaw + (progress * stepYaw);
                        CurrentYawSpeed.setText(Integer.toString(value));
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

        //Max Vertical speed seekbar
        final int stepVertical = 100;
        final int maxVertical = 2000;
        final int minVertical  = 200;
        VerticalSpeedSeekBar.setMax((maxVertical - (int) minVertical) / stepVertical);
        VerticalSpeedSeekBar.setProgress(2000);
        CurrentVerticalSpeed.setText(Integer.toString(2000));
        VerticalSpeedSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        int value = minVertical + (progress * stepVertical);
                        CurrentVerticalSpeed.setText(Integer.toString(value));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();

                try {
                    //The drone must be stationary on the ground otherwise the CONFIG commands are ignored
                    //This is UDP so we have to send the command multiple times to be sure it's executed.
                    for(int iBcl = 0; iBcl < 5; iBcl ++){
                        mDrone.SendCMD("AT*PCMD=" + mPilot.RecupSequenceNum(1) + ",1,0,0,0,0");
                        TimeUnit.MILLISECONDS.sleep(20);
                    }

                    //Camera Type
                    switch(String.valueOf(VideoSpinner.getSelectedItem())){
                        case "Vertical":
                            for(int iBcl = 0; iBcl < 5; iBcl ++){
                                mDrone.SendCMD(SetDroneCAM(mPilot.RecupSequenceNum(1), 1));
                                TimeUnit.MILLISECONDS.sleep(20);
                            }
                            break;
                        case "Horizontal":
                            for(int iBcl = 0; iBcl < 5; iBcl ++){
                                mDrone.SendCMD(SetDroneCAM(mPilot.RecupSequenceNum(1), 2));
                                TimeUnit.MILLISECONDS.sleep(20);
                            }
                            break;
                    }

                    //Altitude Max (millimeters)
                    if(AltitudeMaxOn.isChecked()){
                        for(int iBcl = 0; iBcl < 5; iBcl ++) {
                            mDrone.SendCMD(SetDroneALT(mPilot.RecupSequenceNum(1), Integer.valueOf(CurrentAltitude.getText().toString())));
                            TimeUnit.MILLISECONDS.sleep(20);
                        }
                    }else{
                        for(int iBcl = 0; iBcl < 5; iBcl ++) {
                            mDrone.SendCMD(SetDroneALT(mPilot.RecupSequenceNum(1), 100000));
                            TimeUnit.MILLISECONDS.sleep(20);
                        }
                    }

                    //Angle Max (radians)
                    for(int iBcl = 0; iBcl < 5; iBcl ++) {
                        mDrone.SendCMD(SetDroneMAXANGLE(mPilot.RecupSequenceNum(1),Float.valueOf(CurrentAngle.getText().toString()) * (float)(0.52 / 30)));
                        TimeUnit.MILLISECONDS.sleep(20);
                    }


                    //Vertical speed Max (millimeters per second)
                    for(int iBcl = 0; iBcl < 5; iBcl ++) {
                        SetDroneVERTICALSPEED(mPilot.RecupSequenceNum(1), Integer.valueOf(CurrentVerticalSpeed.getText().toString()));
                        TimeUnit.MILLISECONDS.sleep(20);
                    }

                    //Yaw speed Max (radians per second)
                    for(int iBcl = 0; iBcl < 5; iBcl ++) {
                        SetDroneYAWSPEED(mPilot.RecupSequenceNum(1), Integer.valueOf(CurrentYawSpeed.getText().toString()));
                        TimeUnit.MILLISECONDS.sleep(20);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                bAffJoy = cbAffJoy.isChecked();
                bIndicBatterie = cbBatterie.isChecked();
                bIndicAltitude = cbAltitude.isChecked();
                bIndicVitesse = cbVitesse.isChecked();
                bIndicAvArr = cbAvArr.isChecked();
                bIndicGD = cbGD.isChecked();
                bIndicRotation = cbRotation.isChecked();

                bActivationVideo = cbActivationVideo.isChecked();

                bDebugMode = cbDebugMode.isChecked();
                Intent mIntent;
                if(spinInterfacePilotage.getSelectedItem().equals("Joystick")){
                    mIntent = new Intent(SettingStartupActivity.this, MainActivityPilotage.class);
                } else{
                    mIntent = new Intent(SettingStartupActivity.this, MainActivityInclinaison.class);
                }

                Bundle mBundle = new Bundle();
                mBundle.putBoolean("INDIC_BATT", bIndicBatterie);
                mBundle.putBoolean("INDIC_ALT", bIndicAltitude);
                mBundle.putBoolean("INDIC_VIT", bIndicVitesse);
                mBundle.putBoolean("IDNIC_AVAR", bIndicAvArr);
                mBundle.putBoolean("INDIC_GD", bIndicGD);
                mBundle.putBoolean("IDNIC_ROT", bIndicRotation);
                mBundle.putBoolean("VIDEO_MODE", bActivationVideo);
                mBundle.putBoolean("DEBUG_MODE", bDebugMode);
                mBundle.putBoolean("AffJoy", bAffJoy);
                mIntent.putExtras(mBundle);
                startActivity(mIntent);

            }
        });
    }

    public void ClickAccept(View view){
        if (cbAcceptUse.isChecked()){
            fab.show();
        }
        else{
            fab.hide();
        }
    }

    public String SetDroneALT(int iCount, int iVal){
        //iCount is the command number, iVal is the maximum altitude of the drone in millimeters.
        String sResult;
        /*The pressure sensor allow altitude measurement at any height. */
        if(iVal > 0) {
            sResult = "AT*CONFIG=" + iCount + ",\"control:altitude_max\",\""+iVal+"\"";
            Toast.makeText(SettingStartupActivity.this,"AT*CONFIG=" + iCount + ",\"control:altitude_max\",\""+iVal+"\"", Toast.LENGTH_SHORT).show();
        }else{
            sResult = "AT*CONFIG=" + iCount + ",\"control:altitude_max\",\"500\"";
            Toast.makeText(SettingStartupActivity.this,"alt 500", Toast.LENGTH_SHORT).show();
        }
        return sResult;
    }

    public String SetDroneCAM(int iCount, int iVal){
        /*iCount is the command number,
          iVal is camera used for the video stream ; 1 for the vertical one, 2 for the horizontal one.*/
        String sResult;
        //This is a security if iVal isn't 1 or 2, the default camera is the horizontal one.
        if(iVal == 1) {
            sResult = "AT*CONFIG="+iCount+",\"video:video_channel\",\"1\"";
        }else{
            sResult = "AT*CONFIG="+iCount+",\"video:video_channel\",\"2\"";
        }
        return sResult;
    }

    public String SetDroneVERTICALSPEED(int iCount, int iVal){
        /*iCount is the command number,
          iVal is the maximum vertical speed of the drone in millimeters per second*/
        String sResult;
        //This is a security, recommended values goes from 200 to 2000. Others values may cause instability.
        if(iVal >= 200 && iVal <= 2000){
            sResult = "AT*CONFIG="+iCount+",\"control:control_vz_max\",\""+iVal+"\"";
        }else{
            sResult = "AT*CONFIG="+iCount+",\"control:control_vz_max\",\""+200+"\"";
        }
        return sResult;
    }

    public String SetDroneYAWSPEED(int iCount, int iVal){
        /*iCount is the command number,
          iVal is the maximum yaw speed of the drone in radian per second*/
        String sResult;
        //This is a security, recommended values goes from 1 to 6. Others values may cause instability.
        if(iVal >= 1 && iVal <= 6){
            sResult = "AT*CONFIG="+iCount+",\"control:control_yaw\",\""+iVal+"\"";
        }else{
            sResult = "AT*CONFIG="+iCount+",\"control:control_yaw\",\"1\"";
        }
        return sResult;
    }

    public String SetDroneMAXANGLE(int iCount, float fVal){
        /*iCount is the command number,
          fVal is the maximum pitch and roll angle of the drone in radian*/
        String sResult;
        //This is a security, max is 0.52 . Higher values are not reliable and might not allow the drone to stay at the same altitude.
        if(fVal <= 0.52 && fVal >= 0.0){
            sResult = "AT*CONFIG="+iCount+",\"control:euler_angle_max\",\""+fVal+"\"";
        }else{
            sResult = "AT*CONFIG="+iCount+",\"control:euler_angle_max\",\"0.50\"";
        }
        return sResult;
    }
}
