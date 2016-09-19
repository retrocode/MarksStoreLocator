package com.retrocode;

/**
 * Created by rhett on 2016-02-18.
 */

import static org.springframework.data.mongodb.core.index.GeoSpatialIndexType.*;

import lombok.Value;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Value
@Document
public class MarksStore {

    @Id UUID id = UUID.randomUUID();
    String name;
    String number;
    Address address;

    @Value
    public static class Address {

        String street, city, postal, province;
        @GeoSpatialIndexed(type = GEO_2DSPHERE) Point location;

        /*
         * (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return String.format("%s %s %s %s", street, postal, city, province);
        }
    }
}
