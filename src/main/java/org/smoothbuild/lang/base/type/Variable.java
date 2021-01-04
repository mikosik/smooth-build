package org.smoothbuild.lang.base.type;

/**
 * Type variable.
 *
 * This class is immutable.
 */
public class Variable extends Type {
  public Variable(String name) {
    super(name, new TypeConstructor(name), true);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
