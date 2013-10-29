package org.smoothbuild.function.def;

import static org.smoothbuild.function.base.Type.STRING_SET;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.StringSetTask;
import org.smoothbuild.task.base.Task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class StringSetNode extends AbstractDefinitionNode {
  private final ImmutableList<? extends DefinitionNode> elements;

  public StringSetNode(ImmutableList<? extends DefinitionNode> elements, CodeLocation codeLocation) {
    super(codeLocation);
    this.elements = elements;
  }

  @Override
  public Type type() {
    return STRING_SET;
  }

  @Override
  public Task generateTask() {
    Builder<Task> builder = ImmutableList.builder();
    for (DefinitionNode node : elements) {
      builder.add(node.generateTask());
    }
    ImmutableList<Task> dependencies = builder.build();
    return new StringSetTask(dependencies, codeLocation());
  }
}
