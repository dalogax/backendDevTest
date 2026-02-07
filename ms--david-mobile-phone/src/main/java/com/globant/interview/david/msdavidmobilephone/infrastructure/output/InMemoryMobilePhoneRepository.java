package com.globant.interview.david.msdavidmobilephone.infrastructure.output;

import com.globant.interview.david.msdavidmobilephone.domain.MobilePhone;
import com.globant.interview.david.msdavidmobilephone.domain.MobilePhoneRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InMemoryMobilePhoneRepository implements MobilePhoneRepository {

    @Override
    public List<MobilePhone> findAll() {
        return List.of(
            new MobilePhone("1", "iPhone 15 Pro", 1199.00, true),
            new MobilePhone("2", "Samsung Galaxy S24 Ultra", 1299.00, true),
            new MobilePhone("3", "Google Pixel 8 Pro", 999.00, true),
            new MobilePhone("4", "OnePlus 12", 799.00, false),
            new MobilePhone("5", "Xiaomi 14 Pro", 899.00, true)
        );
    }
}
