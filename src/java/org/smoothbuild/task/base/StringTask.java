package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.plugin.Value;

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
  public Value execute(Sandbox sandbox) {
    return string;
  }
}
