package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.*;
import ca.ubc.cs.cpsc210.translink.parsers.exception.ArrivalsDataMissingException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A parser for the data returned by the Translink arrivals at a stop query
 */
public class ArrivalsParser {


    /**
     * Parse arrivals from JSON response produced by TransLink query.  All parsed arrivals are
     * added to the given stop assuming that corresponding JSON object has a RouteNo: and an
     * array of Schedules:
     * Each schedule must have an ExpectedCountdown, ScheduleStatus, and Destination.  If
     * any of the aforementioned elements is missing, the arrival is not added to the stop.
     *
     * @param stop             stop to which parsed arrivals are to be added
     * @param jsonResponse    the JSON response produced by Translink
     * @throws JSONException  when JSON response does not have expected format
     * @throws ArrivalsDataMissingException  when no arrivals are found in the reply
     */
    public static void parseArrivals(Stop stop, String jsonResponse)
            throws JSONException, ArrivalsDataMissingException {

        try {
            JSONArray routes = new JSONArray(jsonResponse);
            System.out.println("Parsing routes for stop " + stop.getName()+ " number " + stop.getNumber());
            for (int i = 0; i < routes.length(); i++) {
                JSONObject route = routes.getJSONObject(i);
                parseArrival(route, stop);
            }
        } catch(JSONException e) {
            throw e;
        } catch(ArrivalsDataMissingException e) {
            throw e;
        }

    }

    private static void parseArrival(JSONObject route, Stop stop) throws ArrivalsDataMissingException, JSONException {
        try {
            int numArrivals = 0;
            String number;
            String name;
            JSONArray arrivals;
            Integer timeToStop;
            boolean time;
            String destination;
            String status;
            try {
                number = route.getString("RouteNo");
            } catch(JSONException e) {
                throw new ArrivalsDataMissingException();
            }
            try {
                name = route.getString("RouteName");
            } catch(JSONException e) {
                throw new ArrivalsDataMissingException();
            }
            Route newRoute = RouteManager.getInstance().getRouteWithNumber(number);
            newRoute.setName(name);
            if(number != null && name != null) {
                stop.addRoute(newRoute);
            }
            try {
                arrivals = route.getJSONArray("Schedules");
            } catch(JSONException e) {
                throw new JSONException("Schedules not in array format");
            }
            for (int j = 0; j < arrivals.length(); j++) {
                JSONObject arrival;
                try {
                    arrival = arrivals.getJSONObject(j);
                } catch (JSONException e) {
                    throw new JSONException("Arrival object could not be formed");
                }

                try {
                    timeToStop = arrival.getInt("ExpectedCountdown");
                    time = true;
                } catch (JSONException e) {
                    throw new ArrivalsDataMissingException();
                }
                try {
                    destination = arrival.getString("Destination");
                } catch (JSONException e) {
                    throw new ArrivalsDataMissingException();
                }
                try {
                    status = arrival.getString("ScheduleStatus");
                } catch (JSONException e) {
                    throw new ArrivalsDataMissingException();
                }
                Arrival newArrival = new Arrival(timeToStop, destination, newRoute);
                newArrival.setStatus(status);
                if (time && destination != null && status != null) {
                    stop.addArrival(newArrival);
                    numArrivals++;
                }

            }
            if(numArrivals==0) {
                throw new ArrivalsDataMissingException("No arrivals found");
            }

        } catch(JSONException e) {
            throw new JSONException("Cannot parse data");
        }
    }
}
