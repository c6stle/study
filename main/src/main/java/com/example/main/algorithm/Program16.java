package com.example.main.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;

//버블 소트 프로그램(버블정렬) 1377
public class Program16 {

    public static void doing() throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(bf.readLine());

        mData[] arr = new mData[n];
        for (int i = 0; i < n; i++) {
            arr[i] = new mData(Integer.parseInt(bf.readLine()), i);
        }

        Arrays.sort(arr);
        int max = 0;
        for (int i = 0; i < n; i++) {
            if (max < arr[i].index - i) {
                max = arr[i].index - i;
            }
        }
        System.out.println(max + 1);
    }

    static class mData implements Comparable<mData> {

        int value;
        int index;

        public mData(int value, int index) {
            super();
            this.value = value;
            this.index = index;
        }

        @Override
        public int compareTo(mData o) {
            return this.value - o.value;
        }
    }

}
