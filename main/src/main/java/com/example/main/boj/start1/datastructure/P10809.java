package com.example.main.boj.start1.datastructure;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

//알파벳 찾기
public class P10809 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String str = br.readLine();

        int[] arr = new int[26];
        Arrays.fill(arr, -1);

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (arr[c - 97] == -1) {
                arr[c - 97] = i;
            }
        }

        for (int i : arr) {
            System.out.print(i + " ");
        }
    }
}
