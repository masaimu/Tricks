package test;

import java.util.Arrays;
import java.util.HashMap;

public class ConcurrentHashMapTest {
    public static void main(String[] args) {
        ConcurrentHashMapTest t = new ConcurrentHashMapTest();
        String res = "  the sky is    blue   ";
        StringBuffer sb = new StringBuffer();
        String[] array = res.split(" ");
        System.out.println("[" + array[0] + "]");
    }

    /*
     * @param s: A string
     * @return: A string
     */
    public String reverseWords(String s) {
        char[] t = new char[s.length()];
        int j = 0;
        for(int i=0;i<s.length();){

            if(s.charAt(i) == ' '){
                if(t[j] == ' '){
                    i++;
                    continue;
                }else{
                    t[j]=' ';
                }
            }else{
                int iend = getWordEnd(s,i);
                if(t[j] == ' '){
                    j++;
                }
                int jend = setWord(t,j,s,i,iend);
                j = jend + 1;
                i = iend + 1;
            }
        }
        int start =0;
        int stop = j;
        if(t[0] == ' ')
            start = 1;
        if(j>=t.length || t[j] == ' ')
            stop = j-1;

        if(start<=stop){
            char[] r = new char[stop - start + 1];
            System.arraycopy(t, start,r,0,r.length);
            return new String(r);
        }else{
            return "";
        }
    }

    private int getWordEnd(String s, int i){
        int iend = i;
        do{
            iend++;
        }while(iend<s.length() && s.charAt(iend) != ' ');
        return iend-1;
    }

    private int setWord(char[] t, int j, String s, int i, int ie){
        int jstart = j;
        int istart = i;
        int iend = ie;
        while(istart <= iend){
            t[j++] = s.charAt(iend--);
        }
        return j-1;
    }
}
