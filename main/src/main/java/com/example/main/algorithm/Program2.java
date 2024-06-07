package com.example.main.algorithm;

import java.util.Scanner;

//평균구하기 1546
public class Program2 {
    public static void doing() {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        long[] arr = new long[n];
        for (int i = 0; i < n; i++) {
            arr[i] = Long.parseLong(sc.next());
        }

        long sum = 0;
        long max = 0;
        for (int i = 0; i < n; i++) {
            if (max < arr[i]){
                max = arr[i];
            }
            sum += arr[i];
        }

        System.out.println(sum * 100.00 / max / n);
    }
}
