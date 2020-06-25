package org.smoothbuild.parse.expr;

import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.StringLiteralAlgorithm;
import org.smoothbuild.exec.task.base.NormalTask;
import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.object.type.StringType;

import com.google.common.collect.ImmutableList;

public class StringLiteralExpression extends Expression {
  private final StringType stringType;
  private final String string;

  public StringLiteralExpression(StringType stringType, String string, Location location) {
    super(location);
    this.stringType = stringType;
    this.string = string;
  }

  @Override
  public Task createTask(Scope<Task> scope) {
    Algorithm algorithm = new StringLiteralAlgorithm(stringType, string);
    return new NormalTask(algorithm, ImmutableList.of(), location(), true);
  }
}
