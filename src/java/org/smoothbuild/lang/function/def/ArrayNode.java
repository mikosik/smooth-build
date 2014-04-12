package org.smoothbuild.lang.function.def;

import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.ArrayTask;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class ArrayNode<T extends SValue> extends Node<SArray<T>> {
  private final SArrayType<T> arrayType;
  private final ImmutableList<? extends Node<T>> elements;

  public ArrayNode(SArrayType<T> arrayType, ImmutableList<? extends Node<T>> elements,
      CodeLocation codeLocation) {
    super(arrayType, codeLocation);
    this.arrayType = arrayType;
    this.elements = elements;
  }

  @Override
  public Task<SArray<T>> generateTask(TaskGenerator taskGenerator) {
    Builder<Result<T>> builder = ImmutableList.builder();
    for (Node<T> node : elements) {
      builder.add(taskGenerator.generateTask(node));
    }
    ImmutableList<Result<T>> dependencies = builder.build();
    return new ArrayTask<T>(arrayType, dependencies, codeLocation());
  }
}
