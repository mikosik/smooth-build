package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.parse.component.TestModuleLoader.err;
import static org.smoothbuild.lang.parse.component.TestModuleLoader.module;

import java.util.List;

import org.junit.jupiter.api.Test;

public class PipeTest {
  @Test
  public void regression_test_error_in_expression_of_argument_of_not_first_element_of_pipe() {
    module("""
        String myFunction(String a, String b);
        String myIdentity(String value);
        result = "abc" | myIdentity(myFunction(unknown=""));
        """).loadsWithErrors(List.of(
            err(3, "In call to `myFunction`: Unknown parameter 'unknown'."),
            err(3, "In call to `myIdentity`: Too many positional arguments.")
        ));
  }
}
