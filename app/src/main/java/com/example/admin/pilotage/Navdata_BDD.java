package com.example.admin.pilotage;

/**
 * Created by JC on 15/01/2016.
 */

/**
 * Class representing the table Navdata of the bdd
 * It is in this class that all the data corresponding at the table Navdata are created.
 */
public class Navdata_BDD {

    private int id_nav;
    private String battery_nav;
    private String inclavar_nav;
    private String rotation_nav;
    private String incldg_nav;
    private String speed_nav;
    private String altitude_nav;

    /**
     * Constructor of the class
     */
    public Navdata_BDD(int id,String battery,String inclavar, String rotation, String incldg, String speed, String altitude) {
        this.id_nav=id;
        this.battery_nav =battery;
        this.inclavar_nav=inclavar;
        this.rotation_nav=rotation;
        this.incldg_nav=incldg;
        this.speed_nav =speed;
        this.altitude_nav=altitude;
    }

    /**
     * Get and set of all the value of the bdd
     * Allows the display and modification of attributes outside the class
     */

    public int getId_Navdata() { //GET
        return id_nav;
    }

    public void setId_Navdata(int id) { //SET
        this.id_nav = id;
    }
    //-------------------------------------------------------------------
    public String getBattery_Navdata() { //GET
        return battery_nav;
    }

    public void setBattery_Navdata(String batterie) { //SET
        this.battery_nav = batterie;
    }

    //----------------------------------------------------------------
    public String getInclavar_Navdata() { //GET
        return inclavar_nav;
    }

    public void setInclavar_Navdata(String inclavar) { //SET
        this.inclavar_nav = inclavar;
    }

    //-----------------------------------------------------------------

    public String getRotation_Navdata() { //GET
        return rotation_nav;
    }

    public void setRotation_Navdata(String rotation) { //SET
        this.rotation_nav = rotation;
    }

    //-----------------------------------------------------------------

    public String getIncldg_Navdata() { //GET
        return incldg_nav;
    }

    public void setIncldg_Navdata(String incldg) { //SET
        this.incldg_nav = incldg;
    }

    //---------------------------------------------------------------------

    public String getSpeed_Navdata() { //GET
        return speed_nav;
    }

    public void setSpeed_Navdata(String vitesse) { //SET
        this.speed_nav = vitesse;
    }

    //---------------------------------------------------------------------

    public String getAltitude_Navdata() { //GET
        return altitude_nav;
    }

    public void setAltitude_Navdata(String altitude) { //SET
        this.altitude_nav = altitude;
    }



}//Navdata_BDD's class