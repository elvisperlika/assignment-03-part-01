package pcd.ass03;

import java.util.List;
import java.util.Optional;

public class BoidsSimulatorControllerJ {

    private final BoidsModelJ model;
    private Optional<BoidsViewJ> view;
    private static final int FRAMERATE = 50;
    private int framerate;
    private long t0;
    private boolean isTime0Updated = false;

    public BoidsSimulatorControllerJ(BoidsModelJ model) {
        this.model = model;
        view = Optional.empty();
    }

    public void attachView(BoidsViewJ view) {
    	this.view = Optional.of(view);
    }

    public void runSimulation() {
        while (true) {
            if (view.isPresent()) {
                view.get().update(framerate);
                var t0 = System.currentTimeMillis();
                List<BoidJ> boids = model.getBoids();
                for(BoidJ boid : boids)
                    boid.calculateVelocity(model);

                for(BoidJ boid : boids)
                    boid.updateVelocity(model);

                for(BoidJ boid : boids)
                    boid.updatePosition(model);
                updateFrameRate(t0);
            }
        }
    }

    private void updateTime0() {
        if (!isTime0Updated) {
            t0 = System.currentTimeMillis();
            isTime0Updated = true;
        }
    }

    private void updateFrameRate(long t0) {
        isTime0Updated = false;
        var t1 = System.currentTimeMillis();
        var dtElapsed = t1 - t0;
        var frameratePeriod = 1000 / FRAMERATE;
        if (dtElapsed < frameratePeriod) {
            try {
                Thread.sleep(frameratePeriod - dtElapsed);
            } catch (Exception ex) {
                System.out.println(ex);
            }
            framerate = FRAMERATE;
        } else {
            framerate = (int) (1000 / dtElapsed);
        }
    }
}
