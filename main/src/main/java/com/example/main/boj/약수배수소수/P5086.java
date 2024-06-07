package com.example.main.boj.약수배수소수;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class P5086 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            int first = Integer.parseInt(st.nextToken());
            int second = Integer.parseInt(st.nextToken());
            if (first == 0 && second == 0) {
                break;
            }
            if (first > second) {
                int temp = first % second;
                if (temp == 0) {
                    System.out.println("multiple");
                } else {
                    System.out.println("neither");
                }
            } else {
                int temp = second % first;
                if (temp == 0) {
                    System.out.println("factor");
                } else {
                    System.out.println("neither");
                }
            }
        }
    }
}
