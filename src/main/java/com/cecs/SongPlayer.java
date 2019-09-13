package com.cecs;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class SongPlayer {
    private Player player;
    private InputStream is;
    private String currentSong;
    private int pausedFrame;
    private int totalFrames;
    private Thread thread_music;
    private Observable<Double> trackObservable;
    private boolean updateLock;

    SongPlayer() {
        player = null;
        currentSong = null;
        updateLock = false;
        trackObservable = Observable.interval(1, TimeUnit.SECONDS).timeInterval()
                .filter(it -> currentSong != null && is != null && !updateLock).map(it -> trackByteToDouble(pollLength()));
    }

    void playSong(String filename) {
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
                thread_music = new Thread(() -> {
                    try {
                        player.play();
                    } catch (JavaLayerException e) {
                        e.printStackTrace();
                    }
                });
                thread_music.start();
                currentSong = filename;
            } else {
                stopMusic();
                player = new Player(is);
                thread_music = new Thread(() -> {
                    try {
                        player.play();
                    } catch (JavaLayerException e) {
                        e.printStackTrace();
                    }
                });
                thread_music.start();
            }
        } catch (JavaLayerException | IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Reposition header of marker and resume song from that point
     */
    void updateTrack(double percent) {
        var marker = trackDoubleToByte(percent);
        if (thread_music == null) {
            return;
        }
        stopMusic();
        try {
            is = new CECS327InputStream(currentSong);
            player = new Player(is);
            is.skipNBytes(marker);
            thread_music = new Thread(() -> {
                try {
                    player.play();
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            });
            thread_music.start();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }
    }

    private double trackByteToDouble(int bite) {
        return 100 * (1 - (double) bite / (double) totalFrames);
    }

    private int trackDoubleToByte(double percent) {
        return (int) (totalFrames * (percent / 100));
    }

    void pauseSong() {
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
    Observable<Double> getEvents() {
        return trackObservable;
    }

    void blockUpdates() {
        updateLock = true;
    }

    void unblockUpdates() {
        updateLock = false;
    }

    String nowPlaying() {
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
}
