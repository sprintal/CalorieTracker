package helper;

public class GoogleSearchResult {
    public Items[] items;
    static public class Items {
        public Pagemap pagemap;
        static public class Pagemap {
            public Product[] product;
            static public class Product {
                public String image;
                public String description;
            }
        }
    }
}
