package org.smoothbuild.lang.expr;

import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableList;

/**
 * Expression in smooth language.
 */
public interface Expression {
  public Type type();

  public Location location();

  public abstract <C, T> T visit(C context, ExpressionVisitor<C, T> visitor);

  public static ImmutableList<Type> toTypes(List<? extends Expression> expressions) {
    return map(expressions, Expression::type);
  }
}
