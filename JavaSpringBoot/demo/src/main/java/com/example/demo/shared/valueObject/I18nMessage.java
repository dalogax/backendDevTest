package com.example.demo.shared.valueObject;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class I18nMessage implements Serializable {
    private String key;
    private List<Object> params;
    private String message;

    public I18nMessage(){
        this.key="";
        this.params= Collections.emptyList();
    }

    public I18nMessage(String key, List<Object> params){
        this.key=key;
        this.params=params;
    }

    public I18nMessage(String key){
        this.key=key;
        this.params=Collections.emptyList();
    }

    public I18nMessage(String key, List<Object> params, String translatedMessage){
        this.key = key;
        this.params = params;
        this.message = translatedMessage;
    }

    public String getKey() {return this.key;}

    public void setKey(String key) {
        this.key = key;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int hashCode(){
        int result = 1;
        result = 31 * result + (this.key== null ? 0:this.key.hashCode());
        return result;
    }

    public boolean equals(Object obj){
        if(this == obj){
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof I18nMessage)) {
            return false;
        }else {
            I18nMessage other = (I18nMessage) obj;
            if(this.key == null) {
                if(other.getKey() != null){
                    return false;
                }
            } else if (!this.key.equals(other.getKey())) {
                return false;
            }
            return true;
        }
    }
}
