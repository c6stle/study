package com.example.main.boj.심화1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class P1157 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int[] arr = new int[26];
        String str = br.readLine();
        for (int i = 0; i < str.length(); i++) {
            if ('a' <= str.charAt(i) && 'z' >= str.charAt(i)) {
                //소문자
                arr[str.charAt(i) - 97] ++;
            } else {
                //대문자
                arr[str.charAt(i) - 65] ++;
            }
        }

        int max = -1;
        char ch = '?';
        for (int i = 0; i < 26; i++) {
            if (arr[i] > max) {
                max = arr[i];
                ch = (char) (i + 65);
            } else if (arr[i] == max) {
                ch = '?';
            }
        }
        System.out.println(ch);
    }
}
