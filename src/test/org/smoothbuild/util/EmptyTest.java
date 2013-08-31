package org.smoothbuild.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.smoothbuild.task.Task;

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
}
