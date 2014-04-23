package org.smoothbuild.lang.function.def;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.expr.Expr;
import org.smoothbuild.lang.function.base.AbstractFunction;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskWorker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Smooth function defined in smooth language via smooth expression.
 * 
 * @see NativeFunction
 */
public class DefinedFunction<T extends SValue> extends AbstractFunction<T> {
  private final Expr<T> root;

  public DefinedFunction(Signature<T> signature, Expr<T> root) {
    super(signature);
    this.root = checkNotNull(root);
  }

  @Override
  public ImmutableList<? extends Expr<?>> dependencies(ImmutableMap<String, ? extends Expr<?>> args) {
    checkArgument(args.isEmpty(),
        "DefinedFunction.dependencies() cannot accept non-empty arguments");
    return root.dependencies();
  }

  @Override
  public TaskWorker<T> createWorker(ImmutableMap<String, ? extends Expr<?>> args,
      CodeLocation codeLocation) {
    checkArgument(args.isEmpty(),
        "DefinedFunction.createWorker() cannot accept non-empty arguments");
    return root.createWorker();
  }
}
