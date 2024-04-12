package org.smoothbuild.compilerfrontend.acceptance;

import static org.smoothbuild.compilerfrontend.testing.FrontendCompilerTester.err;
import static org.smoothbuild.compilerfrontend.testing.FrontendCompilerTester.module;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SExprUsageTest {
  @Nested
  class _blob_literal_used_as {
    @Test
    public void func_arg() {
      module(
              """
          String myFunc(Blob b) = "abc";
          result = myFunc(0x01);
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_body() {
      module("""
          result() = 0x01;
          """).loadsWithSuccess();
    }

    @Test
    public void value_body() {
      module("""
          result = 0x01;
          """).loadsWithSuccess();
    }

    @Test
    public void array_elem() {
      module("""
          result = [0x01];
          """).loadsWithSuccess();
    }

    @Test
    public void param_default_value() {
      module("""
          String myFunc(Blob b = 0x01) = "abc";
          """).loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression_fails() {
      module("""
          result = 0x01("abc");
          """).loadsWithError(1, "Illegal call.");
    }

    @Test
    public void struct_in_select_expression_fails() {
      module("""
          result = 0x01.accessedField;
          """)
          .loadsWithError(1, "Illegal field access.");
    }

    @Test
    public void parens_content() {
      module("""
          result = (0x01);
          """).loadsWithSuccess();
    }
  }

  @Nested
  class _int_literal_used_as {
    @Test
    public void func_arg() {
      module(
              """
          String myFunc(Int i) = "abc";
          result = myFunc(123);
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_body() {
      module("""
          result() = 123;
          """).loadsWithSuccess();
    }

    @Test
    public void value_body() {
      module("""
          result = 123;
          """).loadsWithSuccess();
    }

    @Test
    public void array_elem() {
      module("""
          result = [123];
          """).loadsWithSuccess();
    }

    @Test
    public void param_default_value() {
      module("""
          String myFunc(Int i = 123) = "abc";
          """).loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression_fails() {
      module("""
          result = 123("abc");
          """).loadsWithError(1, "Illegal call.");
    }

    @Test
    public void struct_in_select_expression_fails() {
      module("""
          result = 123.accessedField;
          """)
          .loadsWithError(1, "Illegal field access.");
    }

    @Test
    public void parens_content() {
      module("""
          result = (17);
          """).loadsWithSuccess();
    }
  }

  @Nested
  class _string_literal_used_as {
    @Test
    public void func_arg() {
      module(
              """
          String myFunc(String param) = "abc";
          result = myFunc("abc");
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_body() {
      module("""
          result() = "abc";
          """).loadsWithSuccess();
    }

    @Test
    public void value_body() {
      module("""
          result = "abc";
          """).loadsWithSuccess();
    }

    @Test
    public void array_elem() {
      module("""
          result = ["abc"];
          """).loadsWithSuccess();
    }

    @Test
    public void param_default_value() {
      module("""
          String myFunc(String s = "abc") = "abc";
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression_fails() {
      module("""
          result = "text"("abc");
          """)
          .loadsWithError(1, "Illegal call.");
    }

    @Test
    public void struct_in_select_expression_fails() {
      module("""
          result = "abc".accessedField;
          """)
          .loadsWithError(1, "Illegal field access.");
    }

    @Test
    public void parens_content() {
      module("""
          result = ("abc");
          """).loadsWithSuccess();
    }
  }

  @Nested
  class _array_literal_used_as {
    @Test
    public void func_arg() {
      module(
              """
          myFunc([String] param) = "abc";
          result = myFunc(["abc"]);
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_body() {
      module("""
          result() = ["abc"];
          """).loadsWithSuccess();
    }

    @Test
    public void value_body() {
      module("""
          result = ["abc"];
          """).loadsWithSuccess();
    }

    @Test
    public void array_elem() {
      module("""
          result = [["abc"]];
          """).loadsWithSuccess();
    }

    @Test
    public void param_default_value() {
      module("""
          String myFunc([String] s = ["abc"]) = "abc";
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression_fails() {
      module("""
          result = ["text"]("abc");
          """)
          .loadsWithError(1, "Illegal call.");
    }

    @Test
    public void struct_in_select_expression_fails() {
      module("""
          result = ["abc"].accessedField;
          """)
          .loadsWithError(1, "Illegal field access.");
    }

    @Test
    public void parens_content() {
      module("""
          result = ([1, 2, 3]);
          """).loadsWithSuccess();
    }
  }

  @Nested
  class _select_used_as {
    @Test
    public void func_arg() {
      module(
              """
          MyStruct{
            String field,
          }
          myValue = MyStruct("abc");
          String myFunc(String s) = "abc";
          result = myFunc(myValue.field);
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_body() {
      module(
              """
          MyStruct{
            String field,
          }
          myValue = MyStruct("abc");
          result() = myValue.field;
          """)
          .loadsWithSuccess();
    }

    @Test
    public void value_body() {
      module(
              """
          MyStruct {
            String field,
          }
          myValue = MyStruct("abc");
          result = myValue.field;
          """)
          .loadsWithSuccess();
    }

    @Test
    public void array_elem() {
      module(
              """
          MyStruct{
            String field,
          }
          myValue = MyStruct("abc");
          result = [myValue.field];
          """)
          .loadsWithSuccess();
    }

    @Test
    public void param_default_value() {
      module(
              """
          MyStruct{
            String field,
          }
          value = MyStruct("abc");
          String myFunc(String value = value.field) = "abc";
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression() {
      module(
              """
          MyStruct{
            ()->String myFunc
          }
          String justAbc() = "abc";
          result = MyStruct(justAbc).myFunc();
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression_fails_when_field_type_is_not_a_func() {
      module(
              """
          MyStruct{
            String myField
          }
          result = MyStruct("abc").myField();
          """)
          .loadsWithError(4, "Illegal call.");
    }

    @Test
    public void struct_in_select_expression() {
      String code =
          """
            S1{
              S2 f1,
            }
            S2{
              String f2,
            }
            String result = S1(S2("abc")).f1.f2;
            """;
      module(code).loadsWithSuccess();
    }

    @Test
    public void struct_in_select_expression_fails_when_field_type_is_not_a_struct() {
      var code =
          """
            MyStruct{
              String myField,
            }
            String result = MyStruct("abc").myField.otherField;
            """;
      module(code).loadsWithError(4, "Illegal field access.");
    }

    @Test
    public void parens_content() {
      module(
              """
          MyStruct{
            String field,
          }
          myValue = MyStruct("abc");
          result = (myValue.field);
          """)
          .loadsWithSuccess();
    }
  }

  @Nested
  class _pipe_used_as {
    @Test
    public void func_arg() {
      module(
              """
          A myIdentity(A a) = a;
          String myFunc(String param) = "abc";
          result = myFunc("abc" > myIdentity());
          """)
          .loadsWith(
              err(
                  3,
                  """
                  mismatched input '>' expecting {')', ','}
                  result = myFunc("abc" > myIdentity());
                                        ^"""),
              err(
                  3,
                  """
                  extraneous input ')' expecting ';'
                  result = myFunc("abc" > myIdentity());
                                                      ^"""));
    }

    @Test
    public void func_body() {
      module(
              """
          A myIdentity(A a) = a;
          String myFunc(String param) = "abc";
          result() = "abc" > myIdentity();
          """)
          .loadsWithSuccess();
    }

    @Test
    public void value_body() {
      module(
              """
          A myIdentity(A a) = a;
          result = "abc" > myIdentity();
          """)
          .loadsWithSuccess();
    }

    @Test
    public void array_elem() {
      module(
              """
          String myIdentity(String string) = string;
          result = ["abc" > myIdentity()];
          """)
          .loadsWith(
              err(
                  2,
                  """
                  mismatched input '>' expecting {',', ']'}
                  result = ["abc" > myIdentity()];
                                  ^"""),
              err(
                  2,
                  """
                  extraneous input ']' expecting ';'
                  result = ["abc" > myIdentity()];
                                                ^"""));
    }

    @Test
    public void param_default_value() {
      module(
              """
          A myIdentity(A a) = a;
          String myFunc(String param = "abc" > myIdentity()) = "abc";
          """)
          .loadsWith(
              err(
                  2,
                  """
                  mismatched input '>' expecting {'(', ')', ',', '.'}
                  String myFunc(String param = "abc" > myIdentity()) = "abc";
                                                     ^"""),
              err(
                  2,
                  """
                  extraneous input ')' expecting {'=', ';'}
                  String myFunc(String param = "abc" > myIdentity()) = "abc";
                                                                   ^"""));
    }

    @Test
    public void parens_content() {
      module(
              """
          MyStruct{
            String field,
          }
          myValue = MyStruct("abc");
          result = (myValue.field);
          """)
          .loadsWithSuccess();
    }
  }

  @Nested
  class _call_used_as {
    @Test
    public void func_arg() {
      module(
              """
          String otherFunc() = "abc";
          String myFunc(String param) = "abc";
          result = myFunc(otherFunc());
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_body() {
      module(
              """
          String otherFunc() = "abc";
          result() = otherFunc();
          """)
          .loadsWithSuccess();
    }

    @Test
    public void value_body() {
      module(
              """
          String otherFunc() = "abc";
          result = otherFunc();
          """)
          .loadsWithSuccess();
    }

    @Test
    public void array_elem() {
      module("""
          String myFunc() = "abc";
          result = [myFunc()];
          """)
          .loadsWithSuccess();
    }

    @Test
    public void param_default_value() {
      module(
              """
          String myFunc() = "abc";
          String otherFunc(String value = myFunc()) = "abc";
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression() {
      module(
              """
          String justAbc() = "abc";
          ()->String highOrderFunc() = justAbc;
          result = highOrderFunc()();
          """)
          .loadsWithSuccess();
    }

    @Test
    public void struct_in_select_expression() {
      var code =
          """
            MyStruct{
              String myField
            }
            myFunc() = MyStruct("abc");
            result = myFunc().myField;
            """;
      module(code).loadsWithSuccess();
    }

    @Test
    public void parens_content() {
      module("""
          myFunc() = 7;
          result = (myFunc());
          """)
          .loadsWithSuccess();
    }
  }

  @Nested
  class _lambda_used_as {
    @Test
    public void func_arg() {
      var code =
          """
          Int myFunc(()->Int param) = param();
          result = myFunc(() -> 7);
          """;
      module(code).loadsWithSuccess();
    }

    @Test
    public void func_body() {
      var code = """
          ()->Int myFunc() = () -> 7;
          """;
      module(code).loadsWithSuccess();
    }

    @Test
    public void value_body() {
      var code = """
          ()->Int myValue = () -> 7;
          """;
      module(code).loadsWithSuccess();
    }

    @Test
    public void array_elem() {
      var code = """
          result = [() -> 7];
          """;
      module(code).loadsWithSuccess();
    }

    @Test
    public void param_default_val() {
      var code = """
          Int myFunc(()->Int value = () -> 7) = 17;
          """;
      module(code).loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression() {
      var code = """
          result = ((Int int) -> int)(7);
          """;
      module(code).loadsWithSuccess();
    }

    @Test
    public void struct_in_select_expression_fails() {
      var code = """
          result = (() -> 7).myField;
          """;
      module(code).loadsWithError(1, "Illegal field access.");
    }

    @Test
    public void parens_content() {
      var code = """
          result = (() -> 7);
          """;
      module(code).loadsWithSuccess();
    }
  }

  @Nested
  class _func_reference_used_as {
    @Test
    public void func_arg() {
      module(
              """
          String otherFunc() = "abc";
          String myFunc(()->String param) = "abc";
          result = myFunc(otherFunc);
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_body() {
      module(
              """
          String otherFunc() = "abc";
          result() = otherFunc;
          """)
          .loadsWithSuccess();
    }

    @Test
    public void value_body() {
      module("""
          String otherFunc() = "abc";
          result = otherFunc;
          """)
          .loadsWithSuccess();
    }

    @Test
    public void array_elem() {
      module("""
          String myFunc() = "abc";
          result = [myFunc];
          """)
          .loadsWithSuccess();
    }

    @Test
    public void param_default_value() {
      module(
              """
          String myFunc() = "abc";
          String otherFunc(()->String value = myFunc) = "abc";
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression() {
      module("""
          String justAbc() = "abc";
          result = justAbc();
          """)
          .loadsWithSuccess();
    }

    @Test
    public void struct_in_select_expression_fails() {
      var code =
          """
            myFunc() = "abc";
            result = myFunc.myField;
            """;
      module(code).loadsWithError(2, "Illegal field access.");
    }

    @Test
    public void parens_content() {
      module("""
          myFunc() = 7;
          result = (myFunc);
          """)
          .loadsWithSuccess();
    }
  }

  @Nested
  class _func_param_used_as {
    @Test
    public void func_arg() {
      module(
              """
          myIdentity(String string) = string;
          myFunc(String string) = myIdentity(string);
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_body() {
      module("""
          myFunc(String string) = string;
          """).loadsWithSuccess();
    }

    @Test
    public void array_elem() {
      module("""
          myFunc(String string) = [string];
           """).loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression() {
      module("""
          myFunc(()->String param) = param();
          """).loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression_fails_when_param_type_is_not_a_func() {
      module("""
          myFunc(String param) = param();
          """)
          .loadsWithError(1, "Illegal call.");
    }

    @Test
    public void struct_in_select_expression() {
      var code =
          """
            MyStruct{
              String myField
            }
            myFunc(MyStruct param) = param.myField;
            """;
      module(code).loadsWithSuccess();
    }

    @Test
    public void struct_in_select_expression_fails_when_param_type_is_not_a_struct() {
      String code = """
            myFunc(String param) = param.myField;
            """;
      module(code).loadsWithError(1, "Illegal field access.");
    }

    @Test
    public void parens_content() {
      module("""
          myFunc(String string) = (string);
          """).loadsWithSuccess();
    }
  }

  @Nested
  class _value_reference_used_as {
    @Test
    public void func_arg() {
      module(
              """
          String myValue = "abc";
          String myFunc(String param) = "abc";
          result = myFunc(myValue);
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_body() {
      module("""
          String myValue = "abc";
          result() = myValue;
          """)
          .loadsWithSuccess();
    }

    @Test
    public void value_body() {
      module("""
          String myValue = "abc";
          result = myValue;
          """)
          .loadsWithSuccess();
    }

    @Test
    public void array_elem() {
      module("""
          String myValue = "abc";
          result = [myValue];
          """)
          .loadsWithSuccess();
    }

    @Test
    public void param_default_value() {
      module(
              """
          String myValue = "abc";
          String myFunc(String value = myValue) = "abc";
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression() {
      module(
              """
          String justAbc() = "abc";
          myValue = justAbc;
          result = myValue();
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression_fails_when_value_type_is_not_a_func() {
      module("""
          myValue = "abc";
          result = myValue();
          """)
          .loadsWithError(2, "Illegal call.");
    }

    @Test
    public void struct_in_select_expression() {
      var code =
          """
            MyStruct {
              String myField
            }
            myValue = MyStruct("abc");
            result = myValue.myField;
            """;
      module(code).loadsWithSuccess();
    }

    @Test
    public void struct_in_select_expression_fails_when_its_type_is_not_struct() {
      module("""
          myValue = "abc";
          result = myValue.someField;
          """)
          .loadsWithError(2, "Illegal field access.");
    }

    @Test
    public void parens_content() {
      module("""
          myValue = 7;
          result = (myValue);
          """)
          .loadsWithSuccess();
    }
  }

  @Nested
  class _struct_type_used_as {
    @Test
    public void field_type() {
      module(
              """
          ReferencingStruct {
           MyStruct field
          }
          MyStruct {}
          """)
          .loadsWithSuccess();
    }

    @Test
    public void field_arrayed_type() {
      module(
              """
          ReferencingStruct {
            String firstField,
            [MyStruct] field
          }
          MyStruct {}
          """)
          .loadsWithSuccess();
    }

    @Test
    public void value_type() {
      module(
              """
          @Native("Impl.met")
          MyStruct myFunc();
          MyStruct myValue = myFunc();
          MyStruct {}
          """)
          .loadsWithSuccess();
    }

    @Test
    public void value_arrayed_type() {
      module(
              """
          @Native("Impl.met")
          [MyStruct] myValue();
          MyStruct {}
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_param_type() {
      module(
              """
          String myFunc(MyStruct param) = "abc";
          MyStruct {}
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_arrayed_type() {
      module(
              """
          String myFunc([MyStruct] param) = "abc";
          MyStruct {}
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_result_type() {
      module(
              """
          @Native("Impl.met")
          MyStruct myFunc(String param);
          MyStruct {}
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_arrayed_result_type() {
      module(
              """
          [MyStruct] myFunc(String param) = [];
          MyStruct {}
          """)
          .loadsWithSuccess();
    }
  }

  @Nested
  class _type_var_used_as {
    @Test
    public void func_arg_fails() {
      module(
              """
          String myFunc(String param) = "abc";
          result = myFunc(A);
          """)
          .loadsWithError(2, "`A` is undefined.");
    }

    @Test
    public void func_body_fails() {
      module("""
          result() = A;
          """).loadsWithError(1, "`A` is undefined.");
    }

    @Test
    public void value_body_fails() {
      module("""
          result = A;
          """).loadsWithError(1, "`A` is undefined.");
    }

    @Test
    public void array_elem_fails() {
      module("""
          result = [A];
          """).loadsWithError(1, "`A` is undefined.");
    }

    @Test
    public void param_default_value_fails() {
      module("""
          String myFunc(String value = A) = "abc";
          """)
          .loadsWithError(1, "`A` is undefined.");
    }

    @Test
    public void func_in_call_expression_fails() {
      module("""
          result = A();
          """).loadsWith(err(1, "`A` is undefined."));
    }

    @Test
    public void struct_in_select_expression() {
      String code = """
            result = A.myField;
            """;
      module(code).loadsWithError(1, "`A` is undefined.");
    }

    @Test
    public void parens_content() {
      module("""
          result = (A);
          """).loadsWithError(1, "`A` is undefined.");
    }
  }

  @Nested
  class _parens_used_as {
    @Test
    public void func_arg() {
      var code =
          """
          Int myFunc(Int int) = int;
          result = myFunc((7));
          """;
      module(code).loadsWithSuccess();
    }

    @Test
    public void func_body() {
      var code = """
          result() = (7);
          """;
      module(code).loadsWithSuccess();
    }

    @Test
    public void value_body() {
      var code = """
          result = (7);
          """;
      module(code).loadsWithSuccess();
    }

    @Test
    public void array_elem() {
      var code = """
          result = [(7)];
          """;
      module(code).loadsWithSuccess();
    }

    @Test
    public void param_default_value() {
      var code = """
          Int myFunc(Int int = (7)) = int;
          """;
      module(code).loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression() {
      var code = """
          Int myFunc() = 7;
          result = (myFunc)();
          """;
      module(code).loadsWithSuccess();
    }

    @Test
    public void struct_in_select_expression() {
      var code =
          """
          MyStruct{
            Int myField
          }
          result = (MyStruct(7)).myField;
          """;
      module(code).loadsWithSuccess();
    }

    @Test
    public void parens_content() {
      var code = """
          result = ((7));
          """;
      module(code).loadsWithSuccess();
    }
  }
}
