package com.example.main.boj.start1.math;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//숨바꼭질 6
public class P17087 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());

        int s = Integer.parseInt(st.nextToken());
        int[] arr = new int[n]; //거리배열
        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < n; i++) {
            int position = Integer.parseInt(st.nextToken());
            arr[i] = Math.abs(position - s);
        }

        int gcd = arr[0];
        for (int i = 1; i < n; i++) {
            gcd = euclidean(gcd, arr[i]);
        }
        System.out.println(gcd);
    }

    //유클리드호제법 : 거리들의 최대공약수
    static int euclidean(int a, int b) {
        if (a > b) {
            if (a % b == 0) {
                return b;
            } else {
                return euclidean(b, a % b);
            }
        } else {
            if (b % a == 0) {
                return a;
            } else {
                return euclidean(a, b % a);
            }
        }
    }
}
