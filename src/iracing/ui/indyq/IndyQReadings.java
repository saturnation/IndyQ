package iracing.ui.indyq;

import jirsdk.data.Run;

public class IndyQReadings {

    int track_surface;
    float last_lap_time;
    float percent_lap;
    float lap_time;
    int lap;

    boolean running;
    boolean ready;

    void update(Run run) {
        if (run.isRunning()) {
            track_surface = run.getVarInt("PlayerTrackSurface");
            lap_time = run.getVarFloat("LapCurrentLapTime");
            last_lap_time = run.getVarFloat("LapLastLapTime");
            running = run.isRunning();
            ready = run.isReady();
            lap = run.getVarInt("Lap");
            percent_lap = run.getVarFloat("LapDistPct");
        }
    }
}
