package time;

public class TimeGadget {
    public final String location;
    public final String zip;
    public final TemperatureUnit temperatureUnit;

    public TimeGadget() { this ("", "", TemperatureUnit.None); }

    public TimeGadget(String location, String zipCode, TemperatureUnit temperatureUnit) {
        this.location = location;
        this.zip = zipCode;
        this.temperatureUnit = temperatureUnit;
    }
}
