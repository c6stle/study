package com.example.main.boj.심화1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class P25206 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        double aimGrade = 0.0;
        double aimSum = 0.0;
        String tmp = "";
        //while ((tmp = br.readLine()) != null) {
        for (int i=0; i<20; i++) {
            String[] arr = br.readLine().split(" ");
            double aim = Double.parseDouble(arr[1]);

            String gradeStr = arr[2];
            double grade = 0.0;
            if ("A+".equals(gradeStr)) {
                grade = 4.5;
            } else if ("A0".equals(gradeStr)) {
                grade = 4.0;
            } else if ("B+".equals(gradeStr)) {
                grade = 3.5;
            } else if ("B0".equals(gradeStr)) {
                grade = 3.0;
            } else if ("C+".equals(gradeStr)) {
                grade = 2.5;
            } else if ("C0".equals(gradeStr)) {
                grade = 2.0;
            } else if ("D+".equals(gradeStr)) {
                grade = 1.5;
            } else if ("D0".equals(gradeStr)) {
                grade = 1.0;
            } else if ("F".equals(gradeStr)) {
                grade = 0.0;
            } else {
                continue;
            }
            aimGrade += aim * grade;
            aimSum += aim;
        }
        System.out.println(aimGrade / aimSum);
    }
}
