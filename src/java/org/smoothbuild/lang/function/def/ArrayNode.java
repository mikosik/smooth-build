package org.smoothbuild.lang.function.def;

import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.ArrayTask;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class ArrayNode extends Node {
  private final ArrayType<?> arrayType;
  private final ImmutableList<? extends Node> elements;

  public ArrayNode(ArrayType<?> arrayType, ImmutableList<? extends Node> elements,
      CodeLocation codeLocation) {
    super(arrayType, codeLocation);
    this.arrayType = arrayType;
    this.elements = elements;
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    Builder<Result> builder = ImmutableList.builder();
    for (Node node : elements) {
      builder.add(taskGenerator.generateTask(node));
    }
    ImmutableList<Result> dependencies = builder.build();
    return new ArrayTask(arrayType, dependencies, codeLocation());
  }
}
