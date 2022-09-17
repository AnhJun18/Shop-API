package com.craw.common.utils;

public class Utils {
  public static String normalPhone(String phone){
    return phone.replaceAll("[^a-zA-Z0-9]+","");
  }
}
