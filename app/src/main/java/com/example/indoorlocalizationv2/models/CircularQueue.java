package com.example.indoorlocalizationv2.models;
import java.util.LinkedList;
import java.util.Queue;

public class CircularQueue {
    // If not static then some magic happens and the distance value in UI updates really fast...
    private static Queue<String> queue;

    public CircularQueue(int maxSize) {
        final int size = maxSize;
        this.queue = new LinkedList<String>(){
            @Override
            public boolean add(String object) {
                boolean result;
                if (this.size() < size) {
                    result = super.add(object);
                }
                else
                {
                    super.removeFirst();
                    result = super.add(object);
                }
                return result;
            }
        };
    }

    public void add(float value) {
        this.queue.add(value + "");
    }

    public String[] getAll() {
        return this.queue.toArray(new String[0]);
    }

    public float getAverageValue() {
        String[] arr = this.getAll();
        float sum = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != null && !arr[i].isEmpty()) {
                sum += Float.parseFloat(arr[i]);
            }
        }
        return sum / arr.length;
    }
}
