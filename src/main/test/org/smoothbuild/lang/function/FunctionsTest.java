package org.smoothbuild.lang.function;

import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;

public class FunctionsTest {
  private Functions functions;
  private Function function;
  private Function function2;
  private Name name;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void contains() throws Exception {
    given(willReturn(name), function).name();
    given(functions = new Functions());
    given(functions = functions.add(function));
    when(functions.contains(name));
    thenReturned(true);
  }

  @Test
  public void get() throws Exception {
    given(willReturn(name), function).name();
    given(functions = new Functions());
    given(functions = functions.add(function));
    when(functions.get(name));
    thenReturned(function);
  }

  @Test
  public void getting_unknown_function_fails() throws Exception {
    given(willReturn(new Name("functionName")), function).name();
    given(functions = new Functions().add(function));
    when(() -> functions.get(new Name("missingFunction")));
    thenThrown(exception(new IllegalArgumentException("Cannot find function 'missingFunction'.\n"
        + "Available functions: ['functionName']")));
  }

  @Test
  public void adding_function_with_same_name_twice_is_forbidden() throws Exception {
    given(willReturn(name), function).name();
    given(willReturn(name), function2).name();
    given(functions = new Functions());
    given(functions = functions.add(function));
    when(() -> functions.add(function2));
    thenThrown(IllegalArgumentException.class);

  }

  @Test
  public void names_returns_collection_that_forbids_removing_elements() {
    given(functions = new Functions());
    when(functions.names()).remove(null);
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void names_returns_collection_that_forbids_adding_elements() {
    given(functions = new Functions());
    when(functions.names()).add(new Name("name"));
    thenThrown(UnsupportedOperationException.class);
  }
}
