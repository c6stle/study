package com.example.main.boj.start1.math;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//진법 변환2
public class P11005 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int num = Integer.parseInt(st.nextToken());
        int n = Integer.parseInt(st.nextToken());

        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int tmp = num % n;
            if (tmp < 10) {
                sb.append((char) (tmp + '0'));
            } else {
                sb.append((char) (tmp - 10 + 'A'));
            }
            num /= n;
        }
        System.out.println(sb.reverse());
    }
}
