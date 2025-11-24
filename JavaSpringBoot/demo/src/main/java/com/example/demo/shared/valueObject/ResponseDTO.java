package com.example.demo.shared.valueObject;


public class ResponseDTO<T> extends GenericDTO<T> {
    private T data;
    public ResponseDTO(){
    }

    public ResponseDTO(T data){this.data=data;}

    public T getData(){return this.data;}

    public void setData(T data) {
        this.data = data;
    }
}
