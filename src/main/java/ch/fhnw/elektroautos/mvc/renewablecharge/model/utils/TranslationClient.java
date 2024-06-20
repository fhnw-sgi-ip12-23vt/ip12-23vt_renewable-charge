package ch.fhnw.elektroautos.mvc.renewablecharge.model.utils;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class TranslationClient {
    private ResourceBundle translations;
    private String language;

    public TranslationClient(String language) {
        switchLanguage(language);
    }

    /**
     * Retrieve a translation string by its key.
     *
     * @param translationPropName The key for the desired translation string
     * @return the translated string
     */
    public String get(String translationPropName) {
        return translations.getString(translationPropName);
    }

    /**
     * Switches the current language of the application and reloads the resource bundle.
     *
     * @param language The new language code (e.g., "de", "en")
     */
    public TranslationClient switchLanguage(String language) {
        this.language = language;
        Locale currentLocale = new Locale(language);
        this.translations = ResourceBundle.getBundle("translations.messages", currentLocale);
        return this;
    }

    public String getLanguage() {
        return language;
    }

    @Override
    public String toString() {
        return "TranslationClient{" +
                "translations=" + translations.getLocale().getDisplayName() +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(language, translations);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TranslationClient that = (TranslationClient) obj;
        return Objects.equals(language, that.language);
    }
}
