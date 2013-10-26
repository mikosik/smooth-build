package org.smoothbuild.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.task.base.Task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class EmptyTest {

  // stringHashMap()

  @Test
  public void emptyStringHashMapIsEmpty() {
    assertThat(Empty.stringHashMap()).isEmpty();
  }

  @Test
  public void emptyStringHashMapIsImmutable() {
    @SuppressWarnings("unused")
    ImmutableMap<String, HashCode> map = Empty.stringHashMap();
  }

  @Test
  public void emptyStringHashMapAlwaysReturnsTheSameObject() {
    assertThat(Empty.stringHashMap()).isSameAs(Empty.stringHashMap());
  }

  // stringObjectMap()

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

  // hashCodeList()

  @Test
  public void emptyHashCodeListIsEmpty() {
    assertThat(Empty.hashCodeList()).isEmpty();
  }

  @Test
  public void emptyHashCodeListIsImmutable() {
    @SuppressWarnings("unused")
    ImmutableList<HashCode> list = Empty.hashCodeList();
  }

  @Test
  public void emptyHashCodeListAlwaysReturnsTheSameObject() {
    assertThat(Empty.hashCodeList()).isSameAs(Empty.hashCodeList());
  }

  // definitionNodeList()

  @Test
  public void definitionNodeListIsEmpty() {
    assertThat(Empty.definitionNodeList()).isEmpty();
  }

  @Test
  public void emptyDefinitionNodeListIsImmutable() {
    @SuppressWarnings("unused")
    ImmutableList<DefinitionNode> list = Empty.definitionNodeList();
  }

  @Test
  public void emptyDefinitionNodeAlwaysReturnsTheSameObject() {
    assertThat(Empty.definitionNodeList()).isSameAs(Empty.definitionNodeList());
  }

  // taskList()

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

  // nullToEmpty

  @Test
  public void nullIsChangedIntoEmptyIterable() throws Exception {
    assertThat(Empty.nullToEmpty(null)).isEmpty();
  }

  @Test
  public void nullToEmptyDoesNotChangeNonNullIterable() throws Exception {
    @SuppressWarnings("unchecked")
    Iterable<String> iterable = mock(Iterable.class);
    assertThat(Empty.nullToEmpty(iterable)).isSameAs(iterable);
  }
}
