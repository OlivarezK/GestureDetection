package com.speakbyhand.app.core;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LimitedStack<T> implements Iterable<T > {
    List<T> items;
    private int count;
    private final int maxCount;
    private int topIndex;
    private int bottomIndex;

    public LimitedStack(int maxCount) {
        items = new ArrayList<>(maxCount);
        for (int i = 0; i < maxCount; i++) {
            items.add(null);
        }
        this.maxCount = maxCount;
        topIndex = 0;
        bottomIndex = 0;
        count = 0;
    }


    public T top() {
        return items.get(topIndex);
    }

    public T bottom() {
        return items.get(bottomIndex);
    }



    public void push(T item) {
        items.set(getNextIndex(topIndex), item);
        topIndex = getNextIndex(topIndex);

        if(isFull()){
            bottomIndex = getNextIndex(bottomIndex);
        } else {
            count += 1;
        }
    }

    public void clear(){
        items.clear();
        topIndex = 0;
        bottomIndex = 0;
    }

    public T pop() {
        if(isEmpty()){
            return null;
        }

        T item = items.get(topIndex);
        count -= 1;
        topIndex = getPreviousIndex(topIndex);
        return item;
    }

    private int getNextIndex(int index) {
        int nextIndex = index + 1;
        if (nextIndex >= maxCount) {

            nextIndex = 0;
        }
        return nextIndex;
    }

    private int getPreviousIndex(int index) {
        int previousIndex = index - 1;
        if (previousIndex < 0) {
            previousIndex = maxCount - 1;
        }
        return previousIndex;
    }

    public boolean isFull() {
        return count == maxCount;
    }

    public boolean isEmpty(){
        return count == 0;
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return new DelimiterDetectorIterator();
    }

    class DelimiterDetectorIterator implements  Iterator<T>{
        int currentIndex = topIndex;

        @Override
        public boolean hasNext() {
            return getPreviousIndex(currentIndex) != bottomIndex;
        }

        @Override
        public T next() {
            T top = items.get(currentIndex);
            currentIndex = getPreviousIndex(currentIndex);
            return top;
        }
    }
}