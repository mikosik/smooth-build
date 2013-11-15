package org.smoothbuild.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.function.def.Node;
import org.smoothbuild.message.base.Message;
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

  // nameToFunctionMap();

  @Test
  public void emptyNameToFunctionMapIsEmpty() {
    assertThat(Empty.nameToFunctionMap()).isEmpty();
  }

  @Test
  public void emptyNameToFunctionMapIsImmutable() {
    @SuppressWarnings("unused")
    ImmutableMap<Name, Function> map = Empty.nameToFunctionMap();
  }

  @Test
  public void emptyNameToFunctionMapAlwaysReturnsTheSameObject() {
    assertThat(Empty.nameToFunctionMap()).isSameAs(Empty.nameToFunctionMap());
  }

  // nodeList()

  @Test
  public void nodeListIsEmpty() {
    assertThat(Empty.nodeList()).isEmpty();
  }

  @Test
  public void emptyNodeListIsImmutable() {
    @SuppressWarnings("unused")
    ImmutableList<Node> list = Empty.nodeList();
  }

  @Test
  public void emptyNodeAlwaysReturnsTheSameObject() {
    assertThat(Empty.nodeList()).isSameAs(Empty.nodeList());
  }

  // messageList()

  @Test
  public void messageListIsEmpty() {
    assertThat(Empty.messageList()).isEmpty();
  }

  @Test
  public void emptyMessageListIsImmutable() {
    @SuppressWarnings("unused")
    ImmutableList<Message> list = Empty.messageList();
  }

  @Test
  public void emptyMessageListAlwaysReturnsTheSameObject() {
    assertThat(Empty.messageList()).isSameAs(Empty.messageList());
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
