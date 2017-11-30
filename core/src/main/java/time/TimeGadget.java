package time;

import java.util.Map;
import java.util.HashMap;

public class TimeGadget {
    public final String location;
    public final TemperatureUnit temperatureUnit;

    public final static Map<String, String> locationWeatherIDs = new HashMap<String, String>() {
        {
            put("Worcester", "us.01609");
        }
    };


    public TimeGadget() {
        this.location = "";
        this.temperatureUnit = TemperatureUnit.None;
    }

    public TimeGadget(String location, TemperatureUnit temperatureUnit) {
        this.location = location;
        this.temperatureUnit = temperatureUnit;
    }
}
