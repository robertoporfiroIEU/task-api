package gr.rk.tasks.util;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
}
