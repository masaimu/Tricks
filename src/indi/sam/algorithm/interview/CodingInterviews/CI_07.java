package indi.sam.algorithm.interview.CodingInterviews;

/**
 * 根据前序遍历和中序遍历的结果生成后序遍历
 */
public class CI_07 {

    class BinaryTreeNode{
        char value;
        BinaryTreeNode left;
        BinaryTreeNode right;

        public String toString(){
            StringBuffer sb = new StringBuffer();
            getEndOrder(this,sb);
            return sb.toString();
        }

        private void getEndOrder(BinaryTreeNode node, StringBuffer sb) {

            if(node == null)
                return;
            getEndOrder(node.left, sb);
            getEndOrder(node.right, sb);
            sb.append(node.value);

        }
    }

    public String createEndOrder(String preOrder, String inOrder) throws Exception {
        if(preOrder == null || inOrder == null){
            return null;
        }
        if(preOrder.equals("") || inOrder.equals("") || preOrder.length() != inOrder.length()){
            return "";
        }

        char[] preArr = preOrder.toCharArray();
        char[] inArr = inOrder.toCharArray();
        char[] endArr = new char[preArr.length];
        BinaryTreeNode tree = createTree(preArr, inArr, 0, 0, inArr.length-1);
        System.out.println(tree);
        StringBuffer endStr = new StringBuffer();
        getEndOrder(tree, endArr, 0);

        return String.valueOf(endArr);
    }

    private int getEndOrder(BinaryTreeNode tree, char[] endArr, int i) {
        if(tree == null)
            return i;

        int index = getEndOrder(tree.left, endArr, i);
        index = getEndOrder(tree.right, endArr, index);

        endArr[index] = tree.value;
        return index+1;
    }

    private BinaryTreeNode createTree(char[] preArr, char[] inArr, int index, int low, int high) throws Exception {
        if(low > high)
            return null;
        BinaryTreeNode node = new BinaryTreeNode();
        node.value = preArr[index];
        int p_InArr = pOfInArr(preArr[index], inArr, low, high);
        if(p_InArr < 0){
            throw new Exception("error!");
        }
        node.left = createTree(preArr, inArr, index+1, low, p_InArr-1);
        node.right = createTree(preArr, inArr, index+1+p_InArr-low, p_InArr+1, high);

        return node;
    }

    private int pOfInArr(char c, char[] inArr, int low, int high) {
        int index = -1;
        for(int i = low; i <= high; i++){
            if(c == inArr[i]){
                index = i;
                break;
            }
        }
        return index;
    }

    public static void main(String[] args) {
        CI_07 obj = new CI_07();
        try {
            String result = obj.createEndOrder("2", "1");
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
