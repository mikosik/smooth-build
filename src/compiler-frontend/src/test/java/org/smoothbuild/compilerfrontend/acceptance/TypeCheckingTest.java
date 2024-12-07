package org.smoothbuild.compilerfrontend.acceptance;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.compilerfrontend.acceptance.Util.arrayTypeMessage;
import static org.smoothbuild.compilerfrontend.acceptance.Util.bodyTypeMessage;
import static org.smoothbuild.compilerfrontend.acceptance.Util.illegalCallMessage;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompileTester;

public class TypeCheckingTest extends FrontendCompileTester {
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
      module(sourceCode).loadsWithError(1, bodyTypeMessage("result", sStringType(), sBlobType()));
    }

    @Test
    void mono_to_poly_error() {
      var sourceCode = """
          A result = 7;
          """;
      module(sourceCode).loadsWithError(1, bodyTypeMessage("result", sIntType(), varA()));
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
      module(sourceCode).loadsWithError(1, bodyTypeMessage("result", sVar1ArrayT(), sIntArrayT()));
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
      module(sourceCode).loadsWithError(1, bodyTypeMessage("result", sVar1ArrayT(), sVarAArrayT()));
    }
  }

  @Nested
  class _lambda {
    @Nested
    class _param_type_and_arg_type extends _abstract_param_type_and_arg_type_suite {
      @Override
      public String buildSourceCode(String params, String argument) {
        // empty space is added so error line number is the same as in the case of
        // implementation of buildSourceCode method used in case of _expression_function
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
        module(sourceCode).loadsWithError(1, bodyTypeMessage("myFunc", sStringType(), sBlobType()));
      }

      @Test
      void mono_to_poly_error() {
        var sourceCode = """
          A myFunc() = 7;
          """;
        module(sourceCode).loadsWithError(1, bodyTypeMessage("myFunc", sIntType(), varA()));
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
        module(sourceCode)
            .loadsWithError(1, bodyTypeMessage("myFunc", sVar1ArrayT(), sIntArrayT()));
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
        module(sourceCode)
            .loadsWithError(1, bodyTypeMessage("myFunc", sVar1ArrayT(), sVarAArrayT()));
      }
    }

    @Nested
    class _param_type_and_arg_type extends _abstract_param_type_and_arg_type_suite {
      @Override
      public String buildSourceCode(String params, String argument) {
        // Call to myFunc is wrapped inside lambda so typing error message we expect would
        // use the same flexible Var names as in the case of implementation of buildSourceCode
        // for lambda
        return """
          String myFunc(%s) = "abc";
          result = () -> myFunc(%s);
          """
            .formatted(params, argument);
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
        module(sourceCode)
            .loadsWithError(
                2, illegalCallMessage(sFuncType(sBlobType(), sStringType()), list(sIntType())));
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
        module(sourceCode)
            .loadsWithError(
                2, illegalCallMessage(sFuncType(sVar1ArrayT(), sStringType()), list(sIntType())));
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
        module(sourceCode)
            .loadsWithError(
                2,
                illegalCallMessage(
                    sFuncType(sIntArrayT(), sStringType()), list(sArrayType(sVar1ArrayT()))));
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
        module(sourceCode)
            .loadsWithError(
                2,
                illegalCallMessage(
                    sFuncType(sFuncType(var1(), var1()), sStringType()), list(sVar2ArrayT())));
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

  abstract class _abstract_param_type_and_arg_type_suite {
    public abstract String buildSourceCode(String params, String argument);

    @Test
    void mono_to_mono_success() {
      var code = buildSourceCode("Int int", "7");
      module(code).loadsWithSuccess();
    }

    @Test
    void mono_to_mono_error() {
      var code = buildSourceCode("Blob blob", "7");
      var called = sFuncType(sBlobType(), sStringType());
      var args = list(sIntType());
      module(code).loadsWithError(2, illegalCallMessage(called, args));
    }

    @Test
    void mono_to_poly_success() {
      var code = buildSourceCode("A a", "7");
      module(code).loadsWithSuccess();
    }

    @Test
    void mono_to_poly_error() {
      var code = buildSourceCode("[A] a", "7");
      module(code)
          .loadsWithError(
              2, illegalCallMessage(sFuncType(sVar2ArrayT(), sStringType()), list(sIntType())));
    }

    @Test
    void poly_to_mono_success() {
      var code = buildSourceCode("[Int] param", "[]");
      module(code).loadsWithSuccess();
    }

    @Test
    void poly_to_mono_error() {
      var code = buildSourceCode("[Int] param", "[[]]");
      var called = sFuncType(sIntArrayT(), sStringType());
      var args = list(sArrayType(sVar2ArrayT()));
      module(code).loadsWithError(2, illegalCallMessage(called, args));
    }

    @Test
    void poly_to_poly_success() {
      var code = buildSourceCode("[A] param", "[]");
      module(code).loadsWithSuccess();
    }

    @Test
    void poly_to_poly_error() {
      var code = buildSourceCode("(A)->A param", "[]");
      module(code)
          .loadsWithError(
              2,
              illegalCallMessage(
                  sFuncType(sFuncType(var2(), var2()), sStringType()), list(sVar3ArrayT())));
    }
  }

  @Nested
  class _array_element_types {
    @Test
    void fails_when_types_have_no_common_super_type() {
      var sourceCode = """
          result = [1, "abc"];
          """;
      module(sourceCode).loadsWithError(1, arrayTypeMessage(1, sIntType(), sStringType()));
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
      module(code)
          .loadsWithError(1, illegalCallMessage(sFuncType(varA(), varA()), list(sStringType())));
    }
  }

  @Test
  void regression_test_type_error_in_param_default_value_should_fail_gracefully_with_error() {
    var code = """
            f([String] param = [7, "abc"]) = 3;
            """;
    module(code).loadsWithError(1, arrayTypeMessage(1, sIntType(), sStringType()));
  }
}
