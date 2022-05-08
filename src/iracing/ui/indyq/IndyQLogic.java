package iracing.ui.indyq;

import jirsdk.data.defines.TrackLocation;

import static iracing.ui.indyq.IndyQLogic.Status.*;
import static iracing.ui.indyq.IndyQLogic.TimeDisplay.*;
import static jirsdk.data.defines.TrackLocation.ON_TRACK;

public class IndyQLogic {

    Data data;

    public IndyQLogic(Data data) {
        this.data = data;
    }

    public enum Status {
        BEFORE,
        OUT_LAP,
        LAP1,
        LAP2,
        LAP3,
        LAP4,
        IN_LAP
    }

    public enum TimeDisplay {
        NO_TIME,
        ON_LAP,
        LAP_COMPLETE
    }

    float track_length_miles = 2.5f;

    Status state = BEFORE;

    Status state() {
        return state;
    }

    void update(IndyQReadings r) {
        if (state == BEFORE) {
            if (r.track_surface == ON_TRACK) {
                state = OUT_LAP;
                data.start_lap = r.lap;
            }
        } else if (state == OUT_LAP) {
            data.reset();
            if (r.lap == data.start_lap + 1) {
                state = LAP1;
            }
        } else if (state == LAP1) {
            data.mph[0] = percent_lap_mph(r);
            if (r.lap == data.start_lap + 2) {
                state = LAP2;
                data.laps[0] = r.lap_time;
            }
            data.avg_mph = data.mph[0];
            data.avg_lap_time = r.lap_time;
        } else if (state == LAP2) {
            data.mph[1] = percent_lap_mph(r);
            if (update_last_lap(r)) {
                data.laps[0] = r.last_lap_time;
                data.mph[0] = full_lap_mph(0);
            }
            if (r.lap == data.start_lap + 3) {
                state = LAP3;
                data.laps[1] = r.lap_time;
            }
            data.avg_lap_time = (data.laps[0] + r.lap_time) / (1 + r.percent_lap);
            data.avg_mph = (track_length_miles * (1 + r.percent_lap)) / ((data.laps[0] + r.lap_time) / 3600);
        } else if (state == LAP3) {
            data.mph[2] = percent_lap_mph(r);
            if (update_last_lap(r)) {
                data.laps[1] = r.last_lap_time;
                data.mph[1] = full_lap_mph(1);
            }
            if (r.lap == data.start_lap + 4) {
                state = LAP4;
                data.laps[2] = r.lap_time;
            }
            data.avg_lap_time = (data.laps[0] + data.laps[1] + r.lap_time) / (2 + r.percent_lap);
            data.avg_mph = (track_length_miles * (2 + r.percent_lap)) / ((data.laps[0] + data.laps[1] + r.lap_time) / 3600);
        } else if (state == LAP4) {
            data.mph[3] = percent_lap_mph(r);
            if (update_last_lap(r)) {
                data.laps[2] = r.last_lap_time;
                data.mph[2] = full_lap_mph(2);
            }
            if (r.lap == data.start_lap + 5) {
                state = IN_LAP;
                data.laps[3] = r.lap_time;
            }
            data.avg_lap_time = (data.laps[0] + data.laps[1] + data.laps[2] + r.lap_time) / (3 + r.percent_lap);
            data.avg_mph = (track_length_miles * (3 + r.percent_lap)) / ((data.laps[0] + data.laps[1] + data.laps[2] + r.lap_time) / 3600);
        } else if (state == IN_LAP) {
            if (update_last_lap(r)) {
                data.laps[3] = r.last_lap_time;
                data.mph[3] = full_lap_mph(3);
                data.avg_mph = (4 * track_length_miles) / ((data.laps[0] + data.laps[1] + data.laps[2] + data.laps[3]) / 3600);
                data.avg_lap_time = (data.laps[0] + data.laps[1] + data.laps[2] + data.laps[3]) / 4;
            }
        }
        if (r.track_surface == TrackLocation.IN_PIT_STALL) {
            state = BEFORE;
        }
    }


    private float full_lap_mph(int i) {
        return track_length_miles / (data.laps[i] / 3600);
    }

    private float percent_lap_mph(IndyQReadings r) {
        return track_length_miles * r.percent_lap / (r.lap_time / 3600);
    }

    private boolean update_last_lap(IndyQReadings r) {
        return 0.05f < r.percent_lap && r.percent_lap < 0.10f;
    }

    String description() {
        return switch (state) {
            case BEFORE -> "In pits";
            case OUT_LAP -> "Out lap";
            case LAP1 -> "Lap 1";
            case LAP2 -> "Lap 2";
            case LAP3 -> "Lap 3";
            case LAP4 -> "Lap 4";
            case IN_LAP -> "In lap";
        };
    }

    TimeDisplay timeDisplay(int on_lap) {
        return switch (state) {
            case BEFORE, OUT_LAP -> NO_TIME;
            case LAP1 -> (on_lap == 1) ? ON_LAP : NO_TIME;
            case LAP2 -> (on_lap == 2) ? ON_LAP : (on_lap == 1) ? LAP_COMPLETE : NO_TIME;
            case LAP3 -> (on_lap == 3) ? ON_LAP : (on_lap < 3) ? LAP_COMPLETE : NO_TIME;
            case LAP4 -> (on_lap == 4) ? ON_LAP : LAP_COMPLETE;
            case IN_LAP -> LAP_COMPLETE;
        };
    }
}
