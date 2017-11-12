package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.task.base.Evaluator.valueEvaluator;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.function.base.Scope;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.util.Dag;

public class StringLiteralExpression extends Expression {
  private final String string;

  public StringLiteralExpression(String string, Location location) {
    super(STRING, location);
    this.string = string;
  }

  @Override
  public Dag<Evaluator> createEvaluator(List<Dag<Expression>> children, ValuesDb valuesDb,
      Scope<Dag<Evaluator>> scope) {
    checkArgument(children.size() == 0);
    return new Dag<>(valueEvaluator(valuesDb.string(string), location()));
  }
}
