package org.smoothbuild.util;

import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.expr.Expr;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.message.base.Message;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Empty {

  public static ImmutableMap<String, SValue> stringValueMap() {
    return ImmutableMap.of();
  }

  public static ImmutableMap<String, Expr<?>> stringExprMap() {
    return ImmutableMap.of();
  }

  public static ImmutableMap<Name, Function<?>> nameFunctionMap() {
    return ImmutableMap.of();
  }

  public static ImmutableMap<SType<?>, Function<?>> typeFunctionMap() {
    return ImmutableMap.of();
  }

  public static ImmutableList<Param> paramList() {
    return ImmutableList.of();
  }

  public static ImmutableList<SValue> svalueList() {
    return ImmutableList.of();
  }

  public static ImmutableList<Expr<?>> exprList() {
    return ImmutableList.of();
  }

  public static ImmutableList<Message> messageList() {
    return ImmutableList.of();
  }

  public static <T> Iterable<T> nullToEmpty(Iterable<T> iterable) {
    if (iterable == null) {
      return ImmutableList.of();
    } else {
      return iterable;
    }
  }
}
