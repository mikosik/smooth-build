package org.smoothbuild.lang.runtime;

import static org.hamcrest.Matchers.hasItem;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.smoothbuild.util.Sets.set;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.function.base.Function;

public class FunctionsTest {
  private Functions functions;
  private Function function;
  private Function function2;
  private String name;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void contains() throws Exception {
    given(willReturn(name), function).name();
    given(functions = new Functions());
    given(functions).add(function);
    when(functions.contains(name));
    thenReturned(true);
  }

  @Test
  public void get() throws Exception {
    given(willReturn(name), function).name();
    given(functions = new Functions());
    given(functions).add(function);
    when(functions.get(name));
    thenReturned(function);
  }

  @Test
  public void getting_unknown_function_fails() throws Exception {
    given(willReturn("functionName"), function).name();
    given(functions = new Functions());
    given(functions).add(function);
    when(() -> functions.get("missingFunction"));
    thenThrown(exception(new IllegalArgumentException("Cannot find function 'missingFunction'.\n"
        + "Available functions: [functionName]")));
  }

  @Test
  public void adding_function_with_same_name_twice_is_forbidden() throws Exception {
    given(willReturn(name), function).name();
    given(willReturn(name), function2).name();
    given(functions = new Functions());
    given(functions).add(function);
    when(() -> functions.add(function2));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void names_returns_collection_that_forbids_adding_elements() {
    given(functions = new Functions());
    when(functions.names()).remove("name");
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void functions_returns_unmodifiable_collection() {
    given(functions = new Functions());
    when(functions.functions()).remove(function);
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void functions_contains_added_function() {
    given(functions = new Functions());
    given(functions).add(function);
    when(functions.functions());
    thenReturned(hasItem(function));
  }

  @Test
  public void name_to_function_map_is_unmodifiable() throws Exception {
    given(functions = new Functions());
    when(() -> functions.nameToFunctionMap().remove("abc"));
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void name_to_function_map_is_empty_initially() throws Exception {
    given(functions = new Functions());
    when(() -> functions.nameToFunctionMap().keySet());
    thenReturned(set());
  }

  @Test
  public void names_to_function_map_contains_name_of_function_that_was_added()
      throws Exception {
    given(functions = new Functions());
    given(functions).add(function);
    when(() -> functions.nameToFunctionMap().get(function.name()));
    thenReturned(function);
  }
}
