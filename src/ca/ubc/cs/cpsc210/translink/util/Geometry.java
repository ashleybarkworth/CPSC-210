package ca.ubc.cs.cpsc210.translink.util;

/**
 * Compute relationships between points, lines, and rectangles represented by LatLon objects
 */
public class Geometry {
    /**
     * Return true if the point is inside of, or on the boundary of, the rectangle formed by northWest and southeast
     * @param northWest         the coordinate of the north west corner of the rectangle
     * @param southEast         the coordinate of the south east corner of the rectangle
     * @param point             the point in question
     * @return                  true if the point is on the boundary or inside the rectangle
     */
    public static boolean rectangleContainsPoint(LatLon northWest, LatLon southEast, LatLon point) {

        return ((between(northWest. getLongitude(), southEast.getLongitude(), point.getLongitude())
                && between(southEast.getLatitude(), northWest.getLatitude(), point.getLatitude())));
    }

    /**
     * Return true if the rectangle intersects the line
     * @param northWest         the coordinate of the north west corner of the rectangle
     * @param southEast         the coordinate of the south east corner of the rectangle
     * @param src               one end of the line in question
     * @param dst               the other end of the line in question
     * @return                  true if any point on the line is on the boundary or inside the rectangle
     */
    public static boolean rectangleIntersectsLine(LatLon northWest, LatLon southEast, LatLon src, LatLon dst) {
        if(rectangleContainsPoint(northWest,southEast,src) || rectangleContainsPoint(northWest,southEast,dst)) return true;

        LatLon northEast = new LatLon(southEast.getLongitude(), northWest.getLatitude());
        LatLon southWest = new LatLon(northWest.getLongitude(), southEast.getLatitude());

        return (linesIntersect(northWest.getLongitude(),northWest.getLatitude(),northEast.getLongitude(),northEast.getLatitude(),
                src.getLongitude(),src.getLatitude(),dst.getLongitude(),dst.getLatitude())|| linesIntersect(northEast.getLongitude(),northEast.getLatitude(),
                southEast.getLongitude(),southEast.getLatitude(),src.getLongitude(),src.getLatitude(),dst.getLongitude(),dst.getLatitude()) ||
                linesIntersect(southEast.getLongitude(),southEast.getLatitude(), southWest.getLongitude(),southWest.getLatitude(), src.getLongitude(),src.getLatitude(),dst.getLongitude(),dst.getLatitude())
                || linesIntersect(southWest.getLongitude(),southWest.getLatitude(),
                northWest.getLongitude(),northWest.getLatitude(),src.getLongitude(),src.getLatitude(),dst.getLongitude(),dst.getLatitude()));
    }

    /**
     * A utility method that you might find helpful in implementing the two previous methods
     * Return true if x is >= lwb and <= upb
     * @param lwb      the lower boundary
     * @param upb      the upper boundary
     * @param x         the value in question
     * @return          true if x is >= lwb and <= upb
     */
    private static boolean between(double lwb, double upb, double x) {
        return lwb <= x && x <= upb;
    }

    /**
     *  A private helper method which determines whether or a line segment from (x1,y1) to (x2,y2) intersects with another
     *  line segment from (x3,y3) to (x4,y4)
     * @param x1 the x coordinate of the starting point of the first line segment
     * @param y1 the y coordinate of the starting point of the first line segment
     * @param x2 the x coordinate of the ending point of the first line segment
     * @param y2 the y coordinate of the ending point of the first line segment
     * @param x3 the x coordinate of the starting point of the second line segment
     * @param y3 the y coordinate of the starting point of the second line segment
     * @param x4 the x coordinate of the ending point of the second line segment
     * @param y4 the y coordinate of the ending point of the second line segment
     * @return true if the lines intersect, false otherwise
     */
    private static boolean linesIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        return ((relativeCCW(x1, y1, x2, y2, x3, y3) *
                relativeCCW(x1, y1, x2, y2, x4, y4) <= 0)
                && (relativeCCW(x3, y3, x4, y4, x1, y1) *
                relativeCCW(x3, y3, x4, y4, x2, y2) <= 0));
    }

    /**
     * A private helper method which returns an indicator of where the specified point (px,py) lies with respect to the
     * line segment from (x1,y1) to (x2,y2)
     * @param x1 the x coordinate of the starting point of the line segment
     * @param y1 the y coordinate of the starting point of the line segment
     * @param x2 the x coordinate of the ending point of the line segment
     * @param y2 the y coordinate of the ending point of the line segment
     * @param px the x coordinate of the point
     * @param py the y coordinate of the point
     * @return
     */
    private static int relativeCCW(double x1, double y1, double x2, double y2, double px, double py) {
        x2 -= x1;
        y2 -= y1;
        px -= x1;
        py -= y1;
        double ccw = px * y2 - py * x2;
        if (ccw == 0.0) {
            ccw = px * x2 + py * y2;
            if (ccw > 0.0) {
                px -= x2;
                py -= y2;
                ccw = px * x2 + py * y2;
                if (ccw < 0.0) {
                    ccw = 0.0;
                }
            }
        }
        return (ccw < 0.0) ? -1 : ((ccw > 0.0) ? 1 : 0);
    }

}
