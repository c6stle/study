package com.example.main.boj.start1.datastructure;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class P10799 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String str = br.readLine();
        int iron = 0;
        int layer = 0;

        for (int i = 0; i < str.length(); i++) {
            char c1 = str.charAt(i);
            char c2;
            if (i == str.length() - 1) {
                c2 = 0;
            } else {
                c2 = str.charAt(i+1);
            }
            if (c1 == '(' && c2 == ')') {
                iron = iron + layer;
                i++; //레이저는 인덱스 두개 잡아먹음
            } else {
                if (c1 == '(') {
                    iron++;
                    layer++;
                } else {
                    layer--;
                }
            }
        }
        System.out.println(iron);
    }
}
