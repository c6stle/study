package com.example.main.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

//DNA 문자열(슬라이딩윈도우) 12891
public class Program9 {

    static int[] checkArr;
    static int[] nowArr;
    static int checkSecret;

    public static void doing() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        int p = Integer.parseInt(st.nextToken());

        int result = 0;

        //input 문자열
        char[] inputArr = br.readLine().toCharArray();

        //4자리 숫자 배열
        st = new StringTokenizer(br.readLine());
        checkArr = new int[4];
        nowArr = new int[4];
        for (int i = 0; i < checkArr.length; i++) {
            checkArr[i] = Integer.parseInt(st.nextToken());
            if (checkArr[i] == 0) {
                checkSecret++;
            }
        }

        //초기값 부분배열 세팅
        for (int i = 0; i < p; i++) {
            Add(inputArr[i]);
        }

        if (checkSecret == 4) {
            result ++; //4개 모두 만족시 result 증가
        }

        //이후 추가되는 건에 한에서만 문자 한개만 처리
        for (int i = p; i < n; i++) {
            int j = i - p; //맨앞자리 인덱스 = j, 맨 끝자리 인덱스 = i
            Add(inputArr[i]);
            Remove(inputArr[j]);
            if (checkSecret == 4) {
                result ++;
            }
        }

        System.out.println(result);
        br.close();
    }

    private static void Add(char c) {
        switch (c) {
            case 'A' -> {
                nowArr[0]++;
                if (nowArr[0] == checkArr[0]) {
                    checkSecret++;
                }
            }
            case 'C' -> {
                nowArr[1]++;
                if (nowArr[1] == checkArr[1]) {
                    checkSecret++;
                }
            }
            case 'G' -> {
                nowArr[2]++;
                if (nowArr[2] == checkArr[2]) {
                    checkSecret++;
                }
            }
            case 'T' -> {
                nowArr[3]++;
                if (nowArr[3] == checkArr[3]) {
                    checkSecret++;
                }
            }
        }
    }

    private static void Remove(char c) {
        switch (c) {
            case 'A' -> {
                if (nowArr[0] == checkArr[0]) {
                    checkSecret--;
                }
                nowArr[0]--;
            }
            case 'C' -> {
                if (nowArr[1] == checkArr[1]) {
                    checkSecret--;
                }
                nowArr[1]--;
            }
            case 'G' -> {
                if (nowArr[2] == checkArr[2]) {
                    checkSecret--;
                }
                nowArr[2]--;
            }
            case 'T' -> {
                if (nowArr[3] == checkArr[3]) {
                    checkSecret--;
                }
                nowArr[3]--;
            }
        }
    }
}
