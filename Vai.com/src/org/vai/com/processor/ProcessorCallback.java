package org.vai.com.processor;

public interface ProcessorCallback {

    void send(int resultCode);

    /** send resultCode with custom error code from server */
    void send(int resultCode, String... strings);

}