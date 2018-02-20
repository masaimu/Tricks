package indi.sam.algorithm.interview;

/**
 * 题目：给定数量不限的硬币，币值为25分、10分、5分和1分，编写代码计算n分有几种表达方法。
 * 解题思路：这道题目和上台阶不同，这是组合不是排列。对于n分的表达方式可以用25分硬币使用的数量做分类：
 * 0个、1个、···、n/25个，可以用函数写成：
 * count(n) = count(n, 0个25)
 *          + count(n-25, 0个25)
 *          + count(n-50, 0个25)
 *          + ···
 *          + count(n-(k*25), 0个25) （其中k = n/25）
 * 其中每一个count(x, 0个25)又可以分为x/10个count(x-(k*10), 0个10) （其中k = x/10）
 * 这种拆分的最后形态是count(x, 0个5)=1，意思是只用1分表示只能有一种表达方式。
 */
public class Coins {

    private static int getCombCount(int n, int coin){
        int nextCoin = 25;
        switch (coin){
            case 25:
                nextCoin = 10;
                break;
            case 10:
                nextCoin = 5;
                break;
            case 5:
                nextCoin = 1;
                break;
            case 1:
                return 1;
        }
        int ways = 0;
        for(int k = 0; k <= n/coin; k++){
            ways += getCombCount(n - k * coin, nextCoin);
        }
        return ways;
    }

    public static void main(String[] args) {
        int ways = getCombCount(755, 25);
        System.out.println(ways);
    }
}
