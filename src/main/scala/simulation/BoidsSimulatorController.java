package simulation;

import java.util.List;
import java.util.Optional;

public class BoidsSimulatorController {

    private final BoidsModel model;
    private Optional<BoidsView> view;
    private static final int FRAMERATE = 50;
    private int framerate;
    private long t0;
    private boolean isTime0Updated = false;

    public BoidsSimulatorController(BoidsModel model) {
        this.model = model;
        view = Optional.empty();
    }

    public void attachView(BoidsView view) {
    	this.view = Optional.of(view);
    }

    public void runSimulation() {
        while (true) {
            if (view.isPresent()) {
                view.get().update(framerate);
                var t0 = System.currentTimeMillis();
                List<Boid> boids = model.getBoids();
                for(Boid boid : boids)
                    boid.calculateVelocity(model);

                for(Boid boid : boids)
                    boid.updateVelocity(model);

                for(Boid boid : boids)
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
