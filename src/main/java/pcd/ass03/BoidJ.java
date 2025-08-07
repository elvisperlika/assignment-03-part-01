package pcd.ass03;

import java.util.ArrayList;
import java.util.List;

public class BoidJ {

    private P2dJ pos;
    private V2dJ vel;
    private volatile V2dJ separation;
    private volatile V2dJ alignment;
    private volatile V2dJ cohesion;

    public BoidJ(P2dJ pos, V2dJ vel) {
    	this.pos = pos;
    	this.vel = vel;
    }
    
    public P2dJ getPos() {
    	return pos;
    }

    public V2dJ getVel() {
    	return vel;
    }

    public void calculateVelocity(BoidsModelJ model) {
        /* change velocity vector according to separation, alignment, cohesion */
        List<BoidJ> nearbyBoids = getNearbyBoids(model);

        separation = calculateSeparation(nearbyBoids, model);
        alignment = calculateAlignment(nearbyBoids, model);
        cohesion = calculateCohesion(nearbyBoids, model);
    }

    public void updateVelocity(BoidsModelJ model) {
        vel = vel.sum(alignment.mul(model.getAlignmentWeight()))
                .sum(separation.mul(model.getSeparationWeight()))
                .sum(cohesion.mul(model.getCohesionWeight()));

        /* Limit speed to MAX_SPEED */
        double speed = vel.abs();

        if (speed > model.getMaxSpeed()) {
            vel = vel.getNormalized().mul(model.getMaxSpeed());
        }
    }
    
    public void updatePosition(BoidsModelJ model) {

        /* Update position */

        pos = pos.sum(vel);
        
        /* environment wrap-around */
        
        if (pos.x() < model.getMinX()) pos = pos.sum(new V2dJ(model.getWidth(), 0));
        if (pos.x() >= model.getMaxX()) pos = pos.sum(new V2dJ(-model.getWidth(), 0));
        if (pos.y() < model.getMinY()) pos = pos.sum(new V2dJ(0, model.getHeight()));
        if (pos.y() >= model.getMaxY()) pos = pos.sum(new V2dJ(0, -model.getHeight()));

    }     
    
    private List<BoidJ> getNearbyBoids(BoidsModelJ model) {
    	var list = new ArrayList<BoidJ>();
        for (BoidJ other : model.getBoids()) {
        	if (other != this) {
        		P2dJ otherPos = other.getPos();
        		double distance = pos.distance(otherPos);
        		if (distance < model.getPerceptionRadius()) {
        			list.add(other);
        		}
        	}
        }
        return new ArrayList<>(list);
    }
    
    private V2dJ calculateAlignment(List<BoidJ> nearbyBoids, BoidsModelJ model) {
        double avgVx = 0;
        double avgVy = 0;
        if (nearbyBoids.size() > 0) {
	        for (BoidJ other : nearbyBoids) {
	        	V2dJ otherVel = other.getVel();
	            avgVx += otherVel.x();
	            avgVy += otherVel.y();
	        }	        
	        avgVx /= nearbyBoids.size();
	        avgVy /= nearbyBoids.size();
	        return new V2dJ(avgVx - vel.x(), avgVy - vel.y()).getNormalized();
        } else {
        	return new V2dJ(0, 0);
        }
    }

    private V2dJ calculateCohesion(List<BoidJ> nearbyBoids, BoidsModelJ model) {
        double centerX = 0;
        double centerY = 0;
        if (nearbyBoids.size() > 0) {
	        for (BoidJ other: nearbyBoids) {
	        	P2dJ otherPos = other.getPos();
	            centerX += otherPos.x();
	            centerY += otherPos.y();
	        }
            centerX /= nearbyBoids.size();
            centerY /= nearbyBoids.size();
            return new V2dJ(centerX - pos.x(), centerY - pos.y()).getNormalized();
        } else {
        	return new V2dJ(0, 0);
        }
    }
    
    private V2dJ calculateSeparation(List<BoidJ> nearbyBoids, BoidsModelJ model) {
        double dx = 0;
        double dy = 0;
        int count = 0;
        for (BoidJ other: nearbyBoids) {
        	P2dJ otherPos = other.getPos();
    	    double distance = pos.distance(otherPos);
    	    if (distance < model.getAvoidRadius()) {
    	    	dx += pos.x() - otherPos.x();
    	    	dy += pos.y() - otherPos.y();
    	    	count++;
    	    }
    	}
        if (count > 0) {
            dx /= count;
            dy /= count;
            return new V2dJ(dx, dy).getNormalized();
        } else {
        	return new V2dJ(0, 0);
        }
    }
}
