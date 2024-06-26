package com.example.main.boj.start2.bruteforce;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//1, 2, 3 더하기(bruteforce)
public class P9095 {

    static int cnt = 0;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());

        for (int i = 0; i < n; i++) {
            cnt = 0;
            count(Integer.parseInt(br.readLine()));
            System.out.println(cnt);
        }
    }

    static void count(int num) {
        if (num == 0) {
            cnt ++;
            return;
        }
        for (int i = 1; i <= 3; i++) {
            int check = num - i;
            if (check >= 0) {
                count(check);
            }
        }
    }
}
