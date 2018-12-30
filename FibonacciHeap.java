/******************************************
 *                                        *
 *  Heap Structure                        *
 *  ***********************************   *
 *  *        Node1                    *   *
 *  *          |                      *   *
 *  *        Node2 - Node3 - Node4    *   *
 *  *          |______________| |     *   *
 *  *                           |     *   *
 *  *                           Node5 *   *
 *  ***********************************   *
 *                                        *
 *  Heap Operations                       *
 *  ***********************************   *
 *  * Insert                          *   *
 *  * Remove Max                      *   *
 *  * Cut                             *   *
 *  * Cascading Cut                   *   *
 *  * Combine nodes of same degree    *   *
 *  * Increase key                    *   *
 *  ***********************************   *
 *                                        *
 ******************************************/
import java.util.*;

public class FibonacciHeap {

    private Node rootNode;

    /**************************************************************
     *                                                            *
     * @param newNode                                             *
     * Perform insert new node into the heap.                     *
     * The new node is inserted to the right of the root node.    *
     *                                                            *
     **************************************************************/

    public void insert(Node newNode){
        if(rootNode == null){
            rootNode = newNode;
        }else{
            //Node is inserted to the right of the root node
            newNode.rightSibling = rootNode.rightSibling;
            newNode.rightSibling.leftSibling = newNode;
            rootNode.rightSibling = newNode;
            newNode.leftSibling = rootNode;

            newNode.childCut = false;
            newNode.parent = null;

            //If key of newly inserted node is greater than key of rootnode, update rootnode
            if(newNode.searchKeywordCount > rootNode.searchKeywordCount)
                rootNode = newNode;
        }
    }

    /**************************************************************
     *                                                            *
     * @param node                                                *
     * Perform cascading cut operation.                           *
     * If the childcut for the given node is true, the subtree is *
     * removed from the heap and inserted at root level.          *
     * Recursivley performed until node with cascading cut as     *
     * false is obtained.                                         *
     *                                                            *
     *************************************************************/

    public void cascadingCut(Node node){
        Node parentNode = node.parent;

        if(parentNode!=null) {
            if (node.childCut) { //check the childcut value of the node
                nodeCut(node); //remove the subtree and insert at root level
                cascadingCut(parentNode); //perform cascading cut for the parent
            } else {
                node.childCut = true; //set childcut to true
            }
        }
    }

    /***************************************************************
     *                                                             *
     * @param node                                                 *
     * Perform removal of a subtree of given node from the heap    *
     * and insert at root level.                                   *
     * Degree of the parent is updated when the child subtree is   *
     * is removed.                                                 *
     * This is called when key of child becomes greater than key   *
     * of the parent.                                              *
     * This is called when performing cascading cut.               *
     *                                                             *
     ***************************************************************/

    public void nodeCut(Node node){
        Node parentNode = node.parent;

        parentNode.degree -= 1;
        if(parentNode.degree == 0){
            parentNode.child = null;
        }
        if(parentNode.child == node){
            parentNode.child = node.rightSibling;
        }

        node.leftSibling.rightSibling = node.rightSibling;
        node.rightSibling.leftSibling = node.leftSibling;
        insert(node);
    }

    /****************************************************************
     *                                                              *
     * @param node                                                  *
     * @param count                                                 *
     * Perform increasing the key of given node by given count.     *
     * The given count value is added to the key of the node.       *
     * If the new key is greater than key of parent, the subtree is *
     * removed from the heap and inserted at root level.            *
     * Cascading cut is invoked                                     *
     *                                                              *
     ****************************************************************/

    public void increaseKeywordCount(Node node, Integer count){

        int curCount = node.searchKeywordCount; //get the current count
        int newCount = curCount + count; //increase the count
        node.searchKeywordCount = newCount; //update new count

        Node parentNode = node.parent;

        if(parentNode != null){
            if(parentNode.searchKeywordCount < node.searchKeywordCount){ //if new count is greater than parent count, remove subtree and insert at root level
                nodeCut(node);
                cascadingCut(parentNode);
            }
        }else {
            if(node.searchKeywordCount > rootNode.searchKeywordCount)
                rootNode = node;
        }
    }

    /****************************************************************
     *                                                              *
     * @return node                                                 *
     * Perform removal of the node with maximum key value from the  *
     * heap and is returned to invoking function.                   *
     * Pairwise combine of nodes at root level based on the degree  *
     * is invoked.                                                  *
     * Max pointer is updated.                                      *
     *                                                              *
     ****************************************************************/

    public Node removeMax(){
        Node root = rootNode;

        if(root != null){
            cutChildren(root);//remove root's children and insert at root level

            //remove root(max) node from the heap
            root.leftSibling.rightSibling = root.rightSibling;
            root.rightSibling.leftSibling = root.leftSibling;

            if(root.rightSibling != root){
                rootNode = root.rightSibling;
                combineOnDegree();//perform meld based on degree of subtrees
                Node tempRoot = rootNode;
                Node tempCur = tempRoot.rightSibling;
                while(tempCur != tempRoot){ //update root node pointer
                    if(tempCur.searchKeywordCount > rootNode.searchKeywordCount)
                        rootNode = tempCur;
                    tempCur = tempCur.rightSibling;
                }
            } else
                rootNode = null;
        }
        return root;
    }

    /****************************************************************
     *                                                              *
     * Perform pairwise combine of the nodes with equal degrees.    *
     * This is recursively called until there are no nodes at root  *
     * level of the heap with equal degree.                         *
     *                                                              *
     ****************************************************************/

    public void combineOnDegree(){
        boolean combined = false;
        HashMap<Integer, Node> degreeNodeMap = new HashMap<Integer, Node>();
        Node temp = rootNode;
        HashMap<Integer, Node> mergedDegreeNodeMap = new HashMap<Integer, Node>();
        int upperNodes = getNumberOfUpperNodes();
        while(upperNodes > 0){

            int degree = temp.degree;
            if(degreeNodeMap.containsKey(degree)){
                temp = temp.rightSibling;
                Node mergedTree = mergeTrees(temp.leftSibling, (Node)degreeNodeMap.get(degree));
                mergedDegreeNodeMap.put(mergedTree.degree, mergedTree);
                degreeNodeMap.remove(degree);
                combined = true;
            }else{
                degreeNodeMap.put(degree, temp);
                temp = temp.rightSibling;
            }

            upperNodes--;
        }
        if(combined){
            combineOnDegree();
        }
    }

    /*****************************************************************
     *                                                               *
     * @return numberOfNodes                                         *
     * Get the number of nodes present at the root level of the heap.*
     * Return this count                                             *
     *                                                               *
     *****************************************************************/

    public int getNumberOfUpperNodes(){
        int count = 0;

        if(rootNode != null){

            count++;
            Node tempRoot = rootNode;
            Node tempCur = tempRoot.rightSibling;
            while(tempCur != tempRoot){
                count++;
                tempCur = tempCur.rightSibling;
            }

        }
        return count;
    }

    /*****************************************************************
     *                                                               *
     * @param node                                                   *
     * Perform removal of all children(with their subtrees) of given *
     * node from the heap and these subtrees are inserted back into  *
     * the heap at the root level.                                   *
     * This is invoked from the removeMax() when the root node is    *
     * removed from the heap.                                        *
     *                                                               *
     *****************************************************************/

    public void cutChildren(Node node){
        Node child = node.child;
        Node temp;
        int numChildren = node.degree;

        while(numChildren>0){ //for every child in the node

            temp = child.rightSibling;

            //remove child subtree
            child.leftSibling.rightSibling = temp;
            temp.leftSibling = child.leftSibling;

            insert(child); //insert removed child at root level of the heap

            child = temp;
            numChildren--;
        }
    }

    /****************************************************************
     *                                                              *
     * @param node1                                                 *
     * @param node2                                                 *
     * @return rootNode(of new subtree)                             *
     * Perform meld of 2 subtrees.                                  *
     * The key values of the given nodes are compared and node with *
     * smaller key value is made child of the node with larger key  *
     * value.                                                       *
     * Pointer to the node with larger key value is returned.       *
     *                                                              *
     ****************************************************************/

    public Node mergeTrees(Node node1, Node node2){
        Node largeNode;
        Node smallNode;

        //identify subtree with larger key value
        if(node1.searchKeywordCount >= node2.searchKeywordCount){
            largeNode = node1;
            smallNode = node2;
        }else{
            largeNode = node2;
            smallNode = node1;
        }

        //remove the smaller subtree from the root level of the heap
        smallNode.leftSibling.rightSibling = smallNode.rightSibling;
        smallNode.rightSibling.leftSibling = smallNode.leftSibling;

        smallNode.parent = largeNode; //make larger node as parent of smaller subtree

        if(largeNode.child == null){
            largeNode.child = smallNode;
            smallNode.leftSibling = smallNode;
            smallNode.rightSibling = smallNode;
        } else {
            smallNode.leftSibling = largeNode.child;
            smallNode.rightSibling = largeNode.child.rightSibling;
            largeNode.child.rightSibling = smallNode;
            smallNode.rightSibling.leftSibling = smallNode;
        }

        largeNode.degree++;

        largeNode.childCut = false;

        if(rootNode == smallNode)
            rootNode = largeNode;

        return largeNode;
    }
}
