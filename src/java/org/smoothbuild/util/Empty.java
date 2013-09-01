package org.smoothbuild.util;

import org.smoothbuild.function.base.Param;
import org.smoothbuild.task.Task;

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
}
