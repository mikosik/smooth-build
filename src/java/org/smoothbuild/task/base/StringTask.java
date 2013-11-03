package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.exec.SandboxImpl;

public class StringTask implements Task {
  private final StringValue string;

  public StringTask(StringValue string) {
    this.string = checkNotNull(string);
  }

  @Override
  public String name() {
    return "String";
  }

  @Override
  public boolean isInternal() {
    return true;
  }

  @Override
  public Value execute(SandboxImpl sandbox) {
    return string;
  }
}
