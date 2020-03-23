package org.smoothbuild.lang.runtime;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.Collection;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.smoothbuild.lang.base.Function;

public class FunctionsTest {
  @Test
  public void contains() {
    Functions functions = new Functions();
    functions.add(function("name"));
    assertThat(functions.contains("name"))
        .isTrue();
  }

  @Test
  public void get() {
    Functions functions = new Functions();
    Function function = function("name");
    functions.add(function);
    assertThat(functions.get("name"))
        .isSameInstanceAs(function);
  }

  @Test
  public void getting_unknown_function_fails() {
    Functions functions = new Functions();
    functions.add(function("name"));
    assertCall(() -> functions.get("missingFunction"))
        .throwsException(new IllegalArgumentException("Cannot find function 'missingFunction'.\n"
        + "Available functions: [name]"));
  }

  @Test
  public void adding_function_with_same_name_twice_is_forbidden() {
    Functions functions = new Functions();
    functions.add(function("name"));
    assertCall(() -> functions.add(function("name")))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void names_returns_collection_that_forbids_adding_elements() {
    Functions functions = new Functions();
    Collection<String> names = functions.names();
    assertCall(() -> names.remove("name"))
        .throwsException(UnsupportedOperationException.class);
  }

  @Test
  public void all_returns_unmodifiable_collection() {
    Functions functions = new Functions();
    Collection<Function> all = functions.all();
    assertCall(() -> all.remove(function("name")))
        .throwsException(UnsupportedOperationException.class);
  }

  @Test
  public void all_contains_added_function() {
    Functions functions = new Functions();
    Function function = function("name");
    functions.add(function);
    assertThat(functions.all())
        .contains(function);
  }

  @Test
  public void name_to_function_map_is_unmodifiable() {
    Functions functions = new Functions();
    Map<String, Function> map = functions.nameToFunctionMap();
    assertCall(() -> map.remove("abc"))
        .throwsException(UnsupportedOperationException.class);
  }

  @Test
  public void name_to_function_map_is_empty_initially() {
    Functions functions = new Functions();
    assertThat(functions.nameToFunctionMap().keySet())
        .isEmpty();
  }

  @Test
  public void names_to_function_map_contains_name_of_function_that_was_added() {
    Functions functions = new Functions();
    Function function = function("name");
    functions.add(function);
    assertThat(functions.nameToFunctionMap().get(function.name()))
        .isSameInstanceAs(function);
  }

  private static Function function(String name) {
    Function function = mock(Function.class);
    Mockito.when(function.name()).thenReturn(name);
    return function;
  }
}
