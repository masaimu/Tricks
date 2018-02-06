import sun.misc.Unsafe;

import java.lang.reflect.Constructor;

/**
 * 此类来源于ConcurrentLinkedQueue，构造一个无锁的线程安全的队列（FIFO），
 * 主要是实现offer和poll两个方法，看看在并发操作的情况下，如何操作队列。
 *
 * 这个队列还有一些特点：
 * 1、队列长度无限制，所以使用该队列要小心内存过度消耗；
 * 2、队列没有阻塞机制：无长度限制所以offer不会阻塞，队列为空时poll返回null。
 *
 * 当然啦，我们的目的还是去学习高手是如何解决并发问题的。
 */
public class ConcurrentLinkedQueueTrick<E> {

    private static class Node<E>{
        volatile E item;
        volatile Node<E> next;

        Node(E item){
            UNSAFE.putObject(this, itemOffset, item);
        }

        boolean casItem(E oldItem, E it){
            return UNSAFE.compareAndSwapObject(this, itemOffset, oldItem, it);
        }

        boolean casNext(Node<E> oldNext, Node<E> n){
            return UNSAFE.compareAndSwapObject(this,nextOffset, oldNext, n);
        }

        private static final sun.misc.Unsafe UNSAFE;
        private static final long itemOffset;
        private static final long nextOffset;
        static{
            try {
                Constructor con = sun.misc.Unsafe.class.getDeclaredConstructor();
                con.setAccessible(true);
                UNSAFE = (Unsafe) con.newInstance(null);
                Class k = Node.class;
                itemOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("itemOffset"));
                nextOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("nextOffset"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    private Node<E> head;
    private Node<E> tail;

    public ConcurrentLinkedQueueTrick(){
        head = tail = new Node<E>(null);
    }

    /**
     * 该方法向队列中插入新元素，为保证线程安全，需要采用如下策略。
     * 1、如果指针p已经指向了队列的尾节点，就利用casNext将新节点链接到队末即可；
     * 2、如果指针p指向的节点已经出队了，说明tail指针可能也已经随着出队的节点失效了，
     *    那就看看tail指针有没有被其他线程进行修正，如果tail没有变化的话，就需要将p设置
     *    成head，从头再捋一遍。
     * 3、
     * @param item
     * @return
     */
    public boolean offer(E item){
        return false;
    }



}
