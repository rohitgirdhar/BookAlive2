package com.rohit.bookalive;

import android.util.Log;

public class Polygon {
	// Polygon coodinates.
	// TODO  make private after draw in QT1
    public double[] polyY, polyX;

    // Number of sides in the polygon.
    private int polySides;

    /**
     * Default constructor.
     * @param px Polygon x coods.
     * @param py Polygon y coods.
     * @param ps Polygon sides count.
     */
    public Polygon(  double[] px, double[] py, int ps )
    {
        polyX = px;
        polyY = py;
        polySides = ps;
    }

    /**
     * Checks if the Polygon contains a point.
     * @see "http://alienryderflex.com/polygon/"
     * @param x Point horizontal pos.
     * @param y Point vertical pos.
     * @return Point is in Poly flag.
     */
    public boolean contains( double x, double y )
    {
        boolean oddTransitions = false;
        for( int i = 0, j = polySides -1; i < polySides; j = i++ )
        {
            if( ( polyY[ i ] < y && polyY[ j ] >= y ) || ( polyY[ j ] < y && polyY[ i ] >= y ) )
            {
                if( polyX[ i ] + ( y - polyY[ i ] ) / ( polyY[ j ] - polyY[ i ] ) * ( polyX[ j ] - polyX[ i ] ) < x )
                {
                    oddTransitions = !oddTransitions;          
                }
            }
        }
        return oddTransitions;
    }
    
    /**
     * Finds percentage overlap of this with p, w.r.t this
     * @param p Other polygon
     * @return Percentage overlap in [0,1]
     */
    public double overlap(Polygon p) {
    	// TODO
    	// For now, running a very simple algo, just aprox to a rectangle and compare
    	// "this" is the key rect, ensure its inside the new rect
    	
    	boolean isin = true;
    	for(int i=0; i<polySides; i++) {
    		if(!p.contains(polyX[i], polyY[i])) {
    			isin = false; break;
    		}
    	}
    	
    	if(isin) {
    		return 0.8;
    	} else {
    		return 0.2;
    	}
    }
}
