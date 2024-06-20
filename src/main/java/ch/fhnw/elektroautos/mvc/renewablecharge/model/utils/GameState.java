package ch.fhnw.elektroautos.mvc.renewablecharge.model.utils;

public enum GameState {
    /**
     * The game is currently in the setup phase.
     * (Language selection)
     */
    SETUP_LANGUAGE,

    /**
     * The game is currently in the setup phase.
     * (Tutorial screen)
     */
    TUTORIAL,

    /**
     * The game is currently in the setup phase.
     * (the second Tutorial screen)
     */
    TUTORIAL_SCREEN_TWO,

    /**
     * The game is currently in the tutorial phase.
     * (Tutorial screen three)
     */
    TUTORIAL_SCREEN_THREE,

    /**
     * The game is currently in the setup phase.
     * (Start screen)
     */
    SETUP_START,

    /**
     * The game is currently in the lobby phase.
     * (Car selection)
     */
    LOBBY,

    /**
     * The game is currently running.
     * (Gameplay)
     */
    RUNNING,

    /**
     * The race is about to start.
     * (Race)
     */
    RACE_COUNTDOWN,

    /**
     * The race is currently running.
     */
    RACE_RUNNING,

    /**
     * The game is currently in the result phase.
     * (Result)
     */
    RESULT,

    /**
     * The game is currently in the shutdown phase.
     * (Game is shutting down)
     */
    SHUTDOWN
}
