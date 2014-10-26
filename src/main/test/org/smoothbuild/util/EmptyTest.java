package org.smoothbuild.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testory.Testory.mock;

import org.junit.Test;
import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.base.Task;

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
    ImmutableMap<String, Value> map = Empty.stringValueMap();
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
    ImmutableMap<String, Expression<?>> map = Empty.stringExprMap();
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

  // typeFunctionMap();

  @Test
  public void empty_type_function_map_is_empty() {
    assertThat(Empty.typeFunctionMap()).isEmpty();
  }

  @Test
  public void empty_type_function_map_is_immutable() {
    @SuppressWarnings("unused")
    ImmutableMap<Type<?>, Function<?>> map = Empty.typeFunctionMap();
  }

  @Test
  public void empty_type_function_map_always_returns_the_same_object() {
    assertThat(Empty.typeFunctionMap()).isSameAs(Empty.typeFunctionMap());
  }

  // paramList()

  @Test
  public void empty_param_list_is_empty() {
    assertThat(Empty.paramList()).isEmpty();
  }

  @Test
  public void empty_param_list_is_immutable() {
    @SuppressWarnings("unused")
    ImmutableList<Param> list = Empty.paramList();
  }

  @Test
  public void empty_param_list_always_returns_the_same_object() {
    assertThat(Empty.paramList()).isSameAs(Empty.paramList());
  }

  // valueList()

  @Test
  public void empty_value_list_is_empty() {
    assertThat(Empty.valueList()).isEmpty();
  }

  @Test
  public void empty_value_list_is_immutable() {
    @SuppressWarnings("unused")
    ImmutableList<Value> list = Empty.valueList();
  }

  @Test
  public void empty_value_list_always_returns_the_same_object() {
    assertThat(Empty.valueList()).isSameAs(Empty.valueList());
  }

  // exprList()

  @Test
  public void empty_expr_list_is_empty() {
    assertThat(Empty.exprList()).isEmpty();
  }

  @Test
  public void empty_expr_list_is_immutable() {
    @SuppressWarnings("unused")
    ImmutableList<Expression<?>> list = Empty.exprList();
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

  // taskList()

  @Test
  public void empty_task_list_is_empty() {
    assertThat(Empty.taskList()).isEmpty();
  }

  @Test
  public void empty_task_list_is_immutable() {
    @SuppressWarnings("unused")
    ImmutableList<Task<?>> list = Empty.taskList();
  }

  @Test
  public void empty_task_list_always_returns_the_same_object() {
    assertThat(Empty.taskList()).isSameAs(Empty.taskList());
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
}
