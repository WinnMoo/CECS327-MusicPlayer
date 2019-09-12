package com.cecs;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.*;

import java.io.IOException;
import java.io.InputStream;

public class SongPlayer {
    private Player player;
    private InputStream is;
    private boolean isSongPlaying;
    private long pausedFrame;
    private long totalFrames;
    private Thread thread_music;

    public SongPlayer(){
        player = null;
        isSongPlaying = false;
    }

    public void playSong(String filename){
        try {

            if(!isSongPlaying){
                // It uses CECS327InputStream as InputStream to play the song
                is = new CECS327InputStream(filename);
                player = new Player(is);
                thread_music = new Thread(){
                    public void run(){
                        try{
                            player.play();
                        } catch (JavaLayerException e) {
                            e.printStackTrace();
                        }

                    }
                };
                thread_music.start();
                isSongPlaying = true;
            }
        }
        catch (JavaLayerException exception)
        {
            exception.printStackTrace();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    public void stopSong(){
        if(!isSongPlaying){
            return;
        }
        if(player != null){
            try {
                isSongPlaying = false;
                player.close();
                thread_music.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
