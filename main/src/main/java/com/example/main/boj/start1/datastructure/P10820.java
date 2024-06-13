package com.example.main.boj.start1.datastructure;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//문자열 분석
public class P10820 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();
        String str;
        while ((str = br.readLine()) != null) {
            int lowerCase = 0;
            int upperCase = 0;
            int number = 0;
            int space = 0;
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (Character.isLowerCase(c)) {
                    lowerCase++;
                }
                if (Character.isUpperCase(c)) {
                    upperCase++;
                }
                if (Character.isDigit(c)) {
                    number++;
                }
                if (c == ' ') {
                    space++;
                }
            }
            sb.append(lowerCase).append(" ").append(upperCase).append(" ").append(number).append(" ").append(space).append("\n");
        }
        System.out.println(sb);
    }
}
