package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.TestModuleLoader.err;
import static org.smoothbuild.lang.TestModuleLoader.module;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;

public class PipeTest {
  @Test
  public void regression_test_error_in_expression_of_argument_of_not_first_element_of_pipe() {
    module("""
        String myFunction(String a, String b) = "abc";
        String myIdentity(String s) = s;
        result = "abc" | myIdentity(myFunction(unknown=""));
        """).loadsWithErrors(list(
            err(3, "In call to function with type `String(String a, String b)`: Unknown parameter `unknown`."),
            err(3, "In call to function with type `String(String s)`: Too many positional arguments.")
        ));
  }

  @Test
  public void non_first_chain_in_a_pipe_must_have_function_call() {
    module("""
        MyStruct {
          String myField
        }
        myValue = myStruct("def");
        result = "abc" | myValue.myField;
        """)
        .loadsWithError(5, """
            extraneous input ';' expecting {'(', '.'}
            result = "abc" | myValue.myField;
                                            ^""");
  }
}
