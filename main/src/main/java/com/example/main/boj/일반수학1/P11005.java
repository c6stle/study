package com.example.main.boj.일반수학1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class P11005 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int num = sc.nextInt();
        int n = sc.nextInt();

        List<Character> arr = new ArrayList<>();
        while (num > 0) {
            int tmp = num % n;
            if (tmp >= 10) {
                arr.add((char) (tmp-10 + 'A'));
            } else {
                arr.add((char) (tmp + '0'));
            }
            num = num / n;
        }

        Collections.reverse(arr);
        for (Character character : arr) {
            System.out.print(character);
        }
    }
}
