package com.example.main.boj.start2.bruteforce.permutation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//이전 순열
public class P10973 {
    static int[] arr;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());
        StringTokenizer st = new StringTokenizer(br.readLine());

        arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = Integer.parseInt(st.nextToken());
        }

        if (beforePermutation()) {
            StringBuilder sb = new StringBuilder();
            for (int a : arr) {
                sb.append(a).append(" ");
            }
            sb.append("\n");
            System.out.println(sb);
        } else {
            System.out.println(-1);
        }
    }

    static boolean beforePermutation() {
        int i = arr.length - 1;
        while (i > 0 && arr[i] > arr[i - 1]) {
            i--;
        }
        if (i == 0) {
            return false;
        }
        int j = arr.length - 1;
        while (arr[i - 1] < arr[j]) {
            j--;
        }
        swap(i - 1, j);

        j = arr.length - 1;
        while (i < j) {
            swap(i, j);
            i++;
            j--;
        }
        return true;
    }

    private static void swap(int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
