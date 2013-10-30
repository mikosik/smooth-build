package org.smoothbuild.message.message;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Location of given function call in smooth source code.
 */
public class CallLocation {
  private final String name;
  private final CodeLocation codeLocation;

  public static CallLocation callLocation(String name, CodeLocation codeLocation) {
    return new CallLocation(name, codeLocation);
  }

  protected CallLocation(String name, CodeLocation codeLocation) {
    this.name = checkNotNull(name);
    this.codeLocation = checkNotNull(codeLocation);
  }

  public String name() {
    return name;
  }

  public CodeLocation location() {
    return codeLocation;
  }
}
