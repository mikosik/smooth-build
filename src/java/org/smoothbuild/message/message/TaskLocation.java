package org.smoothbuild.message.message;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.function.base.Name;

public class TaskLocation {
  private final Name name;
  private final CodeLocation codeLocation;

  public static TaskLocation taskLocation(Name name, CodeLocation codeLocation) {
    return new TaskLocation(name, codeLocation);
  }

  private TaskLocation(Name name, CodeLocation codeLocation) {
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
