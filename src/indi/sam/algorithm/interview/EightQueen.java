package indi.sam.algorithm.interview;

/**
 * 题目：八皇后问题。
 * Created by MaSaimu on 2018/2/13.
 */
public class EightQueen {
    private static final int LEN = 7;

    private static void setNextQueen(int[][] F, int row, int lev){
        if(row > LEN || lev > LEN){
            return;
        }
        if(row == LEN && lev == LEN){
            printQueen(F);
            return;
        }else{
            if((F[row][lev] & 7) ==0){
                F[row][lev] = -1;
                if(row < LEN){
                    F[row+1][lev] += 1;
                    if(lev > 0)
                        F[row+1][lev-1] += 2;
                    if(lev < LEN)
                        F[row+1][lev+1] += 4;
                }
                setNextQueen(F, row+1, 0);
            }else if(row < LEN){
                if((F[row][lev] & 1) != 0){
                    F[row+1][lev] += 1;
                }
                if((F[row][lev] & 2) != 0 && lev > 0){
                    F[row+1][lev-1] += 2;
                }
                if((F[row][lev] & 4) != 0 && lev < LEN){
                    F[row+1][lev+1] += 4;
                }
            }
            setNextQueen(F, row, lev+1);
        }

    }

    private static void printQueen(int[][] f) {
        int[][] p = new int[LEN+1][LEN+1];
        for(int i=0; i<=LEN; i++){
            for(int j=0; j<=LEN; j++){

                if(f[j][i] == -1)
                    p[j][i] = 1;
                System.out.print(p[i][j] + "  ");
            }
            System.out.println("");
        }
        System.out.println("******************");
    }

    public static void main(String[] args) {
        int[][] F = new int[LEN+1][LEN+1];
        setNextQueen(F, 0, 0);
    }
}
