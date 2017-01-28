package com.example.admin.pilotage;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.vov.vitamio.MediaPlayer;

/********************************************************
 * Programme liants les calsses Joystick, Pilotage, DroneManager avec Interface de test (MAIN ACTIVITY)
 * ******************************************************
 * CHANGELOG :
 ********************************************************

 *********************************************************/

public class MainActivityPilotage extends AppCompatActivity {

    // Durée des taches periodique  en MS
    private static final int iDurationFlyCommand = 40;
    private static final int iDurationWDCommand = 900;
    private static final int iDurationRecordingBDD = 1000;

    private boolean bIndicBatterie;
    private boolean bIndicAltitude;
    private boolean bIndicVitesse;
    private boolean bIndicAvArr;
    private boolean bIndicGD;
    private boolean bIndicRotation;
    private boolean bActivationVideo;
    private boolean bDebugMode;
    private boolean bAffJoy;
    private float fJoy1x;
    private float fJoy1y;
    private float fJoy2x;
    private float fJoy2y;


    //Flag autorisation prise en compte des commandes
    private boolean bFlag;

    // Variable pour savoir si le drone est en vol
    private boolean bDroneEnVol;

    // Variable NavData
    int iNavAltitude;
    int iNavBatterie;
    int iNavAv_Ar; // Avant - Arriere
    int iNavRot;  // Rotation
    int iNavG_D; // Gauche - Droite
    int iNavSpeed;

    // Variable resumé BDD Navdata
    int iAltMax;
    int iVitMax;
    int iAltMoy;
    int iVitMoy;

    // String affichage NavData
    String sNavAltitude;
    String sNavBatterie;
    String sNavAv_Ar; // Avant - Arriere
    String sNavRot;  // Rotation
    String sNavG_D; // Gauche - Droite
    String sNavSpeed;

    // Creation objet Pilotage :
    Pilotage mPilot;

    // Creation TextView:
    TextView txtATComand;
    TextView txtTaskPeriodique;

    TextView txtNavAltitude;
    TextView txtNavBatterie;
    TextView txtNavAvArr;
    TextView txtNavRot;
    TextView txtNavG_D;
    TextView txtNavSpeed;

    // Bouton affichage resumées vol (Navdata):
    Button buttAffNavDataBDD;
    Button  butAfLed;
    Button   butAfShot;


    // Valueur pourcentage pilotage
    float fPilot;

    //Commande AT genere
    String strATComand;
    String strATCWD;

    byte[] BBuffer;

    MediaPlayer mMediaPlayer;
    DisplayMetrics metrics;
    SurfaceView mPreview;
    VideoManager mVideo;
    PhotoSaver mSaver;


    // Creation du drone
    DroneManager mDrone;

    // Creation de navdata
    NavData mNavData;

    // Creation NavData
    NavdataManager_BDD mNavDataManager;

    // Creation joystick :
    JoystickView JoyLeft;
    JoystickView JoyRight;

    String str1;
    ArrayList list;

    boolean bAppui;
    int iTouche;
    int iPad;
    double dJoy1X, dJoy1Y,dJoy2X, dJoy2Y;
    GamepadManager Manette;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Obtention des paramètres de SettingsStartupActivity
        GetBundleSettingsStartup();
        //Asociation de la vue au XML
        setContentView(R.layout.activity_main_pilotage);
        //Initialisation de l'UI
        InitialiseUI();

        // Instanciation objets
        mPilot = new Pilotage();
        mDrone = new DroneManager();
        mNavData = new NavData();
        mNavDataManager = new NavdataManager_BDD(this);

        //Initialisation video si paramètre activer
        if(bActivationVideo==true){
            InitialiseVideo();
        }
        // Initialisation des variables
        InitialiseVariables();





        //Initialisation des tâches periodiques
        InitPeriodicTask();
        Manette = new GamepadManager();
        // Instantiation of a new JoystickMovedListener interface,
        // we override his OnMoved method so we can use the x and y datas of the JoyRight.
        JoyLeft.setOnJostickMovedListener(new JoystickMovedListener() {
            @Override
            public void OnMoved(double x, double y) {
                float xFloat = (float) x;
                float yFloat = (float) y;
                bFlag = true;
                try {
                    mPilot.PilotageHorizontal(xFloat, -yFloat);
                } catch (NumberFormatException e) {
                    bFlag = false;
                }

            }

            @Override
            public void OnReleased() {
                mPilot.PilotageHorizontal(0, 0);
            }

        });

        // Instantiation of a new JoystickMovedListener interface,
        // we override his OnMoved method so we can use the x and y datas of the JoyRight.
        JoyRight.setOnJostickMovedListener(new JoystickMovedListener() {
            @Override
            public void OnMoved(double x, double y) {
                float xFloat = (float) x;
                float yFloat = (float) y;
                bFlag = true;
                try {
                    mPilot.PilotageVertical(xFloat, -yFloat);
                } catch (NumberFormatException e) {
                    bFlag = false;
                }
            }

            @Override
            public void OnReleased() {
                mPilot.PilotageVertical(0, 0);
            }

        });
    }

    public void ShowSimpleFlightData(View view) {
        Builder mBuilderAffichageNavDataResume = new Builder(this);
        StringBuffer mStrBuffer = new StringBuffer();

        iAltMax = mNavDataManager.Altmax();
        iAltMoy = mNavDataManager.Altitudemoy();
        iVitMax = mNavDataManager.Speedmax();
        iVitMoy = mNavDataManager.Speedmoy();

        mStrBuffer.append("Valeurs maximales :\n");
        mStrBuffer.append("\n");
        mStrBuffer.append("Altitude: " + iAltMax + " cm\n");
        mStrBuffer.append("Vitesse: " + iVitMax + " cm/s\n");
        mStrBuffer.append("\n\n");
        mStrBuffer.append("Valeurs moyennes :\n");
        mStrBuffer.append("\n");
        mStrBuffer.append("Altitude: " + iAltMoy + " cm\n");
        mStrBuffer.append("Vitesse: " + iVitMoy + " cm/s\n");
        mStrBuffer.append("\n");

        mBuilderAffichageNavDataResume.setCancelable(true);
        mBuilderAffichageNavDataResume.setTitle("Résumé du vol");
        mBuilderAffichageNavDataResume.setMessage(mStrBuffer);


        mBuilderAffichageNavDataResume.setPositiveButton("Détails", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               ShowCompleteFlightData();
            }
        });

        mBuilderAffichageNavDataResume.setNegativeButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mBuilderAffichageNavDataResume.show();

    }

    public void ShowCompleteFlightData() {
        Builder mBuilderAffichageNavData = new Builder(this);
        StringBuffer mStrBuffer = new StringBuffer();

        Cursor cCursor = mNavDataManager.getNavdata();

        if (cCursor.moveToFirst()) {
            while (cCursor.moveToNext()) {

                mStrBuffer.append("Temps: " + (cCursor.getInt(cCursor.getColumnIndex(NavdataManager_BDD.KEY_ID)) / 2) + " s\n");
                mStrBuffer.append("Batterie: " + cCursor.getString(cCursor.getColumnIndex(NavdataManager_BDD.KEY_BATTERY_NAV)) + " %\n");
                mStrBuffer.append("Altitude: " + cCursor.getString(cCursor.getColumnIndex(NavdataManager_BDD.KEY_ALTITUDE_NAV)) + " mm\n");
                mStrBuffer.append("Vitesse: " + cCursor.getString(cCursor.getColumnIndex(NavdataManager_BDD.KEY_SPEED_NAV)) + " cm/s\n");
                mStrBuffer.append("Av/Arr: " + cCursor.getString(cCursor.getColumnIndex(NavdataManager_BDD.KEY_INCLAVAR_NAV)) + "%\n");
                mStrBuffer.append("G/D: " + cCursor.getString(cCursor.getColumnIndex(NavdataManager_BDD.KEY_INCLDG_NAV)) + " %\n");
                mStrBuffer.append("Rotation: " + cCursor.getString(cCursor.getColumnIndex(NavdataManager_BDD.KEY_ROTATION_NAV)) + " %\n");
                mStrBuffer.append("\n");
            }
            cCursor.close(); // fermeture du curseur
        } else {
            mStrBuffer.append("Aucune donnée a afficher");
        }

        // fermeture du gestionnaire
        mNavDataManager.close();

        mBuilderAffichageNavData.setCancelable(true);
        mBuilderAffichageNavData.setTitle("Données détailées du vol");

        mBuilderAffichageNavData.setMessage(mStrBuffer);

        mBuilderAffichageNavData.setNegativeButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mBuilderAffichageNavData.show();

    }

    public void Decollage(View view) {
        strATComand = mPilot.Decollage();
        mDrone.SendCMD(strATComand);
        txtATComand.setText(strATComand);
        bDroneEnVol = true;
        buttAffNavDataBDD.setVisibility(View.INVISIBLE);
        mPilot.Hovering();
    }

    public void Aterissage(View view) {
        if (bDroneEnVol == true) { // Si on étais décoller et qu'on atterie (avec cette fonction))
            buttAffNavDataBDD.setVisibility(View.VISIBLE);
        }
        bDroneEnVol = false;
        strATComand = mPilot.Atterrissage();
        mDrone.SendCMD(strATComand);
        txtATComand.setText(strATComand);
        mPilot.Hovering();
    }

    public void Urgence(View view) {
        bDroneEnVol = false;
        strATComand = mPilot.Urgence();
        mDrone.SendCMD(strATComand);
        txtATComand.setText(strATComand);
    }

    public void Led(View view) {
        strATComand = mPilot.LedControl();
        mDrone.SendCMD(strATComand);
        txtATComand.setText(strATComand);
    }


    public void Shot(View view){
        try {
            mSaver.SavePicture();
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Erreur photo",Toast.LENGTH_SHORT).show();
        }

    }

    // Instruction de la tache periodique
    class PeriodicSendFlyCommand implements Runnable {
            public void run() {

                BBuffer = mDrone.GetBuffer();
				
				// Recupération des NavData dans variables correspondantes a partir du buffer
				ParseNavData();
				
				//Mise a jour string NavData
				SetStringNavData();

                if(bDroneEnVol == true) {
                    strATComand = mPilot.BuildCMD(bFlag);
                    mDrone.SendCMD(strATComand);
                }
            }

        }

    class PeriodicRecordingBDD implements Runnable {
        public void run() {

            if(bDroneEnVol == true) {
                mNavDataManager.open();
                mNavDataManager.addNavdata(new Navdata_BDD(0, sNavBatterie, sNavAv_Ar, sNavRot, sNavG_D, sNavSpeed, sNavAltitude));

            }

        }

    }


    class PeriodicSendWDCommand implements Runnable {
        public void run() {
            // LES 2 LIGNES CI-DESSOUS SONT A DECOMMENTER SI ON TEST LE WATCHDOG
            strATCWD = mPilot.WatchDog();
            mDrone.SendCMD(strATCWD);
            runOnUiThread(new Maj_UI_WD());
        }

    }


    class Maj_UI_WD implements Runnable {
        public void run() {
            if(bDebugMode==true) {
                txtATComand.setText(strATCWD);
            }
			SetIndicatorNavData();

        }
    }

    public void onBackPressed() {
        strATComand = mPilot.Atterrissage();
        mDrone.SendCMD(strATComand);
        Toast.makeText(getApplicationContext(), "Forcage envoie commande atterissage !", Toast.LENGTH_SHORT).show();
        bDroneEnVol=false;
    }

    // Lorque l'application passe en arriere plan ou se ferme peut être
    public void onStop() {
        super.onStop();
        if(bDroneEnVol == true) {
            strATComand = mPilot.Atterrissage();
            mDrone.SendCMD(strATComand);
            Toast.makeText(getApplicationContext(), "Aterissage du drone !", Toast.LENGTH_LONG).show();
            bDroneEnVol=false;
        }
    }
	
	private void GetBundleSettingsStartup(){
		// Reception des données de classe StartupSettingActivity
       Bundle mBundle = getIntent().getExtras();
	   
	    if(mBundle!=null) {
            bIndicBatterie = mBundle.getBoolean("INDIC_BATT");
            bIndicAltitude = mBundle.getBoolean("INDIC_ALT");
            bIndicVitesse = mBundle.getBoolean("INDIC_VIT");
            bIndicAvArr = mBundle.getBoolean("IDNIC_AVAR");
            bIndicGD = mBundle.getBoolean("INDIC_GD");
            bIndicRotation = mBundle.getBoolean("IDNIC_ROT");
            bActivationVideo = mBundle.getBoolean("VIDEO_MODE");
            bDebugMode = mBundle.getBoolean("DEBUG_MODE");
            bAffJoy = mBundle.getBoolean("AffJoy");
        }
	}
	
	private void InitialiseUI(){
		
		// Association textview NavData:
        txtNavAltitude =  (TextView) findViewById(R.id.indic_altitude);
        txtNavAvArr =  (TextView) findViewById(R.id.indic_avant_arriere);
        txtNavBatterie =  (TextView) findViewById(R.id.indic_batterie);
        txtNavG_D =  (TextView) findViewById(R.id.indic_gauche_droite);
        txtNavRot =  (TextView) findViewById(R.id.indic_rotation);
        txtNavSpeed =  (TextView) findViewById(R.id.indic_vitesse);

        // Association textview Debug
        txtATComand = (TextView) findViewById(R.id.txtATCMD);
        txtTaskPeriodique = (TextView) findViewById(R.id.indic_periodic_task);
        txtTaskPeriodique.setText(iDurationFlyCommand + "MS");

        //Bouton affichage données vol BDD NavData:
        buttAffNavDataBDD=(Button)findViewById(R.id.butt_data_flying);

        butAfLed=(Button)findViewById(R.id.butt_led);
        butAfShot=(Button)findViewById(R.id.butt_shot);

        // Instantiation of the Left Joystick
        JoyLeft= (JoystickView)findViewById(R.id.JoyLeft);
        JoyRight= (JoystickView)findViewById(R.id.JoyRight);

		// Asociation SurfaceView objet Preview
        mPreview = (SurfaceView) findViewById(R.id.surface);

        // This code force the view to be fullscreen and with the landscape orientation.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Disparition du bouton d'engistrement de la BDD au déamrage
        buttAffNavDataBDD.setVisibility(View.INVISIBLE);

		// Montrer/cahcer indicateur suivant options choisie au démarage:
		if(bIndicBatterie == false){
            txtNavBatterie.setVisibility(View.GONE);
        }
        if (bIndicAltitude == false){
            txtNavAltitude.setVisibility(View.GONE);
        }
        if(bIndicVitesse == false){
            txtNavSpeed.setVisibility(View.GONE);
        }
        if (bIndicAvArr == false){
            txtNavAvArr.setVisibility(View.GONE);
        }
        if(bIndicGD == false){
            txtNavG_D.setVisibility(View.GONE);
        }
        if (bIndicRotation == false){
            txtNavRot.setVisibility(View.GONE);
        }

        if(bActivationVideo==false){
            mPreview.setVisibility(View.GONE);
		}
		
		if(bDebugMode==false){
            txtATComand.setVisibility(View.GONE);
            txtTaskPeriodique.setVisibility(View.GONE);
        }
        if (bAffJoy==false){
            JoyLeft.setVisibility(View.INVISIBLE);
            JoyRight.setVisibility(View.INVISIBLE);
            butAfLed.setVisibility(View.INVISIBLE);
            butAfShot.setVisibility(View.INVISIBLE);


        }


		
}

	private void InitialiseVideo(){
            metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            mVideo = new VideoManager(this, mPreview,metrics);
            mMediaPlayer = mVideo.PlayVideo();
            mSaver = new PhotoSaver(this,mMediaPlayer);
	}

	private void InitialiseVariables(){
		
        //Initialisation de la valeur du pilotage
        fPilot = 1.0f;

        // Initialisation NavData
        iNavAltitude=0;
        iNavAv_Ar=0;
        iNavBatterie=0;
        iNavG_D=0;
        iNavRot=0;
        iNavSpeed=0;

        // Iniitialisation variable resumé BDD Navdata
        iAltMax=0;
        iVitMax=0;
        iAltMoy=0;
        iVitMoy=0;
		
		// Initialisation de la varaible envoi trame période périodique
		bDroneEnVol = false;
		
        // Definition de la trame en null pour eviter crash, Pas utiliser dans cette version ...
        mPilot.Hovering();
	}

	private void InitPeriodicTask(){
		// Creation objet tache periodique :
        ScheduledExecutorService scheduleTaskExecutor;

        PeriodicSendFlyCommand mPeriodicSendFlyCommand = new PeriodicSendFlyCommand();
        PeriodicSendWDCommand mPeriodicSendWDCommand = new PeriodicSendWDCommand();
        PeriodicRecordingBDD mPeriodicRecordingBDD = new PeriodicRecordingBDD();

        //Creation tâche manager avec en parametres le nombre de taches
        scheduleTaskExecutor = Executors.newScheduledThreadPool(3);

        //Creation des tâches périodiques
        scheduleTaskExecutor.scheduleAtFixedRate(mPeriodicSendFlyCommand, 0, iDurationFlyCommand, TimeUnit.MILLISECONDS);
        scheduleTaskExecutor.scheduleAtFixedRate(mPeriodicSendWDCommand, 0, iDurationWDCommand, TimeUnit.MILLISECONDS);
        scheduleTaskExecutor.scheduleAtFixedRate(mPeriodicRecordingBDD, 0, iDurationRecordingBDD, TimeUnit.MILLISECONDS);
	}

	private void ParseNavData(){
		iNavBatterie = mNavData.GetBattery(BBuffer);
        iNavAltitude= mNavData.GetAltitude(BBuffer);
        iNavAv_Ar = mNavData.GetPitch(BBuffer);
        iNavG_D= mNavData.GetRoll(BBuffer);
        iNavRot= mNavData.GetYaw(BBuffer);
		iNavSpeed=mNavData.GetSpeed(BBuffer);
	}
	
	private void SetStringNavData(){
		sNavBatterie=String.valueOf(iNavBatterie);
		sNavAltitude=String.valueOf(iNavAltitude);
		sNavAv_Ar=String.valueOf(iNavAv_Ar);
		sNavG_D=String.valueOf(iNavG_D);
		sNavRot=String.valueOf(iNavRot);
		sNavSpeed=String.valueOf(iNavSpeed);
	}
	
	private void SetIndicatorNavData(){
			if (bIndicBatterie == true) {
                txtNavBatterie.setText("Batt: " + iNavBatterie + "%");
            }
            if(bIndicAltitude ==true) {
                txtNavAltitude.setText("Alt: " + iNavAv_Ar + "m");
            }
            if(bIndicVitesse==true) {
                txtNavSpeed.setText("Vitt: " + iNavSpeed + "m/s");
            }
            if(bIndicAvArr==true) {
                txtNavAvArr.setText("Av/Ar: " + iNavAv_Ar);
            }
            if(bIndicGD ==true) {
                txtNavG_D.setText("G/D: " + iNavG_D);
            }
            if(bIndicRotation==true) {
                txtNavRot.setText("Rot :" + iNavRot);
            }


	}

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {

        // BOOLEEN QUI RECUPERE L'INFORMATION: "UTILISATEUR A APPUIE SUR UNE TOUCHE OU L'A RELACHE"
        // TRUE = TOUCHE APPUIEE  |  FALSE = TOUCHE RELACHEE
        bAppui = Manette.GetKeyState(keyEvent);
        iTouche = Manette.GetKey(keyEvent);


        if (bAppui) {

            switch (iTouche) {
                case 1:            // APPUI A
                    // Text2.setText("A");
                    strATComand = mPilot.Decollage();
                    mDrone.SendCMD(strATComand);
                    txtATComand.setText(strATComand);
                    bDroneEnVol = true;
                    buttAffNavDataBDD.setVisibility(View.INVISIBLE);
                    mPilot.Hovering();
                    break;

                case 2:
                    // Text2.setText("B");// APPUI B
                    if (bDroneEnVol == true) { // Si on étais décoller et qu'on atterie (avec cette fonction))
                        buttAffNavDataBDD.setVisibility(View.VISIBLE);
                    }
                    bDroneEnVol = false;
                    strATComand = mPilot.Atterrissage();
                    mDrone.SendCMD(strATComand);
                    txtATComand.setText(strATComand);
                    mPilot.Hovering();
                    break;

                case 3:
                    //Text2.setText("X");// APPUI X
                    bDroneEnVol = false;
                    strATComand = mPilot.Urgence();
                    mDrone.SendCMD(strATComand);
                    txtATComand.setText(strATComand);
                    break;

                case 4:
                    // Text2.setText("Y");// APPUI Y
                    try {
                        mSaver.SavePicture();
                    }
                    catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Erreur photo",Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 5:
                    // Text2.setText("start");// APPUI START
                    break;

                case 6:
                    //Text2.setText("select");// APPUI SELECT
                    break;

                case 7:			// APPUI R1
                    // Text2.setText("R1");

                    break;

                case 8:			// APPUI L1
                    //Text2.setText("L1");
                    strATComand = mPilot.LedControl2();
                    mDrone.SendCMD(strATComand);
                    txtATComand.setText(strATComand);

                    break;

                case 9:			// APPUI R2
                    //Text2.setText("R2");

                    break;

                case 10:			// APPUI L2
                    //Text2.setText("L2");
                    break;

            }

        }
        else
        {

            switch(iTouche)
            {
                case 1:			// RELACHE A
                    // Text2.setText("");
                    break;

                case 2:			// RELACHE B
                    // Text2.setText("");
                    break;

                case 3:			// RELACHE X
                    //Text2.setText("");
                    break;

                case 4:			// RELACHE Y
                    //Text2.setText("");
                    break;

                case 5:		// RELACHE START
                    // Text2.setText("");
                    break;
                case 6:
                    // Text2.setText("");// APPUI SELECT
                    break;

                case 7:			// APPUI R1
                    // Text2.setText("");

                    break;

                case 8:			// APPUI L1
                    //Text2.setText("");

                    break;

                case 9:			// APPUI R2
                    //Text2.setText("");

                    break;

                case 10:			// APPUI L2
                    //Text2.setText("");
                    break;


            }




        }
        return true;
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent motionEvent){		// FONCTION S'APPELLANT pour le pad et les joy

        // RECUPERATION DE LA DIRECTION PRESSEE
        iPad = Manette.GetDPad(motionEvent);

        switch(iPad)
        {
            case 1:		// APPUI HAUT
                // Text2.setText("HAUT");
                break;

            case 2:			// APPUI DROITE
                // Text2.setText("DROIT");
                break;

            case 3:		// APPUI BAS
                //Text2.setText("BAS");
                break;

            case 4:		// APPUI GAUCHE
                //Text2.setText("GAUCHE");
                break;

            default :		// RELACHEMENT
                //Text2.setText("");
                break;

        }

        // RECUPERE LES COORDONNEES DES 2 JOYSTICKS


        // JOYSTICK 1
        dJoy1X = Manette.GetJ1PosX(motionEvent);

        dJoy1Y = Manette.GetJ1PosY(motionEvent);
        // JOYSTICK 2
        dJoy2X = Manette.GetJ2PosX(motionEvent);
        dJoy2Y = Manette.GetJ2PosY(motionEvent);

        fJoy1x=(float) dJoy1X;
        fJoy1y=(float) dJoy1Y;
        fJoy2x=(float) dJoy2X;
        fJoy2y=(float) dJoy2Y;

        bFlag=true;
        try {

            mPilot.PilotageHorizontal( fJoy1x, -fJoy1y);

        } catch (NumberFormatException e) {
            bFlag = false;
        }
        try {


            mPilot.PilotageVertical(fJoy2x,-fJoy2y);

        } catch (NumberFormatException e) {
            bFlag = false;
        }





        return true;
    }





    }
