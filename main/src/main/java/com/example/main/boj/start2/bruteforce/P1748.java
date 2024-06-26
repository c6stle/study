package com.example.main.boj.start2.bruteforce;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//수 이어 쓰기 1
public class P1748 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());

        int count = 0;
        int num = 1;
        int position = 10;
        for (int i = 1; i < n+1; i++) {
            if (i % position == 0) {
                num++;
                position *= 10;
            }
            count += num;
        }
        System.out.println(count);
    }
}
