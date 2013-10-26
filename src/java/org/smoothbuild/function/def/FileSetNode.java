package org.smoothbuild.function.def;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.FileSetTask;
import org.smoothbuild.task.base.Task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class FileSetNode implements DefinitionNode {
  private final ImmutableList<? extends DefinitionNode> elements;
  private final CodeLocation codeLocation;

  public FileSetNode(ImmutableList<? extends DefinitionNode> elements, CodeLocation codeLocation) {
    this.elements = elements;
    this.codeLocation = codeLocation;
  }

  @Override
  public Type type() {
    return Type.FILE_SET;
  }

  @Override
  public Task generateTask() {
    Builder<Task> builder = ImmutableList.builder();
    for (DefinitionNode node : elements) {
      builder.add(node.generateTask());
    }
    ImmutableList<Task> elementTasks = builder.build();
    return new FileSetTask(elementTasks, codeLocation);
  }

}
