package com.example.main.boj.일반수학1;

import java.util.Scanner;

public class P1193 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        int num = 1;
        int den = 1;
        int count = 1;
        if (n != 1) {
            while (count < n) {
                if (num == 1 && num <= den) {
                    den++;
                    count++;
                    while (den != 1 && count < n) {
                        num++;
                        den--;
                        count++;
                    }
                } else if (den == 1 && den < num) {
                    num++;
                    count++;
                    while (num != 1 && count < n) {
                        num--;
                        den++;
                        count++;
                    }
                }
            }
        }

        System.out.println(num + "/" + den);
    }
}
