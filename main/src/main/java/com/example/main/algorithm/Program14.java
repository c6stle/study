package com.example.main.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

//절댓값 힙 구현하기(우선순위 큐) 11286
public class Program14 {

    public static void doing() throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(bf.readLine());
        PriorityQueue<Integer> myQueue = new PriorityQueue<>((o1, o2) -> {
            int first_abs = Math.abs(o1);
            int second_abs = Math.abs(o2);
            if (first_abs == second_abs) {
                //절대값이 같은경우 실제 값이 더 작은게 우선순위 높음
                return o1 > o2 ? 1 : -1;
            }
            return first_abs - second_abs;//앞수가 더크면 양수 리턴, 두번째 수가 더 크면 음수 리턴
        });

        for (int i = 0; i < n; i++) {
            int req = Integer.parseInt(bf.readLine());
            if (req == 0) {
                if (myQueue.isEmpty()) {
                    System.out.println("0");
                } else {
                    System.out.println(myQueue.poll());
                }
            } else {
                myQueue.add(req);
            }
        }

    }
}
