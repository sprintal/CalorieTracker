package helper;


public class FoodSearchList {
    public List list;

    public class List {
        public String q;
        public String sr;
        public int start;
        public int end;
        public int total;
        public String group;
        public String sort;
        public Item[] item;

        public class Item {
            public int offset;
            public String group;
            public String name;
            public String ndbno;
            public String ds;
            public String manu;
        }

    }


}
