package com.example.android.sggstiec;

public class User {

    private String firstName;
    private String middleName;
    private String lastName;
    private int year;
    private String email;
    private String regNo;

    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setMiddleNameName(String middleName) {this.middleName = middleName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public String getName() {
        return firstName + " " + middleName + " " + lastName;
    }

    public void setYear(int year) {this.year = year;}
    public int getYear() {return year;}

    public void setEmail(String email) {this.email = email;}
    public String getEmail() {return email;}

    public void setRegNo(String regNo) {this.regNo = regNo;}
    public String getRegNo() {return regNo;}
}
