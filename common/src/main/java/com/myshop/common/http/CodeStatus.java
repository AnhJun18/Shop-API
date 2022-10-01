package com.myshop.common.http;

import org.springframework.http.HttpStatus;

public enum CodeStatus {
  UNAUTHORIZED(0, HttpStatus.UNAUTHORIZED),
  NOT_FOUND (404, HttpStatus.NOT_FOUND),
  BAD_REQUEST (400, HttpStatus.BAD_REQUEST),
  INTERNAL_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR),


  REQUEST_EXPIRED (410, HttpStatus.GONE),

  MISSING_PARAM(1000, HttpStatus.BAD_REQUEST),
  USERNAME_EMPTY(1001, HttpStatus.BAD_REQUEST, "Can not get phone or email of account"),

  PHONE_INVALID(2000, HttpStatus.BAD_REQUEST),
  PHONE_SMS_FAIL(2001, HttpStatus.BAD_REQUEST),
  PHONE_OTP_FAIL(2002, HttpStatus.BAD_REQUEST),
  PHONE_EXIST(2003, HttpStatus.BAD_REQUEST),
  PHONE_NOT_EXIST(2004, HttpStatus.NOT_FOUND),
  RATING_NOT_ENOUGH(2005, HttpStatus.BAD_REQUEST),

  CODE_NOT_EXIST(2014, HttpStatus.BAD_REQUEST),

  DATING_RATED(2015, HttpStatus.BAD_REQUEST),


  ;


  private int code;
  private HttpStatus status;
  private String message;

  CodeStatus(int code, HttpStatus status) {
    this.code = code;
    this.status = status;
  }

  CodeStatus(int code, HttpStatus status, String message) {
    this.code = code;
    this.status = status;
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }
}
