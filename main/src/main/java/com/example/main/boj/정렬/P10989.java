package com.example.main.boj.정렬;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class P10989 {

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int n = Integer.parseInt(br.readLine());

        int[] arr = new int[n];
        int[] countArr = new int[10001];

        for (int i = 0; i < arr.length; i++) {
            arr[i] = Integer.parseInt(br.readLine());
            countArr[arr[i]]++;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < countArr.length; i++) {
            while (countArr[i] > 0) {
                sb.append(i).append("\n");
                countArr[i]--;
            }
        }
        System.out.println(sb);
    }
}
