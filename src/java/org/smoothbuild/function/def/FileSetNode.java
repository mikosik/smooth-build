package org.smoothbuild.function.def;

import static org.smoothbuild.function.base.Type.FILE_SET;

import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.FileSetTask;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class FileSetNode extends AbstractNode {
  private final ImmutableList<? extends Node> elements;

  public FileSetNode(ImmutableList<? extends Node> elements, CodeLocation codeLocation) {
    super(FILE_SET, codeLocation);
    this.elements = elements;
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    Builder<Result> builder = ImmutableList.builder();
    for (Node node : elements) {
      builder.add(taskGenerator.generateTask(node));
    }
    ImmutableList<Result> elementTasks = builder.build();
    return new FileSetTask(elementTasks, codeLocation());
  }
}
