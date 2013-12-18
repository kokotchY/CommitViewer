package be.rvponp.build.model;

/**
 * User: vermb
 * Date: 7/30/13
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
public enum JiraStatus {
    Undefined_A(0), Open(1), Undefined_C(2), InProgress(3), Reopened(4), Resolved(5), Closed(6);

    private int id;

    private JiraStatus(int i){
        this.id = i;
    }
}
