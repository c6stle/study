package com.example.main.boj.start1.math;

import java.util.Scanner;

//나머지
public class P10430 {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        int a = sc.nextInt();
        int b = sc.nextInt();
        int c = sc.nextInt();

        System.out.println((a + b) % c);
        System.out.println(((a % c) + (b % c)) % c);
        System.out.println((a*b) % c);
        System.out.println(((a % c) * (b % c)) % c);
    }
}
