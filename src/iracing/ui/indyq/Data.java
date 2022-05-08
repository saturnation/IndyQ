package iracing.ui.indyq;

import java.util.Arrays;

public class Data {
    public float avg_lap_time;
    int start_lap;
    float[] laps = new float[4];
    float[] mph = new float[4];
    float avg_mph;

    void reset() {
        Arrays.fill(laps, 0.0f);
        Arrays.fill(mph, 0.0f);
        avg_mph = 0.0f;
        avg_lap_time = 0.0f;
    }


}
