package org.smoothbuild.lang.function.def;

import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.ArrayWorker;
import org.smoothbuild.task.base.TaskWorker;

import com.google.common.collect.ImmutableList;

public class ArrayNode<T extends SValue> extends Node<SArray<T>> {
  private final SArrayType<T> arrayType;

  public ArrayNode(SArrayType<T> arrayType, ImmutableList<? extends Node<T>> elements,
      CodeLocation codeLocation) {
    super(arrayType, elements, codeLocation);
    this.arrayType = arrayType;
  }

  @Override
  public TaskWorker<SArray<T>> createWorker() {
    return new ArrayWorker<T>(arrayType, codeLocation());
  }
}
