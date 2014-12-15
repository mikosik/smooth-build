package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.smoothbuild.lang.base.Type;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.TaskWorker;

import com.google.common.collect.ImmutableList;

/**
 * Expression in smooth language.
 */
public abstract class Expression {
  private final Type type;
  private final CodeLocation codeLocation;
  private final ImmutableList<? extends Expression> dependencies;

  public Expression(Type type, List<? extends Expression> dependencies, CodeLocation codeLocation) {
    this.type = checkNotNull(type);
    this.dependencies = ImmutableList.copyOf(dependencies);
    this.codeLocation = checkNotNull(codeLocation);
  }

  public Type type() {
    return type;
  }

  public CodeLocation codeLocation() {
    return codeLocation;
  }

  public ImmutableList<? extends Expression> dependencies() {
    return dependencies;
  }

  public abstract TaskWorker createWorker();
}
