package com.example.demo.shared.valueObject;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenericDTO<T> implements Serializable {
    protected List<I18nMessage> errorMessages = new ArrayList<>();
    protected List<I18nMessage> warningMessages = new ArrayList<>();
    protected List<I18nMessage> infoMessages = new ArrayList<>();

    public GenericDTO() {
    }

    public void addInfo(String key, Object... params) {
        this.infoMessages.add(new I18nMessage(key, Arrays.asList(params)));
    }

    public void addError(String key, Object... params) {
        this.errorMessages.add(new I18nMessage(key, Arrays.asList(params)));
    }

    public void addWarning(String key, Object... params) {
        this.warningMessages.add(new I18nMessage(key, Arrays.asList(params)));
    }

    public boolean hasErrors(){
        return !this.errorMessages.isEmpty();
    }

    public List<I18nMessage> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<I18nMessage> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public List<I18nMessage> getWarningMessages() {
        return warningMessages;
    }

    public void setWarningMessages(List<I18nMessage> warningMessages) {
        this.warningMessages = warningMessages;
    }

    public List<I18nMessage> getInfoMessages() {
        return infoMessages;
    }

    public void setInfoMessages(List<I18nMessage> infoMessages) {
        this.infoMessages = infoMessages;
    }

    public boolean hasObjectNotFoundException(){
        return this.getErrorMessages().stream().anyMatch((em) -> {
            return em.getKey().equalsIgnoreCase("general.objectnotfound");
        });
    }

}
