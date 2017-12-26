package com.enipro;

import com.enipro.model.DateTimeStringProcessor;

import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class UtilDateTest {

    @Test
    public void testUtilDate(){
        String date = "2017-9-21T04:23:36.792Z";

        org.joda.time.format.DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
        LocalDateTime localDate =  parser.parseDateTime(date).toLocalDateTime();

        assertEquals(12, DateTimeStringProcessor.process(localDate));
    }

    @Test
    public void testProcessDate(){
        String date = "2017-9-21T04:23:36.792Z";

        org.joda.time.format.DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
        LocalDateTime localDateTime =  parser.parseDateTime(date).toLocalDateTime();

        String processed = DateTimeStringProcessor.process(localDateTime);

    }
}