package com.example.main.boj.start1.dynamic;

import java.util.Arrays;
import java.util.Scanner;

//동물원
public class P1309 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        int[][] dp = new int[n + 1][3];

        dp[0][0] = 0;
        dp[0][1] = 0;
        dp[0][2] = 0;
        dp[1][0] = 1;
        dp[1][1] = 1;
        dp[1][2] = 1;
        for (int i = 2; i < n+1; i++) {
            dp[i][0] = (dp[i-1][0] + dp[i-1][1] + dp[i-1][2]) % 9901;
            dp[i][1] = (dp[i-1][0] + dp[i-1][2]) % 9901;
            dp[i][2] = (dp[i-1][0] + dp[i-1][1]) % 9901;
        }
        System.out.println(Arrays.stream(dp[n]).sum() % 9901);
    }
}
