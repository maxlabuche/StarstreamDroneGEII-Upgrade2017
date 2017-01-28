package com.example.admin.pilotage;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;

import io.vov.vitamio.MediaPlayer;

/**
 * Handles video recording and image capturing.
 * <p>This class is independent of VideoManager and works differently.</p>
 * @see VideoManager
 * @author Jules Simon
 */
public class PhotoSaver {

    // Variables used to save the picture
    /**
     * Base name used to name all the pictures, with values of the day/month/year
     */
    String filename;
    /**
     * Final name of the picture.
     * It is obtained by adding the seconds/minutes/hours of the moment of the capture to filename.
     * @see PhotoSaver#filename
     */
    String finalname;
    Bitmap image;
    /**
     * Date used to name the files
     */
    Calendar rightNow;
    MediaPlayer mMediaPlayer;
    Context context;
    String imgname;

    // Variables used to SavePicture the stream
    /**
     * This is the stream that will be recorded.
     */
    DataInputStream in;
    /**
     * This is the file in which the stream will be recorded
     */
    FileOutputStream VideoFile;
    int len;
    byte Buffer[] = new byte[8192];

    /**
     * This url is set to the adress of the live video feed.
     * @see PhotoSaver#path
     */
    URL url;
    URLConnection urlConnection;
    Socket feed_socket;
    Thread tRecordFeed;

    RecordFeed mRecordFeed;
    Processing mProcessor;
    byte Pattern[] = {0x50, 0x61, 0x56, 0x45}; // PaVE en hexa

    String path = "tcp://192.168.1.1:5555/";

    /**
     * Initializes the filename used to save the pictures. Initializes the objects used for video recording.
     * @param c Used to display toasts
     * @param m Mediaplayer from which the frames are saved.
     */
    public PhotoSaver(Context c, MediaPlayer m) {
        this.context = c;
        this.mMediaPlayer = m;
        rightNow = Calendar.getInstance();
        filename = rightNow.get(Calendar.DAY_OF_MONTH) + "_" + (rightNow.get(Calendar.MONTH) + 1) + "_" + rightNow.get(Calendar.YEAR) + ".jpeg";

        // Seting up the connection in order to SavePicture the live video feed.
        try {
            url = new URL(path);
            urlConnection = url.openConnection();
        } catch (IOException e) {

        }
        // Used to SavePicture and process the live feed.
        mProcessor = new Processing();
        mRecordFeed = new RecordFeed();
    }

    /**
     * Gets the current frame of the MediaPlayer and saves it in the local storage of the phone at
     * the PNG format.
     */
    public void SavePicture() {
        if (Environment.getExternalStorageState() != null) {
            try {
                image = mMediaPlayer.getCurrentFrame();
                File picture = getOutputMediaFile();
                FileOutputStream fos = new FileOutputStream(picture);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                Toast.makeText(context, "Picture saved:" + imgname, Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                Toast.makeText(context, "File error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(context, "Failed to close", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Directory unavailable", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Initializes the file which will be used to save the frame. Called in the SavePicture() method.
     *
     * @return the file to be used.
     */
    private File getOutputMediaFile() {

        rightNow = Calendar.getInstance();
        finalname = "DronePicture_" + rightNow.get(Calendar.HOUR) + ":" + rightNow.get(Calendar.MINUTE) + ":" + rightNow.get(Calendar.SECOND) + "_" + filename;
        //Create a media file name
        File mediaFile;
        imgname = Environment.getExternalStorageDirectory() + "/Pictures/" + finalname;
        mediaFile = new File(imgname);
        return mediaFile;
    }

    /**
     * Starts the video recording thread.
     */
    void RecordVideo(){
        tRecordFeed = new Thread(new RecordFeed());
        tRecordFeed.start();
    }

    /**
     * Stops the video recording.
     */
    void StopRecord(){
        mRecordFeed.Flag(true);
    }

    /**
     * Makes a copy of the video file without the PaVE header.
     */
    void StreamCopy_No_PaVE(){
        try{
            // Reading the original file
            InputStream in = new FileInputStream(Environment.getExternalStorageDirectory() + "/Pictures/" + "video.mp4");
            // Creating the output file
            OutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/Pictures/" + "videocopy.mp4");
            // Size of the bytes read from the InputStream
            int length = 0;
            // Buffer used to scan through the list and gather the index of each PaVE header
            byte Tampon_Liste[] = new byte[8192];
            // Buffer used to go through the list
            byte Tampon_Sup[] = new byte[1];
            // Used to keep track of all the bytes read in total
            Integer bytes_lus = 0;
            int i = 0;
            // Used to check the amount of bytes skipped when calling the skip() method
            long bytes_skipped = 0;
            // Value of the header list's last index
            int LastIndex_Totaux = 0;

            Integer tmp = 0;
            Integer Tmp2 = 0;

            ArrayList<Integer> ListeHeader_Tampon = new ArrayList<>();
            ArrayList<Integer> ListeHeader_Totaux = new ArrayList<>();

            // Boucle de lecture des headers
            while((length=in.read(Tampon_Liste)) != -1){

                ListeHeader_Tampon = mProcessor.indexOf_bufferedData(Tampon_Liste, Pattern, bytes_lus);
                for (i = 0; i < ListeHeader_Tampon.size(); i++) {
                    tmp = (ListeHeader_Tampon.get(i));
                    ListeHeader_Totaux.add(tmp);
                }

                bytes_lus = length + bytes_lus;
            }

            LastIndex_Totaux = ListeHeader_Totaux.size()-1;
            // Fermeture du fichier
            in.close();

            bytes_lus = 0;
            in = new FileInputStream(Environment.getExternalStorageDirectory() + "/Pictures/" + "video.mp4");
            i = 0;

            // Recherche des headers et recopiage du fichier
            while((length=in.read(Tampon_Sup)) != -1){

                Tmp2 = ListeHeader_Totaux.get(i);

                if((bytes_lus.equals(Tmp2))&& (i<LastIndex_Totaux)){
                    Log.v("Header trouve:", "-----------" + ListeHeader_Totaux.get(i));
                    bytes_skipped = in.skip(63);
                    i++;
                    Log.v("PhotoSaver", "Bytes skipped : " + bytes_skipped);
                    bytes_lus = bytes_lus + (int)bytes_skipped;
                }
                else {
                    out.write(Tampon_Sup, 0, length);
                }

                bytes_lus = length + bytes_lus;

            }

            out.close();
            in.close();

            Log.v("PhotoSaver", "Copie terminee");
        }catch(FileNotFoundException fn){
            Log.v("PhotoSaver", "Copie : file not found");
        }catch (IOException io){
            Log.v("PhotoSaver", "Copie : io exception");
        }

    }

    /**
     * Video recording thread
     */
    class RecordFeed implements Runnable{

        public boolean bStop;

        public void run(){
            try{
                bStop = false;
                // Connection au drone
                feed_socket = new Socket("192.168.1.1",5555);
                Log.v("PhotoSaver", "Socket cree");

                if(feed_socket.isConnected()){
                    Log.v("PhotoSaver", "Socket connecte");
                }
                // Lecture du flux
                in = new DataInputStream(feed_socket.getInputStream());
                // Ecriture du flux
                VideoFile = new FileOutputStream(Environment.getExternalStorageDirectory() + "/Pictures/" + "video.mp4");
                Log.v("PhotoSaver", "Démarrrage enregistrement");

                while((bStop == false)){
                    len = in.read(Buffer);
                    VideoFile.write(Buffer,0,len);
                }

                VideoFile.close();
                Log.v("PhotoSaver", "Fin enregistrement");
                VideoFile.close();
            }catch (IOException io){
                Log.v("PhotoSaver", "IO exeption enregistrement");
            }
        }

        public void Flag(boolean bFlag){
            bStop = bFlag;
        }
    }

}