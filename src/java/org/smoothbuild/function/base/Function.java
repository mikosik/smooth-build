package org.smoothbuild.function.base;

import java.util.Map;

import org.smoothbuild.expression.Expression;
import org.smoothbuild.expression.ExpressionIdFactory;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.exc.FunctionException;

import com.google.common.collect.ImmutableMap;

public interface Function {
  public FunctionSignature signature();

  public Type type();

  public FullyQualifiedName name();

  public ImmutableMap<String, Param> params();

  public Expression apply(ExpressionIdFactory idFactory, Map<String, Expression> arguments);

  public Object execute(Path resultDir, ImmutableMap<String, Object> arguments)
      throws FunctionException;
}
