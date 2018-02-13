package indi.sam.algorithm.interview;

import java.util.ArrayList;

/**
 * 题目：实现一种算法，打印n对括号的全部有效组合。
 * 这道题的关键是如果表达出“有效组合”，其实很简单：构造括号组合的字符串时，只要剩余的右括号多于左括号，
 * 就是一种合法的试探。如果尚未使用的右括号少于左括号了，最终所组成的括号组合就是非法的。
 * Created by MaSaimu on 2018/2/13.
 */
public class LegalRemCombination {

    public static void findAllComb(int remNum){
        char[] remArr = new char[remNum * 2];
        ArrayList<String> list = new ArrayList<String>();
        insertRem(list, remNum, remNum, remArr);

        System.out.println(list);
    }

    private static void insertRem(ArrayList<String> list, int leftRem, int rightRem, char[] remArr){
        if(leftRem < 0 || rightRem < 0)
            return;
        if(leftRem == 0 && rightRem == 0){
            list.add(String.copyValueOf(remArr));
            return;
        }

        int index = remArr.length - leftRem - rightRem;

        if(leftRem > 0){
            remArr[index] = '(';
            insertRem(list, leftRem-1, rightRem, remArr);
        }

        if(leftRem < rightRem){
            remArr[index] = ')';
            insertRem(list, leftRem, rightRem-1, remArr);
        }
    }

    public static void main(String[] args) {
        findAllComb(0);
    }
}
