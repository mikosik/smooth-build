package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.type.SString;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.SandboxImpl;

public class StringTask extends Task {
  private final SString string;

  public StringTask(SString string, CodeLocation codeLocation) {
    super("String", true, codeLocation);
    this.string = checkNotNull(string);
  }

  @Override
  public SValue execute(SandboxImpl sandbox) {
    return string;
  }
}
