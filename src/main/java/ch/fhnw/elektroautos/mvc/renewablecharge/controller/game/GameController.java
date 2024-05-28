package ch.fhnw.elektroautos.mvc.renewablecharge.controller.game;

import java.io.IOException;
import java.util.Random;

import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.*;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.utils.TranslationClient;
import ch.fhnw.elektroautos.mvc.util.Pi4JContext;

public class GameController {
    private static int numberOfPackagesShown = 0; //TODO: Remove after the first prototype is done
    private static final int NUMBER_OF_RACE_LEDS = 100; //TODO: Replace with number of leds on the LED strip
    private static RenewableChargeGame game;
    private static TranslationClient translationClient;

    public static void startGame() {
        translationClient = new TranslationClient("en");
        translationClient.switchLanguage("de");
        System.out.println(translationClient.get("welcome_message"));

        //GameConfiguration selection
        //IRenewableChargeConfiguration gameConfiguration = new MockupRenewableChargeConfiguration(Pi4JContext.createMockContext());//PropertiesRenewableChargeConfiguration

        //game = new RenewableChargeGame(gameConfiguration);
        //gameTick(game);

        var raceLength = game.raceLength();
        System.out.println("The Race results are: max length " + raceLength);

        for (var player : game.getGameConfiguration().getPlayers()) {
            if (!player.isActive()) {
                continue;
            }
            System.out.println(translationClient.get(player.getTranslationPropName()));
            //TODO: Not sure why this is broken. It should print the distance traveled by the car, but is always full.
            System.out.println(getDistance(player.getSelectedCar().getRangeInKm(), raceLength, NUMBER_OF_RACE_LEDS));
        }

        System.out.println();
        System.out.println("Thanks for playing the Renewable Charge game, we hope you had fun and learnt sth new about renewable energy");
    }

    public static void assignCarToPlayer(String serialId){
        for (var player : game.getGameConfiguration().getPlayers()) {
            Car car = game.getGameConfiguration().getCars().stream()
                          //.filter(c -> c.getCorrespondingRfidCard().getSerial().equals(serialId))
                          .findFirst()
                          .orElse(null);
            if (car == null) {
                System.out.println("Car not found!");
            } else {
                System.out.println("Player " + player.getTranslationPropName() + " has selected car " + car.getName());
                player.selectCar(car);
            }
        }

    }

    public static void assignMockUpCarToPlayer(){
        int i = 0;
        for (var player : game.getGameConfiguration().getPlayers()) {
            player.activate();
            Car car = game.getGameConfiguration().getCars().get(i);
            System.out.println("Player " + translationClient.get(player.getTranslationPropName()) + " has selected car " + translationClient.get(car.getName()));
            player.selectCar(car);
        }
    }

    /**
     * Simulates a game tick, distributing energy packages and charging cars.
     *
     * @param game The game context.
     */
    private static void gameTick(RenewableChargeGame game) {
        var energyPackage = game.getPackage();
        System.out.println(game.getWeather());

        if (energyPackage != null){
            System.out.println("recieved energypackage: " + translationClient.get(energyPackage.getEnergyType().getTranslatePropName()) + " " + energyPackage.getSize());
            var random = new Random();
            var index = random.nextInt(game.getGameConfiguration().getPlayers().size());
            game.getGameConfiguration().getPlayers().get(index).getSelectedCar().charge(energyPackage.getSize());

            if (numberOfPackagesShown >= 4) {
                return;
            }
            numberOfPackagesShown++;
        } else {
            System.out.println("no package");
        }


        gameTick(game);
    }

    /**
     * Generates a visual representation of the distance traveled by a car.
     *
     * @param maxDistance The maximum distance to represent.
     * @param achievedDistance The actual distance achieved.
     * @param numberOfLEDS The number of LEDs available for visualization.
     * @return A string representing the distance with dashes.
     */
    private static String getDistance(int maxDistance, int achievedDistance, int numberOfLEDS) {
        var rangePerLED = Math.max(1, achievedDistance / numberOfLEDS);
        var numberOfSteps = maxDistance / rangePerLED;
        return "- ".repeat(Math.max(0, numberOfSteps));
    }
}
