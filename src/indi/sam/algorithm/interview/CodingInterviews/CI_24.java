package indi.sam.algorithm.interview.CodingInterviews;

/**
 * 翻转链表，返回链表的头节点
 */
public class CI_24 {

    class ListNode{
        int value;
        ListNode next;
    }

    public ListNode reverseList(ListNode head){
        if(head == null){
            return null;
        }
        ListNode current = head;
        ListNode pre = null;
        ListNode next = null;
        while(current.next != null){
            next = current.next;
            current.next = pre;
            pre = current;
            current = next;
        }
        current.next = pre;
        return current;
    }

    public static void main(String[] args) {
        CI_24 obj = new CI_24();
        ListNode head = obj.init(-1);
        print(head);
        head = obj.reverseList(head);
        print(head);
    }

    private static void print(ListNode head) {
        ListNode p = head;
        while(p != null){
            System.out.print(p.value);
            p = p.next;
        }
        System.out.println();
    }

    private ListNode init(int length) {
        ListNode head = new ListNode();
        head.value = 0;
        ListNode p = head;
        for(int i = 1; i<length; i++){
            ListNode node = new ListNode();
            node.value = i;
            p.next = node;
            p = p.next;
        }
        return head;
    }
}
