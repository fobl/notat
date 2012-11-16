package com.drivesync.dto;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.joda.time.LocalDateTime;

import java.io.IOException;

import static org.joda.time.DateTimeFieldType.*;

public class LocalDateTimeDeserializer extends TypeAdapter<LocalDateTime>{

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.get(year())+":"+value.get(monthOfYear())+":"+value.get(dayOfMonth())+":"+value.get(hourOfDay())+":"
                +value.get(minuteOfHour())+":"+value.get(secondOfMinute())+":"+value.get(millisOfSecond()));
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String[] s = in.nextString().split(":");

        return new LocalDateTime(Integer.valueOf(s[0]), Integer.valueOf(s[1]),Integer.valueOf(s[2]),
                Integer.valueOf(s[3]),Integer.valueOf(s[4]),Integer.valueOf(s[5]),Integer.valueOf(s[6]));
    }
}
