package nz.co.scuff.android.event;

import java.util.List;

/**
 * Created by Callum on 30/04/2015.
 */
public class SelectionEvent {

    private List items;

    public SelectionEvent(List items) {
        this.items = items;
    }

    public List getItems() {
        return items;
    }

    @Override
    public String toString() {
        String toReturn = "SelectionEvent{" +
                "items=[";
        for (Object item : items) {
            toReturn += item;
            toReturn += " ";
        }
        toReturn += "]}";
        return toReturn;
    }
}
