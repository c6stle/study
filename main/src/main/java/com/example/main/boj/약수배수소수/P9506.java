package com.example.main.boj.약수배수소수;

import java.util.Scanner;

public class P9506 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            int n = sc.nextInt();
            if (n == -1) {
                break;
            }
            StringBuffer str = new StringBuffer();
            str.append(n);

            StringBuilder str1 = new StringBuilder();
            int sum = 0;
            for (int i = 1; i <= n/2; i++) {
                if (n % i == 0) {
                    sum += i;
                    str1.append(i);
                    if (i != n / 2) {
                        str1.append(" + ");
                    }
                }
            }

            if (sum == n) {
                str.append(" = ").append(str1);
            } else {
                str.append(" is NOT perfect.");
            }
            System.out.println(str);
        }
    }
}
