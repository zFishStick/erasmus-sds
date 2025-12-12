package com.sds2.classes.response;

public record LoginResponse(boolean success, String errorMessage, String redirectUrl) {}
