package com.toptal.travelplanner.controller.rest_api.parsers;

public interface IParser<T> {
    public T parseResponse(String response);
}
