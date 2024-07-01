package com.example.main.boj.start2.bruteforce.recursion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//퇴사(bruteforce-recursion)
public class P14501 {

    static int result = Integer.MIN_VALUE;
    static int[] t;
    static int[] p;
    static int n;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        n = Integer.parseInt(br.readLine());

        t = new int[n];
        p = new int[n];

        StringTokenizer st;
        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            t[i] = Integer.parseInt(st.nextToken());
            p[i] = Integer.parseInt(st.nextToken());
        }

        dfs(0, 0);
        System.out.println(result);
    }

    static void dfs(int idx, int price) {
        if (idx >= n) {
            result = Math.max(price, result);
            return;
        }

        if (idx + t[idx] <= n) {
            dfs(idx + t[idx], price + p[idx]);
        } else {
            dfs(idx + t[idx], price); //if (idx >= n) 조건에 걸리도록
        }

        dfs(idx + 1, price);
    }
}
