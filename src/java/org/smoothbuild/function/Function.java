package org.smoothbuild.function;

import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.type.Path;

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

  public FullyQualifiedName name() {
    return signature.name();
  }

  public Object execute(Path resultDir, ImmutableMap<String, Object> arguments)
      throws FunctionException {
    return functionInvoker.invoke(resultDir, arguments);
  }
}
