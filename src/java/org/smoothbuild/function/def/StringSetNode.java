package org.smoothbuild.function.def;

import static org.smoothbuild.function.base.Type.STRING_SET;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.LocatedTask;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.StringSetTask;
import org.smoothbuild.task.exec.TaskGenerator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class StringSetNode extends AbstractNode {
  private final ImmutableList<? extends Node> elements;

  public StringSetNode(ImmutableList<? extends Node> elements, CodeLocation codeLocation) {
    super(codeLocation);
    this.elements = elements;
  }

  @Override
  public Type type() {
    return STRING_SET;
  }

  @Override
  public LocatedTask generateTask(TaskGenerator taskGenerator) {
    Builder<Result> builder = ImmutableList.builder();
    for (Node node : elements) {
      builder.add(taskGenerator.generateTask(node));
    }
    ImmutableList<Result> dependencies = builder.build();
    StringSetTask task = new StringSetTask(dependencies);
    return new LocatedTask(task, codeLocation());
  }
}
