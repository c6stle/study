package com.example.main.boj.start1.dynamic;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//1, 2, 3 더하기(dp)
public class P9095 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int t = Integer.parseInt(br.readLine());
        int[] d = new int[12];
        d[0] = 0;
        d[1] = 1;
        d[2] = 2;
        d[3] = 4;

        for (int i = 4; i < d.length; i++) {
            d[i] = d[i - 1] + d[i - 2] + d[i - 3];
        }

        for (int i = 0; i < t; i++) {
            int n = Integer.parseInt(br.readLine());
            System.out.println(d[n]);
        }
    }
}
