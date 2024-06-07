package com.example.main.boj.일반수학1;

import java.util.Scanner;

public class P2720 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        for (int i = 0; i < n; i++) {
            int num = sc.nextInt();
            int quarter = num / 25;

            num = num % 25;
            int dime = num / 10;

            num = num % 10;
            int nickel = num / 5;

            int penny = num % 5;

            System.out.println(quarter + " " + dime + " " + nickel + " " + penny);
        }
    }
}
