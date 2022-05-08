package iracing.ui.indyq;

import jirsdk.data.Run;

public class Weather {

    public float airDensity;
    public float airTemp;
    public float relHumidity;
    public int skies;
    public float trackTemp;
    public float trackTempCrew;
    public float windVel;
    public float windDir;
    public int weatherType;

    public void update(Run run) {
        airDensity = run.getVarFloat("AirDensity");
        airTemp = run.getVarFloat("AirTemp");
        relHumidity = run.getVarFloat("RelativeHumidity");
        skies = run.getVarInt("Skies");
        trackTemp = run.getVarFloat("TrackTemp");
        trackTempCrew = run.getVarFloat("TrackTempCrew");
        windVel = run.getVarFloat("WindVel");
        windDir = run.getVarFloat("WindDir");
        weatherType = run.getVarInt("WeatherType");
    }
}
