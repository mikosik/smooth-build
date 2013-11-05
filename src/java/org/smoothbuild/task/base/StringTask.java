package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.exec.SandboxImpl;

public class StringTask extends Task {
  private final StringValue string;

  public StringTask(StringValue string, CodeLocation codeLocation) {
    super("String", true, codeLocation);
    this.string = checkNotNull(string);
  }

  @Override
  public Value execute(SandboxImpl sandbox) {
    return string;
  }
}
