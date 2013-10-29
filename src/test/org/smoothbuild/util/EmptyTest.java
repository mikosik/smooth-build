package org.smoothbuild.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.base.Result;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class EmptyTest {

  // stringValueMap()

  @Test
  public void emptyStringValueMapIsEmpty() {
    assertThat(Empty.stringValueMap()).isEmpty();
  }

  @Test
  public void emptyStringValueMapIsImmutable() {
    @SuppressWarnings("unused")
    ImmutableMap<String, Value> map = Empty.stringValueMap();
  }

  @Test
  public void emptyStringValueMapAlwaysReturnsTheSameObject() {
    assertThat(Empty.stringValueMap()).isSameAs(Empty.stringValueMap());
  }

  // stringTaskResultMap()

  @Test
  public void emptyStringTaskResultMapIsEmpty() {
    assertThat(Empty.stringTaskResultMap()).isEmpty();
  }

  @Test
  public void emptyStringTaskResultMapIsImmutable() {
    @SuppressWarnings("unused")
    ImmutableMap<String, Result> map = Empty.stringTaskResultMap();
  }

  @Test
  public void emptyStringTaskResultMapAlwaysReturnsTheSameObject() {
    assertThat(Empty.stringTaskResultMap()).isSameAs(Empty.stringTaskResultMap());
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
