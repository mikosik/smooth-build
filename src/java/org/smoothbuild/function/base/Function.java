package org.smoothbuild.function.base;

import java.util.Map;

import org.smoothbuild.function.expr.Expression;
import org.smoothbuild.function.expr.ExpressionIdFactory;

import com.google.common.collect.ImmutableMap;

public interface Function {
  public Signature signature();

  public Type type();

  public QualifiedName name();

  public ImmutableMap<String, Param> params();

  public Expression apply(ExpressionIdFactory idFactory, Map<String, Expression> arguments);
}
