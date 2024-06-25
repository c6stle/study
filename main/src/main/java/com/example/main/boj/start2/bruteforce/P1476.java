package com.example.main.boj.start2.bruteforce;

import java.util.Scanner;

//날짜 계산
public class P1476 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int e = sc.nextInt();
        int s = sc.nextInt();
        int m = sc.nextInt();

        int e1 = 0;
        int s1 = 0;
        int m1 = 0;

        int year = 0;
        while (e != e1 || s != s1 || m != m1) {
            e1++;
            s1++;
            m1++;
            year++;
            if (e1 > 15) {
                e1 = 1;
            }
            if (s1 > 28) {
                s1 = 1;
            }
            if (m1 > 19) {
                m1 = 1;
            }
        }
        System.out.println(year);
    }
}
