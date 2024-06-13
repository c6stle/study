package com.example.main.boj.start1.math;

import java.util.Scanner;

//최대공약수와 최소공배수
public class P2609 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int num1 = sc.nextInt();
        int num2 = sc.nextInt();

        int a = Math.min(num1, num2);
        while ((num1 % a) != 0 || (num2 % a) != 0) {
            a--;
        }
        System.out.println(a);
        System.out.println((num1 * num2) / a);
    }
}
