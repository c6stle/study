package com.example.main.boj.start1.dynamic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//스티커
public class P9465 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int t = Integer.parseInt(br.readLine());
        for (int i = 0; i < t; i++) {
            int n = Integer.parseInt(br.readLine());
            StringTokenizer st1 = new StringTokenizer(br.readLine());
            StringTokenizer st2 = new StringTokenizer(br.readLine());

            int[][] dp = new int[2][n+1];
            dp[0][1] = Integer.parseInt(st1.nextToken());
            dp[1][1] = Integer.parseInt(st2.nextToken());
            for (int j = 2; j < n + 1; j++) {
                dp[0][j] = Math.max(dp[1][j - 1], dp[1][j - 2]) + Integer.parseInt(st1.nextToken());
                dp[1][j] = Math.max(dp[0][j - 1], dp[0][j - 2]) + Integer.parseInt(st2.nextToken());
            }
            System.out.println(Math.max(dp[0][n], dp[1][n]));
        }
    }
}
