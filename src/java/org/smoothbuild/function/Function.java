package org.smoothbuild.function;

import java.util.Map;

import org.smoothbuild.expression.CallExpression;
import org.smoothbuild.expression.Expression;
import org.smoothbuild.expression.ExpressionId;
import org.smoothbuild.expression.ExpressionIdFactory;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.exc.FunctionException;

import com.google.common.collect.ImmutableMap;

public class Function {
  private final FunctionSignature signature;
  private final FunctionInvoker functionInvoker;

  public Function(FunctionSignature signature, FunctionInvoker functionInvoker) {
    this.signature = signature;
    this.functionInvoker = functionInvoker;
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

  public Expression apply(ExpressionIdFactory idFactory, Map<String, Expression> arguments) {
    ExpressionId id = idFactory.createId(name().full());
    return new CallExpression(id, this, arguments);
  }

  public Object execute(Path resultDir, ImmutableMap<String, Object> arguments)
      throws FunctionException {
    return functionInvoker.invoke(resultDir, arguments);
  }
}
