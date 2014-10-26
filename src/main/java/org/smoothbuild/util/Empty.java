package org.smoothbuild.util;

import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.expr.Expr;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.base.Task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Empty {

  public static ImmutableMap<String, Value> stringValueMap() {
    return ImmutableMap.of();
  }

  public static ImmutableMap<String, Expr<?>> stringExprMap() {
    return ImmutableMap.of();
  }

  public static ImmutableMap<Name, Function<?>> nameFunctionMap() {
    return ImmutableMap.of();
  }

  public static ImmutableMap<Type<?>, Function<?>> typeFunctionMap() {
    return ImmutableMap.of();
  }

  public static ImmutableList<Param> paramList() {
    return ImmutableList.of();
  }

  public static ImmutableList<Value> valueList() {
    return ImmutableList.of();
  }

  public static ImmutableList<Expr<?>> exprList() {
    return ImmutableList.of();
  }

  public static ImmutableList<Message> messageList() {
    return ImmutableList.of();
  }

  public static ImmutableList<Task<?>> taskList() {
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
