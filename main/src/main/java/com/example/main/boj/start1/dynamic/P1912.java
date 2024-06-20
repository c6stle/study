package com.example.main.boj.start1.dynamic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//연속합
public class P1912 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());
        StringTokenizer st = new StringTokenizer(br.readLine());

        int[] arr = new int[n + 1];
        int[] dp = new int[n + 1];

        for (int i = 1; i < n + 1; i++) {
            arr[i] = Integer.parseInt(st.nextToken());
        }
        dp[1] = arr[1];
        int max = arr[1];
        for (int i = 1; i < n + 1; i++) {
            dp[i] = Math.max(dp[i - 1] + arr[i], arr[i]);
            max = Math.max(max, dp[i]);
        }
        System.out.println(max);
    }
    //구간합을 이용
    //public static void main(String[] args) throws Exception {
    //    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    //    int n = Integer.parseInt(br.readLine());
    //    StringTokenizer st = new StringTokenizer(br.readLine());
    //    int[] arr = new int[n + 1];
    //    int[] S = new int[n + 1];
    //    for (int i = 1; i < n + 1; i++) {
    //        arr[i] = Integer.parseInt(st.nextToken());
    //        S[i] = S[i-1] + arr[i];
    //    }
    //    int max = 0;
    //    for (int i = 0; i < S.length; i++) {
    //        for (int j = i + 1; j < S.length; j++) {
    //            if (max < S[j] - S[i]) {
    //                max = S[j] - S[i];
    //            }
    //        }
    //    }
    //    System.out.println(max);
    //}
}
