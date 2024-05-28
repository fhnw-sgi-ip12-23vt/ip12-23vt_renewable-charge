package ch.fhnw.elektroautos.mvc.renewablecharge.model.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class TranslationClient {
    private ResourceBundle translations;

    public TranslationClient(String language) {
        switchLanguage(language);
    }

    /**
     * Retrieve a translation string by its key.
     *
     * @param   translationPropName     The key for the desired translation string
     * @return  the translated string
     */
    public String get(String translationPropName){
        return translations.getString(translationPropName);
    }

    /**
     * Switches the current language of the application and reloads the resource bundle.
     * @param   language    The new language code (e.g., "de", "en")
     */
    public TranslationClient switchLanguage(String language) {
        Locale currentLocale = new Locale(language);
        this.translations = ResourceBundle.getBundle("translations.messages", currentLocale);
        return this;
    }

    @Override
    public String toString() {
        return "TranslationClient{" +
                "translations=" + translations.getLocale().getDisplayName() +
                '}';
    }
}
