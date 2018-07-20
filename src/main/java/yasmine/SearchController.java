package yasmine;

import javafx.util.Pair;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class SearchController {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @RequestMapping(value = "/1/queries/count/{date}", produces = "application/json")
    public AbstractMap.SimpleEntry count(@PathVariable("date") String date, HttpServletResponse response) {

        LocalDateTime[] localDateTime = datePrefix(date);
        if (localDateTime == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new AbstractMap.SimpleEntry("countr", 0);
        }

        List<Pair<LocalDateTime, String>> listOfSearches = getListOfSearches();

        long count = listOfSearches
                .stream()
                .filter(p -> (p.getKey().isAfter(localDateTime[0]) || p.getKey().isEqual(localDateTime[0])) && p.getKey().isBefore(localDateTime[1]))
                .map(p -> p.getValue())
                .distinct()
                .count();

        return new AbstractMap.SimpleEntry("count", count);
    }

    @RequestMapping(value = "/1/queries/popular/{date}", params = "size", produces = "application/json")
    public String popular(@PathVariable("date") String date, @RequestParam(value = "size") int size, HttpServletResponse response) {

        LocalDateTime[] localDateTime = datePrefix(date);
        if (localDateTime == null || size == 0 ) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "";
        }
        List<Pair<LocalDateTime, String>> listOfSearches = getListOfSearches();

        Map<String, Long> mapSearchesByCount = listOfSearches.stream()
                .filter(p -> (p.getKey().isAfter(localDateTime[0]) || p.getKey().isEqual(localDateTime[0])) && p.getKey().isBefore(localDateTime[1]))
                .map(p -> p.getValue().trim())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<String, Long> sortedMap = new LinkedHashMap<>();
        mapSearchesByCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(size)
                .forEachOrdered(e -> sortedMap.put(e.getKey(), e.getValue()));

        return "{ \"queries\": [" + sortedMap.entrySet().stream().map(e -> "{ \"query\": \"" + e.getKey() + "\", \"count\": " + e.getValue() + " }").collect(Collectors.joining(", ")) + "]}";
    }

    private LocalDateTime[] datePrefix(String datePrefix) {

        LocalDateTime sup = null, inf = null;
        if (datePrefix.matches("\\d{4}")) {
            sup = LocalDateTime.parse(datePrefix + "-01-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            inf = sup.plusYears(1);
        } else if (datePrefix.matches("\\d{4}-\\d{2}")) {
            sup = LocalDateTime.parse(datePrefix + "-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            inf = sup.plusMonths(1);
        } else if (datePrefix.matches("\\d{4}-\\d{2}-\\d{2}")) {
            sup = LocalDateTime.parse(datePrefix + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            inf = sup.plusDays(1);
        } else if (datePrefix.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}")) {
            sup = LocalDateTime.parse(datePrefix + ":00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            inf = sup.plusHours(1);
        } else if (datePrefix.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")) {
            sup = LocalDateTime.parse(datePrefix + ":00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            inf = sup.plusMinutes(1);
        } else if (datePrefix.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
            sup = LocalDateTime.parse(datePrefix, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            inf = sup.plusSeconds(1);
        } else {
            return null;
        }
        return (sup != null && inf != null) ? new LocalDateTime[]{sup, inf} : null;
    }

    private List<Pair<LocalDateTime, String>> getListOfSearches() {

        InputStream input = getClass().getResourceAsStream("/hn_logs.tsv");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));

        return bufferedReader.lines()
                .map(s -> {
                    String searchDate = s.substring(0, s.indexOf("\t")).trim();
                    String search = s.substring(s.indexOf("\t") + 1).trim();
                    return new Pair<>(LocalDateTime.parse(searchDate, formatter), search);
                })
                .collect(Collectors.toList());
    }
}
