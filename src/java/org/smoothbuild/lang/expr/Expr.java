package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskWorker;

import com.google.common.collect.ImmutableList;

/**
 * Expression in smooth language.
 */
public abstract class Expr<T extends SValue> {
  private final SType<T> type;
  private final CodeLocation codeLocation;
  private final ImmutableList<? extends Expr<?>> dependencies;

  public Expr(SType<T> type, ImmutableList<? extends Expr<?>> dependencies,
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

  public ImmutableList<? extends Expr<?>> dependencies() {
    return dependencies;
  }

  public abstract TaskWorker<T> createWorker();
}
