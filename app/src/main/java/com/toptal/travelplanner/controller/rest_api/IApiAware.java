package com.toptal.travelplanner.controller.rest_api;

public interface IApiAware<T> {
    public void onGetResponse(T response);
}