package org.smoothbuild.lang.type.api;

public interface Bounded<T extends Type> {
  public Var var();
  public Sides<T> bounds();
}
