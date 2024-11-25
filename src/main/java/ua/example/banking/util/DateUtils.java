package ua.example.banking.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateUtils {

    public static OffsetDateTime timestampToOffsetDateTime(Timestamp timestamp) {
        return Objects.isNull(timestamp) ? null : OffsetDateTime.of(timestamp.toLocalDateTime(), ZoneOffset.UTC);
    }

    public static Timestamp nowTimestamp() {
        return Timestamp.valueOf(nowUTC().toLocalDateTime());
    }

    public static OffsetDateTime nowUTC() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }
}
