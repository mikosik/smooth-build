package org.smoothbuild.lang.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.def.err.CannotCreateTaskWorkerFromInvalidNodeError;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskWorker;
import org.smoothbuild.util.Empty;

public class InvalidNode<T extends SValue> extends Node<T> {
  private final SType<T> type;

  public InvalidNode(SType<T> type, CodeLocation codeLocation) {
    super(type, Empty.nodeList(), codeLocation);
    this.type = checkNotNull(type);
  }

  @Override
  public SType<T> type() {
    return type;
  }

  @Override
  public TaskWorker<T> createWorker() {
    throw new CannotCreateTaskWorkerFromInvalidNodeError();
  }
}
