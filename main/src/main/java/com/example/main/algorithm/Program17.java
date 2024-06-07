package com.example.main.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;

//내림차순으로 자릿수 정렬하기(선택정렬) 1427
public class Program17 {

    public static void doing() throws IOException {
        Scanner sc = new Scanner(System.in);
        String str = sc.next();
        int[] arr = new int[str.length()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = Integer.parseInt(str.substring(i, i+1));
        }

        for (int i = 0; i < str.length(); i++) {
            int max = i;
            for (int j = i + 1; j < str.length(); j++) {
                if (arr[j] > arr[max]) {
                    max = j;
                }
            }
            if (arr[i] < arr[max]) {
                int temp = arr[max];
                arr[max] = arr[i];
                arr[i] = temp;
            }
        }

        for (int i = 0; i < str.length(); i++) {
            System.out.print(arr[i]);
        }
    }
}
