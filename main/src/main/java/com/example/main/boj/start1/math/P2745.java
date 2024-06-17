package com.example.main.boj.start1.math;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//진법 변환
public class P2745 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        String str = st.nextToken();
        int n = Integer.parseInt(st.nextToken());
        int z = 1;
        int sum = 0;
        for (int i = str.length() - 1; i >= 0; i--) {
            char c = str.charAt(i);
            int temp;
            if (c - '0' >= 0 && c - '0' <= 9) {
                temp = c - '0';
            } else {
                temp = c + 10 - 'A';
            }
            sum += z * temp;
            z *= n;
        }
        System.out.println(sum);
    }
}
