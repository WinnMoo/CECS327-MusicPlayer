package com.cecs.controller;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class SongPlayer {
    private Player player;
    private CECS327InputStream is;
    private String currentSong;
    private int totalFrames;
    private Thread thread_music;
    private Observable<Double> trackObservable;
    private boolean updateLock;

    public SongPlayer() {
        player = null;
        currentSong = null;
        updateLock = false;
        trackObservable = Observable.interval(1, TimeUnit.SECONDS).timeInterval()
                .filter(it -> currentSong != null && is != null && !updateLock)
                .map(it -> trackByteToDouble(pollLength()));
    }

    public void playSong(String filename) {
        try {
            // If playing a new song, not just resuming
            if (currentSong == null || !currentSong.equals(filename)) {
                if (thread_music != null) {
                    stopMusic();
                }
                // It uses CECS327InputStream as InputStream to play the song
                is = new CECS327InputStream(filename);
                player = new Player(is);
                totalFrames = pollLength();
                playMusic();
                currentSong = filename;
            } else {
                stopMusic();
                player = new Player(is);
                playMusic();
            }
        } catch (JavaLayerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Could not find song requested.");
        }
    }

    /**
     * Reposition header of marker and resume song from that point
     */
    public void updateTrack(double percent) {
        long marker = trackDoubleToByte(percent);
        if (thread_music == null) {
            return;
        }
        stopMusic();
        try {
            is = new CECS327InputStream(currentSong);
            player = new Player(is);
            is.skip(marker);
            playMusic();
        } catch (IOException | JavaLayerException e) {
            e.printStackTrace();
        }
    }

    private double trackByteToDouble(int bite) {
        return 100 * (1 - (double) bite / (double) totalFrames);
    }

    private int trackDoubleToByte(double percent) {
        return (int) (totalFrames * (percent / 100));
    }

    public void pauseSong() {
        if (currentSong == null) {
            return;
        }
        if (player != null) {
            try {
                stopMusic();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private int pollLength() throws IOException {
        return is.available();
    }

    /**
     * Returns an Observable that emits the percentage of the song completed at a
     * given point
     */
    public Observable<Double> getEvents() {
        return trackObservable;
    }

    public void blockUpdates() {
        updateLock = true;
    }

    public void unblockUpdates() {
        updateLock = false;
    }

    public String nowPlaying() {
        return currentSong;
    }

    /**
     * Stops the current track. These two methods are used everywhere together, so I
     * put them in this convenience function
     */
    private void stopMusic() {
        player.close();
        thread_music.interrupt();
    }

    private void playMusic() {
        thread_music = new Thread(() -> {
            try {
                player.play();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        });
        thread_music.start();
    }
}