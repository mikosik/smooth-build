package org.smoothbuild.compile.ps.component;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class TypeCheckingTest extends TestContext {
  @Nested
  class _value_type_and_its_body_type {
    @Test
    public void succeeds_when_types_are_assignable() {
      var sourceCode = """
          Int result = 7;
          """;
      module(sourceCode)
          .loadsWithSuccess();
    }

    @Test
    public void fails_when_types_are_not_assignable() {
      var sourceCode = """
          Blob result = "abc";
          """;
      module(sourceCode)
          .loadsWithError(1, "`result` body type is not equal to declared type.");
    }

    @Test
    public void succeeds_when_body_type_is_more_general_than_declared_type() {
      var sourceCode = """
          [Int] result = [];
          """;
      module(sourceCode)
          .loadsWithSuccess();
    }

    @Test
    public void fails_when_body_type_is_more_specific_than_declared_type() {
      var sourceCode = """
          A result = 7;
          """;
      module(sourceCode)
          .loadsWithError(1, "`result` body type is not equal to declared type.");
    }
  }

  @Nested
  class _func_res_type_and_its_body_type {
    @Test
    public void succeeds_when_types_are_assignable() {
      var sourceCode = """
          Int myFunc() = 7;
          """;
      module(sourceCode)
          .loadsWithSuccess();
    }

    @Test
    public void fails_when_types_are_not_assignable() {
      var sourceCode = """
          Int myFunc() = "abc";
          """;
      module(sourceCode)
          .loadsWithError(1, "`myFunc` body type is not equal to declared type.");
    }

    @Test
    public void succeeds_when_body_type_is_more_general_than_declared_result_type() {
      var sourceCode = """
          [Int] myFunc() = [];
          """;
      module(sourceCode)
          .loadsWithSuccess();
    }

    @Test
    public void fails_when_body_type_is_more_specific_than_declared_result_type() {
      var sourceCode = """
          A myFunc() = 7;
          """;
      module(sourceCode)
          .loadsWithError(1, "`myFunc` body type is not equal to declared type.");
    }
  }

  @Nested
  class _func_param_type_and_arg_type {
    @Test
    public void fails_when_types_are_not_assignable() {
      var sourceCode = """
          String myFunc([Blob] blobArray) = "abc";
          result = myFunc([1, 2, 3]);
          """;
      module(sourceCode)
          .loadsWithError(2, "Illegal call.");
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
  class _func_named_param_type_and_arg_type {
    @Test
    public void fails_when_types_are_not_assignable() {
      var sourceCode = """
          String myFunc([Blob] blobArray) = "abc";
          result = myFunc(blobArray = [1, 2, 3]);
          """;
      module(sourceCode)
          .loadsWithError(2, "Illegal call.");
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
  class _func_param_type_and_default_arg_type {
    @Nested
    class _fails_when_types_are_not_assignable {
      @Test
      public void mono_to_mono_case() {
        var sourceCode = """
            String myFunc([Blob] blobArray = [1, 2, 3]) = "abc";
            """;
        module(sourceCode)
            .loadsWithError(1, "Parameter `blobArray` has type `[Blob]` so it cannot have default "
                + "value with type `[Int]`.");
      }

      @Test
      public void mono_to_poly_case() {
        var sourceCode = """
            Int twoParamFunc(String s, Blob b) = 7;
            A(B) myFunc(A(B) funcParam = twoParamFunc) = funcParam;
            """;
        module(sourceCode)
            .loadsWithError(2, "Parameter `funcParam` has type `A(B)` so it cannot have default "
                + "value with type `Int(String,Blob)`.");
      }

      @Test
      public void poly_to_mono_case() {
        var sourceCode = """
            A myIdentity (A a) = a;
            Int(Int, Int) myFunc(Int(Int, Int) funcParam = myIdentity) = funcParam;
            """;
        module(sourceCode)
            .loadsWithError(2, "Parameter `funcParam` has type `Int(Int,Int)` so it cannot have"
                + " default value with type `A(A)`.");
      }

      @Test
      public void poly_to_poly_case() {
        var sourceCode = """
            A myIdentity (A a) = a;
            A(A, A) myFunc(A(A, A) funcParam = myIdentity) = funcParam;
            """;
        module(sourceCode)
            .loadsWithError(2, "Parameter `funcParam` has type `A(A,A)` so it cannot have "
                + "default value with type `A(A)`.");
      }
    }

    @Nested
    class _succeeds_when_types_are_assignable {
      @Test
      public void mono_to_mono_case() {
        var sourceCode = """
            Int myFunc(Int int = 7) = int;
            """;
        module(sourceCode)
            .loadsWithSuccess();
      }

      @Test
      public void mono_to_poly_case() {
        var sourceCode = """
            A myFunc(A a = 7) = a;
            """;
        module(sourceCode)
            .loadsWithSuccess();
      }

      @Test
      public void poly_to_mono_case1() {
        var sourceCode = """
            A myIdentity (A a) = a;
            Int(Int) myFunc(Int(Int) funcParam = myIdentity) = funcParam;
            """;
        module(sourceCode)
            .loadsWithSuccess();
      }

      @Test
      public void poly_to_mono_case2() {
        var sourceCode = """
            String myFunc([[Int]] blobArray = []) = "abc";
            """;
        module(sourceCode)
            .loadsWithSuccess();
      }

      @Test
      public void poly_to_poly() {
        var sourceCode = """
            A myIdentity (A a) = a;
            B(B) myFunc(B(B) funcParam = myIdentity) = funcParam;
            """;
        module(sourceCode)
            .loadsWithSuccess();
      }
    }
  }

  @Nested
  class _array_element_types {
    @Test
    public void fails_when_types_have_no_common_super_type() {
      var sourceCode = """
          result = [1, "abc"];
          """;
      module(sourceCode)
          .loadsWithError(1, "Cannot infer type for array literal. Its element types are not compatible.");
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

  @Nested
  class _rankness {
    @Test
    public void of_higher_order_is_not_possible() {
      var code = """
            f(String s, A(A) id) = id(s);
            """;
      module(code)
          .loadsWithError(1, "Illegal call.");
    }
  }

  @Test
  public void regression_test_type_error_in_param_default_val_should_fail_gracefully_with_error() {
    var code = """
            f([String] param = [7, "abc"]) = 3;
            """;
    module(code)
        .loadsWithError(1,
            "Cannot infer type for array literal. Its element types are not compatible.");
  }
}
