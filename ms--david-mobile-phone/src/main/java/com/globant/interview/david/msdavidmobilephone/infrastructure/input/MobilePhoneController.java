package com.globant.interview.david.msdavidmobilephone.infrastructure.entry;

import com.globant.interview.david.msdavidmobilephone.application.GetMobilePhonesUseCase;
import com.globant.interview.david.msdavidmobilephone.domain.MobilePhone;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mobilephones")
public class MobilePhoneController {

    private final GetMobilePhonesUseCase getMobilePhonesUseCase;

    public MobilePhoneController(GetMobilePhonesUseCase getMobilePhonesUseCase) {
        this.getMobilePhonesUseCase = getMobilePhonesUseCase;
    }

    @GetMapping
    public List<MobilePhone> getAll() {
        return getMobilePhonesUseCase.execute();
    }
}
