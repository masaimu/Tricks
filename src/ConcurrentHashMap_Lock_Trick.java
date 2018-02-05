import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.util.concurrent.locks.LockSupport;

/**
 * 这个类是ConcurrentHashmap类中的一种读写锁，用于控制对某个节点的红黑树进行读写时的并发控制。
 * 由于ConcurrentHashmap的设计对每一个tab[i]的写操作都加了synchronized同步锁，
 * 因此该锁不用考虑写-写竞争的情况。
 * 该锁允许多个线程同时进行读操作，此时写操作的线程会通过自旋进入LockSupport的阻塞中，直到最后
 * 一个读线程离开时将其唤醒。但是当红黑树正在由写线程进行树结构维护时，读线程可以按照链表的方式
 * 线性去读。
 */
public class ConcurrentHashMap_Lock_Trick {
    private volatile int lockState;
    private static final int WRITER = 1;
    private static final int WAITER = 2;
    private static final int READER = 4;
    private Thread waiter = null;

    public void write(){
        lockRoot();
        try {
            /*
            进行写操作
             */
        }finally {
            unlockRoot();
        }

    }

    /**
     * 读操作首先看有没有写操作正在进行，如果有，就以链表方式进行读
     * 如果没有写操作，就加上读锁，最后一个读锁离开的时候要记得唤醒
     * pack的写线程。
     */
    public void read(){

        for(int s;;){
            if(((s = lockState) & (WAITER|WRITER)) != 0){
            /*
            说明WAITER或者WRITER位被置为1，有线程正在进行写操作
            可以等待，在concurrentHahsmap中，此处开始以链表的方式进行读操作。
            其实此处有一点巧妙设计，当有写线程在等待读操作时，此时后来的读操
            作不再加读锁，以防止写线程产生饥饿。
             */
            }else if(U.compareAndSwapInt(this,LOCKSTATE,s,s+READER)){
                /*
                没有写锁，所以加一个读锁，这样的读锁可以加好多个。
                 */
                try{
                    //进行读操作
                }finally {
                    if(U.getAndAddInt(this,LOCKSTATE,-READER) == (READER|WAITER)
                            && waiter != null){
                        /*
                        READER说明现在有读锁，WAITER是写线程加上去的，说明有写线程在等待
                         */
                        Thread w = waiter;
                        LockSupport.unpark(w);
                    }
                }
                return;
            }
        }

    }

    private void lockRoot(){
        if(!U.compareAndSwapInt(this, LOCKSTATE,0,WRITER)){
            contentedLock();
        }
    }

    /**
     * 这里可以看到，该方法并不足以将阻塞中的线程激活，只是许可了线程继续工作
     */
    private void unlockRoot(){
        lockState = 0;
    }

    private void contentedLock() {
        boolean waiting = false;
        for(int s;;){
            if(((s = lockState) & ~WAITER) == 0){
                /*
                说明此时没有线程加读锁，写线程可以正常获取到写锁
                所以此时先把所状态置位写锁状态
                 */
                if(U.compareAndSwapInt(this,LOCKSTATE,s,WRITER)){
                    if(waiting)
                        waiter = null;
                    return;
                }
            }else if((s & WAITER) == 0){
                /*
                进入此处说明已经有线程加了读锁，且WAITER位还没有被占，先占领WAITER位
                 */
                if(U.compareAndSwapInt(this,LOCKSTATE,s,s|WAITER)){
                    waiting = true;
                    waiter = Thread.currentThread();
                }
            }else if(waiting){
                /*
                写线程自旋到此处时，说明WAITER位已经置成功，需要进入阻塞状态了
                 */
                LockSupport.park(this);
            }
        }
    }


    private static final sun.misc.Unsafe U;
    private static final long LOCKSTATE;
    static {
        try {
            Constructor con = Unsafe.class.getDeclaredConstructor();
            con.setAccessible(true);
            U = (Unsafe) con.newInstance(null);
            Class k = ConcurrentHashMap_Lock_Trick.class;
            LOCKSTATE = U.objectFieldOffset(k.getDeclaredField("lockState"));
        } catch (Exception e) {
            throw new Error(e);
        }

    }
}
