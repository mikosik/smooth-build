package org.smoothbuild.lang.base;


public interface ArrayBuilder<T extends Value> {

  public ArrayBuilder<T> add(T elem);

  public Array<T> build();

}
