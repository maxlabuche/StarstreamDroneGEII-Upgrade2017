package com.example.admin.pilotage;


import android.app.Activity;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import io.vov.vitamio.MediaPlayer;

/**
 * Allows real time displaying of the video stream from the drone
 * Requires the library vitamio in order to handle the live stream.
 * @author Jules Simon
 */
public class VideoManager implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener {

    SurfaceHolder holder;
    MediaPlayer mMediaPlayer;
    DisplayMetrics metrics;
    String path ="tcp://192.168.1.1:5555/";

    /**
     * Constructor
     * @param mMainActivity Used to check the librairy
     * @param mPreview Surface on which the video will be displayed
     * @param mMetrics Used to get info about the smartphone's screen
     */
    public VideoManager(Activity mMainActivity, SurfaceView mPreview, DisplayMetrics mMetrics){

        if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(mMainActivity))
            return;

        metrics = mMetrics;

        holder = mPreview.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {

            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                // TODO Auto-generated method stub

            }

            public void surfaceCreated(SurfaceHolder holder) {
                // TODO Auto-generated method stub

            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                // TODO Auto-generated method stub

            }
        });

        holder.setFormat(PixelFormat.RGBA_8888);
        mMediaPlayer = new MediaPlayer(mMainActivity);
    }

    /**
     * Sets up the MediaPlayer. This method does not directly play the video.
     * Instead it sets everything up and once the video is buffered then it will play.
     * @return The media player which will play the video.
     * @see VideoManager#onPrepared(MediaPlayer)
     */
    MediaPlayer PlayVideo(){

        try {
            mMediaPlayer.setDataSource(path);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        mMediaPlayer.setDisplay(holder);
        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);

        return mMediaPlayer;

    }

    /**
     * Call this function before quitting / destroying the application to save ressources.
     */
    void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * Starts the video once everything is set up.
     */
    void startVideoPlayback() {
        holder.setFixedSize(metrics.widthPixels, metrics.heightPixels);
        mMediaPlayer.start();
    }

    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        // TODO Auto-generated method stub
    }

    /**
     * Waits for the playback to be ready before playing the video (MediaPlayer is initialized, video is buffered...)
     * @param mp The MediaPlayer that is ready for playback
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        // TODO Auto-generated method stub
        this.startVideoPlayback();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        // TODO Auto-generated method stub
    }
}
