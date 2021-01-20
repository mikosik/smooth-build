package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.TestModuleLoader.module;
import static org.smoothbuild.lang.TestingLang.function;
import static org.smoothbuild.lang.TestingLang.parameter;
import static org.smoothbuild.lang.TestingLang.string;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;

import org.junit.jupiter.api.Test;

public class FunctionTest {
  @Test
  public void default_parameter_before_non_default_is_allowed() {
    module("""
        String myFunction(
          String default = "value",
          String nonDefault);
        """)
        .loadsSuccessfully()
        .containsDeclared(function(1, STRING, "myFunction",
            parameter(2, STRING, "default", string(2, "value")),
            parameter(3, STRING, "nonDefault")));
  }

  @Test
  public void polytype_parameter_can_have_default_value() {
    module("""
        A myFunc(A value = "abc") = value;
        """)
       .loadsSuccessfully();
  }

  @Test
  public void default_value_gets_converted_to_polytype_parameter() {
    module("""
        [A] myFunc(A param1, [A] param2 = []) = param2;
        [String] result = myFunc("abc");
        """)
        .loadsSuccessfully();
  }
}
