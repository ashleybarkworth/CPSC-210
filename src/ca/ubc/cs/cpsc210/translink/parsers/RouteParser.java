package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RouteManager;
import ca.ubc.cs.cpsc210.translink.model.RoutePattern;
import ca.ubc.cs.cpsc210.translink.parsers.exception.RouteDataMissingException;
import ca.ubc.cs.cpsc210.translink.providers.DataProvider;
import ca.ubc.cs.cpsc210.translink.providers.FileDataProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Parse route information in JSON format.
 */
public class RouteParser {
    private String filename;

    public RouteParser(String filename) {
        this.filename = filename;
    }
    /**
     * Parse route data from the file and add all route to the route manager.
     *
     */
    public void parse() throws IOException, RouteDataMissingException, JSONException{
        DataProvider dataProvider = new FileDataProvider(filename);

        parseRoutes(dataProvider.dataSourceToString());
    }
    /**
     * Parse route information from JSON response produced by Translink.
     * Stores all routes and route patterns found in the RouteManager.
     *
     * @param  jsonResponse    string encoding JSON data to be parsed
     * @throws JSONException   when JSON data does not have expected format
     * @throws RouteDataMissingException when
     * <ul>
     *  <li> JSON data is not an array </li>
     *  <li> JSON data is missing Name, StopNo, Routes or location elements for any stop</li>
     * </ul>
     */

    public void parseRoutes(String jsonResponse)
            throws JSONException, RouteDataMissingException {
        try {
            JSONArray routes = new JSONArray(jsonResponse);
            for (int i = 0; i < routes.length(); i++) {
                JSONObject route = routes.getJSONObject(i);
                parseRoute(route);
            }
        } catch(JSONException e) {
            throw new JSONException("Cannot parse data");
        } catch(RouteDataMissingException e) {
            throw e;
        }

    }

    private void parseRoute(JSONObject route) throws RouteDataMissingException, JSONException {
        try {
            String name;
            String number;
            List<RoutePattern> list = new ArrayList<>();
            Route newRoute;
            JSONArray patterns;
            try {
                number = route.getString("RouteNo");
            }catch(JSONException e){
                throw new RouteDataMissingException("Missing route number");
            } try {
                name = route.getString("Name");
            } catch(JSONException e) {
                throw new RouteDataMissingException("Missing route name");
            }

            newRoute = RouteManager.getInstance().getRouteWithNumber(number, name);

            try {
                patterns = route.getJSONArray("Patterns");
            } catch(JSONException e) {
                throw new RouteDataMissingException("Patterns is missing");
            }
            for (int j = 0; j < patterns.length(); j++) {
                JSONObject pattern = patterns.getJSONObject(j);
                String patternName;
                String destination;
                String direction;
                try {
                    patternName = pattern.getString("PatternNo");
                } catch(JSONException e) {
                    throw new RouteDataMissingException("Missing pattern name");
                }
                try {
                    destination = pattern.getString("Destination");
                } catch(JSONException e) {
                    throw new RouteDataMissingException("Missing destination");
                }
                try {
                    direction = pattern.getString("Direction");
                } catch(JSONException e) {
                    throw new RouteDataMissingException("Missing direction");
                }
                RoutePattern newPattern = new RoutePattern(patternName, destination, direction, newRoute);
                list.add(newPattern);
            }

            for(RoutePattern rp: list) {
                newRoute.addPattern(rp);
            }
        } catch(JSONException e) {
            throw new JSONException("Cannot parse data");
        }
    }

}
