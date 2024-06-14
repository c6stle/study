package com.example.main.boj.start1.math;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//GCD(최대공약수) 합
public class P9613 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        int n = Integer.parseInt(br.readLine());

        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            int k = Integer.parseInt(st.nextToken());
            int[] arr = new int[k];
            for (int j = 0; j < k; j++) {
                arr[j] = Integer.parseInt(st.nextToken());
            }
            long gcdSum = 0;
            for (int j = 0; j < k-1; j++) {
                for (int l = j+1; l < k; l++) {
                    if (arr[j] != 0 && arr[l] != 0) {
                        gcdSum += gcd(arr[j], arr[l]);
                    }
                }
            }
            System.out.println(gcdSum);
        }
    }

    static long gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }
}
