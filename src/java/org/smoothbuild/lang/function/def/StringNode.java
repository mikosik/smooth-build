package org.smoothbuild.lang.function.def;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.base.STypes.STRING;

import org.smoothbuild.lang.base.SString;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.StringTask;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public class StringNode extends Node<SString> {
  private final SString string;

  public StringNode(SString string, CodeLocation codeLocation) {
    super(STRING, codeLocation);
    this.string = checkNotNull(string);
  }

  @Override
  public Task<SString> generateTask(TaskGenerator taskGenerator) {
    return new StringTask(string, codeLocation());
  }
}
