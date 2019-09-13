package com.cecs;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.*;

import java.io.IOException;
import java.io.InputStream;

public class SongPlayer {
    private Player player;
    private InputStream is;
    private String currentSong;
    private int pausedFrame;
    private int totalFrames;
    private Thread thread_music;

    public SongPlayer() {
        player = null;
        currentSong = null;
        pausedFrame = 0;
    }

    public void playSong(String filename) {
        System.out.println("Play button pressed");
        try {
            // If playing a new song, not just resuming
            if (currentSong == null || !currentSong.equals(filename)) {
                if (thread_music != null) {
                    stopMusic();
                }
                // It uses CECS327InputStream as InputStream to play the song
                is = new CECS327InputStream(filename);
                player = new Player(is);
                // When the input stream is fresh, available gives us the total number of bytes
                // of the song
                totalFrames = is.available();
                thread_music = new Thread() {
                    public void run() {
                        try {
                            System.out.println("Now playing...");
                            player.play();
                        } catch (JavaLayerException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread_music.start();
                currentSong = filename;
            } else {
                stopMusic();
                player = new Player(is);
                thread_music = new Thread() {
                    public void run() {
                        try {
                            System.out.println("Resuming at " + pausedFrame);
                            player.play();
                        } catch (JavaLayerException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread_music.start();
            }
        } catch (JavaLayerException exception) {
            exception.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void pauseSong() {
        if (currentSong == null) {
            return;
        }
        if (player != null) {
            try {
                pausedFrame = player.getPosition();
                System.out.println("Song paused at " + pausedFrame);
                stopMusic();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public int totalLength() {
        return totalFrames;
    }

    public int pollLength() throws IOException {
        return is.available();
    }

    public void stopSong() {
        if (currentSong == null) {
            return;
        }
        if (player != null) {
            try {
                System.out.println("Song stopped");
                currentSong = null;
                pausedFrame = 0;
                stopMusic();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
