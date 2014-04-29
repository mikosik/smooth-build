package org.smoothbuild.util;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;

public class EnvironmentVariableTest {
  String name = "name";
  String value = "value";
  EnvironmentVariable environmentVariable;

  @Test
  public void name_returns_name_passed_to_constructor() throws Exception {
    given(environmentVariable = new EnvironmentVariable(name, value));
    when(environmentVariable).name();
    thenReturned(name);
  }

  @Test
  public void null_environment_variable_is_not_set() {
    given(environmentVariable = new EnvironmentVariable(name, null));
    when(environmentVariable).isSet();
    thenReturned(false);
  }

  @Test
  public void non_null_environment_variable_is_set() {
    given(environmentVariable = new EnvironmentVariable(name, value));
    when(environmentVariable).isSet();
    thenReturned(true);
  }

  @Test
  public void value_returns_value_passed_to_constructor() throws Exception {
    given(environmentVariable = new EnvironmentVariable(name, value));
    when(environmentVariable).value();
    thenReturned(value);
  }

  @Test
  public void value_throws_exception_for_not_set_variable() throws Exception {
    given(environmentVariable = new EnvironmentVariable(name, null));
    when(environmentVariable).value();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void test_to_string() {
    given(environmentVariable = new EnvironmentVariable(name, value));
    when(environmentVariable).toString();
    thenReturned(name + "=" + value);
  }
}
