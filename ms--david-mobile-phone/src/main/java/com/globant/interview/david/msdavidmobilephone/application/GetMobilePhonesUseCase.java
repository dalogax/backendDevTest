package com.globant.interview.david.msdavidmobilephone.application;

import com.globant.interview.david.msdavidmobilephone.domain.MobilePhone;
import com.globant.interview.david.msdavidmobilephone.domain.MobilePhoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetMobilePhonesUseCase {

    private final MobilePhoneRepository repository;

    public GetMobilePhonesUseCase(MobilePhoneRepository repository) {
        this.repository = repository;
    }

    public List<MobilePhone> execute() {
        return repository.findAll();
    }
}
