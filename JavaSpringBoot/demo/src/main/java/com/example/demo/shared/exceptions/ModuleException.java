package com.example.demo.shared.exceptions;

public class ModuleException extends GenericException {
    public ModuleException(){
    }

    public ModuleException(String key, Object... params){super(key,params);}

    public ModuleException(Throwable t, String key, Object... params){super(key,params);}
}
