package com.example.main.boj.start1.dynamic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//정수 삼각형
public class P1932 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());

        int[][] dp = new int[n + 1][n + 1];
        int[][] arr = new int[n + 1][n + 1];
        for (int i = 1; i < n + 1; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            for (int j = 1; j < i + 1; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < n + 1; j++) {
                dp[i][j] = Math.max(dp[i-1][j], dp[i-1][j-1]) + arr[i][j];
            }
        }
        int result = 0;
        for (int i = 1; i < n + 1; i++) {
            if (result < dp[n][i]) {
                result = dp[n][i];
            }
        }
        System.out.println(result);
    }
}
