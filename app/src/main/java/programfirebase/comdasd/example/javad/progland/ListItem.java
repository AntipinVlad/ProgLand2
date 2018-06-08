package programfirebase.comdasd.example.javad.progland;

/**
 * Created by javad on 28.02.2018.
 */

public class ListItem {
    private String head;
    private String desc;
    private String information;


    public ListItem(String head, String desc, String information) {
        this.head = head;
        this.desc = desc;
        this.information = information;

    }

    public String getHead() {return head;}

    public String getDesc() { return desc; }

    public String getInformation() { return information; }
}
