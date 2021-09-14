package com.levent.mqtt11.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Request<T> {
    @JsonProperty("Data")
    public T data;
    @JsonProperty("RequestType")
    public int requestType;
}
