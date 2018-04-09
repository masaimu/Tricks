package indi.sam.algorithm.interview.CodingInterviews;

/**
 * 剪绳子
 * 方案一：动态规划 f(n) = max(f(i) * f(n-i))
 * 方案二：贪婪 尽量减3，最后4减2
 */
public class CI_14 {

    public int max(int length){
        if(length < 2)
            return 0;
        if(length == 2)
            return 1;
        if(length == 3)
            return 2;

        int[] f = new int[length + 1];
        f[0] = 0;
        f[1] = 1;
        f[2] = 2;
        f[3] = 3;

        for(int i = 4; i <= length; i++){
            int max = 0;
            for(int j=1; j <= i/2; j++){
                int temp = f[j] * f[i-j];
                if(temp > max)
                    max = temp;
            }
            f[i] = max;
        }
        return f[length];
    }

    public static void main(String[] args) {
        CI_14 obj = new CI_14();
        int max = obj.max(100);
        System.out.println(max);
    }
}
