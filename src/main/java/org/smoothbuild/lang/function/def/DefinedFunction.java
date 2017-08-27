package org.smoothbuild.lang.function.def;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import static org.smoothbuild.task.base.Evaluator.virtualEvaluator;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.function.base.AbstractFunction;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.task.base.Evaluator;

/**
 * Smooth function defined in smooth language via smooth expression.
 *
 * @see NativeFunction
 */
public class DefinedFunction extends AbstractFunction {
  private final Expression root;

  public DefinedFunction(Signature signature, Expression root) {
    super(signature);
    this.root = checkNotNull(root);
  }

  public Expression root() {
    return root;
  }

  public Expression createCallExpression(List<Expression> args, boolean isGenerated,
      Location location) {
    checkArgument(args.isEmpty());
    checkArgument(!isGenerated);
    return new Expression(type(), asList(root), location) {
      public Evaluator createEvaluator(ValuesDb valuesDb) {
        return virtualEvaluator(
            DefinedFunction.this, location(), createDependenciesEvaluator(valuesDb));
      }
    };
  }
}
