package com.example.main.algorithm;

import java.io.IOException;
import java.util.Scanner;

//연속된 자연수의 합(투포인터) 2018
public class Program6 {
    public static void doing() throws IOException {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int cnt = 1;
        int start = 1;
        int end = 1;
        int sum = 1;

        while (end != n) {
            if (sum == n) {
                cnt ++;
                end ++;
                sum = sum + end;
            } else if (sum > n) {
                sum = sum - start;
                start ++;
            } else {
                end ++;
                sum = sum + end;
            }
        }

        System.out.println(cnt);
    }
}
