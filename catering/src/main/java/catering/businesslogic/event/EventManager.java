package catering.businesslogic.event;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.menu.Menu;
import catering.businesslogic.staff.StaffMember;
import catering.businesslogic.staff.Team;
import catering.businesslogic.user.User;
import catering.util.LogManager;

/**
 * EventManager handles all operations related to events and services in the
 * CatERing system.
 * It manages event creation, modification, and deletion, as well as service
 * management and menu assignments for services.
 */
public class EventManager {

    private static final Logger LOGGER = LogManager.getLogger(EventManager.class);
    private ArrayList<EventReceiver> eventReceivers;
    private Event selectedEvent;
    private Service currentService;
    private Event currentEvent;
    private SummaryScheme currentSummaryScheme;

    

    /**
     * Constructor initializes the event receivers list
     */
    public EventManager() {
        eventReceivers = new ArrayList<>();
    }

    /**
     * Adds an event receiver to be notified of events changes
     * 
     * @param receiver The event receiver to add
     */
    public void addEventReceiver(EventReceiver receiver) {
        if (receiver != null && !eventReceivers.contains(receiver)) {
            eventReceivers.add(receiver);
        }
    }

    /**
     * Removes an event receiver
     * 
     * @param receiver The event receiver to remove
     */
    public void removeEventReceiver(EventReceiver receiver) {
        eventReceivers.remove(receiver);
    }

    /**
     * Gets all events in the system
     * 
     * @return List of all events
     */
    public ArrayList<Event> getEvents() {
        return Event.loadAllEvents();
    }

    /**
     * Sets the current service based on service ID
     * 
     * @param serviceId ID of the service to select
     */
    public void setSelectedServiceIndex(int serviceId) {
        if (selectedEvent != null && selectedEvent.getServices() != null) {
            for (Service si : selectedEvent.getServices()) {
                if (si.getId() == serviceId) {
                    currentService = si;
                    return;
                }
            }
        }
        // If service not found, currentService remains unchanged
    }

    /**
     * Sets the current service directly
     * 
     * @param service Service to set as current
     */
    public void setCurrentService(Service service) {
        this.currentService = service;
    }

    /**
     * Gets the current service
     * 
     * @return Current service or null if none selected
     */
    public Service getCurrentService() {
        return this.currentService;
    }

    /**
     * Gets the selected event
     * 
     * @return Selected event or null if none selected
     */
    public Event getSelectedEvent() {
        return selectedEvent;
    }

    /**
     * Sets the selected event
     * 
     * @param event Event to select
     */
    public void setSelectedEvent(Event event) {
        this.selectedEvent = event;
    }

    /**
     * Gets the current event
     * 
     * @return Selected currentEvent or null if none curently
     */
    public Event getCurrentEvent() {
        return currentEvent;
    }

    /**
     * Sets the current event
     * 
     * @param the currentEvent Event 
     */
    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }



    /**
     * Creates a new event with the given details
     * 
     * @param name      Event name
     * @param dateStart Start date
     * @param dateEnd   End date (can be null)
     * @param organizer User organizing the event
     * @param team the team assigned to this event
     * @return The newly created event
     */
    public Event createEvent(String name, Date dateStart, Date dateEnd, User chef, Team team) {
        try {
            LOGGER.info("Creating new event '" + name + "' with chef " + chef.getUserName());

            Event event = new Event();
            event.setName(name);
            event.setDateStart(dateStart);
            event.setDateEnd(dateEnd);
            event.setChef(chef);
            
            // Save to database
            event.saveNewEvent();

            // Set as selected event
            this.selectedEvent = event;
            this.currentService = null;

            // Notify all receivers
            setCurrentEvent(event);
            notifyEventCreated(event);

            return event;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to create event '" + name + "'", e);
            return null;
        }
    }
    

    public void selectEvent(Event event) {
        LOGGER.info("Selecting event '" + event.getName() + "' (ID: " + event.getId() + ")");
        this.selectedEvent = event;
        this.currentService = null;
    }

    public Service createService(String name, Date date, Time timeStart, Time timeEnd, String location)
            throws UseCaseLogicException {
        if (selectedEvent == null) {
            String msg = "Cannot create service: no event selected";
            LOGGER.warning(msg);
            throw new UseCaseLogicException(msg);
        }

        try {
            LOGGER.info("Creating new service '" + name + "' for event '" + selectedEvent.getName() + "'");

            Service service = new Service();
            service.setName(name);
            service.setDate(date);
            service.setTimeStart(timeStart);
            service.setTimeEnd(timeEnd);
            service.setLocation(location);
            service.setEventId(selectedEvent.getId());

            // Save to database
            service.saveNewService();

            // Add to event and set as current service
            selectedEvent.addService(service);
            this.currentService = service;

            // Notify all receivers
            notifyServiceCreated(service);

            return service;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to create service '" + name + "'", e);
            return null;
        }
    }

    /**
     * This method can only be used by users with the "organizer" role. It requires that an event
    * is currently selected in the EventManager. The created summary scheme includes
    * information about staff requirements, transportation, service type, and client-specific requests.
     * 
     * @param nrOfStaffMembersRequired          The number of staff reqwuired for the event 
     * @param transportationNeeds               The Trasportqation nedded fot the event
     * @param typeOfService                     The Type of service
     * @param clientRequest                     the specific request from the client
     * @throws UseCaseLogicException            if no event is selected
     */
    public SummaryScheme createSummaryScheme(int nrOfStaffMembersRequired, String transportationNeeds, String typeOfService, String clientRequest) throws UseCaseLogicException{
        User u=CatERing.getInstance().getUserManager().getCurrentUser();
        Event currEvent= this.selectedEvent;
        if (!u.isOrganizer()) {
            throw new UseCaseLogicException("The User is not an organizer you can't accept the vacation request of the staff member");
        }
        if(currEvent==null){
            throw new UseCaseLogicException("Nessun evento selezionato di cui creare il summary scheme");
        }
        try {
            LOGGER.info("Creating new summaryScheme for event:" + selectedEvent.getName() + "'");
        SummaryScheme ss=new SummaryScheme(nrOfStaffMembersRequired, transportationNeeds, typeOfService, clientRequest);
        ss.saveNewSummaryScheme();
        selectedEvent.addSummaryScheme(ss);
        this.currentSummaryScheme=ss;
        notifySummarySchemeCreated(ss);
        return ss;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to create the summaryscheme");
            return null;
        }
        
    }

    /**
     * Modifies an existing event
     * 
     * @param eventId ID of the event to modify
     * @param name    New name for the event
     * @param date    New date for the event
     */
    public void modifyEvent(int eventId, String name, Date date) {
        Event event = Event.loadById(eventId);
        if (event != null) {
            event.setName(name);
            event.setDateStart(date);

            // Notify all receivers
            notifyEventModified(event);

            // Update selected event if it's the same one
            if (selectedEvent != null && selectedEvent.getId() == eventId) {
                this.selectedEvent = event;
            }
        }
    }

    
     /**
     * add summaryScheme for the Event
     * 
     * @param nrOfStaffMembersRequired          number of staff required for the event
     * @param transportationNeeds               any trasportation nedded for the event
     * @param typeOfService                    type of the service for the event
     * @param clientRequest                    any request from the client
     */
    public SummaryScheme addSummaryScheme(int nrOfStaffMembersRequired, String transportationNeeds, String typeOfService, String clientRequest){
        SummaryScheme summaryScheme= new SummaryScheme(nrOfStaffMembersRequired, transportationNeeds, typeOfService, clientRequest);
        currentEvent.setSummaryScheme(summaryScheme);
        summaryScheme.saveNewSummaryScheme();
        currentEvent.updateEvent();
        return summaryScheme;
    }

    public SummaryScheme addSummaryScheme(){
        SummaryScheme summaryScheme=this.addSummaryScheme(15, null, "bartender", null);
        return summaryScheme;
    }


    /**
     * Modifies a service
     * 
     * @param serviceId ID of the service to modify
     * @param name      New name for the service
     * @param date      New date for the service
     * @param location  New location for the service
     * @param menuId    ID of the menu to assign (0 for no menu)
     * @return The modified service, or null if not found
     */
    public Service modifyService(int serviceId, String name, Date date, String location, int menuId) {
        // First try to find service in the current event's services list
        Service service = findServiceById(serviceId);

        if (service != null) {
            // Update service properties
            service.setName(name);
            service.setDate(date);
            service.setLocation(location);

            // Handle menu assignment if needed
            if (menuId > 0 && (service.getMenuId() == 0 || service.getMenuId() != menuId)) {
                try {
                    Menu menu = Menu.load(menuId);
                    if (menu != null) {
                        service.setMenu(menu);
                    }
                } catch (Exception e) {
                    System.err.println("Error loading menu: " + e.getMessage());
                }
            }

            // Notify all receivers
            notifyServiceModified(service);

            // Update current service reference if this is the current service
            if (currentService != null && currentService.getId() == serviceId) {
                currentService = service;
            }
        }

        return service;
    }

    /**
     * Deletes a service by its ID
     * 
     * @param serviceId ID of the service to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteService(int serviceId) {
        try {
            if (selectedEvent == null) {
                LOGGER.warning("Cannot delete service: no event selected");
                return false;
            }

            Service serviceToDelete = findServiceById(serviceId);
            if (serviceToDelete == null) {
                LOGGER.warning("Service with ID " + serviceId + " not found");
                return false;
            }

            LOGGER.info("Deleting service '" + serviceToDelete.getName() + "' (ID: " + serviceId + ")");

            // Delete from database
            boolean deleted = serviceToDelete.deleteService();

            if (!deleted) {
                LOGGER.warning("Database operation failed while deleting service " + serviceId);
                return false;
            }

            selectedEvent.removeService(serviceToDelete);

            // Clear current service if it was the one deleted
            if (currentService != null && currentService.getId() == serviceId) {
                currentService = null;
            }

            // Notify all receivers
            notifyServiceDeleted(serviceToDelete);

            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to delete service " + serviceId, e);
            return false;
        }
    }

    /**
     * Deletes an event and all its associated services
     * 
     * @param eventId ID of the event to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteEvent(int eventId) {
        try {
            Event eventToDelete = Event.loadById(eventId);
            if (eventToDelete == null) {
                LOGGER.warning("Event with ID " + eventId + " not found");
                return false;
            }

            LOGGER.info("Deleting event '" + eventToDelete.getName() + "' (ID: " + eventId + ")");

            // Use the event's delete method instead of direct SQL
            boolean deleted = eventToDelete.deleteEvent();

            if (!deleted) {
                LOGGER.warning("Database operation failed while deleting event " + eventId);
                return false;
            }

            // Clear references if this was the selected event
            if (selectedEvent != null && selectedEvent.getId() == eventId) {
                selectedEvent = null;
                currentService = null;
            }

            // Notify all receivers
            notifyEventDeleted(eventToDelete);

            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to delete event " + eventId, e);
            return false;
        }
    }
    /**
     * gives the Staff of the current event 
     * 
     * @throws UseCaseLogicException if no event is selected
     */
    public Team getStaffAvailability()throws UseCaseLogicException{
        Event currEvent= getCurrentEvent();
        if(currEvent==null){
            throw new UseCaseLogicException("Nessun evento corrente di cui creare il summary scheme");
        }
        Team t= currEvent.getTeam();
        return t;
    }

    /**
     * Assigns a menu to the current service
     * 
     * @param menu The menu to assign
     * @throws UseCaseLogicException if no event or service is selected
     */
    public void assignMenu(Menu menu) throws UseCaseLogicException {
        if (selectedEvent == null) {
            String msg = "Cannot assign menu: no event selected";
            LOGGER.warning(msg);
            throw new UseCaseLogicException(msg);
        }

        if (currentService == null) {
            String msg = "Cannot assign menu: no service selected";
            LOGGER.warning(msg);
            throw new UseCaseLogicException(msg);
        }

        LOGGER.info("Assigning menu '" + menu.getTitle() + "' to service '" + currentService.getName() + "'");

        currentService.assignMenuToService(menu);

        // Notify all receivers
        notifyMenuAssigned(currentService, menu);
    }

    /**
     * Removes the menu from the current service
     * 
     * @return true if removed successfully, false if no service selected
     */
    public boolean removeMenu() {
        if (currentService == null) {
            return false;
        }

        currentService.removeMenu();

        // Notify all receivers
        notifyMenuRemoved(currentService);

        return true;
    }

    /**
     * Helper method to find a service by ID within the selected event
     */
    private Service findServiceById(int serviceId) {
        if (selectedEvent == null || selectedEvent.getServices() == null) {
            return null;
        }

        for (Service s : selectedEvent.getServices()) {
            if (s.getId() == serviceId) {
                return s;
            }
        }

        return null;
    }

    // Notification methods to avoid code duplication

    private void notifyEventCreated(Event event) {
        for (EventReceiver receiver : eventReceivers) {
            receiver.updateEventCreated(event);
        }
    }

    private void notifyEventModified(Event event) {
        for (EventReceiver receiver : eventReceivers) {
            receiver.updateEventModified(event);
        }
    }

    private void notifyEventDeleted(Event event) {
        for (EventReceiver receiver : eventReceivers) {
            receiver.updateEventDeleted(event);
        }
    }

    private void notifyServiceCreated(Service service) {
        for (EventReceiver receiver : eventReceivers) {
            receiver.updateServiceCreated(selectedEvent, service);
        }
    }

    private void notifyServiceModified(Service service) {
        for (EventReceiver receiver : eventReceivers) {
            receiver.updateServiceModified(service);
        }
    }

    private void notifyServiceDeleted(Service service) {
        for (EventReceiver receiver : eventReceivers) {
            receiver.updateServiceDeleted(service);
        }
    }

    private void notifyMenuAssigned(Service service, Menu menu) {
        for (EventReceiver receiver : eventReceivers) {
            receiver.updateMenuAssigned(service, menu);
        }
    }

    private void notifyMenuRemoved(Service service) {
        for (EventReceiver receiver : eventReceivers) {
            receiver.updateMenuRemoved(service);
        }
    }

    // Notification methods to avoid code duplication
    private void notifySummarySchemeCreated(SummaryScheme ss) {
        for(EventReceiver receiver: eventReceivers){
            receiver.updateSummarySchemeCreated(ss);
        }
    }

    public ArrayList<EventReceiver> getEventReceivers() {
        return eventReceivers;
    }

    public void setEventReceivers(ArrayList<EventReceiver> eventReceivers) {
        this.eventReceivers = eventReceivers;
    }

    public SummaryScheme getCurrentSummaryScheme() {
        return currentSummaryScheme;
    }

    public void setCurrentSummaryScheme(SummaryScheme currentSummaryScheme) {
        this.currentSummaryScheme = currentSummaryScheme;
    }
}
