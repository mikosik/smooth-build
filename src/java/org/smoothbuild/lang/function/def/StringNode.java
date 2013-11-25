package org.smoothbuild.lang.function.def;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.type.STypes.STRING;

import org.smoothbuild.lang.type.SString;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.StringTask;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public class StringNode extends Node {
  private final SString string;

  public StringNode(SString string, CodeLocation codeLocation) {
    super(STRING, codeLocation);
    this.string = checkNotNull(string);
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    return new StringTask(string, codeLocation());
  }
}
