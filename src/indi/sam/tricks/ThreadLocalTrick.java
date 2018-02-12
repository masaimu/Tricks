package indi.sam.tricks;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 通过阅读ThreadLocal类的代码，我们要从中提炼出开放定址法hashmap的基本用法，
 * 本类仿照ThreadLocalMap对开放定址法hashmap的实现。
 **/
public class ThreadLocalTrick {
    private int size = 0;

    private Entry[] table = new Entry[16];

    /**
     * 这个类代表了map中的key，我们将采用“0x61c88647”这个神奇的数字来生成hash值，
     * 在2的指数长度的hashmap中，它拥有着良好的散列值
     */
    public static class Key{
        private static final int HASH_INCREMENT = 0x61c88647;
        final int keyHashCode = getNextHashCode();
        private static AtomicInteger nextHashCode = new AtomicInteger();
        static int getNextHashCode(){
            return nextHashCode.getAndAdd(HASH_INCREMENT);
        }
    }

    /**
     * 让元素Entry继承自弱引用，就可以让Entry对Key实现弱引用，不会因为这个hashmap
     * 而影响到Key对象的gc。所以我们这个map注定是一个话语权比较弱的数据结构，哈哈。
     */
    public class Entry extends WeakReference<Key> {
        Object value;
        public Entry(Key key, Object value) {
            super(key);
            this.value = value;
        }
    }

    public Entry getEntry(Key key){
        int index = key.getNextHashCode() & (table.length-1);
        Entry e = table[index];
        if(e != null && e.get() == key){
            return e;
        }else{
            return getEntryAfterMiss(key,e,index);
        }
    }

    public void set(Key key, Object value){

        int len = table.length;
        Entry[] tab = table;
        int i = key.keyHashCode & (len-1);

        Entry e = tab[i];
        if(e == null){
            tab[i] = new Entry(key, value);
        }else if(e.get() != null){//发生了碰撞，且该出e还没有过期
            if(e.get() == key){
                e.value = value;
            }else{

            }

        }else{//发生了碰撞，且该处的e已经过期了

        }

    }

    private Entry getEntryAfterMiss(Key key, Entry e, int index) {
        Entry[] tab = table;
        int len = tab.length;

        while(e != null){
            Key k = e.get();
            if(k == key){return e;}
            if(k == null){expungeStaleEntry(index);}
            else{index = nextIndex(index,len);}

            e = tab[index];
        }
        return null;
    }

    /**
     * 这个方法是清除掉index位置上的entry，
     * 除此之外，还需要将后面的相同key的元素回填。
     * @param index
     */
    private int expungeStaleEntry(int index) {
        Entry[] tab = table;
        int len = table.length;

        if(tab[index] != null){
            tab[index].value=null;
            tab[index] = null;
            size--;
        }


        /**
         * 目的是把后面的元素往前挪
         */
        Entry e;
        int i;
        for(i=nextIndex(index, len); (e = tab[i])!= null; i=nextIndex(i,len)){
            Key k = e.get();
            if(k == null){
                tab[i].value = null;
                tab[i] = null;
                size--;
            }else{
                int h = k.keyHashCode & (len-1);
                if(h != i){//这个Entry原本不应该在这儿，因为碰撞才被放到此处，现在将其尽量归位
                    tab[i] = null;

                    while(tab[h] != null){
                        h = nextIndex(h,len);
                    }
                    tab[h] = e;
                }
            }
        }
        return i;

    }

    private int nextIndex(int index, int len) {
        return (index + 1) < len ? index+1 : 0;
    }

}
