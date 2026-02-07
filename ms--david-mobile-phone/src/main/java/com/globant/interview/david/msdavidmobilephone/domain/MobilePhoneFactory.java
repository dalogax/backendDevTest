package com.globant.interview.david.msdavidmobilephone.domain;

public final class MobilePhoneFactory {

    public static MobilePhone create(String id, String name, double price, boolean availability) {
        return new MobilePhone(id, name, price, availability);
    }

    public static MobilePhone createIphone(String id, String name) {
        return new MobilePhone(id, name, 1199.00, true);
    }

    public static MobilePhone createSamsung(String id, String name) {
        return new MobilePhone(id, name, 1299.00, true);
    }

    public static MobilePhone createGoogle(String id, String name) {
        return new MobilePhone(id, name, 999.00, true);
    }

    public static MobilePhone createOnePlus(String id, String name) {
        return new MobilePhone(id, name, 799.00, false);
    }

    public static MobilePhone createXiaomi(String id, String name) {
        return new MobilePhone(id, name, 899.00, true);
    }
}
