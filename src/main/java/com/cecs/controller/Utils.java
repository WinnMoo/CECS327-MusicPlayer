package com.cecs.controller;

public class Utils {
    public static boolean canPlayNext(int index, int size) {
        return index < size - 1;
    }

    public static boolean canPlayPrev(int index) {
        return index > 0;
    }
}