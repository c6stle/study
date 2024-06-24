package com.example.main.boj.start1.dynamic;

import java.util.Scanner;

//타일 채우기
public class P2133 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        int[] dp = new int[n+2];
        dp[0] = 1;
        dp[2] = 3;
        for (int i = 2; i < n + 1; i+=2) {
            dp[i] = dp[i-2] * 3;
            for (int j = i-4; j >=0; j-=2) {
                dp[i] += dp[j] * 2;
            }
        }
        System.out.println(dp[n]);
    }
}
