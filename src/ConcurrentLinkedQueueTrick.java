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
     * 3、如果p没有指向尾节点，并且所指节点也没有出队列，就看看尾节点有没有发生过变化(t!=(t=tail))，
     *    如果有变化，就将p指向tail节点，否则就让p=q，往后走一步。
     * @param item
     * @return
     */
    public boolean offer(E item){
        Node<E> node = new Node(item);

        for(Node<E> t = tail, p = t;;){
            Node<E> q = p.next;
            if(q == null){
                if(p.casNext(null, node)){
                    //此处还需要改一下tail指针的指向，因为有可能上一次添加节点的时候，tail没有做cas更新
                    if(p!=t){
                        casTail(t, node);
                    }
                    return true;
                }
            }else if(q == p){
                p = (t!=(t=tail)) ? t : head;
            }else{
                p = (t != (t=tail)) ? t : q;
            }
        }
    }

    /**
     * 当一个节点出队列后，只是把Item置为null，然后查看是否需要更新head
     * 更新的条件也比较简单：当出队节点是head指向节点的下一个节点时，说明此时已经有两个节点出队了，
     * 所以要更新head指向了。更新head指向的时候，要把前一个节点（也就是刚刚出队的节点）的next指向它自己，
     * 这样才能让其他线程知道此节点已经出队了。
     * @return
     */
    public E poll(){
        return null;
    }

    private boolean casTail(Node<E> t, Node<E> node) {
        return UNSAFE.compareAndSwapObject(this, tailOffset, t, node);
    }

    private static final sun.misc.Unsafe UNSAFE;
    private static final long tailOffset;
    static{
        try {
            Constructor con = Unsafe.class.getDeclaredConstructor();
            con.setAccessible(true);
            UNSAFE = (Unsafe) con.newInstance(null);
            Class k = ConcurrentLinkedQueueTrick.class;
            tailOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("tail"));
        }catch(Exception e){
            throw new Error(e);
        }
    }

}
