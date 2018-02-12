package indi.sam.algorithm.interview;

/**
 * 题目：在数组A[0...n-1]中，有所谓的魔术索引，满足条件A[i]=i。给定一个有序整数数组，元素值各不相同，编写一个方法，
 * 在数组A中找出一个魔术索引，若存在的话。
 * Created by MaSaimu on 2018/2/12.
 */
public class MagicIndex {

    /**
     * 元素值各不相同，且整个数组有序，所以对于A[5]=3 的这种情况，魔术索引肯定不存在于5的左边，
     * 可以想象成5左边的空间都已经被挤压了，所以不可能有A[i]=i 的情况出现了。我们只需在5的右侧继续寻找即可。
     * 这就相当于二分法查找了。
     * @param A
     * @return
     */
    public static int findMagic(int[] A){
//        return doFind(A,0, A.length-1);
        return doFindRepeat(A,0, A.length-1);
    }

    /**
     * 这种情况是数字不会有重复。所以可以采用二分法去查找。
     * @param A
     * @param low
     * @param high
     * @return
     */
    private static int doFind(int[] A, int low, int high) {
        while(low <= high){
            int mid = (low + high)/2;
            int value = A[mid];
            if(value == mid)
                return mid;
            else if(value < mid)
                low = mid + 1;
            else if(value > mid)
                high = mid - 1;
        }
        return -1;
    }

    /**
     * 如果数组中有重复的元素，就不能用二分法查找了，因为无法通过比较A[i]和i 的大小来直接判断出魔术索引在左边还是在右边。
     * 但是A[mid] = midValue, 如果midValue<mid，那么（midValue, mid]里面肯定不包含魔术索引，此时将high改成min(midValue,mid-1);
     * 如果midValue>mid，那么[mid, midValue)里面肯定不包含魔术索引，此时将low改成max(midValue,mid+1);
     * @param A
     * @param low
     * @param high
     * @return 索引
     */
    private static int doFindRepeat(int[] A, int low, int high) {
        if(low > high || low<0 || high>A.length-1){
            return -1;
        }

        int mid = (low + high)/2;
        int midValue = A[mid];

        if(midValue == mid){
            return mid;
        }

        int leftIndex = Math.min(mid-1, midValue);
        int leftResult = doFindRepeat(A, low, leftIndex);
        if(leftResult >= 0)
            return leftResult;

        int rightIndex = Math.max(mid+1, midValue);
        int rightResult = doFindRepeat(A, rightIndex, high);
        if(rightResult >= 0)
            return rightResult;
        return -2;
    }

    public static void main(String[] args) {
        int[] A = {6,6,6,6,6,6,6,6,6,6,6,6,6};
        int r = findMagic(A);
        System.out.println(r);
    }
}
