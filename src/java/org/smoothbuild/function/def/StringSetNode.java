package org.smoothbuild.function.def;

import static org.smoothbuild.function.base.Type.STRING_SET;
import static org.smoothbuild.task.base.Constants.SET_TASK_NAME;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.StringSetTask;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class StringSetNode extends AbstractDefinitionNode {
  private final ImmutableList<? extends DefinitionNode> elements;

  public StringSetNode(ImmutableList<? extends DefinitionNode> elements, CodeLocation codeLocation) {
    super(CallLocation.callLocation(SET_TASK_NAME, codeLocation));
    this.elements = elements;
  }

  @Override
  public Type type() {
    return STRING_SET;
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    Builder<Result> builder = ImmutableList.builder();
    for (DefinitionNode node : elements) {
      builder.add(taskGenerator.generateTask(node));
    }
    ImmutableList<Result> dependencies = builder.build();
    return new StringSetTask(dependencies);
  }
}
