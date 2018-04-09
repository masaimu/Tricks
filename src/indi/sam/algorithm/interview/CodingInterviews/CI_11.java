package indi.sam.algorithm.interview.CodingInterviews;

/**
 * 旋转数组的最小数字
 * 要点1：low永远指向前面的子数组，high永远指向后面的子数组，二分的时候low = mid 或 high = mid,而不是mid-1、mid+1
 * 要点2：旋转数组可能只是旋转了前0个元素，此时最小数字还是第一个
 * 要点3：arr[mid] == arr[low] == arr[high]时，无法确定最小数字在左边还是右边，顺序查找
 */
public class CI_11 {
    public int min(int[] arr){
        return searchMin(arr, 0, arr.length-1);
    }

    private int searchMin(int[] arr, int low, int high) {
        int mid = low;
        while(arr[low] >= arr[high]){
            if(high - low == 1){
                mid = high;
                break;
            }
            mid = (low + high) / 2;
            if(arr[mid] == arr[low] && arr[mid]== arr[high]){
                return searchMinInOrder(arr, low, high);
            }
            if(arr[mid] >= arr[low])
                low = mid;
            else if(arr[mid] <= arr[high])
                high = mid;
        }
        return arr[mid];
    }

    private int searchMinInOrder(int[] arr, int low, int high) {
        int min = arr[low];
        for(int i = low; i <= high; i++){
            if(arr[i] < min){
                min = arr[i];
            }
        }
        return min;
    }

    public static void main(String[] args) {
        CI_11 obj = new CI_11();
        int[] arr = {4,5,1,2,3,4};
        int min = obj.min(arr);
        System.out.println(min);
    }
}
