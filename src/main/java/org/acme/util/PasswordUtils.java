package org.acme.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordUtils {

  public static String hash(String plainPassword) {
    return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
  }

  public static boolean verify(String plainPassword, String hashedPassword) {
    return BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword).verified;
  }
}
