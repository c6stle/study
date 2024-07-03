package com.example.main.boj.정렬;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//버블 소트
public class P1517 {

    public static int[] arr;
    public static int[] temp;
    public static int count = 0;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        int n = Integer.parseInt(br.readLine());
        temp = new int[n];
        arr = new int[n];

        st = new StringTokenizer(br.readLine());
        for(int i=0; i<n; i++) {
            arr[i] = Integer.parseInt(st.nextToken());
        }
        mergeSort(0, n-1);

        System.out.println(count);
    }

    static void mergeSort(int low, int high) {
        if(low < high) {
            int mid = (low+high)/2;
            mergeSort(low, mid);
            mergeSort(mid+1, high);
            merge(low, mid, high);
        }
    }

    static void merge(int low, int mid, int high) {
        int i = low;
        int j = mid + 1;
        int index = low;

        while (i <= mid && j <= high) {
            if (arr[i] <= arr[j])
                temp[index++] = arr[i++];
            else {
                temp[index++] = arr[j++];
                count += (mid + 1 - i);
            }
        }
        while (i <= mid) {
            temp[index++] = arr[i++];
        }
        while (j <= high) {
            temp[index++] = arr[j++];
        }
        if (high + 1 - low >= 0) System.arraycopy(temp, low, arr, low, high + 1 - low);
    }
}
