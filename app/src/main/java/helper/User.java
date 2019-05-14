package helper;

public class User {
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String username = "";
    public String passwordhash = "";
    public Userid userid = new Userid();

    public static class Userid {
        public String address = "";
        public String dob = "";
        public String email = "";
        public String gender = "";
        public double height = 0;
        public double weight = 0;
        public int levelofactivity = 0;
        public String name = "";
        public String postcode = "";
        public int stepspermile = 0;
        public String surname = "";

        public Userid() {
        }

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

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
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
    }



}
