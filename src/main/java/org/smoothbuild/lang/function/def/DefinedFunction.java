package org.smoothbuild.lang.function.def;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import static org.smoothbuild.lang.function.base.Scope.scope;
import static org.smoothbuild.task.base.Evaluator.virtualEvaluator;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.function.base.AbstractFunction;
import org.smoothbuild.lang.function.base.Scope;
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

  @Override
  public Expression createCallExpression(List<Expression> args, boolean isGenerated,
      Location location) {
    checkArgument(!isGenerated);
    return new Expression(type(), asList(root), location) {
      @Override
      public Evaluator createEvaluator(ValuesDb valuesDb, Scope<Evaluator> scope) {
        Scope<Evaluator> functionScope = scope();
        for (int i = 0; i < args.size(); i++) {
          Evaluator evaluator = args.get(i).createEvaluator(valuesDb, scope);
          functionScope.add(parameters().get(i).name(), evaluator);
        }
        return virtualEvaluator(
            DefinedFunction.this, location(), createDependenciesEvaluator(valuesDb, functionScope));
      }
    };
  }
}
