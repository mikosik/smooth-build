package org.smoothbuild.common.log.location;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * This class is immutable.
 */
public record Location(CodeSource codeSource, int line) {
  public Location {
    checkArgument(0 <= line);
  }

  public String description() {
    if (codeSource instanceof UnknownSource) {
      return codeSource.description();
    } else {
      return codeSource.description() + ":" + line;
    }
  }

  @Override
  public String toString() {
    return description();
  }
}
