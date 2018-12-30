/************************************
 *    Node Structure                *
 *   ***************************    *
 *   *   --------------------  *    *
 *   *  |       Degree       | *    *
 *   *   --------------------  *    *
 *   *  |       Child        | *    *
 *   *   --------------------  *    *
 *   *  |       Parent       | *    *
 *   *   --------------------  *    *
 *   *  |     Left Sibling   | *    *
 *   *   --------------------  *    *
 *   *  |    Right Sibling   | *    *
 *   *   --------------------  *    *
 *   *  |     Child Cut      | *    *
 *   *   --------------------  *    *
 *   *  | Data (word, count) | *    *
 *   *   --------------------  *    *
 *   ***************************    *
 *                                  *
 ************************************/

public class Node {
    int degree;
    Node child;
    Node parent;
    Node leftSibling, rightSibling;
    boolean childCut;
    String searchKeyword;
    int searchKeywordCount;

    Node(String key, int value){
        this.degree = 0;
        this.child = null;
        this.parent = null;
        this.leftSibling = this;
        this.rightSibling = this;
        this.childCut = false;
        this.searchKeyword = key;
        this.searchKeywordCount = value;
    }
}
