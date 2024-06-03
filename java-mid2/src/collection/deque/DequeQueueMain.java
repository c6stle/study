package collection.deque;

import java.util.ArrayDeque;
import java.util.Deque;

public class DequeQueueMain {

    public static void main(String[] args) {
        Deque<Integer> deque = new ArrayDeque<>();
        //Deque<Integer> deque = new LinkedList<>();

        //DequeQueue 데이터 추가 offer
        deque.offer(1);
        deque.offer(2);
        deque.offer(3);
        System.out.println(deque);

        System.out.println("deque.peek() = " + deque.peek());

        //DequeQueue 데이터 꺼내기 poll
        System.out.println("deque.poll() = " + deque.poll());
        System.out.println("deque.poll() = " + deque.poll());
        System.out.println("deque.poll() = " + deque.poll());
        System.out.println(deque);
    }
}
