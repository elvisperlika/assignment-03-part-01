package pcd.ass03;/*
 *   V2d.java
 *
 * Copyright 2000-2001-2002  aliCE team at deis.unibo.it
 *
 * This software is the proprietary information of deis.unibo.it
 * Use is subject to license terms.
 *
 */

/**
 *
 * 2-dimensional vector
 * objects are completely state-less
 *
 */
public record V2dJ(double x, double y) {

    public V2dJ sum(V2dJ v){
        return new V2dJ(x+v.x,y+v.y);
    }

    public double abs(){
        return (double)Math.sqrt(x*x+y*y);
    }

    public V2dJ getNormalized(){
        double module=(double)Math.sqrt(x*x+y*y);
        return new V2dJ(x/module,y/module);
    }

    public V2dJ mul(double fact){
        return new V2dJ(x*fact,y*fact);
    }

    public String toString(){
        return "V2d("+x+","+y+")";
    }
    
}
