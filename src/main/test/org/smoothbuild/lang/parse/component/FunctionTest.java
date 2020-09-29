package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.parse.component.TestModuleLoader.module;

import org.junit.jupiter.api.Test;

public class FunctionTest {
  @Test
  public void default_parameter_before_non_default_causes_error() {
    module("""
        String myFunction(
          String default = "value",
          String nonDefault);
        """)
        .loadsWithError(3, "parameter with default value must be placed after all parameters " +
            "which don't have default value.");
  }

  @Test
  public void generic_parameter_can_have_default_value() {
    module("""
        A myFunc(A value = "abc") = value;
        """)
       .loadsSuccessfully();
  }

  @Test
  public void default_value_gets_converted_to_generic_parameter() {
    module("""
        [A] myFunc(A param1, [A] param2 = []) = param2;
        [String] result = myFunc("abc");
        """)
        .loadsSuccessfully();
  }
}
