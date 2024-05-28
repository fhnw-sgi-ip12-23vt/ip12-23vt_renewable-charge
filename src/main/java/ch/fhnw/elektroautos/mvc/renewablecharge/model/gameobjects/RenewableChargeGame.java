package ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects;

import ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.EnergyPackage;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.*;

import java.util.Random;

public class RenewableChargeGame {
    private final IRenewableChargeConfiguration gameConfiguration;
    private WeatherConfiguration currentWeatherConfiguration;

    public RenewableChargeGame(IRenewableChargeConfiguration gameConfiguration) {
        this.gameConfiguration = gameConfiguration;
        currentWeatherConfiguration = new WeatherConfiguration(StorgeState.HalfFull, SunType.Sunny, ThermalType.Operating, WaterType.MediumWaterLevel, WindType.WindStill);
    }

    public WeatherConfiguration getWeather() {
        generateNewWeather();
        return currentWeatherConfiguration;
    }

    private void generateNewWeather() {
        var sunType     = SunType.changeEvent(currentWeatherConfiguration.getSunType());
        var storgeState = StorgeState.changeEvent(currentWeatherConfiguration.getStorageState(), sunType);
        var thermalType = ThermalType.changeEvent(currentWeatherConfiguration.getThermalType());
        var waterState  = WaterType.changeEvent(currentWeatherConfiguration.getWaterType(), sunType);
        var windState   = WindType.changeEvent(currentWeatherConfiguration.getWindType());
        currentWeatherConfiguration = new WeatherConfiguration(storgeState, sunType, thermalType, waterState, windState);

    }

    public EnergyPackage getPackage() {
        return gameConfiguration.getSelectedSeason().getPackage(currentWeatherConfiguration);
    }

    public IRenewableChargeConfiguration getGameConfiguration() {
        return gameConfiguration;
    }

    public int raceLength() {
        int maxLength = 0;
        for (Car car : gameConfiguration.getCars()) {
            if (maxLength < car.getRangeInKm()) {
                maxLength = car.getRangeInKm();
            }
        }
        var mod = maxLength % 100;
        if (mod != 0) {
            maxLength += 100 - mod;
        }
        return maxLength;
    }
}

