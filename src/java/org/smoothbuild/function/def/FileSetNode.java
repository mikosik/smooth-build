package org.smoothbuild.function.def;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.task.base.FileSetTask;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class FileSetNode implements Node {
  private final ImmutableList<? extends LocatedNode> elements;

  public FileSetNode(ImmutableList<? extends LocatedNode> elements) {
    this.elements = elements;
  }

  @Override
  public Type type() {
    return Type.FILE_SET;
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    Builder<Result> builder = ImmutableList.builder();
    for (LocatedNode node : elements) {
      builder.add(taskGenerator.generateTask(node));
    }
    ImmutableList<Result> elementTasks = builder.build();
    return new FileSetTask(elementTasks);
  }
}
