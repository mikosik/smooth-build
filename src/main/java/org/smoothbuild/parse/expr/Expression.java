package org.smoothbuild.parse.expr;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.exec.task.base.BuildTask;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;

import com.google.common.collect.ImmutableList;

/**
 * Expression in smooth language.
 */
public abstract class Expression {
  private final ImmutableList<Expression> children;
  private final Location location;

  public Expression(Location location) {
    this(ImmutableList.of(), location);
  }

  public Expression(List<? extends Expression> children, Location location) {
    this.children = ImmutableList.copyOf(children);
    this.location = checkNotNull(location);
  }

  public List<BuildTask> childrenTasks(Scope<BuildTask> scope) {
    return map(children, ch -> ch.createTask(scope));
  }

  public Location location() {
    return location;
  }

  public abstract BuildTask createTask(Scope<BuildTask> scope);
}
