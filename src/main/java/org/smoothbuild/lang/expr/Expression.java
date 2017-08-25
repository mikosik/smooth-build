package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.base.Computer;

import com.google.common.collect.ImmutableList;

/**
 * Expression in smooth language.
 */
public abstract class Expression {
  private final Type type;
  private final CodeLocation codeLocation;
  private final ImmutableList<Expression> dependencies;

  public Expression(Type type, List<Expression> dependencies, CodeLocation codeLocation) {
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

  public ImmutableList<Expression> dependencies() {
    return dependencies;
  }

  protected ImmutableList<Computer> createDependenciesComputers(ValuesDb valuesDb) {
    return dependencies
        .stream()
        .map(e -> e.createComputer(valuesDb))
        .collect(ImmutableList.toImmutableList());
  }

  public abstract Computer createComputer(ValuesDb valuesDb);
}
