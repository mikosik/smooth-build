package org.smoothbuild.util;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class EmptyTest {

  // stringValueMap()

  @Test
  public void empty_string_value_map_is_empty() {
    assertThat(Empty.stringValueMap().keySet(), empty());
  }

  @Test
  public void empty_string_value_map_is_immutable() {
    when(Empty.stringValueMap());
    thenReturned(instanceOf(ImmutableMap.class));
  }

  @Test
  public void empty_string_value_map_always_returns_the_same_object() {
    assertSame(Empty.stringValueMap(), Empty.stringValueMap());
  }

  // stringExpressionMap()

  @Test
  public void empty_string_expression_map_is_empty() {
    assertThat(Empty.stringExpressionMap().keySet(), empty());
  }

  @Test
  public void empty_string_expression_map_is_immutable() {
    when(Empty.stringExpressionMap());
    thenReturned(instanceOf(ImmutableMap.class));
  }

  @Test
  public void empty_string_expression_map_always_returns_the_same_object() {
    assertSame(Empty.stringExpressionMap(), Empty.stringExpressionMap());
  }

  // nameFunctionMap();

  @Test
  public void empty_name_function_map_is_empty() {
    assertThat(Empty.nameFunctionMap().keySet(), empty());
  }

  @Test
  public void empty_name_function_map_is_immutable() {
    when(Empty.nameFunctionMap());
    thenReturned(instanceOf(ImmutableMap.class));
  }

  @Test
  public void empty_name_function_map_always_returns_the_same_object() {
    assertSame(Empty.nameFunctionMap(), Empty.nameFunctionMap());
  }

  // typeFunctionMap();

  @Test
  public void empty_type_function_map_is_empty() {
    assertThat(Empty.typeFunctionMap().keySet(), empty());
  }

  @Test
  public void empty_type_function_map_is_immutable() {
    when(Empty.typeFunctionMap());
    thenReturned(instanceOf(ImmutableMap.class));
  }

  @Test
  public void empty_type_function_map_always_returns_the_same_object() {
    assertSame(Empty.typeFunctionMap(), Empty.typeFunctionMap());
  }

  // paramList()

  @Test
  public void empty_param_list_is_empty() {
    assertThat(Empty.paramList(), empty());
  }

  @Test
  public void empty_param_list_is_immutable() {
    when(Empty.paramList());
    thenReturned(instanceOf(ImmutableList.class));
  }

  @Test
  public void empty_param_list_always_returns_the_same_object() {
    assertSame(Empty.paramList(), Empty.paramList());
  }

  // valueList()

  @Test
  public void empty_value_list_is_empty() {
    assertThat(Empty.valueList(), empty());
  }

  @Test
  public void empty_value_list_is_immutable() {
    when(Empty.valueList());
    thenReturned(instanceOf(ImmutableList.class));
  }

  @Test
  public void empty_value_list_always_returns_the_same_object() {
    assertSame(Empty.valueList(), Empty.valueList());
  }

  // expressionList()

  @Test
  public void empty_expression_list_is_empty() {
    assertThat(Empty.expressionList(), empty());
  }

  @Test
  public void empty_expression_list_is_immutable() {
    when(Empty.expressionList());
    thenReturned(instanceOf(ImmutableList.class));
  }

  @Test
  public void empty_expression_list_always_returns_the_same_object() {
    assertSame(Empty.expressionList(), Empty.expressionList());
  }

  // messageList()

  @Test
  public void empty_message_list_is_empty() {
    assertThat(Empty.messageList(), empty());
  }

  @Test
  public void empty_message_list_is_immutable() {
    when(Empty.messageList());
    thenReturned(instanceOf(ImmutableList.class));
  }

  @Test
  public void empty_message_list_always_returns_the_same_object() {
    assertSame(Empty.messageList(), Empty.messageList());
  }

  // taskList()

  @Test
  public void empty_task_list_is_empty() {
    assertThat(Empty.taskList(), empty());
  }

  @Test
  public void empty_task_list_is_immutable() {
    when(Empty.taskList());
    thenReturned(instanceOf(ImmutableList.class));
  }

  @Test
  public void empty_task_list_always_returns_the_same_object() {
    assertSame(Empty.taskList(), Empty.taskList());
  }

  // nullToEmpty

  @Test
  public void null_is_changed_into_empty_iterable() throws Exception {
    assertThat(Empty.nullToEmpty(null), emptyIterable());
  }

  @Test
  public void null_to_empty_does_not_change_non_empty_iterable() throws Exception {
    Iterable<String> iterable = mock(Iterable.class);
    assertSame(iterable, Empty.nullToEmpty(iterable));
  }
}
