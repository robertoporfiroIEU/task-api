package gr.rk.tasks.util;

import gr.rk.tasks.exception.InvalidSortFieldException;
import gr.rk.tasks.exception.i18n.I18nErrorMessage;
import org.springframework.data.domain.Sort;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Util {

    private Util() {}

    public static String getEndPointRelationURL(String endpointName) {
        String serverPath = ServletUriComponentsBuilder.fromCurrentRequest().toUriString().split("[?]")[0] + "/";
        String[] endpoint = endpointName.split("/");
        if (serverPath.contains(endpoint[0].toString())) {
            return serverPath + endpoint[1].toString();
        }
        return serverPath + endpointName;
    }

    public static String toDateISO8601WithTimeZone(LocalDateTime localDateTime) {
        if (Objects.isNull(localDateTime)) {
            return null;
        }

        localDateTime = localDateTime.atZone(ZoneOffset.UTC).toLocalDateTime();
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }

    public static LocalDateTime toLocalDateTimeFromISO8601WithTimeZone(String dateTime) {
        if (Objects.isNull(dateTime)) {
            return null;
        }
        return ZonedDateTime.parse(dateTime).toLocalDateTime();
    }

    public static String getOrderByStatement(String entityVariable, Sort sort, Map<String, String> applicableSortFieldsMap) throws InvalidSortFieldException {
        if (Objects.isNull(sort) || Objects.isNull(applicableSortFieldsMap) || applicableSortFieldsMap.size() == 0) {
            return "";
        }

        Optional<Sort.Order> optionalOrder = sort.stream().findFirst();
        if (optionalOrder.isEmpty()){
            return "";
        }

        Sort.Order order = optionalOrder.get();

        if (!applicableSortFieldsMap.keySet().contains(order.getProperty())) {
            throw new InvalidSortFieldException(I18nErrorMessage.INVALID_SORT_FIELD);
        }

        entityVariable += ".";
        String orderProperty = applicableSortFieldsMap.get(order.getProperty());

        return " ORDER BY " + entityVariable + orderProperty + " " + order.getDirection();
    }
}
