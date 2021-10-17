package gr.rk.tasks.util;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;

public class Util {

    private Util() {}

    public static String getEndPointRelationURL(String endpointName) {
        return ServletUriComponentsBuilder.fromCurrentRequest().toUriString().split("[?]")[0] + "/" + endpointName;
    }

    public static String toDateISO8601WithTimeZone(LocalDateTime localDateTime) {
        if (Objects.isNull(localDateTime)) {
            return null;
        }
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }

    public static LocalDateTime toLocalDateTimeFromISO8601WithTimeZone(String dateTime) {
        if (Objects.isNull(dateTime)) {
            return null;
        }
        return ZonedDateTime.parse(dateTime).toLocalDateTime();
    }

    public static Long getIdFromIdentifier(String identifier) {
        // The format of the identifier is <prefixIdentifier>-<number>
        if (Objects.isNull(identifier)) {
            return null;
        }

        String []identifierParts = identifier.split("-");
        try {
            return Long.parseLong(identifierParts[identifierParts.length - 1]);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    public static String getPrefixIdentifierFromIdentifier(String identifier) {
        // The format of the identifier is <prefixIdentifier>-<number>
        if (Objects.isNull(identifier)) {
            return null;
        }

        String []identifierParts = identifier.split("-");
        return String.join("-", Arrays.copyOfRange(identifierParts, 0, identifierParts.length-1));
    }
}
