package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.*;
import ca.ubc.cs.cpsc210.translink.parsers.exception.StopDataMissingException;
import ca.ubc.cs.cpsc210.translink.providers.DataProvider;
import ca.ubc.cs.cpsc210.translink.providers.FileDataProvider;
import ca.ubc.cs.cpsc210.translink.util.LatLon;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A parser for the data returned by Translink stops query
 */
public class StopParser {

    private String filename;

    public StopParser(String filename) {
        this.filename = filename;
    }
    /**
     * Parse stop data from the file and add all stops to stop manager.
     *
     */
    public void parse() throws IOException, StopDataMissingException, JSONException{
        DataProvider dataProvider = new FileDataProvider(filename);

        parseStops(dataProvider.dataSourceToString());
    }
    /**
     * Parse stop information from JSON response produced by Translink.
     * Stores all stops and routes found in the StopManager and RouteManager.
     *
     * @param  jsonResponse    string encoding JSON data to be parsed
     * @throws JSONException   when JSON data does not have expected format
     * @throws StopDataMissingException when
     * <ul>
     *  <li> JSON data is not an array </li>
     *  <li> JSON data is missing Name, StopNo, Routes or location (Latitude or Longitude) elements for any stop</li>
     * </ul>
     */

    public void parseStops(String jsonResponse)
            throws JSONException, StopDataMissingException {
        try {
            JSONArray stops = new JSONArray(jsonResponse);
            for (int i = 0; i < stops.length(); i++) {
                JSONObject stop = stops.getJSONObject(i);
                parseStop(stop);
            }
        } catch(JSONException e) {
            throw e;
        } catch(StopDataMissingException e) {
            throw e;
        }

    }

    private void parseStop(JSONObject stop) throws StopDataMissingException {
        int number;
        String name;
        LatLon newLocation;

        try {
            number = stop.getInt("StopNo");
        } catch(JSONException e) {
            throw new StopDataMissingException("Missing stop number");
        }
        try {
            name = stop.getString("Name");
        } catch(JSONException e) {
            throw new StopDataMissingException("Missing stop name");
        }
        try {
            newLocation = new LatLon(stop.getDouble("Latitude"), stop.getDouble("Longitude"));
        } catch(JSONException e) {
            throw new StopDataMissingException("Missing location fields");
        }
        Stop s = StopManager.getInstance().getStopWithId(number, name, newLocation);
        try {
            String routes = stop.getString("Routes");
            String[] routeList = routes.split(",");
            for (int j = 0; j < routeList.length; j++) {
                String route = routeList[j].trim();
                Route newRoute = RouteManager.getInstance().getRouteWithNumber(route);
                newRoute.addStop(s);
            }
        } catch(JSONException e) {
            throw new StopDataMissingException("Missing routes");
        }

    }
}
