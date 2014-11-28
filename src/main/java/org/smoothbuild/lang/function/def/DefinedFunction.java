package org.smoothbuild.lang.function.def;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.function.base.AbstractFunction;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.NativeFunction;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Smooth function defined in smooth language via smooth expression.
 *
 * @see NativeFunction
 */
public class DefinedFunction<T extends Value> extends AbstractFunction<T> {
  private final Expression<T> root;

  public DefinedFunction(Signature<T> signature, Expression<T> root) {
    super(signature);
    this.root = checkNotNull(root);
  }

  @Override
  public ImmutableList<? extends Expression<?>> dependencies(
      ImmutableMap<String, ? extends Expression<?>> args) {
    checkArgument(args.isEmpty(),
        "DefinedFunction.dependencies() cannot accept non-empty arguments");
    return ImmutableList.of(root);
  }
}
