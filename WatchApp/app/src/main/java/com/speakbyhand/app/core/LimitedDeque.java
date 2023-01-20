package com.speakbyhand.app.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LimitedDeque<T> {
    List<T> items;
    private int count;
    private final int maxCount;
    private int topIndex;
    private int bottomIndex;

    public LimitedDeque(int maxCount) {
        items = new ArrayList<>(maxCount);
        for (int i = 0; i < maxCount; i++) {
            items.add(null);
        }
        this.maxCount = maxCount;
        topIndex = -1;
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
        topIndex = getNextIndex(topIndex);
        items.set(topIndex, item);

        if(isFull()){
            bottomIndex = getNextIndex(bottomIndex);
        } else {
            count += 1;
        }
    }

    public void clear(){
        items = new ArrayList<>(maxCount);
        for (int i = 0; i < maxCount; i++) {
            items.add(null);
        }
        topIndex = 0;
        bottomIndex = 0;
        count = 0;
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

    private int getVirtualIndex(int index){
        if (index >= bottomIndex){
            return  index - bottomIndex;
        } else {
            return maxCount - bottomIndex + index + 1;
        }
    }

    public boolean isFull() {
        return count == maxCount;
    }

    public boolean isEmpty(){
        return count == 0;
    }

    public Iterator<T> getForwardIterator() {
        return new ForwardIterator();
    }

    public Iterator<T> getBackwardIterator() {
        return new BackwardIterator();
    }




    class ForwardIterator implements  Iterator<T>{
        int currentIndex = bottomIndex;
        int iterationCount = 0;

        @Override
        public boolean hasNext() {
            return iterationCount < maxCount;
        }

        @Override
        public T next() {
            T top = items.get(currentIndex);
            currentIndex = getNextIndex(currentIndex);
            iterationCount++;
            return top;
        }
    }

    class BackwardIterator implements  Iterator<T>{
        int currentIndex = topIndex;
        int iterationCount = 0;

        @Override
        public boolean hasNext() {
            return iterationCount < maxCount;
        }

        @Override
        public T next() {
            T top = items.get(currentIndex);
            currentIndex = getPreviousIndex(currentIndex);
            iterationCount++;
            return top;
        }
    }
}