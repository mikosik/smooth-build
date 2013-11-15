package org.smoothbuild.lang.function.base;

import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class ImmutableModuleTest {
  Name name1 = name("name1");
  Name name2 = name("name2");
  Function function1 = mock(Function.class);
  Function function2 = mock(Function.class);

  ImmutableModule module;

  @Test
  public void empty_immutable_module_does_not_contain_function() throws Exception {
    given(module = new ImmutableModule(Empty.nameToFunctionMap()));
    when(module.containsFunction(name1));
    thenReturned(false);
  }

  @Test
  public void contains_function_returns_true_when_module_contains_function() throws Exception {
    given(module = new ImmutableModule(ImmutableMap.of(name1, function1)));
    when(module.containsFunction(name1));
    thenReturned(true);
  }

  @Test
  public void get_function_returns_function_with_given_name() {
    given(module = new ImmutableModule(ImmutableMap.of(name1, function1, name2, function2)));
    when(module.getFunction(name1));
    thenReturned(function1);
  }

  @Test
  public void get_function_returns_null_when_no_function_with_given_name_exists() throws Exception {
    given(module = new ImmutableModule(ImmutableMap.of(name1, function1)));
    when(module.getFunction(name2));
    thenReturned(null);
  }

  @Test
  public void available_names_returns_all_function_names() throws Exception {
    given(module = new ImmutableModule(ImmutableMap.of(name1, function1, name2, function2)));
    when(module.availableNames());
    thenReturned(ImmutableSet.of(name1, name2));
  }
}
