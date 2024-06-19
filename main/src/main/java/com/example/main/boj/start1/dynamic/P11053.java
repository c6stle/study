package com.example.main.boj.start1.dynamic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//가장 긴 증가하는 부분 수열
public class P11053 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());

        StringTokenizer st = new StringTokenizer(br.readLine());
        int[] arr = new int[n+1];
        int[] d = new int[n+1];
        for (int i = 1; i <= n; i++) {
            arr[i] = Integer.parseInt(st.nextToken());
            d[i] = 1;
        }

        int max = 1;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j < i; j++) {
                if (arr[i] > arr[j]) {
                    d[i] = Math.max(d[i], d[j] + 1);
                }
            }
            if (max < d[i]) {
                max = d[i];
            }
        }
        System.out.println(max);
    }
}
