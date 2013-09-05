package org.smoothbuild.function.def;

import static org.smoothbuild.function.base.Type.STRING_SET;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.task.StringSetTask;
import org.smoothbuild.task.Task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class StringSetNode implements DefinitionNode {
  private final ImmutableList<? extends DefinitionNode> elemNodes;

  public StringSetNode(ImmutableList<? extends DefinitionNode> elemNodes) {
    this.elemNodes = elemNodes;
  }

  @Override
  public Type type() {
    return STRING_SET;
  }

  @Override
  public Task generateTask() {
    Builder<Task> builder = ImmutableSet.builder();
    for (DefinitionNode node : elemNodes) {
      builder.add(node.generateTask());
    }
    return new StringSetTask(builder.build());
  }

}
