package ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects;


import ch.fhnw.elektroautos.components.catalog.base.SerialHelper;
import ch.fhnw.elektroautos.mvc.renewablecharge.controller.hardware.LedStripController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.Season;

import java.util.HashMap;
import java.util.List;

public interface IRenewableChargeConfiguration {
    public String getSelectedLanguage();

    public Season getSelectedSeason();

    public List<Car> getCars();

    public List<Player> getPlayers();

    public float timeTillWeatherChange();

    public float timeTillNewEnergyPackage();

    public int getMinimumRequiredPlayers();

    public int getLobbyCountdown();

    public float getBlockedTimePerKWh();

    public HashMap<String, String> getConfiguredLanguages();

    public int getRaceCountdown();

    public int getGameDuration();

    public int getResultCountdown();

    public SerialHelper getSerialHelper();

    public LedStripController getLedStripController();

    public void setPlayers(List<Player> players);
}
