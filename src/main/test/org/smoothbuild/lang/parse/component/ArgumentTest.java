package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.parse.component.TestModuleLoader.module;

import org.junit.jupiter.api.Test;

public class ArgumentTest {
  @Test
  public void passing_more_positional_arguments_than_parameters_causes_error() {
    module("""
           myIdentity(String param) = param;
           result = myIdentity("abc", "def");
           """)
        .loadsWithError(2, "In call to `myIdentity`: Too many positional arguments.");
  }

  @Test
  public void passing_less_positional_arguments_than_parameters_causes_error() {
    module("""
           returnFirst(String param1, String param2) = param1;
           result = returnFirst("abc");
           """)
        .loadsWithError(2, "In call to `returnFirst`: Parameter 'param2' must be specified.");
  }

  @Test
  public void named_argument_which_doesnt_exist_causes_error() {
    module("""
           myIdentity(String param) = param;
           result = myIdentity(wrongName="abc");
           """)
        .loadsWithError(2, "In call to `myIdentity`: Unknown parameter 'wrongName'.");
  }

  @Test
  public void named_arguments_can_be_passed_in_the_same_order_as_parameters() {
    module("""
           returnFirst(String param1, String param2) = param1;
           result = returnFirst(param1="abc", param2="def");
           """)
        .loadsSuccessfully();
  }

  @Test
  public void named_arguments_can_be_passed_in_different_order_than_parameters() {
    module("""
           returnFirst(String param1, String param2) = param1;
           result = returnFirst(param2="def", param1="abc");
           """)
        .loadsSuccessfully();
  }

  @Test
  public void all_named_arguments_must_come_after_positional() {
    module("""
           returnFirst(String param1, String param2) = param1;
           result = returnFirst(param2="def", "abc");
           """)
        .loadsWithError(2, "In call to `returnFirst`: "
            + "Positional arguments must be placed before named arguments.");
  }

  @Test
  public void assigning_argument_by_name_twice_causes_error() {
    module("""
           myIdentity(String param) = param;
           result = myIdentity(param="abc", param="abc");
           """)
        .loadsWithError(2, "In call to `myIdentity`: `param` is already assigned.");
  }

  @Test
  public void assigning_by_name_argument_that_is_assigned_by_position_causes_error() {
    module("""
           myIdentity(String param) = param;
           result = myIdentity("abc", param="abc");
           """)
        .loadsWithError(2, "In call to `myIdentity`: `param` is already assigned.");
  }

  @Test
  public void parameter_with_default_value_can_be_assigned_positionally() {
    module("""
           myIdentity(String param1="abc", String param2="def") = param1;
           result = myIdentity("abc", "def");
           """)
        .loadsSuccessfully();
  }

  @Test
  public void parameter_with_default_value_can_be_assigned_by_name() {
    module("""
           myIdentity(String param1="abc", String param2="def") = param1;
           result = myIdentity(param1="abc", param2="def");
           """)
        .loadsSuccessfully();
  }
}
