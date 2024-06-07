package com.example.main.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

//좋은 수 구하기(투포인터) 1253
public class Program8 {
    public static void doing() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());

        long[] arr = new long[n];

        StringTokenizer st = new StringTokenizer(br.readLine());
        for (int i = 0; i < n; i++) {
            arr[i] = Long.parseLong(st.nextToken());
        }
        Arrays.sort(arr); //정렬 후 양쪽 끝에서 투포인터 시도

        int cnt = 0;
        for (int k = 0; k < n; k++) {
            int i = 0;
            int j = n - 1;

            while (i < j) {
                if (arr[i] + arr[j] < arr[k]) {
                    i++;
                } else if (arr[i] + arr[j] > arr[k]) {
                    j--;
                } else {
                    if (i != k && j != k) {
                        cnt++;
                        break;
                    } else if (i == k) {
                        i++;
                    } else { //j==k
                        j--;
                    }
                }
            }
        }
        System.out.println(cnt);
        br.close();
    }
}
