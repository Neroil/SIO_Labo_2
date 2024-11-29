package ch.heig.sio.lab2.groupD.Utilities;

/**
 * A simple linked list class that implements only what's useful in this lab. It also allows manual node manipulation.
 * This manual manipulation is useful for inserting in the list in O(1) complexity.
 *
 * @param <T>   The type of the value of the list
 * @author Edwin Häffner
 * @author Arthur Junod
 */
public class OptimizedLinkedList<T>{

    public static class Node<T>{
        private T value;
        private Node<T> next;
        private Node<T> previous;

        Node(T value) {
            this.value = value;
            this.next = null;
            this.previous = null;
        }

        public T getValue() {
            return value;
        }

        public Node<T> getNext() {
            return next;
        }

        public Node<T> getPrevious(){
            return previous;
        }
    }

    private Node<T> head;
    private Node<T> tail;

    private int size;

    /**
     * Insert a value after a specified node.
     * @param n The node after which the value should be inserted
     * @param value The value to insert, it will create a new node with this value
     * @return The newly created node
     */
    public Node<T> insertAfter(Node<T> n, T value){
        Node<T> newNode = new Node<>(value);
        Node<T> afterN = n.next;
        if (afterN != null) {
            afterN.previous = newNode;
        }
        n.next = newNode;
        newNode.previous = n;
        newNode.next = afterN;

        if (n == tail) {
            tail = newNode;
        }

        ++size;

        return newNode;
    }

    /**
     * Get the head of the list
     * @return The head of the list
     */
    public Node<T> getFirst(){
        return head;
    }

    /**
     * Add a value at the end of the list
     * @param value The value to add
     * @return The newly created node
     */
    public Node<T> add(T value){
        Node<T> newNode = new Node<>(value);

        if(isEmpty()){
            tail = newNode;
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.previous = tail;
            tail = newNode;
        }

        ++size;

        return newNode;
    }

    /**
     * Remove a node from the list
     * WARNING : This function is unsafe, it doesn't check if the node is in the list !
     * It is unsafe so it can be O(1) complexity
     *
     * @param node The node to remove
     */
    public void remove(Node<T> node){
        if (node == null) {
            return;
        }

        if(isEmpty()){
            return;
        }

        if (node == head) {
            head = node.next;
        }

        if (node == tail) {
            tail = node.previous;
        }

        if (node.previous != null) {
            node.previous.next = node.next;
        }

        if (node.next != null) {
            node.next.previous = node.previous;
        }

        --size;
    }

    public boolean isEmpty(){
        return tail == null && head == null;
    }

    public int size(){
        return size;
    }

}