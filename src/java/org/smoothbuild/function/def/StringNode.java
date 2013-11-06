package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.task.base.StringTask;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public class StringNode extends Node {
  private final StringValue string;

  public StringNode(StringValue string, CodeLocation codeLocation) {
    super(Type.STRING, codeLocation);
    this.string = checkNotNull(string);
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    return new StringTask(string, codeLocation());
  }
}
