package org.smoothbuild.function.def;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.FileSetTask;
import org.smoothbuild.task.Task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class FileSetNode implements DefinitionNode {
  private final ImmutableList<? extends DefinitionNode> elemNodes;
  private final CodeLocation codeLocation;

  public FileSetNode(ImmutableList<? extends DefinitionNode> elemNodes, CodeLocation codeLocation) {
    this.elemNodes = elemNodes;
    this.codeLocation = codeLocation;
  }

  @Override
  public Type type() {
    return Type.FILE_SET;
  }

  @Override
  public Task generateTask() {
    Builder<Task> builder = ImmutableSet.builder();
    for (DefinitionNode node : elemNodes) {
      builder.add(node.generateTask());
    }
    return new FileSetTask(builder.build(), codeLocation);
  }

}
