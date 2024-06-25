package com.example.main.boj.start1.dynamic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//RGB거리 2
public class P17404 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());

        StringTokenizer st;
        int[][] arr = new int[n + 1][3];

        for(int i = 1; i < n + 1; i++) {
            st = new StringTokenizer(br.readLine(), " ");
            arr[i][0] = Integer.parseInt(st.nextToken());
            arr[i][1] = Integer.parseInt(st.nextToken());
            arr[i][2] = Integer.parseInt(st.nextToken());
        }

        int result = 1_000_001;

        for (int i = 0; i < 3; i++) {
            int[][] dp = new int[n + 1][3];

            for (int j = 0; j < 3; j++) {
                if (i == j) {
                    dp[1][i] = arr[1][i];
                } else {
                    dp[1][j] = 1_000_001;
                }
            }

            for (int j = 2; j < n + 1; j++) {
                dp[j][0] = Math.min(dp[j - 1][1], dp[j - 1][2]) + arr[j][0];
                dp[j][1] = Math.min(dp[j - 1][0], dp[j - 1][2]) + arr[j][1];
                dp[j][2] = Math.min(dp[j - 1][0], dp[j - 1][1]) + arr[j][2];
            }

            for (int j = 0; j < 3; j++) {
                if (i != j) {
                    result = Math.min(result, dp[n][j]);
                }
            }
        }
        System.out.println(result);
    }
}
