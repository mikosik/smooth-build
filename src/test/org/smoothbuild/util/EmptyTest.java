package org.smoothbuild.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.def.DefinitionNode;
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
  public void emptyStringMapAlwaysReturnsTheSameObject() {
    assertThat(Empty.stringTaskMap()).isSameAs(Empty.stringTaskMap());
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
  public void emptyStringObjectAlwaysReturnsTheSameObject() {
    assertThat(Empty.stringObjectMap()).isSameAs(Empty.stringObjectMap());
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
  public void emptyStringParamAlwaysReturnsTheSameObject() {
    assertThat(Empty.stringParamMap()).isSameAs(Empty.stringParamMap());
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

  @Test
  public void emptyTaskListAlwaysReturnsTheSameObject() {
    assertThat(Empty.taskList()).isSameAs(Empty.taskList());
  }

  @Test
  public void emptyDefinitionNodeListIsImmutable() {
    @SuppressWarnings("unused")
    ImmutableList<DefinitionNode> list = Empty.definitionNodeList();
  }

  @Test
  public void emptyDefinitionNOdeAlwaysReturnsTheSameObject() {
    assertThat(Empty.definitionNodeList()).isSameAs(Empty.definitionNodeList());
  }

  @Test
  public void nullIsChangedIntoEmptyIterable() throws Exception {
    assertThat(Empty.nullToEmpty(null)).isEmpty();
  }

  @Test
  public void nonNullIterableIsNotChanged() throws Exception {
    @SuppressWarnings("unchecked")
    Iterable<String> iterable = mock(Iterable.class);
    assertThat(Empty.nullToEmpty(iterable)).isSameAs(iterable);
  }
}
