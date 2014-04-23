package org.smoothbuild.lang.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskWorker;

import com.google.common.collect.ImmutableList;

public abstract class Node<T extends SValue> {
  private final SType<T> type;
  private final CodeLocation codeLocation;
  private final ImmutableList<? extends Node<?>> dependencies;

  public Node(SType<T> type, ImmutableList<? extends Node<?>> dependencies,
      CodeLocation codeLocation) {
    this.type = checkNotNull(type);
    this.dependencies = checkNotNull(dependencies);
    this.codeLocation = checkNotNull(codeLocation);
  }

  public SType<T> type() {
    return type;
  }

  public CodeLocation codeLocation() {
    return codeLocation;
  }

  public ImmutableList<? extends Node<?>> dependencies() {
    return dependencies;
  }

  public abstract TaskWorker<T> createWorker();
}
