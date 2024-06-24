package com.example.main.boj.start1.dynamic;

import java.util.Scanner;

//연속합 2
public class P13398 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        int[] arr = new int[n + 1];
        int[][] dp = new int[n + 1][2];

        for (int i = 1; i < n + 1; i++) {
            arr[i] = sc.nextInt();
        }

        dp[1][0] = arr[1];
        dp[1][1] = arr[1];

        int max = arr[1];
        for (int i = 2; i < n + 1; i++) {
            dp[i][0] = Math.max(dp[i - 1][0] + arr[i], arr[i]);
            dp[i][1] = Math.max(dp[i - 1][0], dp[i - 1][1] + arr[i]);
            max = Math.max(max, Math.max(dp[i][0], dp[i][1]));
        }
        System.out.println(max);
    }
}
