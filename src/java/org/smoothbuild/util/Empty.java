package org.smoothbuild.util;

import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.task.base.Task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class Empty {

  public static ImmutableMap<String, HashCode> stringHashMap() {
    return ImmutableMap.of();
  }

  public static ImmutableMap<String, Object> stringObjectMap() {
    return ImmutableMap.of();
  }

  public static ImmutableList<HashCode> hashCodeList() {
    return ImmutableList.of();
  }

  public static ImmutableList<DefinitionNode> definitionNodeList() {
    return ImmutableList.of();
  }

  public static ImmutableList<Task> taskList() {
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
