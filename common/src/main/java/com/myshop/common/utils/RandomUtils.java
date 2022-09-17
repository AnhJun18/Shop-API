package com.craw.common.utils;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import java.nio.charset.Charset;
import java.util.Random;

public class RandomUtils {

  private static final char[] NUMBER= "0123456789".toCharArray();

  public static String generateRandomNumber(int length) {
    return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NUMBER, length);
  }

  public static double randomRatingValue() {
    Random rand = new Random();
    int val = rand.nextInt(50);
    return (double)val/10;
  }

  public static int randomGender() {
    Random rand = new Random();
    int val = rand.nextInt(3);
    return val;
  }

  public static String getAlphaNumericString(int n) {
    // length is bounded by 256 Character
    byte[] array = new byte[256];
    new Random().nextBytes(array);

    String randomString
            = new String(array, Charset.forName("UTF-8"));

    // Create a StringBuffer to store the result
    StringBuffer r = new StringBuffer();

    // remove all spacial char
    String AlphaNumericString
            = randomString
            .replaceAll("[^A-Za-z0-9]", "");

    // Append first 20 alphanumeric characters
    // from the generated random String into the result
    for (int k = 0; k < AlphaNumericString.length(); k++) {

      if (Character.isLetter(AlphaNumericString.charAt(k))
              && (n > 0)
              || Character.isDigit(AlphaNumericString.charAt(k))
              && (n > 0)) {

        r.append(AlphaNumericString.charAt(k));
        n--;
      }
    }

    // return the resultant string
    return r.toString();
  }


}
