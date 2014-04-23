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
  public void empty_string_value_map_is_empty() {
    assertThat(Empty.stringValueMap()).isEmpty();
  }

  @Test
  public void empty_string_value_map_is_immutable() {
    @SuppressWarnings("unused")
    ImmutableMap<String, SValue> map = Empty.stringValueMap();
  }

  @Test
  public void empty_string_value_map_always_returns_the_same_object() {
    assertThat(Empty.stringValueMap()).isSameAs(Empty.stringValueMap());
  }

  // stringExprMap()

  @Test
  public void empty_string_expr_map_is_empty() {
    assertThat(Empty.stringExprMap()).isEmpty();
  }

  @Test
  public void empty_string_expr_map_is_immutable() {
    @SuppressWarnings("unused")
    ImmutableMap<String, Expr<?>> map = Empty.stringExprMap();
  }

  @Test
  public void empty_string_expr_map_always_returns_the_same_object() {
    assertThat(Empty.stringExprMap()).isSameAs(Empty.stringExprMap());
  }

  // nameFunctionMap();

  @Test
  public void empty_name_function_map_is_empty() {
    assertThat(Empty.nameFunctionMap()).isEmpty();
  }

  @Test
  public void empty_name_function_map_is_immutable() {
    @SuppressWarnings("unused")
    ImmutableMap<Name, Function<?>> map = Empty.nameFunctionMap();
  }

  @Test
  public void empty_name_function_map_always_returns_the_same_object() {
    assertThat(Empty.nameFunctionMap()).isSameAs(Empty.nameFunctionMap());
  }

  // exprList()

  @Test
  public void empty_expr_list_is_empty() {
    assertThat(Empty.exprList()).isEmpty();
  }

  @Test
  public void empty_expr_list_is_immutable() {
    @SuppressWarnings("unused")
    ImmutableList<Expr<?>> list = Empty.exprList();
  }

  @Test
  public void empty_expr_list_always_returns_the_same_object() {
    assertThat(Empty.exprList()).isSameAs(Empty.exprList());
  }

  // messageList()

  @Test
  public void empty_message_list_is_empty() {
    assertThat(Empty.messageList()).isEmpty();
  }

  @Test
  public void empty_message_list_is_immutable() {
    @SuppressWarnings("unused")
    ImmutableList<Message> list = Empty.messageList();
  }

  @Test
  public void empty_message_list_always_returns_the_same_object() {
    assertThat(Empty.messageList()).isSameAs(Empty.messageList());
  }

  // nullToEmpty

  @Test
  public void null_is_changed_into_empty_iterable() throws Exception {
    assertThat(Empty.nullToEmpty(null)).isEmpty();
  }

  @Test
  public void null_to_empty_does_not_change_non_empty_iterable() throws Exception {
    @SuppressWarnings("unchecked")
    Iterable<String> iterable = mock(Iterable.class);
    assertThat(Empty.nullToEmpty(iterable)).isSameAs(iterable);
  }

  // typeConverterMap()

  @Test
  public void empty_type_converter_map_is_empty() {
    assertThat(Empty.typeConverterMap()).isEmpty();
  }

  @Test
  public void empty_type_converter_map_is_immutable() {
    @SuppressWarnings("unused")
    ImmutableMap<SType<?>, Converter<?, ?>> map = Empty.typeConverterMap();
  }

  @Test
  public void empty_type_converter_map_always_returns_the_same_object() {
    assertThat(Empty.typeConverterMap()).isSameAs(Empty.typeConverterMap());
  }
}
