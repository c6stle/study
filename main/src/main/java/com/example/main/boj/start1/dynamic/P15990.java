package com.example.main.boj.start1.dynamic;

import java.io.BufferedReader;
import java.io.InputStreamReader;

// 1, 2, 3 더하기 5
public class P15990 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int t = Integer.parseInt(br.readLine());

        long[][] d = new long[100001][4];
        d[1][1] = 1;
        d[2][2] = 1;
        d[3][1] = 1;
        d[3][2] = 1;
        d[3][3] = 1;
        for (int j = 4; j < 100001; j++) {
            d[j][1] = (d[j - 1][2] + d[j - 1][3]) % 1_000_000_009;
            d[j][2] = (d[j - 2][1] + d[j - 2][3]) % 1_000_000_009;
            d[j][3] = (d[j - 3][1] + d[j - 3][2]) % 1_000_000_009;
        }
        for (int i = 0; i < t; i++) {
            int n = Integer.parseInt(br.readLine());
            System.out.println((d[n][1] + d[n][2] + d[n][3]) % 1_000_000_009);
        }
    }
}
