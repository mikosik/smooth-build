package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.smoothbuild.lang.base.Location;

import com.google.common.collect.ImmutableList;

/**
 * Expression in smooth language.
 */
public abstract class Expression {
  private final ImmutableList<Expression> children;
  private final Location location;

  public Expression(Location location) {
    this(ImmutableList.of(), location);
  }

  public Expression(List<? extends Expression> children, Location location) {
    this.children = ImmutableList.copyOf(children);
    this.location = checkNotNull(location);
  }

  public ImmutableList<Expression> children() {
    return children;
  }

  public Location location() {
    return location;
  }

  public abstract <T> T visit(ExpressionVisitor<T> visitor);
}
