package indi.sam.algorithm.interview;

import java.util.Arrays;

/**
 * 题目：八皇后问题。
 * Created by MaSaimu on 2018/2/13.
 */
public class EightQueen {
    private static final int LEN = 8;
    private static int count = 0;

    /**
     * 这个方法的思路是对的，每往下走一行就将逐一试验每一个位置能否放置棋子，
     * 但是采用二维数组存在两个问题：
     * 1、占用的内存空间比较大；
     * 2、当row+1行的尝试不成功时，要回退到row行将棋子放到col+1的位置去尝试，此时要重新标记row+1行的位置，比较麻烦。
     * @param F
     * @param row
     */
    private static void setNextQueen(int[][] F, int row){
        if(row == LEN){
            printQueen(F);
            count++;
        }else{
            for(int col = 0; col < LEN; col++){
                check(F, row);
                if((F[row][col] & 7) ==0){
                    F[row][col] = 8;
                    setNextQueen(F, row+1);
                    F[row][col] = 0;
                }
            }
        }

    }

    private static void check(int[][] F, int row) {
        if(row > 0){
            for(int col = 0; col < LEN; col++){F[row][col] = 0;}
            for(int col = 0; col < LEN; col++){
                if((F[row-1][col] & 8) != 0){
                    F[row][col] |= 1;
                    if(col > 0)
                        F[row][col-1] |= 2;
                    if(col < LEN-1)
                        F[row][col+1] |= 4;
                }else{
                    if((F[row-1][col] & 1) != 0){
                        F[row][col] |= 1;
                    }
                    if((F[row-1][col] & 2) != 0 && col > 0){
                        F[row][col-1] |= 2;
                    }
                    if((F[row-1][col] & 4) != 0 && col < LEN-1){
                        F[row][col+1] |= 4;
                    }
                }
            }
        }

    }

    /**
     * 既然二维数组太占地方，就用一维数组存储，columns[row]表示第row行的棋子放的列位置。
     * @param columns
     * @param row
     */
    private static void setNextQueen2(int[] columns, int row){
        if(row == LEN){
            System.out.println(Arrays.toString(columns));
        }else {
            for(int col = 0; col < LEN; col++){
                if(check2(columns,row,col)){
                    columns[row] = col;
                    setNextQueen2(columns, row+1);
                }
            }
        }
    }

    /**
     * 检查row,col这个位置是否能落子，对于对角线的检查采用（Math.abs(col - col2) == (row - row2)）即可。
     * @param columns
     * @param row
     * @param col
     * @return
     */
    private static boolean check2(int[] columns, int row, int col) {
        for(int row2 = 0; row2 < row; row2++){
            int col2 = columns[row2];
            if(col2 == col)
                return false;
            int s = Math.abs(col - col2);
            if(s == (row - row2)){
                return false;
            }
        }
        return true;
    }

    private static void printQueen(int[][] f) {
        int[][] p = new int[LEN][LEN];
        for(int i=0; i<LEN; i++){
            for(int j=0; j<LEN; j++){
                if(f[i][j] == 8)
                    p[i][j] = 1;
                System.out.print(p[i][j] + "  ");
            }
            System.out.println("");
        }
        System.out.println("******************");
    }

    public static void main(String[] args) {
//        int[][] F = new int[LEN][LEN];
//        setNextQueen(F, 0);
        int[] columns = new int[LEN];
        setNextQueen2(columns, 0);
        System.out.println(count);
    }
}
