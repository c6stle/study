package com.example.main.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.StringTokenizer;

//ATM 인출 시간 계산하기(삽입정렬) 11399
public class Program18 {

    public static void doing() throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(bf.readLine());
        int[] arr = new int[n];
        int[] srr = new int[n];
        StringTokenizer st = new StringTokenizer(bf.readLine());
        for (int i = 0; i < n; i++) {
            arr[i] = Integer.parseInt(st.nextToken());
        }

        for (int i = 1; i < n; i++) {
            int insert_point = i;
            int insert_value = arr[i];
            for (int j = i - 1; j >= 0; j--) {
                if (arr[j] < arr[i]) {
                    insert_point = j + 1;
                    break;
                 }
                if (j == 0) {
                    insert_point = 0;
                }
            }
            for (int j = i; j > insert_point; j--) {
                arr[j] = arr[j - 1];
            }
            arr[insert_point] = insert_value;
        }
        srr[0] = arr[0];
        for (int i = 1; i < n; i++) {
            srr[i] = srr[i - 1] + arr[i];
        }
        int sum = 0;
        for (int i = 0; i < n; i++) {
            sum = sum + srr[i];
        }
        System.out.println(sum);
    }
}
