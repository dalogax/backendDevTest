package com.globant.interview.david.msdavidmobilephone.domain;

public class MobilePhone {
    private final String id;
    private final String name;
    private final double price;
    private final boolean availability;

    public MobilePhone(String id, String name, double price, boolean availability) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.availability = availability;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public boolean isAvailability() {
        return availability;
    }
}
