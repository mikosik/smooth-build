package org.smoothbuild.io.fs.match;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.io.fs.match.Constants.DOUBLE_STAR;
import static org.smoothbuild.io.fs.match.Constants.SINGLE_STAR;
import static org.smoothbuild.io.fs.match.Constants.SINGLE_STAR_CHAR;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Pattern for matching directory/file name.
 * 
 * <ul>
 * <li>Each star '*' in pattern matches zero or more characters.</li>
 * <li>Double stars "**" are forbidden unless whole pattern equals to "**". Such
 * pattern matches one or more whole directory/file names</li>
 * </ul>
 */
public class NamePattern {
  private final String value;

  public static NamePattern namePattern(String value) {
    return new NamePattern(value);
  }

  private NamePattern(String value) {
    checkArgument(value.indexOf('/') == -1, "NamePattern cannot contain slash.");
    checkArgument(0 < value.length(), "Pattern length can't be zero.");
    checkArgument(!value.contains(DOUBLE_STAR) || value.length() == DOUBLE_STAR.length());
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

  public ImmutableList<String> parts() {
    if (isDoubleStar()) {
      return ImmutableList.of(DOUBLE_STAR);
    }

    Builder<String> builder = ImmutableList.builder();
    int startIndex = 0;
    while (startIndex < value.length()) {
      int foundIndex = value.indexOf(SINGLE_STAR_CHAR, startIndex);
      if (foundIndex == -1) {
        builder.add(value.substring(startIndex));
        startIndex = value.length();
      } else {
        if (foundIndex != startIndex) {
          builder.add(value.substring(startIndex, foundIndex));
        }
        builder.add(SINGLE_STAR);
        startIndex = foundIndex + 1;
      }
    }
    return builder.build();
  }
}
