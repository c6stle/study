package collection.deque;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

public class DequeStackMain {

    public static void main(String[] args) {
        Deque<Integer> deque = new ArrayDeque<>();
        //Deque<Integer> deque = new LinkedList<>();

        //DequeStack 데이터 추가 push
        deque.push(1);
        deque.push(2);
        deque.push(3);
        System.out.println(deque);

        System.out.println("deque.peek() = " + deque.peek());

        //DequeStack 데이터 꺼내기 pop
        System.out.println("deque.pop() = " + deque.pop());
        System.out.println("deque.pop() = " + deque.pop());
        System.out.println("deque.pop() = " + deque.pop());
        System.out.println(deque);
    }
}
