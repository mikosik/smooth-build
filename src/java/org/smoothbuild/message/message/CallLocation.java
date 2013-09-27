package org.smoothbuild.message.message;

import org.smoothbuild.function.base.Name;

public class CallLocation {
  private final Name name;
  private final CodeLocation codeLocation;

  public CallLocation(Name name, CodeLocation codeLocation) {
    this.name = name;
    this.codeLocation = codeLocation;
  }

  public Name name() {
    return name;
  }

  public CodeLocation location() {
    return codeLocation;
  }
}
