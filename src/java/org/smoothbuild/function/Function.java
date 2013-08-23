package org.smoothbuild.function;

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

  public Object execute(Path resultDir, ImmutableMap<String, Object> arguments)
      throws FunctionException {
    return functionInvoker.invoke(resultDir, arguments);
  }
}
