package com.example.main.boj.start1.math;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//-2진수
public class P2089 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int N = Integer.parseInt(br.readLine());
        StringBuilder sb = new StringBuilder();

        if(N == 0){
            sb.append(0);
        } else{
            while (N != 1){
                sb.append(Math.abs(N % -2));
                N = (int)(Math.ceil((double)N/-2));
            }
            sb.append(N);
        }
        System.out.println(sb.reverse());
    }
}
