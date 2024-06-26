package com.example.main.boj.start2.bruteforce;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//카잉 달력
public class P6064 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int t = Integer.parseInt(br.readLine());

        boolean check = false;
        StringTokenizer st;
        for (int i = 0; i < t; i++) {
            st = new StringTokenizer(br.readLine());
            int m = Integer.parseInt(st.nextToken());
            int n = Integer.parseInt(st.nextToken());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;

            for (int j = x; j < (m * n); j+=m) {
                if (j % n == y) {
                    System.out.println(j + 1);
                    check = true;
                    break;
                }
            }
            if (!check) {
                System.out.println(-1);
            }
            check = false;
        }
    }
}
