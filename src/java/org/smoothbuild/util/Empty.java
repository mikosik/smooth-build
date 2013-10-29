package org.smoothbuild.util;

import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.base.Result;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Empty {

  public static ImmutableMap<String, Value> stringValueMap() {
    return ImmutableMap.of();
  }

  public static ImmutableMap<String, Result> stringTaskResultMap() {
    return ImmutableMap.of();
  }

  public static ImmutableList<DefinitionNode> definitionNodeList() {
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
