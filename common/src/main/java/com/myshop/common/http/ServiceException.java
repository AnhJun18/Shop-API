package com.myshop.common.http;

public class ServiceException extends RuntimeException {

  private CodeStatus codeStatus;

  public ServiceException(CodeStatus codeStatus, String message) {
    super(message);
    this.codeStatus = codeStatus;
  }

  public ServiceException(CodeStatus codeStatus) {
    super("");
    this.codeStatus = codeStatus;
  }

  public ServiceException(CodeStatus codeStatus, Throwable throwable) {
    super(throwable);
    this.codeStatus = codeStatus;
  }

  public ServiceException(CodeStatus codeStatus, String message, Throwable throwable) {
    super(message, throwable);
    this.codeStatus = codeStatus;
  }

  public CodeStatus getCodeStatus() {
    return codeStatus;
  }
}
