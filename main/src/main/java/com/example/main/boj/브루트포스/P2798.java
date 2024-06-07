package com.example.main.boj.브루트포스;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//블랙잭
public class P2798 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int n = Integer.parseInt(st.nextToken());
        int sum = Integer.parseInt(st.nextToken());

        st = new StringTokenizer(br.readLine());
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = Integer.parseInt(st.nextToken());
        }

        int max = 0;
        for (int i = 0; i < n - 2; i++) {
            int first = arr[i];
            for (int j = i + 1; j < n - 1; j++) {
                int second = arr[j];
                for (int k = j + 1; k < n; k++) {
                    int third = arr[k];
                    int s = first + second + third;
                    if (sum >= s) {
                        if (max < s) {
                            max = s;
                        }
                    }
                }
            }
        }
        System.out.println(max);
    }
}
