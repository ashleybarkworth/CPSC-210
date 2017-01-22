package ca.ubc.cs.cpsc210.translink.model;

/**
 * Represents an estimated arrival with time to arrival in minutes,
 * the route, the name of destination, and the status (early/late).
 */

//Translink API Key: hu4RqpkW2ZrO6Aw1EnL7

public class Arrival implements Comparable<Arrival>{

    private int timeToStop;
    private Route route;
    private String destination;
    private String status;

    /**
     * Constructs a new arrival with the given time to stop (in minutes),
     * destination and platform.
     * @param timeToStop     time until bus arrives at stop (in minutes)
     * @param destination    name of destination stop
     * @param route          route of the bus to arrive
     */
    public Arrival(int timeToStop, String destination, Route route) {
        this.timeToStop = timeToStop;
        this.route = route;
        this.destination = destination;
    }

    /**
     * Get time until bus arrives at stop in minutes.
     *
     * @return  time until bus arrives at stop in minutes
     */
    public int getTimeToStopInMins() {
        return timeToStop;
    }

    public String getDestination() {
        return destination;
    }

    public Route getRoute() {
        return route;
    }

    /**
     * Order bus arrivals by time until bus arrives at stop
     * (shorter times ordered before longer times)
     */
    @Override
    public int compareTo(Arrival arrival) {
        // Do not modify the implementation of this method!
        return this.getTimeToStopInMins() - arrival.getTimeToStopInMins();
    }

    /**
     * Get the status, an indicator of whether the arrival is on schedule, early, or late
     * @return      the status
     */
    public String getStatus() {
        switch(status) {
            case "+":
                return "early";
            case "*":
                return "on schedule";
            case "-":
                return "late";
            default:
                return "Unspecified";
        }
    }

    /**
     * Set the status, an indicator of whether the arrival is on schedule, early, or late
     * @param status  the status
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
