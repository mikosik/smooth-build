package org.smoothbuild.function.base;

import java.util.Map;

import org.smoothbuild.function.expr.Expression;
import org.smoothbuild.function.expr.ExpressionIdFactory;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.exc.FunctionException;

import com.google.common.collect.ImmutableMap;

public abstract class AbstractFunction implements Function {
  private final FunctionSignature signature;

  public AbstractFunction(FunctionSignature signature) {
    this.signature = signature;
  }

  public FunctionSignature signature() {
    return signature;
  }

  public Type type() {
    return signature.type();
  }

  public FullyQualifiedName name() {
    return signature.name();
  }

  public ImmutableMap<String, Param> params() {
    return signature.params();
  }

  public abstract Expression apply(ExpressionIdFactory idFactory, Map<String, Expression> arguments);

  public abstract Object execute(Path resultDir, ImmutableMap<String, Object> arguments)
      throws FunctionException;
}
