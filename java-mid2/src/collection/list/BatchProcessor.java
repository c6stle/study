package collection.list;

public class BatchProcessor {

    private final MyList<Integer> list;

    //MyList = new MyArrayList
    //MyList = new MyLinkedList
    public BatchProcessor(MyList<Integer> list) {
        this.list = list;
    }

    public void logic(int size) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            list.add(0, i);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("크기: " + size + ", 계산시간: " + (endTime - startTime) + "ms");
    }
}
