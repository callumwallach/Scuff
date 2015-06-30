package nz.co.scuff.android.data;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Callum on 29/06/2015.
 */
@Table(name="TicketQueue")
public class TicketQueueItem extends BaseModel {

    @Column(name="TicketId")
    private long ticketId;

    public TicketQueueItem() {}

    public TicketQueueItem(long ticketId) {
        this.ticketId = ticketId;
    }

    public long getTicketId() {
        return ticketId;
    }

}
