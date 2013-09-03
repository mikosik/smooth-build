package org.smoothbuild.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.task.Task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class EmptyTest {

  @Test
  public void emptyStringMapIsEmpty() {
    assertThat(Empty.stringTaskMap()).isEmpty();
  }

  @Test
  public void emptyStringTaskMapIsImmutable() {
    @SuppressWarnings("unused")
    ImmutableMap<String, Task> map = Empty.stringTaskMap();
  }

  @Test
  public void emptyStringObjectIsEmpty() {
    assertThat(Empty.stringObjectMap()).isEmpty();
  }

  @Test
  public void emptyStringObjectMapIsImmutable() {
    @SuppressWarnings("unused")
    ImmutableMap<String, Object> map = Empty.stringObjectMap();
  }

  @Test
  public void emptyStringParamIsEmpty() {
    assertThat(Empty.stringParamMap()).isEmpty();
  }

  @Test
  public void emptyStringParamMapIsImmutable() {
    @SuppressWarnings("unused")
    ImmutableMap<String, Param> map = Empty.stringParamMap();
  }

  @Test
  public void emptyTaskListIsEmpty() {
    assertThat(Empty.taskList()).isEmpty();
  }

  @Test
  public void emptyTaskListIsImmutable() {
    @SuppressWarnings("unused")
    ImmutableList<Task> list = Empty.taskList();
  }
}
