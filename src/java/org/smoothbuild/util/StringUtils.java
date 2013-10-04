package org.smoothbuild.util;

import com.google.common.base.Preconditions;

public class StringUtils {
  public static int countOf(String string, char character) {
    Preconditions.checkNotNull(string);

    int result = 0;

    int index = 0;
    index = string.indexOf(character);
    while (index != -1) {
      result++;
      index = string.indexOf(character, index + 1);
    }

    return result;
  }
}
