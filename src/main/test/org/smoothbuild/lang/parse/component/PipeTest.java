package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.TestModuleLoader.err;
import static org.smoothbuild.lang.TestModuleLoader.module;

import java.util.List;

import org.junit.jupiter.api.Test;

public class PipeTest {
  @Test
  public void regression_test_error_in_expression_of_argument_of_not_first_element_of_pipe() {
    module("""
        @Native("impl")
        String myFunction(String a, String b);
        @Native("impl")
        String myIdentity(String value);
        result = "abc" | myIdentity(myFunction(unknown=""));
        """).loadsWithErrors(List.of(
            err(5, "In call to function with type `String(String a, String b)`: Unknown parameter `unknown`."),
            err(5, "In call to function with type `String(String value)`: Too many positional arguments.")
        ));
  }
}
