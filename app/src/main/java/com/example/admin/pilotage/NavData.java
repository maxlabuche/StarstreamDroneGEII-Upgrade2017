package com.example.admin.pilotage;

//06.01 : edition de la classe terminée
//Modif alex(11/01/2015) : Ajout securiter : try catch.

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Class which extracts the information from the sensors of the drone,
 * treats them, and sends back a usable value for every sensor.
 */
public class NavData {

    public void NavData(){

    }

    /**
     * Function that returns the charge of the battery
     * @param buffer =hexadecimal data communication sent by the drone
     * @return the percentage of charge of the battery
     */
    public int GetBattery(byte[] buffer){
        int iBatt;
        try {
            iBatt = byteArrayToInt(buffer, 24);
        } catch(NumberFormatException e){
            iBatt=0;
        }
        return iBatt;

    }

    /**
     * Function that returns the pitch of the drone in percentage
     * @param buffer
     * @return the pitch
     */
    public int GetPitch(byte[] buffer){

        float fPitch = 0;
        int iPitch = 0;
        try {
            fPitch = byteArrayToFloat(buffer, 28);
        } catch(NumberFormatException e){
            fPitch=0;
        }
        fPitch = (fPitch/86000)*100;
        iPitch = (int) fPitch;
        return iPitch;
    }

    /**
     * Function that returns the Yaw of the drone in percentage
     * @param buffer
     * @return the yaw
     */
    public int GetYaw(byte[] buffer){ //Rotation

        float fYaw = 0;
        int iYaw = 0;
        try {
            fYaw = byteArrayToFloat(buffer, 36);
        } catch(NumberFormatException e){
            fYaw=0;
        }
        fYaw = (fYaw/86000)*100;
        iYaw = (int) fYaw;
        return iYaw;
    }

    /**
     * Function that returns the Roll of the drone in percentage
     * @param buffer
     * @return the roll
     */
    public int GetRoll(byte[] buffer){ //Gauche Droite

        float fRoll = 0;
        int iRoll = 0;
        try {
            fRoll = byteArrayToFloat(buffer, 32);
        } catch (NumberFormatException e){
            fRoll=0; //Valeur par defaut si echec.
        }
        fRoll = (fRoll/86000)*100;
        iRoll = (int) fRoll;
        return iRoll;
    }

    /**
     * Function that return the speed of the drone by a mathematical formula
     * @param buffer
     * @return speed
     */
    public int GetSpeed(byte[] buffer){ //fait

        float vx = 0;
        float vy = 0;
        float vz = 0;
        int iSpeed = 0;
        try {
            vx = byteArrayToFloat(buffer, 44);
        } catch (NumberFormatException e){
            vx=0; //Valeur par defaut si echec.
        }
        try {
            vy = byteArrayToFloat(buffer, 48);
        } catch(NumberFormatException e){
            vy=0; //Valeur par defaut si echec.
        }
        try{
            vz = byteArrayToFloat(buffer, 52);
        } catch(NumberFormatException e){
            vz=0; //Valeur par defaut si echec.
        }
        iSpeed = (int) Math.sqrt(vx*vx + vy*vy + vz*vz);
        return iSpeed;

    }


    /**
     * Function that return the altitude of the drone in mm
     * @param buffer
     * @return the altitude
     */
    public int GetAltitude(byte[] buffer){
        int iAlt;
        try {
            iAlt = byteArrayToInt(buffer, 40);
        } catch(NumberFormatException e){
            iAlt=0; //Valeur par defaut si echec.
        }
        return iAlt;
    }

    /**
     * Conversion function to extract the part of the buffer required for each function and convert
     * it into an integer value
     * @param b buffer frome drone
     * @param offset interesting part of the buffer different for each sensor
     * @return the int value of the requested part of the buffer
     */
    public int byteArrayToInt(byte[] b, int offset){

        int value = 0;
        for(int i = 3; i >= 0; i--)
        {
            int shift = i * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }


    /**
     * Conversion function to extract the part of the buffer required for each function and convert
     * it into a float value (with morte precision than an int)
     * @param buffer buffer frome drone
     * @param offset interesting part of the buffer different for each sensor
     * @return the float value of the requested part of the buffer
     */
    public float byteArrayToFloat(byte[] buffer, int offset){

        byte[] buff_tmp = new byte[4];
        int iBcl = 0;
        float fResult=0;

        for(iBcl=0; iBcl<4; iBcl++)
        {
            buff_tmp[iBcl]=buffer[iBcl+offset];
        }
        fResult = ByteBuffer.wrap(buff_tmp).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        return fResult;

    }


}