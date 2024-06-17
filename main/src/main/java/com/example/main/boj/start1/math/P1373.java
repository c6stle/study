package com.example.main.boj.start1.math;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//2진수 8진수
public class P1373 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String str = br.readLine();

        for (int i = str.length(); i % 3 > 0; i++) {
            str = "0" + str;
        }
        System.out.println(str);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i += 3) {
            sb.append((str.charAt(i) - '0') * 4 + (str.charAt(i + 1) - '0') * 2 + str.charAt(i + 2) - '0');
            System.out.println(sb);
        }

        System.out.println(sb);
    }
}
