package fr.Nat0uille.NatMOTD;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimerMOTD {

    private static final Pattern TIMER_PATTERN = Pattern.compile("\\{timer:([a-zA-Z0-9_-]+)\\}");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String replaceTimers(String text, Function<String, String> endLookup, Function<String, String> expiredLookup) {
        if (text == null || !text.contains("{timer:")) return text;

        Matcher matcher = TIMER_PATTERN.matcher(text);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            result.append(text, lastEnd, matcher.start());
            result.append(resolveTimer(matcher.group(1), endLookup, expiredLookup));
            lastEnd = matcher.end();
        }
        result.append(text.substring(lastEnd));
        return result.toString();
    }

    private static String resolveTimer(String name, Function<String, String> endLookup, Function<String, String> expiredLookup) {
        String end = endLookup.apply(name);
        if (end == null || end.isBlank()) return "";

        try {
            LocalDateTime endTime = LocalDateTime.parse(end, DATE_FORMAT);
            Duration remaining = Duration.between(LocalDateTime.now(), endTime);

            if (remaining.isNegative() || remaining.isZero()) {
                String expired = expiredLookup.apply(name);
                return (expired != null && !expired.isBlank()) ? expired : "00:00:00";
            }

            long days = remaining.toDays();
            long hours = remaining.toHoursPart();
            long minutes = remaining.toMinutesPart();
            long seconds = remaining.toSecondsPart();

            StringBuilder sb = new StringBuilder();
            if (days > 0) sb.append(days).append("j ");
            if (days > 0 || hours > 0) sb.append(String.format("%02dh ", hours));
            sb.append(String.format("%02dm %02ds", minutes, seconds));
            return sb.toString().trim();
        } catch (Exception e) {
            return "";
        }
    }
}
