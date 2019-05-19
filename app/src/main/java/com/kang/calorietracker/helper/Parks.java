package com.kang.calorietracker.helper;

public class Parks {
    public Response response;
    public class Response {
        public Group[] groups;
        public class Group {
            public Item[] items;
            public class Item {
                public Venue venue;
                public class Venue {
                    public String name;
                    public Location location;
                    public class Location {
                        public String address;
                        public int distance;
                        public LabeledLatLng[] labeledLatLngs;
                        public class LabeledLatLng {
                            public double lat;
                            public double lng;
                        }
                    }
                }
            }
        }
    }
}
