package com.example.demo.shared.exceptions;

import com.example.demo.shared.valueObject.I18nMessage;

import java.util.*;

public class GenericException extends RuntimeException {
    private final List<I18nMessage> errorMessages = new ArrayList();

    public GenericException(){
    }

    public GenericException(String key, Object... params){
        super(key);
        this.addError(key,params);
    }

    public GenericException(Throwable t, String key, Object... params){
        super(key, t);
        this.addError(key,params);
    }

    /** @deprecated **/
    @Deprecated
    public Map<String, List<Object>> getErrorMap(){
        Map<String, List<Object>> map = new HashMap<>();
        Iterator iterator = this.errorMessages.iterator();

        while (iterator.hasNext()){
            I18nMessage msg = (I18nMessage) iterator.next();
            map.put(msg.getKey(), msg.getParams());
        }

        return map;
    }

    public void addErrors(Map<String, List<?>> errorMap){
        Iterator iterator = errorMap.entrySet().iterator();

        while (iterator.hasNext()){
            Map.Entry<String,List<?>>  e = (Map.Entry) iterator.next();
            this.addError((String) e.getKey(), e.getValue());
        }
    }

    public void addError(String key, Object... params){
        this.errorMessages.add(new I18nMessage(key, Arrays.asList(params)));
    }

    public List<I18nMessage> getErrorMessages(){return this.errorMessages;}

    public Boolean containsError(String key){return this.errorMessages.contains(new I18nMessage(key));}
}
