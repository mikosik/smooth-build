package org.smoothbuild.lang.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.function.base.Type;
import org.smoothbuild.lang.function.value.StringValue;
import org.smoothbuild.message.base.CodeLocation;
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
