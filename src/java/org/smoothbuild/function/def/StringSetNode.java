package org.smoothbuild.function.def;

import static org.smoothbuild.function.base.Type.STRING_SET;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.StringSetTask;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class StringSetNode implements Node {
  private final ImmutableList<? extends LocatedNode> elements;

  public StringSetNode(ImmutableList<? extends LocatedNode> elements) {
    this.elements = elements;
  }

  @Override
  public Type type() {
    return STRING_SET;
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    Builder<Result> builder = ImmutableList.builder();
    for (LocatedNode node : elements) {
      builder.add(taskGenerator.generateTask(node));
    }
    ImmutableList<Result> dependencies = builder.build();
    return new StringSetTask(dependencies);
  }
}
