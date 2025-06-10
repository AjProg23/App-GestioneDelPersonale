package catering.businesslogic.staff;

import catering.businesslogic.event.Event;

public interface StaffEventReceiver{
    void updateEventCreated(Event e);
}