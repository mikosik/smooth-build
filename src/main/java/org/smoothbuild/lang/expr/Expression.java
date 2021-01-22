package org.smoothbuild.lang.expr;

import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableList;

/**
 * Expression in smooth language.
 */
public interface Expression {
  public Type type();

  public abstract <T> T visit(ExpressionVisitor<T> visitor) throws ExpressionVisitorException;

  public static ImmutableList<Type> toTypes(List<? extends Expression> expressions) {
    return map(expressions, Expression::type);
  }
}
