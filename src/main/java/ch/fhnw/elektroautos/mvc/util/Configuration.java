package ch.fhnw.elektroautos.mvc.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private static final Properties props = new Properties();
    private static final Object lock = new Object();
    private static volatile boolean loaded = false;

    private static void load() {
        synchronized (lock) {
            if (!loaded) {
                try (InputStream stream = Configuration.class.getClassLoader().getResourceAsStream("app.properties")) {
                    if (stream == null) {
                        throw new IllegalArgumentException("Configuration file 'app.properties' not found in the classpath.");
                    }
                    props.load(stream);
                    loaded = true;
                } catch (IOException e) {
                    throw new IllegalStateException("Failed to load configuration properties.", e);
                }
            }
        }
    }

    /**
     * Get a property value from the configuration file.
     *
     * @param key the property key
     * @return the property value
     */
    public static String get(String key) {
        if (!loaded) {
            load();
        }
        return props.getProperty(key);
    }

    /**
     * Generic method to get a property value with automatic type conversion.
     *
     * @param <T>       the type of the property value
     * @param key       the property key to retrieve
     * @param typeClass the class of the type T
     * @return the property value or the default value if the key is not found
     */
    public static <T> T get(String key, Class<T> typeClass) {
        String value = get(key);

        if (value == null) {
            return null;
        }

        return castValue(value, typeClass);
    }

    /**
     * Generic method to get a property value with automatic type conversion.
     *
     * @param <T>          the type of the property value
     * @param key          the property key to retrieve
     * @param defaultValue the default value to return if the key is not found
     * @param typeClass    the class of the type T
     * @return the property value or the default value if the key is not found
     */
    public static <T> T get(String key, T defaultValue, Class<T> typeClass) {
        String value = get(key);
        if (value == null) {
            return defaultValue;
        }

        return castValue(value, typeClass);
    }

    private static <T> T castValue(String value, Class<T> typeClass) {
        try {
            if (typeClass == Integer.class) {
                return typeClass.cast(Integer.parseInt(value));
            } else if (typeClass == Double.class) {
                return typeClass.cast(Double.parseDouble(value));
            } else if (typeClass == Float.class) {
                return typeClass.cast(Float.parseFloat(value));
            } else if (typeClass == Boolean.class) {
                return typeClass.cast(Boolean.parseBoolean(value));
            } else if (typeClass == Long.class) {
                return typeClass.cast(Long.parseLong(value));
            } else if (typeClass == Short.class) {
                return typeClass.cast(Short.parseShort(value));
            } else {
                return typeClass.cast(value);
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing number: " + e.getMessage());
            return null;  // Or handle this situation more gracefully depending on your needs
        } catch (ClassCastException e) {
            System.err.println("Invalid cast operation: " + e.getMessage());
            return null;  // Or handle this situation more gracefully depending on your needs
        }
    }
}
