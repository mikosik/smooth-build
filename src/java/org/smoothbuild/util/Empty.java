package org.smoothbuild.util;

import org.smoothbuild.lang.convert.Converter;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.lang.type.SType;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.base.Result;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Empty {

  public static ImmutableMap<String, SValue> stringValueMap() {
    return ImmutableMap.of();
  }

  public static ImmutableMap<String, Result> stringTaskResultMap() {
    return ImmutableMap.of();
  }

  public static ImmutableMap<Name, Function> nameToFunctionMap() {
    return ImmutableMap.of();
  }

  public static ImmutableList<Node> nodeList() {
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

  public static ImmutableMap<SType<?>, Converter<?>> typeToConverterMap() {
    return ImmutableMap.of();
  }
}
