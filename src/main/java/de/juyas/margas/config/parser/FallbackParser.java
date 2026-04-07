package de.juyas.margas.config.parser;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.ConfigValueReader;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Class FallbackParser to try 2 parsers and show an error message if both fail.
 *
 * @param <T> the type produced by the reader
 */
public class FallbackParser<T> implements ConfigValueReader<T> {

    /**
     * The 1st parser to try.
     */
    private final ConfigValueReader<T> firstParser;

    /**
     * The 2nd parser to try.
     */
    private final ConfigValueReader<T> secondParser;

    /**
     * The error message to show if both parsers fail.
     */
    private final String errorMessage;

    /**
     * Creates a new instance of FallbackParser for the given parsers.
     *
     * @param firstParser  1st parser to try
     * @param secondParser 2nd parser to try
     * @param errorMessage error message to show if both parsers fail
     */
    public FallbackParser(final ConfigValueReader<T> firstParser, final ConfigValueReader<T> secondParser, final String errorMessage) {
        this.firstParser = firstParser;
        this.secondParser = secondParser;
        this.errorMessage = errorMessage;
    }

    @Override
    @SuppressWarnings("PMD.PreserveStackTrace")
    public T read(final ConfigurationSection section, final String path) throws MargasException {
        try {
            return firstParser.read(section, path);
        } catch (final MargasException e) {
            try {
                return secondParser.read(section, path);
            } catch (final MargasException ex) {
                throw new MargasException("Failed fallback: '%s' / '%s'%n => %s".formatted(e.getMessage(), ex.getMessage(), errorMessage), ex);
            }
        }
    }
}
