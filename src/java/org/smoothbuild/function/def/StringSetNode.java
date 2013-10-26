package org.smoothbuild.function.def;

import static org.smoothbuild.function.base.Type.STRING_SET;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.StringSetTask;
import org.smoothbuild.task.base.Task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class StringSetNode implements DefinitionNode {
  private final ImmutableList<? extends DefinitionNode> elements;
  private final CodeLocation codeLocation;

  public StringSetNode(ImmutableList<? extends DefinitionNode> elements, CodeLocation codeLocation) {
    this.elements = elements;
    this.codeLocation = codeLocation;
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
    return new StringSetTask(dependencies, codeLocation);
  }
}
