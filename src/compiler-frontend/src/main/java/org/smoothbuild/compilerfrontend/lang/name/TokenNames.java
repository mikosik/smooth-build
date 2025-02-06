package org.smoothbuild.compilerfrontend.lang.name;

public class TokenNames {
  public static boolean isTypeVarName(String name) {
    return !name.isEmpty() && name.chars().allMatch(CharUtils::isUpperCase);
  }
}
