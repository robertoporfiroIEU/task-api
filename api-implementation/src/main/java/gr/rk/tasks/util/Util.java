package gr.rk.tasks.util;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class Util {
    public static String getEndPointRelationURL(String endpointName) {
        return ServletUriComponentsBuilder.fromCurrentRequest().toUriString() + "/" + endpointName;
    }
}
