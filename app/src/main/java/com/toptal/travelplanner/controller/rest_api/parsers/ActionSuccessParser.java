package com.toptal.travelplanner.controller.rest_api.parsers;

/**
 * Created by user on 24.10.2014.
 */
public class ActionSuccessParser implements IParser<Boolean> {

    private static final ActionSuccessParser instance = new ActionSuccessParser();

    private ActionSuccessParser() {}

    public static ActionSuccessParser getInstance() {
        return instance;
    }

    @Override
    public Boolean parseResponse(String response) {
        return true;
    }
}
