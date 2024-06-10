package com.example.main.boj.정렬;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class P2751 {

    public static int[] arr;
    public static int[] temp;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        int n = Integer.parseInt(br.readLine());

        arr = new int[n];
        temp = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = Integer.parseInt(br.readLine());
        }
        //start index, last index
        merge_sort(0, n - 1);

        for (int i : arr) {
            bw.write(i + "\n");
        }
        bw.flush();
        bw.close();
    }

    private static void merge_sort(int start, int end) {
        if (end - start < 1) {
            return;
        }
        //홀수 이면 앞쪽이 더 많도록 함
        int mid = start + (end - start) / 2;
        merge_sort(start, mid);
        merge_sort(mid + 1, end);

        for (int i = start; i <= end; i++) {
            temp[i] = arr[i];
        }

        int k = start;
        int index1 = start;
        int index2 = mid + 1;

        while (index1 <= mid && index2 <= end) {
            if (temp[index1] < temp[index2]) {
                arr[k] = temp[index1];
                index1++;
            } else {
                arr[k] = temp[index2];
                index2++;
            }
            k++;
        }
        //두 그룹이 정렬이 끝나고 나머지 배열을 붙여야함
        while (index1 <= mid) {
            arr[k] = temp[index1];
            index1++;
            k++;
        }
        while (index2 <= end) {
            arr[k] = temp[index2];
            index2++;
            k++;
        }
    }
}
