package org.smoothbuild.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testory.Testory.mock;

import org.junit.Test;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.convert.Converter;
import org.smoothbuild.lang.expr.Expr;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.base.Message;

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
    ImmutableMap<String, SValue> map = Empty.stringValueMap();
  }

  @Test
  public void emptyStringValueMapAlwaysReturnsTheSameObject() {
    assertThat(Empty.stringValueMap()).isSameAs(Empty.stringValueMap());
  }

  // nameToFunctionMap();

  @Test
  public void emptyNameToFunctionMapIsEmpty() {
    assertThat(Empty.nameToFunctionMap()).isEmpty();
  }

  @Test
  public void emptyNameToFunctionMapIsImmutable() {
    @SuppressWarnings("unused")
    ImmutableMap<Name, Function<?>> map = Empty.nameToFunctionMap();
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
    ImmutableList<Expr<?>> list = Empty.nodeList();
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

  // typeToConverterMap()

  @Test
  public void typeToConverterMapIsEmpty() {
    assertThat(Empty.typeToConverterMap()).isEmpty();
  }

  @Test
  public void typeToConverterMapIsImmutable() {
    @SuppressWarnings("unused")
    ImmutableMap<SType<?>, Converter<?, ?>> map = Empty.typeToConverterMap();
  }

  @Test
  public void typeToConverterMapAlwaysReturnsTheSameObject() {
    assertThat(Empty.typeToConverterMap()).isSameAs(Empty.typeToConverterMap());
  }
}
