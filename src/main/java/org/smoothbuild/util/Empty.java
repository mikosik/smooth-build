package org.smoothbuild.util;

import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.base.Task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Empty {

  public static ImmutableMap<String, Value> stringValueMap() {
    return ImmutableMap.of();
  }

  public static ImmutableMap<String, Expression> stringExpressionMap() {
    return ImmutableMap.of();
  }

  public static ImmutableMap<Name, Function> nameFunctionMap() {
    return ImmutableMap.of();
  }

  public static ImmutableMap<Type, Function> typeFunctionMap() {
    return ImmutableMap.of();
  }

  public static ImmutableList<Parameter> paramList() {
    return ImmutableList.of();
  }

  public static ImmutableList<Value> valueList() {
    return ImmutableList.of();
  }

  public static ImmutableList<Expression> expressionList() {
    return ImmutableList.of();
  }

  public static ImmutableList<Message> messageList() {
    return ImmutableList.of();
  }

  public static ImmutableList<Task> taskList() {
    return ImmutableList.of();
  }
}
