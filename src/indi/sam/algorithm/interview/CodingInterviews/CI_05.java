package indi.sam.algorithm.interview.CodingInterviews;

/**
 * 把字符串中的空格替换成"%20"
 */
public class CI_05 {

    public String replacePlane(String str){
        char[] arr = str.toCharArray();
        int countOfPlane = countOf(arr);
        int newLength = arr.length + countOfPlane * 2;
        char[] newArr = new char[newLength];

        int p = newArr.length - 1;
        for(int i = arr.length-1; i >= 0; i--){
            if(arr[i] == ' '){
                newArr[p] = '0';
                newArr[p - 1] = '2';
                newArr[p - 2] = '%';
                p -= 3;
            }else{
             newArr[p] = arr[i];
             p--;
            }
        }
        return  String.valueOf(newArr);
    }

    private int countOf(final char[] arr) {
        int count = 0;
        for(int i = 0; i < arr.length; i++){
            if(arr[i] == ' ')
                count ++;
        }
        return count;
    }

    public static void main(String[] args) {
        CI_05 obj = new CI_05();
        System.out.println(obj.replacePlane("ab c d   "));
    }


}
