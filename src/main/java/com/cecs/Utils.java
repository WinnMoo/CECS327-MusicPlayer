package com.cecs;

class Utils {
    static boolean canPlayNext(int index, int size) {
        return index < size - 1;
    }

    static boolean canPlayPrev(int index) { return index > 0; }
}