package com.kang.calorietracker.helper;

public class GeoLocation {
    public ResourceSet[] resourceSets;
    public class ResourceSet {
        public Resource[] resources;
        public class Resource {
            public Point point;
            public class Point {
                public double[] coordinates;
            }
        }

    }
}
