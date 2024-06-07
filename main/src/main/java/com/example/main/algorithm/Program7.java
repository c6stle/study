package com.example.main.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;

//주몽의명령(투포인터) 1940
public class Program7 {
    public static void doing() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());
        int m = Integer.parseInt(br.readLine());

        int cnt = 0;
        int start = 0;
        int end = n - 1;

        int[] arr = new int[n];
        StringTokenizer st = new StringTokenizer(br.readLine());
        for (int i = 0; i < n; i++) {
            arr[i] = Integer.parseInt(st.nextToken());
        }
        Arrays.sort(arr); //정렬 후 양쪽 끝에서 투포인터 시도

        while (start < end) {
            if (arr[start] + arr[end] < m) {
                start ++;
            } else if (arr[start] + arr[end] > m) {
                end --;
            } else {
                cnt ++;
                start ++;
                end --;
            }
        }
        System.out.println(cnt);
        br.close();
    }
}
