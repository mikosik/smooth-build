package org.smoothbuild.util;

import org.smoothbuild.function.base.Param;
import org.smoothbuild.task.Task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Empty {
  public static ImmutableMap<String, Task> stringTaskMap() {
    return ImmutableMap.of();
  }

  public static ImmutableMap<String, Object> stringObjectMap() {
    return ImmutableMap.of();
  }

  public static ImmutableMap<String, Param> stringParamMap() {
    return ImmutableMap.of();
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
