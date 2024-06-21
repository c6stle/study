package com.example.main.boj.start1.dynamic;

import java.util.Arrays;
import java.util.Scanner;

//오르막 수
public class P11057 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        int[][] dp = new int[n + 1][10];
        for (int i = 0; i < 10; i++) {
            dp[1][i] = 1;
        }
        for (int i = 2; i < n + 1; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = j; k < 10; k++) {
                    dp[i][j] += dp[i-1][k] % 10_007;
                }
            }
        }
        System.out.println(Arrays.stream(dp[n]).sum() % 10_007);
    }
}
