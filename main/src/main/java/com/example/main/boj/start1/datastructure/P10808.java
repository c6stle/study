package com.example.main.boj.start1.datastructure;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//알파벳 개수
public class P10808 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String str = br.readLine();

        int[] arr = new int[26];
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            arr[c - 97]++;
        }

        for (int i : arr) {
            System.out.print(i + " ");
        }
    }
}
