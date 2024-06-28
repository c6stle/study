package com.example.main.boj.start2.bruteforce.permutation;

import java.util.Scanner;

//모든 순열
public class P10974 {

    static int[] arr;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        arr = new int[n];
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            arr[i - 1] = i;
            sb.append(arr[i - 1]).append(" ");
        }
        sb.append("\n");
        while (hasNextPermutation()) {
            for (int i = 0; i < n; i++) {
                sb.append(arr[i]).append(" ");
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }

    static boolean hasNextPermutation() {
        int i = arr.length - 1;
        while (i > 0 && arr[i - 1] > arr[i]) {
            i--;
        }

        if (i == 0) {
            return false;
        }

        int j = arr.length - 1;
        while (arr[i - 1] > arr[j]) {
            j--;
        }

        swap(i - 1, j);

        j = arr.length - 1;
        while (i < j) {
            swap(i, j);
            i++;
            j--;
        }
        return true;
    }

    static void swap(int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
