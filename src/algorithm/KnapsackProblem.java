package algorithm;

import javafx.scene.shape.VLineTo;

import java.util.Arrays;

/**
 * 背包九讲的实现代码
 * 题目:有 N 件物品和一个容量为 V 的背包。第 i 件物品的费用是c[i]，价值是w[i]。
 * 求解将哪些物品装入背包可使价值总和最大。
 * Created by MaSaimu on 2018/2/11.
 */
public class KnapsackProblem {

    /**
     * 最基础的背包问题
     * 题目:有 N 件物品和一个容量为 V 的背包。第 i 件物品的费用是c[i]，价值是w[i]。
     * 求解将哪些物品装入背包可使价值总和最大。
     * @param c
     * @param w
     * @param N
     * @param V
     */
    public static int[] Part01_02(int[] c, int[] w, int N, int V){
        int tempN = N;
        int tempV = V;
        int[] costs = c;
        int[] weights = w;
        int[] f = new int[tempV+1]; //前i件物品装满v空间所能拥有的最大价值。

        /*
        第一种初始化：如果是要求N件物品尽量装满容量为V的背包，则按照如下方法进行初始化，
                      它表示前0件物品装满v空间所能拥有的最大价值为0，这虽然是废话，
                      但是也是满足题设条件的。
        for(int v=0; v<=tempV; v++){
            f[v] = 0;
        }
        第二种初始化：如果是要求N件物品恰好装满容量为V的背包，则除了f[0]初始化为0之外，
                      其他f[v]都初始化为无穷小，因为前0件物品除了能填满空间为0的包外，其他
                      空间量的包都无法恰好填满。
        f[0] = 0;
        for(int v=1; v<=tempV; v++){
            f[v] = Integer.MIN_VALUE;;
        }
         */
        for(int v=0; v<=tempV; v++){
            f[v] = 0;
        }

        for(int i=1; i<=tempN; i++){
//            ZeroOnePack(f, costs[i], weights[i], tempV);
            CompletePack(f, costs[i], weights[i], tempV);
        }
        return f;
    }

    /**
     * 如果 c[i] 物品的数量没有限制，那么这件物品入选的数量就可以是 0···v/c[i] 件，
     * f[i] 的迁移公式就应该是：f[i][v] = max{f[i-1][v], f[i][v - k*c[i]] + k*w[i]}, 1<=k<=v/c[i]，
     * 在算法中，当我们从cost到V去更新f[v]的时候，就好比是随着背包空间不断变大，我们不断尝试将大小为cost的物品放入背包，
     * 如果放进去更划算，我们就修改f[v]的值，表示在背包空间为v时，我们放进去了一个物品i，然后继续增大背包空间，尝试放入物品i；
     * 如果放进去不划算，我们就比较已经放进去若干个i的价值和一个都不放的价值，取价高者。
     * f[v]的原有值就是f[i-1][v]，而f[v - cost]就是刚才已经更新过的、已经放进去若干个i之后的最大价值。
     * @param f
     * @param cost
     * @param weight
     * @param V
     */
    private static void CompletePack(int[] f, int cost, int weight, int V){
        System.out.println(Arrays.toString(f));
        for(int v = cost; v<=V; v++){
            f[v] = f[v] > (f[v - cost] + weight) ? f[v] : (f[v - cost] + weight);
        }
    }

    /**
     * 如果c[i] 物品的数量有限制：0或者1个，那么f[i]的迁移公式就应该是：
     * f[i][v] = max{f[i-1][v], f[i-1][v - c[i]] + w[i]},
     * 在算法中，当我们从V到cost去更新f[v]的时候，就好比是随着背包空间不断缩小，我们不断尝试将大小为cost的物品放入背包，
     * 如果放进去更划算，我们就修改f[v]的值，但是这个修改只会对下一次循环，也就是计算前i+1个物品时产品影响，对于此时处理
     * 的前i个物品无影响。相当于要么放进去1个，要么一个都不放。
     * @param f
     * @param cost
     * @param weight
     * @param V
     */
    private static void ZeroOnePack(int[] f, int cost, int weight, int V) {
        System.out.println(Arrays.toString(f));
        for(int v = V; v>=cost; v--){
            f[v] = f[v] > (f[v - cost] + weight) ? f[v] : (f[v - cost] + weight);
        }
    }

    public static void main(String[] args) {
        int[] c = {0, 2, 3, 5, 7, 11, 13};
        int[] w = {0, 2, 3, 5, 7, 11, 13};
        int N = 5;
        int V = 40;

        int[] f = Part01_02(c, w, N, V);
        System.out.println(Arrays.toString(f));
    }
}
