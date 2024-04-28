package org.smoothbuild.compilerfrontend.acceptance;

import static org.smoothbuild.compilerfrontend.testing.FrontendCompilerTester.module;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TypeCheckingTest {
  @Nested
  class _named_value_type_and_its_body_type {
    @Test
    void mono_to_mono_success() {
      var sourceCode = """
          Int result = 7;
          """;
      module(sourceCode).loadsWithSuccess();
    }

    @Test
    void mono_to_mono_error() {
      var sourceCode = """
          Blob result = "abc";
          """;
      module(sourceCode).loadsWithError(1, "`result` body type is not equal to declared type.");
    }

    @Test
    void mono_to_poly_error() {
      var sourceCode = """
          A result = 7;
          """;
      module(sourceCode).loadsWithError(1, "`result` body type is not equal to declared type.");
    }

    @Test
    void poly_to_mono_success() {
      var sourceCode = """
          [Int] result = [];
          """;
      module(sourceCode).loadsWithSuccess();
    }

    @Test
    void poly_to_mono_error() {
      var sourceCode = """
          [Int] result = [[]];
          """;
      module(sourceCode).loadsWithError(1, "`result` body type is not equal to declared type.");
    }

    @Test
    void poly_to_poly_success() {
      var sourceCode = """
          [A] result = [];
          """;
      module(sourceCode).loadsWithSuccess();
    }

    @Test
    void poly_to_poly_error() {
      var sourceCode = """
          [A] result = [[]];
          """;
      module(sourceCode).loadsWithError(1, "`result` body type is not equal to declared type.");
    }
  }

  @Nested
  class _lambda {
    @Nested
    class _param_type_and_arg_type extends _abstract_param_type_and_arg_type_suite {
      @Override
      public String buildSourceCode(String params, String argument) {
        return """
          result = ((%s) -> "abc")(%s);
          """
            .formatted(params, argument);
      }
    }
  }

  @Nested
  class _expression_function {
    @Nested
    class _res_type_and_its_body_type {
      @Test
      void mono_to_mono_success() {
        var sourceCode = """
          Int myFunc() = 7;
          """;
        module(sourceCode).loadsWithSuccess();
      }

      @Test
      void mono_to_mono_error() {
        var sourceCode = """
          Blob myFunc() = "abc";
          """;
        module(sourceCode).loadsWithError(1, "`myFunc` body type is not equal to declared type.");
      }

      @Test
      void mono_to_poly_error() {
        var sourceCode = """
          A myFunc() = 7;
          """;
        module(sourceCode).loadsWithError(1, "`myFunc` body type is not equal to declared type.");
      }

      @Test
      void poly_to_mono_success() {
        var sourceCode = """
          [Int] myFunc() = [];
          """;
        module(sourceCode).loadsWithSuccess();
      }

      @Test
      void poly_to_mono_error() {
        var sourceCode = """
          [Int] myFunc() = [[]];
          """;
        module(sourceCode).loadsWithError(1, "`myFunc` body type is not equal to declared type.");
      }

      @Test
      void poly_to_poly_success() {
        var sourceCode = """
          [A] myFunc() = [];
          """;
        module(sourceCode).loadsWithSuccess();
      }

      @Test
      void poly_to_poly_error() {
        var sourceCode = """
          [A] myFunc() = [[]];
          """;
        module(sourceCode).loadsWithError(1, "`myFunc` body type is not equal to declared type.");
      }
    }

    @Nested
    class _param_type_and_arg_type extends _abstract_param_type_and_arg_type_suite {
      @Override
      public String buildSourceCode(String params, String argument) {
        return """
          result = myFunc(%s);
          String myFunc(%s) = "abc";
          """
            .formatted(argument, params);
      }
    }

    @Nested
    class _named_param_type_and_arg_type {
      @Test
      void mono_to_mono_success() {
        var sourceCode =
            """
          String myFunc(Int int) = "abc";
          result = myFunc(int=7);
          """;
        module(sourceCode).loadsWithSuccess();
      }

      @Test
      void mono_to_mono_error() {
        var sourceCode =
            """
          String myFunc(Blob blob) = "abc";
          result = myFunc(blob=7);
          """;
        module(sourceCode).loadsWithError(2, "Illegal call.");
      }

      @Test
      void mono_to_poly_success() {
        var sourceCode =
            """
          String myFunc(A a) = "abc";
          result = myFunc(a=7);
          """;
        module(sourceCode).loadsWithSuccess();
      }

      @Test
      void mono_to_poly_error() {
        var sourceCode =
            """
          String myFunc([A] a) = "abc";
          result = myFunc(a=7);
          """;
        module(sourceCode).loadsWithError(2, "Illegal call.");
      }

      @Test
      void poly_to_mono_success() {
        var sourceCode =
            """
          String myFunc([Int] param) = "abc";
          result = myFunc(param=[]);
          """;
        module(sourceCode).loadsWithSuccess();
      }

      @Test
      void poly_to_mono_error() {
        var sourceCode =
            """
          String myFunc([Int] param) = "abc";
          result = myFunc(param=[[]]);
          """;
        module(sourceCode).loadsWithError(2, "Illegal call.");
      }

      @Test
      void poly_to_poly_success() {
        var sourceCode =
            """
          String myFunc([A] param) = "abc";
          result = myFunc(param=[]);
          """;
        module(sourceCode).loadsWithSuccess();
      }

      @Test
      void poly_to_poly_error() {
        var sourceCode =
            """
          String myFunc((A)->A param) = "abc";
          result = myFunc(param=[]);
          """;
        module(sourceCode).loadsWithError(2, "Illegal call.");
      }
    }

    @Nested
    class _param_type_and_param_default_value_type {
      @Test
      void mono_to_mono_success() {
        var sourceCode = """
            Int myFunc(Int int = 7) = int;
            """;
        module(sourceCode).loadsWithSuccess();
      }

      @Test
      void mono_to_mono_error() {
        var sourceCode =
            """
            String myFunc([Blob] blobArray = [1, 2, 3]) = "abc";
            """;
        module(sourceCode)
            .loadsWithError(
                1,
                "Parameter `blobArray` has type `[Blob]` so it cannot have default "
                    + "value with type `[Int]`.");
      }

      @Test
      void mono_to_poly_success() {
        var sourceCode = """
            A myFunc(A a = 7) = a;
            """;
        module(sourceCode).loadsWithSuccess();
      }

      @Test
      void mono_to_poly_with_different_instantiation_of_same_var_in_different_param_success() {
        var sourceCode = """
            A myFunc(A a = 7, A b = "abc") = a;
            """;
        module(sourceCode).loadsWithSuccess();
      }

      @Test
      void mono_to_poly_error() {
        var sourceCode =
            """
            Int twoParamFunc(String s, Blob b) = 7;
            (B)->A myFunc((B)->A funcParam = twoParamFunc) = funcParam;
            """;
        module(sourceCode)
            .loadsWithError(
                2,
                "Parameter `funcParam` has type `(B)->A` so it cannot have default "
                    + "value with type `(String,Blob)->Int`.");
      }

      @Test
      void poly_to_mono_success1() {
        var sourceCode =
            """
            A myIdentity (A a) = a;
            (Int)->Int myFunc((Int)->Int funcParam = myIdentity) = funcParam;
            """;
        module(sourceCode).loadsWithSuccess();
      }

      @Test
      void poly_to_mono_success2() {
        var sourceCode =
            """
            String myFunc([[Int]] blobArray = []) = "abc";
            """;
        module(sourceCode).loadsWithSuccess();
      }

      @Test
      void poly_to_mono_error() {
        var sourceCode =
            """
            A myIdentity (A a) = a;
            (Int,Int)->Int myFunc((Int,Int)->Int funcParam = myIdentity) = funcParam;
            """;
        module(sourceCode)
            .loadsWithError(
                2,
                "Parameter `funcParam` has type `(Int,Int)->Int` so it cannot have"
                    + " default value with type `(A)->A`.");
      }

      @Test
      void poly_to_poly_success() {
        var sourceCode =
            """
            A myIdentity (A a) = a;
            (B)->B myFunc((B)->B funcParam = myIdentity) = funcParam;
            """;
        module(sourceCode).loadsWithSuccess();
      }

      @Test
      void poly_to_poly_error() {
        var sourceCode =
            """
            A myIdentity (A a) = a;
            (A, A)->A myFunc((A, A)->A funcParam = myIdentity) = funcParam;
            """;
        module(sourceCode)
            .loadsWithError(
                2,
                "Parameter `funcParam` has type `(A,A)->A` so it cannot have "
                    + "default value with type `(A)->A`.");
      }
    }
  }

  abstract static class _abstract_param_type_and_arg_type_suite {
    public abstract String buildSourceCode(String params, String argument);

    @Test
    void mono_to_mono_success() {
      var code = buildSourceCode("Int int", "7");
      module(code).loadsWithSuccess();
    }

    @Test
    void mono_to_mono_error() {
      var code = buildSourceCode("Blob blob", "7");
      module(code).loadsWithError(1, "Illegal call.");
    }

    @Test
    void mono_to_poly_success() {
      var code = buildSourceCode("A a", "7");
      module(code).loadsWithSuccess();
    }

    @Test
    void mono_to_poly_error() {
      var code = buildSourceCode("[A] a", "7");
      module(code).loadsWithError(1, "Illegal call.");
    }

    @Test
    void poly_to_mono_success() {
      var code = buildSourceCode("[Int] param", "[]");
      module(code).loadsWithSuccess();
    }

    @Test
    void poly_to_mono_error() {
      var code = buildSourceCode("[Int] param", "[[]]");
      module(code).loadsWithError(1, "Illegal call.");
    }

    @Test
    void poly_to_poly_success() {
      var code = buildSourceCode("[A] param", "[]");
      module(code).loadsWithSuccess();
    }

    @Test
    void poly_to_poly_error() {
      var code = buildSourceCode("(A)->A param", "[]");
      module(code).loadsWithError(1, "Illegal call.");
    }
  }

  @Nested
  class _array_element_types {
    @Test
    void fails_when_types_have_no_common_super_type() {
      var sourceCode = """
          result = [1, "abc"];
          """;
      module(sourceCode)
          .loadsWithError(
              1, "Cannot infer type for array literal. Its element types are not compatible.");
    }

    @Test
    void succeeds_when_types_have_common_super_type() {
      var sourceCode = """
          result = [[[1, 2, 3]], []];
          """;
      module(sourceCode).loadsWithSuccess();
    }
  }

  @Nested
  class _rankness {
    @Test
    void of_higher_order_is_not_possible() {
      var code = """
            f(String s, (A)->A id) = id(s);
            """;
      module(code).loadsWithError(1, "Illegal call.");
    }
  }

  @Test
  void regression_test_type_error_in_param_default_value_should_fail_gracefully_with_error() {
    var code = """
            f([String] param = [7, "abc"]) = 3;
            """;
    module(code)
        .loadsWithError(
            1, "Cannot infer type for array literal. Its element types are not compatible.");
  }
}
