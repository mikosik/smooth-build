package org.smoothbuild.lang.base.type;

import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;

public interface TestingT<T extends Type> {
  public Typing<T> typing();
  public ImmutableList<T> typesForBuildWideGraph();
  public ImmutableList<T> elementaryTypes();
  public ImmutableList<T> allTestedTypes();
  public T array(T elemT);

  public T func(T resT, ImmutableList<T> params);
  public T any();
  public T blob();
  public T bool();
  public T int_();
  public T nothing();
  public T string();
  public boolean isStructSupported();
  public T struct();
  public boolean isTupleSupported();
  public T tuple();
  public T tuple(ImmutableList<T> items);
  public T a();
  public T b();
  public T x();

  public Side<T> lower();
  public Side<T> upper();
  public Bounds<T> oneSideBound(Side<T> side, T type);
}
