import java.util.Arrays;

public class ArrayQueue {
    private String[] queue;
    private int capacity;

    private int head;
    private int tail;

    public ArrayQueue(int capacity) {
        queue = new String[capacity];
        this.capacity = capacity;
    }

    public boolean enqueue(String item) {
        if (tail == capacity) {
            if (head == 0) { // queue is full
                System.out.println("queue is full");
                return false;
            }

            // move items to 0~[tail - head]
            for (int i = head; i < tail; i++) {
                queue[i - head] = queue[i];
            }

            // update tail and head
            tail -= head;
            head = 0;
        }

        queue[tail++] = item;
        System.out.println("in: " + Arrays.toString(queue));
        return true;
    }

    public String dequeue() {
        if (head == tail) {
            System.out.println("queue is empty");
            return null;
        }

        String ret = queue[head];
        queue[head++] = null;

        System.out.println("out: " + Arrays.toString(queue));

        return ret;
    }

    public static void main(String[] args) {
        ArrayQueue arrayQueue = new ArrayQueue(3);
        arrayQueue.enqueue("1");
        arrayQueue.enqueue("2");
        arrayQueue.enqueue("3");
        arrayQueue.enqueue("4");

        System.out.println(arrayQueue.dequeue());
        arrayQueue.enqueue("4");

        for (int i = 0; i < arrayQueue.capacity; i++) {
            System.out.println(arrayQueue.dequeue());
        }

        arrayQueue.enqueue("5");
        arrayQueue.enqueue("6");

    }
}
