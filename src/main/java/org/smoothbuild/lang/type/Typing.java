package org.smoothbuild.lang.type;

import java.util.List;
import java.util.function.Supplier;

import org.smoothbuild.lang.type.api.Bounded;
import org.smoothbuild.lang.type.api.FuncT;
import org.smoothbuild.lang.type.api.Side;
import org.smoothbuild.lang.type.api.Sides;
import org.smoothbuild.lang.type.api.Type;
import org.smoothbuild.lang.type.api.TypeF;
import org.smoothbuild.lang.type.api.VarBounds;

import com.google.common.collect.ImmutableList;

public interface Typing<T extends Type> {
  public boolean contains(T type, T inner);

  public T inferCallResT(FuncT funcT, ImmutableList<T> argTs,
      Supplier<RuntimeException> illegalArgsExcThrower);

  public boolean isAssignable(T target, T source);

  public boolean isParamAssignable(T target, T source);

  public boolean inequal(T type1, T type2, Side side);

  public boolean inequalParam(T type1, T type2, Side side);

  public VarBounds<T> inferVarBoundsLower(List<? extends T> types1, List<? extends T> types2);

  public VarBounds<T> inferVarBounds(
      List<? extends T> types1, List<? extends T> types2, Side side);

  public VarBounds<T> inferVarBoundsLower(T type1, T type2);

  public VarBounds<T> inferVarBounds(T type1, T type2, Side side);

  public T mapVarsLower(T type, VarBounds<T> varBounds);

  public T mapVars(T type, VarBounds<T> varBounds, Side side);

  public T mergeUp(T type1, T type2);

  public T mergeDown(T type1, T type2);

  public T merge(T type1, T type2, Side direction);

  public Bounded<T> merge(Bounded<T> a, Bounded<T> b);

  public Sides<T> merge(Sides<T> bounds1, Sides<T> bounds2);

  public T rebuildComposed(T type, ImmutableList<T> covars, ImmutableList<T> contravars);

  public TypeF<T> typeF();
}
