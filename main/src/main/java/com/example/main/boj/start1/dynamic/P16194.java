package com.example.main.boj.start1.dynamic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//카드 구매하기 2
public class P16194 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());
        StringTokenizer st = new StringTokenizer(br.readLine());

        int[] p = new int[n+1];
        for (int i = 1; i <= n; i++) {
            p[i] = Integer.parseInt(st.nextToken());
        }

        int[] d = new int[n+1];
        for (int i = 1; i <= n; i++) {
            d[i] = Integer.MAX_VALUE;
            for (int j = 1; j <= i; j++) {
                d[i] = Math.min(d[i], d[i - j] + p[j]);
            }
        }
        System.out.println(d[n]);
    }
}
