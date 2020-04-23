package org.smoothbuild.builtin.file.match;

import static org.smoothbuild.builtin.file.match.Constants.DOUBLE_STAR;
import static org.smoothbuild.builtin.file.match.Constants.SINGLE_STAR;
import static org.smoothbuild.builtin.file.match.Constants.SINGLE_STAR_CHAR;
import static org.smoothbuild.util.Lists.list;

import java.util.ArrayList;
import java.util.List;

/**
 * Pattern for matching dir/file name.
 *
 * <ul>
 * <li>Each star '*' in pattern matches zero or more characters.</li>
 * <li>Double stars "**" are forbidden unless whole pattern equals to "**". Such pattern matches one
 * or more whole dir/file names</li>
 * </ul>
 */
public class NamePattern {
  private final String value;

  public static NamePattern namePattern(String value) {
    return new NamePattern(value);
  }

  private NamePattern(String value) {
    if (!(value.indexOf('/') == -1)) {
      throw new IllegalArgumentException("NamePattern cannot contain slash.");
    }
    if (!(0 < value.length())) {
      throw new IllegalArgumentException("Pattern length can't be zero.");
    }
    if (!(!value.contains(DOUBLE_STAR) || value.length() == DOUBLE_STAR.length())) {
      throw new IllegalArgumentException();
    }
    this.value = value;
  }

  public String value() {
    return value;
  }

  public boolean hasStars() {
    return value.indexOf(SINGLE_STAR_CHAR) != -1;
  }

  public boolean isSingleStar() {
    return value.equals(SINGLE_STAR);
  }

  public boolean isDoubleStar() {
    return value.equals(DOUBLE_STAR);
  }

  public List<String> parts() {
    if (isDoubleStar()) {
      return list(DOUBLE_STAR);
    }

    List<String> result = new ArrayList<>();
    int startIndex = 0;
    while (startIndex < value.length()) {
      int foundIndex = value.indexOf(SINGLE_STAR_CHAR, startIndex);
      if (foundIndex == -1) {
        result.add(value.substring(startIndex));
        startIndex = value.length();
      } else {
        if (foundIndex != startIndex) {
          result.add(value.substring(startIndex, foundIndex));
        }
        result.add(SINGLE_STAR);
        startIndex = foundIndex + 1;
      }
    }
    return result;
  }
}
