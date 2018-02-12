package indi.sam.tricks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by MaSaimu on 2018/2/8.
 *
 * 该类是来自于LinkedBlockingQueue，是一个由链表构成的阻塞队列。
 * 本类中提供put操作和take操作，他们都拥有阻塞技能，在队列不满足操作要求时进入阻塞等待。
 * 另外本类中还会提供有超时时间的put和take操作，看看超时时间是如何发挥作用的。
 *
 * 与ConcurrentLinkedQueue不同的是，该类采用了ReentrantLock锁来解决并发问题，而CLQ采用CAS机制。
 * 采用锁的好处是队列中的节点个数可以被精确统计，这样就能限制队列的capacity，所以LBQ的阻塞机制才有意义。
 * CLQ这种unbounded队列根本就没有触发阻塞的条件 :)
 */
public class LinkedBlockingQueueTrick<E> {

    /**
     * 看起来就没有CLQ的Node节点复杂，因为不需要大量的cas操作。
     * @param <E>
     */
    static class Node<E>{
        E item;
        Node<E> next;

        Node(E e){
            item = e;
        }
    }

    private Node<E> head;
    private Node<E> tail;
    private final int capacity; //容量一经初始化，就不能再改变了，加上final
    private AtomicInteger count = new AtomicInteger();
    private final ReentrantLock putlock = new ReentrantLock();
    private final Condition notFull = putlock.newCondition();
    private final ReentrantLock takelock = new ReentrantLock();
    private final Condition notEmpty = takelock.newCondition();

    public LinkedBlockingQueueTrick(int capacity){
        if(capacity < 0){
            throw new IllegalArgumentException();
        }
        this.capacity = capacity;
        head = tail = new Node(null);
    }

    public LinkedBlockingQueueTrick(){
        this(Integer.MAX_VALUE);
    }

    /**
     * 入队列的流程如下：
     * 1、加锁，使用ReentrantLock
     * 2、阻塞等待队列有空间可以做入队操作，等待条件使用Condition
     * 3、进行入队列操作，因为已经加锁了，所以这里的入队列操作比较简单，不用考虑线程安全问题。
     * 4、入队完成后，可以唤醒下一个阻塞的入队操作线程。
     * 5、释放锁。
     * 6、如果入队操作之前队列里元素为0，则说明很有可能有take线程在阻塞中，尝试去唤醒。
     * @param item
     * @throws InterruptedException
     */
    public void put(E item) throws InterruptedException {
        checkNotNull(item);
        Node node = new Node(item);
        int c = -1;
        ReentrantLock lock = this.putlock;
        AtomicInteger count = this.count;
        lock.lockInterruptibly();
        try{
            while (count.get() == capacity){
                notFull.await();
            }
            enqueue(node);
            c = count.getAndIncrement();
            if(c+1 < capacity){
                notFull.signal();
            }
        }finally {
            lock.unlock();
        }
        if(c == 0){
            /*
            可能有一堆take线程都在await，这个线程此时就需要招呼一下这些等吃的的狗狗。
             */
            signalNotEmpty();
        }
    }

    /**
     * 加上了阻塞时间的put方法
     * @param item
     * @param timeout
     * @param unit
     */
    public void put(E item, long timeout, TimeUnit unit) throws InterruptedException {
        checkNotNull(item);
        Node<E> node = new Node<E>(item);
        ReentrantLock putlock = this.putlock;
        AtomicInteger count = this.count;
        int c = -1;
        long nanotime = unit.toNanos(timeout);
        putlock.lockInterruptibly();
        try{
            while (count.get() == capacity){
                if(nanotime < 0)
                    return;
                nanotime = this.notFull.awaitNanos(nanotime);
            }
            enqueue(node);
            c = count.getAndIncrement();
            if(c + 1 < capacity){
                this.notFull.signal();
            }
        }finally {
            putlock.unlock();
        }
        if(c == 0){
            this.notEmpty.signal();
        }
    }

    /**
     * 出队列和入队列的操作流程类似。
     * @return
     */
    public E take() throws InterruptedException {
        ReentrantLock takelock = this.takelock;
        AtomicInteger count = this.count;
        int c = -1;
        E item;
        takelock.lockInterruptibly();
        try{
            while(count.get() == 0){
                this.notEmpty.await();
            }
            item = dequeue();
            c = count.getAndDecrement();
            if(c - 1 > 0){
                this.notEmpty.signal();
            }
        }finally {
            takelock.unlock();
        }
        if(c == capacity){
            signalNotFull();
        }
        return item;
    }

    /**
     * 加了阻塞时间的出队操作
     * @return
     */
    public E take(long timeout, TimeUnit unit) throws InterruptedException {
        ReentrantLock takelock = this.takelock;
        AtomicInteger count = this.count;
        int c = -1;
        long nanotime = unit.toNanos(timeout);
        E item;
        takelock.lockInterruptibly();
        try{
            while(count.get() == 0){
                if(nanotime < 0){
                    return null;
                }
                nanotime = this.notEmpty.awaitNanos(nanotime);
            }
            item = dequeue();
            c = count.getAndDecrement();
            if(c - 1 > 0){
                this.notEmpty.signal();
            }
        }finally {
            takelock.unlock();
        }
        if(c == capacity){
            signalNotFull();
        }
        return item;
    }

    private void checkNotNull(E item) {
        if(item == null){
            throw new NullPointerException();
        }
    }

    private void signalNotFull() {
        ReentrantLock putlock = this.putlock;
        putlock.lock();
        try {
            this.notFull.signal();
        }finally {
            putlock.unlock();
        }
    }

    private void signalNotEmpty() {
        ReentrantLock takelock = this.takelock;
        takelock.lock();
        try{
            this.notEmpty.signal();
        }finally {
            takelock.unlock();
        }
    }

    private void enqueue(Node node) {
        tail = tail.next = node;
    }

    private E dequeue() {
        Node<E> h = head;
        Node<E> first = h.next;
        E item = first.item;
        first.item = null;
        head = first;
        h.next = h;
        return item;
    }

}
