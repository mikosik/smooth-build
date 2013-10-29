package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.function.base.Name.simpleName;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.task.base.StringTask;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public class StringNode extends AbstractDefinitionNode {
  private final StringValue string;

  public StringNode(StringValue string, CodeLocation codeLocation) {
    super(CallLocation.callLocation(simpleName("string"), codeLocation));
    this.string = checkNotNull(string);
  }

  @Override
  public Type type() {
    return Type.STRING;
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    return new StringTask(string);
  }
}
