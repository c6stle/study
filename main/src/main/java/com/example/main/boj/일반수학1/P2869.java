package com.example.main.boj.일반수학1;

import java.util.Scanner;

public class P2869 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        double a = sc.nextDouble();
        double b = sc.nextDouble();
        double height = sc.nextDouble();

        int day = (int) Math.ceil((height - b) / (a - b));
        System.out.println(day);
    }
}
