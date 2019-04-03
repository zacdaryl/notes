import java.util.Arrays;

public class CircularQueue {
    private String[] queue;
    private int capacity;

    private int head;
    private int tail;

    public CircularQueue(int capacity) {
        this.capacity = capacity;
        queue = new String[capacity];
    }

    public boolean enqueue(String item) {
        // 入队操作，判断队列是否已满，未满则可入队
        if ((tail + 1) % capacity == head) {
            System.out.println("*** queue is full");
            return false;
        }

        queue[tail] = item;
        tail = (tail + 1) % capacity;

        System.out.println("enqueue: " + this.toString());

        return true;
    }

    public String dequeue() {
        // 当队列为空时，返回null
        if (tail == head) {
            System.out.println("*** queue is empty");
            return null;
        }

        String item = queue[head];
        queue[head] = null;

        head = (head + 1) % capacity;
        System.out.println("dequeue " + item + " :" + this.toString());

        return item;
    }

    @Override
    public String toString() {
        return Arrays.toString(queue);
    }

    public static void main(String[] args) {
        CircularQueue circularQueue = new CircularQueue(8);
        for (int i = 0; i < 8; i++) {
            circularQueue.enqueue("item-" + i);
        }

        for (int i = 0; i < 16; i++) {
            circularQueue.dequeue();
            circularQueue.enqueue(i + "");
        }
    }
}
