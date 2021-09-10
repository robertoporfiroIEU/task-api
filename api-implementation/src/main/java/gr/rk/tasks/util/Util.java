package gr.rk.tasks.util;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Util {
    public static String getEndPointRelationURL(String endpointName) {
        return ServletUriComponentsBuilder.fromCurrentRequest().toUriString().split("[?]")[0] + "/" + endpointName;
    }

    public static String toDateISO8601WithTimeZone(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }
}
