package com.cecs.controller;

/*
  The CECS327InputStream extends InputStream class. The class implements
  markers that are used in AudioInputStream

  @author  Oscar Morales-Ponce
  @version 0.15
  @since   2019-01-24
 */

import java.io.InputStream;

import com.cecs.def.ProxyInterface;
import com.google.gson.JsonObject;
import java.util.concurrent.Semaphore;

public class CECS327InputStream2 extends InputStream {
    /**
     * Total number of bytes in the file
     */
    private int total;
    /**
     * Marker
     */
    private int mark = 0;
    /**
     * Current reading position
     */
    private int pos = 0;
    /**
     * It stores a buffer with FRAGMENT_SIZE bytes for the current reading. This
     * variable is useful for UDP sockets. Thus bur is the datagram
     */
    private byte[] buf;
    /**
     * It prepares for the next buffer. In UDP sockets you can read next buffer
     * while buf is in use
     */
    private byte[] nextBuf;
    /**
     * It is used to read the buffer
     */
    private int fragment = 0;
    private static final int FRAGMENT_SIZE = 16384;
    /**
     * File name to stream
     */
    private String fileName;
    /**
     * Instance of an implementation of proxyInterface
     */
    private ProxyInterface proxy;

    private Semaphore sem;

    /**
     * Constructor of the class. Initialize the variables and reads the first
     * fragment in nextBuf
     *
     * @param fileName The name of the file
     */
    CECS327InputStream2(String fileName, ProxyInterface proxy) {
        sem = new Semaphore(1);
        try {
            sem.acquire();
        } catch (InterruptedException exc) {
            exc.printStackTrace();
        }
        this.proxy = proxy;
        this.fileName = fileName;
        this.buf = new byte[FRAGMENT_SIZE];
        this.nextBuf = new byte[FRAGMENT_SIZE];
        String[] param = new String[] { this.fileName };
        JsonObject jsonRet = proxy.synchExecution("getFileSize", param, Communication.Semantic.AT_LEAST_ONCE);
        this.total = Integer.parseInt(jsonRet.get("ret").getAsString());
        getBuff(fragment);
        fragment++;
    }

    /**
     * Spawns a thread to read the next buffer for the song playing. Uses remote
     * method <code>getSongChunk(String filename, int fragment)</code>
     */
    private void getBuff(int fragment) {
        new Thread(() -> {
            var request = proxy.synchExecution("getSongChunk", new String[] { fileName, String.valueOf(fragment) },
                    Communication.Semantic.AT_LEAST_ONCE);
            nextBuf = JsonService.unpackBytes(request);
            sem.release();
        }).start();
    }

    /**
     * Reads the next byte of data from the input stream.
     */
    @Override
    public synchronized int read() {

        if (pos >= total) {
            pos = 0;
            return -1;
        }
        int posmod = pos % FRAGMENT_SIZE;
        if (posmod == 0) {
            // Wait for getBuff()'s thread to finish
            try {
                sem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.arraycopy(nextBuf, 0, buf, 0, FRAGMENT_SIZE);
            getBuff(fragment);
            fragment++;
        }
        int p = pos % FRAGMENT_SIZE;
        pos++;
        return buf[p] & 0xff;
    }

    /**
     * Reads some number of bytes from the input stream and stores them into the
     * buffer array b.
     */
    @Override
    public synchronized int read(byte[] b, int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }

        if (pos >= total) {
            return -1;
        }
        int avail = total - pos;
        if (len > avail) {
            len = avail;
        }
        if (len <= 0) {
            return 0;
        }
        for (int i = off; i < off + len; i++)
            b[i] = (byte) read();
        return len;
    }

    /**
     * Skips over and discards n bytes of data from this input stream.
     */
    @Override
    public synchronized long skip(long n) {
        long k = total - pos;
        if (n < k) {
            k = n < 0 ? 0 : n;
        }

        pos += k;
        fragment = pos / FRAGMENT_SIZE;
        getBuff(fragment);
        fragment++;
        getBuff(fragment);
        return k;
    }

    /**
     * Returns an estimate of the number of bytes that can be read (or skipped over)
     * from this input stream without blocking by the next invocation of a method
     * for this input stream.
     */
    @Override
    public synchronized int available() {
        return total - pos;
    }

    /**
     * Tests if this input stream supports the mark and reset methods.
     */
    @Override
    public boolean markSupported() {
        return true;
    }

    /**
     * Marks the current position in this input stream.
     */
    @Override
    public void mark(int readAheadLimit) {
        mark = pos;
    }

    /**
     * Repositions this stream to the position at the time the mark method was last
     * called on this input stream.
     */
    @Override
    public synchronized void reset() {
        pos = mark;
        fragment = pos / FRAGMENT_SIZE;
        getBuff(fragment);
        fragment++;
        getBuff(fragment);
    }

    /**
     * Closes this input stream and releases any system resources associated with
     * the stream.
     */
    @Override
    public void close() {
    }

}