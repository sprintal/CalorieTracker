package com.kang.calorietracker.helper;

public class FoodSearchListEdamam {
    public Hints[] hints;

    public class Hints {
        public Food food;

        public class Food {
            public String foodId;
            public String label;
            public Nutrients nutrients;
            public String category;
            public String categoryLabel;
            public String image;

            public class Nutrients {
                public double ENERC_KCAL;
//                public double PROCNT;
                public double FAT;
//                public double CHOCDF;
//                public double FIBTG;
            }
        }
    }
}