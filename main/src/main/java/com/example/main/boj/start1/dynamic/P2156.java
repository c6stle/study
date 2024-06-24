package com.example.main.boj.start1.dynamic;

import java.util.Scanner;

//포도주 시식
public class P2156 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] arr = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            arr[i] = sc.nextInt();
        }

        int[] dp = new int[n + 1];
        dp[0] = 0;
        for (int i = 1; i <= n; i++) {
            if (i == 1) {
                dp[i] = arr[i];
            } else if (i == 2) {
                dp[i] = arr[i] + dp[i - 1];
            } else {
                dp[i] = Math.max(dp[i - 1], Math.max(dp[i - 2] + arr[i], dp[i - 3] + arr[i] + arr[i - 1]));
            }
        }
        System.out.println(dp[n]);
    }
}
