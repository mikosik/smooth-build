package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.task.base.StringTask;
import org.smoothbuild.task.base.Task;

public class StringNode extends AbstractDefinitionNode {
  private final StringValue string;

  public StringNode(StringValue string, CodeLocation codeLocation) {
    super(codeLocation);
    this.string = checkNotNull(string);
  }

  @Override
  public Type type() {
    return Type.STRING;
  }

  @Override
  public Task generateTask() {
    return new StringTask(string);
  }
}
