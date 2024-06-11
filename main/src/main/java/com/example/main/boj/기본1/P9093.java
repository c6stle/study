package com.example.main.boj.기본1;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//단어뒤집기
public class P9093 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int l = Integer.parseInt(br.readLine());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < l; i++) {
            String[] strArr = br.readLine().split(" ");
            for (String str : strArr) {
                StringBuilder tmpSb = new StringBuilder(str);
                sb.append(tmpSb.reverse()).append(" ");
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }
}
