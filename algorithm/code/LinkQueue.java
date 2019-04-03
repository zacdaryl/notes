public class LinkQueue {
    private class Node {
        private String item;
        private Node next;

        public Node(String item) {
            this.item = item;
        }
    }

    private Node head;
    private Node tail;

    private int size = 0;

    public void enqueue(String item) {
        Node node = new Node(item);
        if (this.isEmpty()) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            tail = node;
        }

        size++;
        String str = String.format("enqueue size: %d, %s", size, toString());
        System.out.println(str);
    }

    public String dequeue() {
        if (this.isEmpty()) {
            return null;
        }

        Node ret = head;
        head = head.next;
        size--;

        String retItem = ret.item;

        String str = String.format("dequeue size: %d, %s", size, toString());
        System.out.println(str);
        System.out.println(retItem + " -> out");

        return retItem;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }

        StringBuilder strBuilder = new StringBuilder("[");

        Node nodePointer = head;

        do {
            strBuilder.append(nodePointer.item);
            if (nodePointer.next != null) {
                strBuilder.append(", ");
            }
            nodePointer = nodePointer.next;
        } while (nodePointer != null);

        strBuilder.append("]");

        return strBuilder.toString();
    }

    public static void main(String[] args) {
        LinkQueue linkQueue = new LinkQueue();

        System.out.println("size: " + linkQueue.size());

        for (int i = 0; i < 8; i++) {
            linkQueue.enqueue(String.valueOf(i));
        }

        for (int i = 0; i < 9; i++) {
            linkQueue.dequeue();
        }

        linkQueue.dequeue();
    }
}