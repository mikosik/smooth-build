package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.TaskWorker;

import com.google.common.collect.ImmutableList;

/**
 * Expression in smooth language.
 */
public abstract class Expression<T extends Value> {
  private final Type<T> type;
  private final CodeLocation codeLocation;
  private final ImmutableList<? extends Expression<?>> dependencies;

  public Expression(Type<T> type, ImmutableList<? extends Expression<?>> dependencies,
      CodeLocation codeLocation) {
    this.type = checkNotNull(type);
    this.dependencies = checkNotNull(dependencies);
    this.codeLocation = checkNotNull(codeLocation);
  }

  public Type<T> type() {
    return type;
  }

  public CodeLocation codeLocation() {
    return codeLocation;
  }

  public ImmutableList<? extends Expression<?>> dependencies() {
    return dependencies;
  }

  public abstract TaskWorker<T> createWorker();
}
