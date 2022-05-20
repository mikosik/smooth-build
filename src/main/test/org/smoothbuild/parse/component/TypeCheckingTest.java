package org.smoothbuild.parse.component;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class TypeCheckingTest extends TestingContext {
  @Nested
  class _type_checking_value_type_and_its_body_type {
    @Test
    public void fails_when_types_are_not_assignable() {
      var sourceCode = """
          [Blob] result = [1, 2, 3];
          """;
      module(sourceCode)
          .loadsWithError(1, "`result` has body which type is " + arrayTS(intTS()).q()
              + " and it is not convertible to its declared type " + arrayTS(blobTS()).q() + ".");
    }

    @Test
    public void succeeds_when_types_are_assignable() {
      var sourceCode = """
          [[Int]] result = [];
          """;
      module(sourceCode)
          .loadsWithSuccess();
    }
  }

  @Nested
  class _type_checking_func_res_type_and_its_body_type {
    @Test
    public void fails_when_types_are_not_assignable() {
      var sourceCode = """
          [Blob] myFunc() = [1, 2, 3];
          """;
      module(sourceCode)
          .loadsWithError(1, "`myFunc` has body which type is " + arrayTS(intTS()).q()
              + " and it is not convertible to its declared type " + arrayTS(blobTS()).q() + ".");
    }

    @Test
    public void succeeds_when_types_are_assignable() {
      var sourceCode = """
          [[Int]] myFunc() = [];
          """;
      module(sourceCode)
          .loadsWithSuccess();
    }
  }

  @Nested
  class _type_checking_func_param_type_and_arg_type {
    @Test
    public void fails_when_types_are_not_assignable() {
      var sourceCode = """
          String myFunc([Blob] blobArray) = "abc";
          result = myFunc([1, 2, 3]);
          """;
      var blobArray = arrayTS(blobTS());
      module(sourceCode)
          .loadsWithError(2, "In call to function with parameters ([Blob] blobArray):"
              + " Cannot assign argument of type " + arrayTS(intTS()).q()
              + " to parameter `blobArray` of type " + blobArray.q() + ".");
    }

    @Test
    public void succeeds_when_types_are_assignable() {
      var sourceCode = """
          String myFunc([[Int]] param) = "abc";
          result = myFunc([]);
          """;
      module(sourceCode)
          .loadsWithSuccess();
    }
  }

  @Nested
  class _type_checking_func_named_param_type_and_arg_type {
    @Test
    public void fails_when_types_are_not_assignable() {
      var sourceCode = """
          String myFunc([Blob] blobArray) = "abc";
          result = myFunc(blobArray = [1, 2, 3]);
          """;
      var blobArray = arrayTS(blobTS());
      module(sourceCode)
          .loadsWithError(2,
              "In call to function with parameters ([Blob] blobArray):"
                  + " Cannot assign argument of type " + arrayTS(intTS()).q()
                  + " to parameter `blobArray` of type " + blobArray.q() + ".");
    }

    @Test
    public void succeeds_when_types_are_assignable() {
      var sourceCode = """
          String myFunc([[Int]] param) = "abc";
          result = myFunc(param = []);
          """;
      module(sourceCode)
          .loadsWithSuccess();
    }
  }

  @Nested
  class _type_checking_func_param_type_and_default_arg_type {
    @Test
    public void fails_when_types_are_not_assignable() {
      var sourceCode = """
          String myFunc([Blob] blobArray = [1, 2, 3]) = "abc";
          """;
      module(sourceCode)
          .loadsWithError(1, "Parameter `blobArray` is of type " + arrayTS(blobTS()).q()
              + " so it cannot have default argument of type " + arrayTS(intTS()).q() + ".");
    }

    @Test
    public void succeeds_when_types_are_assignable() {
      var sourceCode = """
          String myFunc([[Int]] blobArray = []) = "abc";
          """;
      module(sourceCode)
          .loadsWithSuccess();
    }
  }

  @Nested
  class _type_checking_array_element_types {
    @Test
    public void fails_when_types_have_no_common_super_type() {
      var sourceCode = """
          result = [1, "abc"];
          """;
      module(sourceCode)
          .loadsWithError(1, "Array elements don't have common super type.");
    }

    @Test
    public void succeeds_when_types_have_common_super_type() {
      var sourceCode = """
          result = [[[1, 2, 3]], []];
          """;
      module(sourceCode)
          .loadsWithSuccess();
    }
  }
}
