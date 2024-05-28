package collection.array;

import java.util.Arrays;

public class MyArrayListV4<E> {

    private static final int DEFAULT_CAPACITY = 5;

    private Object[] elementData;
    private int size = 0;

    public MyArrayListV4() {
        elementData = new Object[DEFAULT_CAPACITY];
    }

    public MyArrayListV4(int initialCapacity) {
        elementData = new Object[initialCapacity];
    }

    public int size() {
        return size;
    }

    public void add(E e) {
        if (size == elementData.length) {
            grow();
        }
        elementData[size] = e;
        size++;
    }

    //추가부분
    public void add(int index, E e) {
        if (size == elementData.length) {
            grow();
        }
        shiftRightFrom(index);
        elementData[index] = e;
        size++;
    }
    //추가부분, 요소의 마지막부터 index 까지 오른쪽으로 이동
    private void shiftRightFrom(int index) {
        for (int i = size; i > index; i--) {
            elementData[i] = elementData[i - 1];
        }
    }

    private void grow() {
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity * 2;
        //배열을 새로 만들고, 기존 배열을 새로운 배열에 복사
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    /** @noinspection unchecked*/
    public E get(int index) {
        return (E) elementData[index];
    }

    public E set(int index, E e) {
        //기존값 반환
        E oldValue = get(index);
        elementData[index] = e;
        return oldValue;
    }

    //추가부분
    public E remove(int index) {
        E oldValue = get(index);
        //데이터 이동
        shiftLeftFrom(index);
        size --;
        elementData[size] = null;
        return oldValue;
    }

    private void shiftLeftFrom(int index) {
        for (int i = index; i < size - 1; i++) {
            elementData[i] = elementData[i + 1];
        }
    }

    public int indexOf(E obj) {
        for (int i = 0; i < size; i++) {
            if (obj.equals(elementData[i])) {
                return i;
            }
        }
        return -1;
    }

    public String toString() {
        //[1,2,3]
        //Arrays.copyOf(elementData, size) : size 만큼만 출력
        return Arrays.toString(Arrays.copyOf(elementData, size)) +
                " size=" + size +
                ", capacity=" + elementData.length;
    }
}
