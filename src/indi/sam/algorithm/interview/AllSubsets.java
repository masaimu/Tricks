package indi.sam.algorithm.interview;

import java.util.ArrayList;

/**
 * 题目：编写一个方法，返回某集合的所有子集。
 * Created by MaSaimu on 2018/2/12.
 */
public class AllSubsets {
    /**
     * 这道题采用动态规划的方式，时间复杂度是O(2^n)，因为每个元素都有“存在于子集”和“不存在与子集”两种选择，所以n
     * 个元素就有2^n种可能。
     * 动态规划的递推方程是：S(n) = S(n-1) + (S(n-1) <- an)
     * 包含n个元素的所有子集就把(n-1)的子集复制一份，然后将an插入到每一个子集中所得到的子集集合。
     * @param S
     * @return
     */
    public static ArrayList<ArrayList<String>> getAllSubsets(ArrayList<String> S){
        ArrayList<ArrayList<String>> subsets = new ArrayList<ArrayList<String>>();//子集全集
        subsets.add(new ArrayList<>());//初始化，S(0)
        for(int i=0; i<S.size(); i++){
            subsets = insert(subsets, S, i);
        }
        return subsets;

    }

    /**
     * 该方法用来实现将S[index]元素纳入到subsets子集集合中，如果原有的子集集合是{{},{a},{b},{a,b}}，且S[index]=c，
     * 则返回的子集集合是{{},{a},{b},{a,b},{c},{a,c},{b,c},{a,b,c}}
     * 实现的过程是将子集集合复制，然后将S[index]元素逐一插入到各复制子集中。
     * @param subsets
     * @param S
     * @param index
     * @return
     */
    private static ArrayList<ArrayList<String>> insert(ArrayList<ArrayList<String>> subsets,
                                                       ArrayList<String> S,
                                                       int index){
        String a = S.get(index);
        ArrayList<ArrayList<String>> cloneSubsets = new ArrayList<ArrayList<String>>();

        for(int i=0; i<subsets.size(); i++){
            ArrayList<String> sub = subsets.get(i);
            ArrayList<String> clonesub = new ArrayList<String>(sub);
            clonesub.add(a);
            cloneSubsets.add(sub);
            cloneSubsets.add(clonesub);
        }
        return cloneSubsets;
    }

    public static void main(String[] args) {
        ArrayList<String> S = new ArrayList<String>();
        S.add("a");
        S.add("b");
        S.add("c");
//        S.add("d");
        ArrayList<ArrayList<String>> subsets = getAllSubsets(S);
        System.out.println(subsets);
    }
}
