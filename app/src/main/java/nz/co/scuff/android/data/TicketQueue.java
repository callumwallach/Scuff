package nz.co.scuff.android.data;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Callum on 25/06/2015.
 */
public final class TicketQueue {

    public enum Queue {
        RECEIVED, STAMPED
    }

    public static void add(long ticketId) {
        TicketQueueItem item = new TicketQueueItem(ticketId);
        item.save();
    }

    public static void add(Set<Long> ticketIds) {
        for (long ticketId : ticketIds) {
            add(ticketId);
        }
    }

    private static Set<Long> get() {
        List<TicketQueueItem> list = new Select()
                .from(TicketQueueItem.class)
                .execute();
        TreeSet<Long> set = new TreeSet<>();
        for (TicketQueueItem item : list) {
            set.add(item.getTicketId());
        }
        return set;
    }

    public static Set<Long> remove() {
        Set<Long> set = get();
        new Delete().from(TicketQueueItem.class).execute();
        return set;
    }

}
