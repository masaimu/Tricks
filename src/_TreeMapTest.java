/**
 * 这个类是从treemap中提取出红黑树的实现代码
 */
public class _TreeMapTest<K,V> {
    Entry<K,V> root;

    /**
     * 二叉树的左旋转
     * @param p
     */
    public void rotateLeft(Entry<K,V> p){
        if(p != null){
            Entry r = p.right;
            p.right = r.left;
            if(r.left != null){
                r.left.parent = p;
            }
            r.parent = p.parent;
            if(p.parent == null){
                root = r;
            }else if(p.parent.left == p){
                p.parent.left = r;
            }else {
                p.parent.right = r;
            }
            p.parent = r;
            r.left = p;
        }
    }

    /**
     * 二叉树的右旋转
     * @param p
     */
    public void rotateRight(Entry<K,V> p){
        if(p != null){
            Entry<K,V> r = p.right;
            p.left = r.right;
            if(r.right != null){
                r.right.parent = p;
            }
            r.parent = p.parent;
            if(p.parent == null){
                root = r;
            }else if(p.parent.left == p){
                p.parent.left = r;
            }else{
                p.parent.right = r;
            }
            r.right = p;
            p.parent = r;
        }
    }

    public void deleteEntry(Entry p){

        if(p.left != null && p.right != null){
            Entry s = successor(p);
            p.key = s.key;
            p.value = s.value;
            p = s;
        }

        Entry replacement = (p.left != null ? p.left : p.right);
        if(replacement != null){
            /*
            这种情况是被删除的节点只有一颗子树，这种情况下就用子树代替
             */
            replacement.parent = p.parent;
            if(p.parent == null){
                root = replacement;
            }else if(p.parent.left == p){
                p.parent.left = replacement;
            }else{
                p.parent.right = replacement;
            }
            p.parent = p.left = p.right = null;

            if(p.Color == BLACK){
                fixAfterDeletion(replacement);
            }
        }else if(p.parent == null){
            /*
            到这儿说明没有左右子树，也没有父节点，就是一个孤立的节点
             */
            root = null;
        }else{
            /*
            没有左右子树的节点在此处理，如果是左右子树都健全的节点，
            由于p已经指到了后继节点上，因此也是属于没有左右子树的情况
             */
            if(p.Color == BLACK){
                fixAfterDeletion(p);
            }

            if(p.parent != null){
                if(p.parent.left == p){p.parent.left = null;}
                else if(p.parent.right == p){p.parent.right = null;}
                p.parent = null;
            }
        }
    }

    /**
     * 此方法用来在删除节点后，调整红黑树，使之重新满足红黑树特性
     * @param x
     */
    private void fixAfterDeletion(Entry x) {
        while(x != root && colorOf(x) == BLACK){
            if(x == x.parent.left){
                Entry sib = x.parent.right;

                if(colorOf(sib) == RED){
                 /*
                 case1:当前结点是黑色，兄弟结点是红色
                 1.将 "兄弟结点" 改为黑色；
                 2.将 "父亲结点" 改为红色；
                 3.以 "父亲结点" 为支点进行左旋；
                 4.左旋后，重新设置 "兄弟结点"。
                 现在兄弟节点就变成黑色的了。
                 */
                    sib.Color = BLACK;
                    x.parent.Color = RED;
                    rotateLeft(x.parent);
                    sib = x.parent.right;
                }

                if(colorOf(sib.left) == BLACK && colorOf(sib.right) == BLACK){
                /*
                case2:当前结点是黑色，兄弟结点是黑色，两个孩子为空或是黑色
                1.将 "兄弟结点" 改为红色；
                2.将 "父亲结点" 设置为新的 "当前结点"，继续进行操作。
                 */
                    sib.Color = RED;
                    x = x.parent;
                }else{
                    if(colorOf(sib.right) == BLACK){
                    /*
                    case3:当前结点是黑色，兄弟结点是黑色，兄弟结点的左孩子是红色，右孩子是为空或是黑色
                    1.将 "兄弟结点" 的左孩子改为黑色；
                    2.将 "兄弟结点" 改为红色；
                    3.以 "兄弟结点" 为支点进行右旋；
                    4.右旋后，重新设置 "当前结点" 的 "兄弟结点"。
                     */
                        sib.left.Color = BLACK;
                        sib.Color = RED;
                        rotateRight(sib);
                        sib = x.parent.right;
                    }

                /*
                case4:当前结点是黑色，兄弟结点是黑色，兄弟结点的右孩子是红色，左孩子为空或红黑皆可
                1.将 "父亲结点" 的颜色赋给 "兄弟结点"；
                2.将 "父亲结点" 改为黑色；
                3.将 "兄弟结点" 的右孩子改为黑色；
                4.以 "父亲结点" 为支点进行左旋；
                 */
                    sib.Color = x.parent.Color;
                    x.parent.Color = BLACK;
                    sib.right.Color = BLACK;
                    rotateLeft(x.parent);
                    x = root;
                }

            }else{
                Entry sib = x.parent.left;

                if(colorOf(sib) == RED){
                /*
                case1:当前结点是黑色，兄弟结点是红色
                1.将 "兄弟结点" 改为黑色；
                2.将 "父亲结点" 改为红色；
                3.以 "父亲结点" 为支点进行右旋；
                4.右旋后，重新设置 "兄弟结点"。
                现在兄弟节点就变成黑色的了。
                */
                    sib.Color = BLACK;
                    x.parent.Color = RED;
                    rotateRight(x.parent);
                    sib = x.parent.left;
                }

                if(colorOf(sib.left) == BLACK && colorOf(sib.right) == BLACK){
                /*
                case2:当前结点是黑色，兄弟结点是黑色，两个孩子为空或是黑色
                1.将 "兄弟结点" 改为红色；
                2.将 "父亲结点" 设置为新的 "当前结点"，继续进行操作。
                 */
                    sib.Color = RED;
                    x = x.parent;
                }else{
                    if(colorOf(sib.left) == BLACK){
                    /*
                    case3:当前结点是黑色，兄弟结点是黑色，兄弟结点的右孩子是红色，左孩子是为空或是黑色
                    1.将 "兄弟结点" 的右孩子改为黑色；
                    2.将 "兄弟结点" 改为红色；
                    3.以 "兄弟结点" 为支点进行左旋；
                    4.左旋后，重新设置 "当前结点" 的 "兄弟结点"。
                     */
                        sib.right.Color = BLACK;
                        sib.Color = RED;
                        rotateLeft(sib);
                        sib = x.parent.left;
                    }

                /*
                case4:当前结点是黑色，兄弟结点是黑色，兄弟结点的左孩子是红色，右孩子为空或红黑皆可
                1.将 "父亲结点" 的颜色赋给 "兄弟结点"；
                2.将 "父亲结点" 改为黑色；
                3.将 "兄弟结点" 的左孩子改为黑色；
                4.以 "父亲结点" 为支点进行右旋；
                 */
                    sib.Color = x.parent.Color;
                    x.parent.Color = BLACK;
                    sib.left.Color = BLACK;
                    rotateRight(x.parent);
                    x = root;
                }
            }
        }
        x.Color = BLACK;
    }

    private boolean colorOf(Entry sib) {
        return sib == null ? BLACK : sib.Color;
    }

    /**
     * 寻找后继节点，两种方式，一种是右子树的最左孩子。
     * 如果没有右子树，就先找到这个节点所在的左子树，
     * 那么这个节点所在的左子树的父节点就是后继节点
     * @param p
     * @return
     */
    private Entry successor(Entry p) {
        if(p == null){
            return null;
        }else if(p.right != null){
            Entry r = p.right;
            while (r.left != null)
                r = r.left;
            return r;
        }else{
            Entry r = p.parent;
            Entry ch = p;
            while(r != null && ch == r.right){
                ch = r;
                r = r.parent;
            }
            return r;
        }
    }


    private static final boolean RED   = false;
    private static final boolean BLACK = true;

    class Entry<K,V>{
        K key;
        V value;
        Entry<K,V> parent;
        Entry<K,V> left;
        Entry<K,V> right;
        boolean Color = BLACK;
    }
}
