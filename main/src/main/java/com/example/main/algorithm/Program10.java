package com.example.main.algorithm;

import java.io.*;
import java.util.Deque;
import java.util.LinkedList;
import java.util.StringTokenizer;

//최소값 찾기(슬라이딩 윈도우, 덱 구조) 11003
public class Program10 {

    public static void doing() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        int p = Integer.parseInt(st.nextToken());

        st = new StringTokenizer(br.readLine());

        Deque<Node> myDeque = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            int now = Integer.parseInt(st.nextToken());

            while (!myDeque.isEmpty() && myDeque.getLast().value > now) {
                myDeque.removeLast();
            }
            myDeque.addLast(new Node(now, i));

            if (myDeque.getFirst().index <= i - p) {
                myDeque.removeFirst();
            }
            bw.write(myDeque.getFirst().value + " ");
        }
        bw.flush();
        bw.close();
    }

    static class Node {
        public int value;
        public int index;

        Node(int value, int index) {
            this.value = value;
            this.index = index;
        }
    }


}
