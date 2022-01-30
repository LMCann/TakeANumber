package com.takeanumber;

public class Person {

    int ticketNum;

    public int getGroupSize(){
        return groupSize;
    }

    public void setGroupSize(int groupSize){
        this.groupSize = groupSize;
    }

    public int getTicketNum() {
        return ticketNum;
    }

    public void setTicketNum(int ticketNum) {
        this.ticketNum = ticketNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    String name;
    String phoneNum;
    int groupSize;

    public Person(int ticketNum, String name, String phoneNum, int groupSize) {
        this.ticketNum = ticketNum;
        this.name = name;
        this.phoneNum = phoneNum;
        this.groupSize = groupSize;
    }
}
