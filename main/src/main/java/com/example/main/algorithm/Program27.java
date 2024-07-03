package com.example.main.algorithm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

//미로탐색하기
public class Program27 {
    static int[] dx = {0, 1, 0, -1};
    static int[] dy = {1, 0, -1, 0};
    static boolean[][] visited;
    static int[][] arr;
    static int n, m;
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());

        arr = new int[n][m];
        visited = new boolean[n][m];

        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            String line = st.nextToken();
            for (int j = 0; j < m; j++) {
                arr[i][j] = line.charAt(j) - '0';
            }
        }
        bfs(0, 0);
        System.out.println(arr[n - 1][m - 1]);
    }

    static void bfs(int bx, int by) {
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{bx, by});
        while (!queue.isEmpty()) {
            int now[] = queue.poll();
            visited[bx][by] = true;
            for (int k = 0; k < 4; k++) { //상하좌우 4가지
                int x = now[0] + dx[k];
                int y = now[1] + dy[k];
                if (x >= 0 && y >= 0 && x < n && y < m) { //배열 넘어가지 않도록
                    if (arr[x][y] != 0 && !visited[x][y]) { //0이어서 갈 수 없거나 방문한 배열 제외
                        visited[x][y] = true;
                        arr[x][y] = arr[now[0]][now[1]] + 1;
                        queue.add(new int[]{x, y});
                    }
                }
            }
        }
    }
}
