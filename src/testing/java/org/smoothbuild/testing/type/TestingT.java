package org.smoothbuild.testing.type;

import org.smoothbuild.lang.type.Typing;
import org.smoothbuild.lang.type.api.Side;
import org.smoothbuild.lang.type.api.Sides;
import org.smoothbuild.lang.type.api.Type;
import org.smoothbuild.lang.type.api.VarSet;
import org.smoothbuild.lang.type.api.VarT;

import com.google.common.collect.ImmutableList;

public interface TestingT<T extends Type> {
  public Typing<T> typing();
  public ImmutableList<T> typesForBuildWideGraph();
  public ImmutableList<T> elementaryTypes();
  public ImmutableList<T> allTestedTypes();
  public T array(T elemT);

  public T func(VarSet<T> tParams, T resT, ImmutableList<T> params);
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
  public T varA();
  public T varB();
  public T varX();
  public T varY();
  public VarSet<T> vs(VarT... elements);

  public Sides<T> oneSideBound(Side side, T type);
}
