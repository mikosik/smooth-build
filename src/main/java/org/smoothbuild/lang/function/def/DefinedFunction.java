package org.smoothbuild.lang.function.def;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import static org.smoothbuild.task.base.Computer.virtualComputer;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.function.base.AbstractFunction;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.task.base.Computer;

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
      CodeLocation codeLocation) {
    checkArgument(args.isEmpty());
    checkArgument(!isGenerated);
    return new Expression(type(), asList(root), codeLocation) {
      public Computer createComputer(ValuesDb valuesDb) {
        return virtualComputer(DefinedFunction.this, codeLocation());
      }
    };
  }
}
