package com.example.wishwaveapp.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class QuickSort<T> {

    public void sort(List<T> list, Comparator<T> comparator) {
        if (list == null || list.size() < 2) return;
        shuffle(list);
        quickSort(list, 0, list.size() - 1, comparator);
    }

    private void quickSort(List<T> list, int low, int high, Comparator<T> comparator) {
        if (low < high) {
            int pivot = partition(list, low, high, comparator);
            quickSort(list, low, pivot - 1, comparator);
            quickSort(list, pivot + 1, high, comparator);
        }
    }

    private int partition(List<T> list, int low, int high, Comparator<T> comparator) {
        T pivot = list.get(low);
        int i = low + 1;
        int j = high;

        while (i <= j) {
            while (i <= high && comparator.compare(list.get(i), pivot) <= 0) i++;
            while (j >= low + 1 && comparator.compare(list.get(j), pivot) > 0) j--;
            if (i < j) Collections.swap(list, i, j);
        }

        Collections.swap(list, low, j);
        return j;
    }

    private void shuffle(List<T> list) {
        Random rand = new Random();
        for (int i = list.size() - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            Collections.swap(list, i, j);
        }
    }
}

