package org.smoothbuild.lang.base;


public interface ArrayBuilder<T extends SValue> {

  public ArrayBuilder<T> add(T elem);

  public Array<T> build();

}
