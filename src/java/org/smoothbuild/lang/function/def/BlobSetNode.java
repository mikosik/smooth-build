package org.smoothbuild.lang.function.def;

import static org.smoothbuild.lang.function.base.Type.BLOB_SET;

import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.BlobSetTask;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class BlobSetNode extends Node {
  private final ImmutableList<? extends Node> elements;

  public BlobSetNode(ImmutableList<? extends Node> elements, CodeLocation codeLocation) {
    super(BLOB_SET, codeLocation);
    this.elements = elements;
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    Builder<Result> builder = ImmutableList.builder();
    for (Node node : elements) {
      builder.add(taskGenerator.generateTask(node));
    }
    ImmutableList<Result> elementTasks = builder.build();
    return new BlobSetTask(elementTasks, codeLocation());
  }
}
