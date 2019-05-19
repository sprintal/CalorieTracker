package com.kang.calorietracker.data;

import android.app.Application;

public class LoginUser extends Application {
    private int id;
    private String passwordhash;
    private Userid userid;
    private String username;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPasswordhash() {
        return passwordhash;
    }

    public void setPasswordhash(String passwordhash) {
        this.passwordhash = passwordhash;
    }

    public Userid getUserid() {
        return userid;
    }

    public void setUserid(Userid userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public class Userid {
        private String address;
        private String dob;
        private String email;
        private String gender;
        private double height;
        private int id;
        private int levelofactivity;
        private String name;
        private String postcode;
        private int stepspermile;
        private String surname;
        private double weight;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public double getHeight() {
            return height;
        }

        public void setHeight(double height) {
            this.height = height;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getLevelofactivity() {
            return levelofactivity;
        }

        public void setLevelofactivity(int levelofactivity) {
            this.levelofactivity = levelofactivity;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPostcode() {
            return postcode;
        }

        public void setPostcode(String postcode) {
            this.postcode = postcode;
        }

        public int getStepspermile() {
            return stepspermile;
        }

        public void setStepspermile(int stepspermile) {
            this.stepspermile = stepspermile;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
