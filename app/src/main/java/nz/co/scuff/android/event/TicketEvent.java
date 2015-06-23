package nz.co.scuff.android.event;

import java.util.Collection;

import nz.co.scuff.data.journey.Ticket;

/**
 * Created by Callum on 30/04/2015.
 */
public class TicketEvent {

    private Collection<Ticket> tickets;

    public TicketEvent(Collection<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Collection<Ticket> getTickets() {
        return tickets;
    }

    @Override
    public String toString() {
        String toReturn = "TicketEvent{" +
                "tickets=[";
        for (Ticket ticket : tickets) {
            toReturn += ticket;
            toReturn += " ";
        }
        toReturn += "]}";
        return toReturn;
    }
}
