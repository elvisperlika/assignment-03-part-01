package pcd.ass03;

/**
 *
 * 2-dimensional point
 * objects are completely state-less
 *
 */
public record P2dJ(double x, double y) {


    public P2dJ sum(V2dJ v){
        return new P2dJ(x+v.x(),y+v.y());
    }

    public V2dJ sub(P2dJ v){
        return new V2dJ(x-v.x,y-v.y);
    }
    
    public double distance(P2dJ p) {
    	double dx = p.x - x;
    	double dy = p.y - y;
    	return Math.sqrt(dx*dx + dy*dy);

    }
    
    public String toString(){
        return "P2d("+x+","+y+")";
    }

}
