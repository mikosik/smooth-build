package org.smoothbuild.message.message;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.function.base.Name;

/**
 * Location of given function call in smooth source code.
 */
public class CallLocation {
  private final Name name;
  private final CodeLocation codeLocation;

  public static CallLocation callLocation(Name name, CodeLocation codeLocation) {
    return new CallLocation(name, codeLocation);
  }

  private CallLocation(Name name, CodeLocation codeLocation) {
    this.name = checkNotNull(name);
    this.codeLocation = checkNotNull(codeLocation);
  }

  public Name name() {
    return name;
  }

  public CodeLocation location() {
    return codeLocation;
  }
}
