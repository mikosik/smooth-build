package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.testing.TestingModLoader.err;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class ExprSUsageTest extends TestingContext {
  @Nested
  class _blob_literal_used_as {
    @Test
    public void func_arg() {
      module("""
          String myFunc(Blob b) = "abc";
          result = myFunc(0x01);
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_body() {
      module("""
          result() = 0x01;
          """)
          .loadsWithSuccess();
    }

    @Test
    public void value_body() {
      module("""
          result = 0x01;
          """)
          .loadsWithSuccess();
    }

    @Test
    public void array_elem() {
      module("""
           result = [ 0x01 ];
           """)
          .loadsWithSuccess();
    }

    @Test
    public void param_default_arg() {
      module("""
        String myFunc(Blob b = 0x01) = "abc";
        """)
          .loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression_fails() {
      module("""
          result = 0x01("abc");
          """)
          .loadsWithError(1, """
              mismatched input '(' expecting {';', '|'}
              result = 0x01("abc");
                           ^""");
    }

    @Test
    public void struct_in_select_expression_fails() {
      module(
          """
          result = 0x01.accessedField;
          """)
          .loadsWithError(1, """
              mismatched input '.' expecting {';', '|'}
              result = 0x01.accessedField;
                           ^""");
    }
  }

  @Nested
  class _int_literal_used_as {
    @Test
    public void func_arg() {
      module("""
          String myFunc(Int i) = "abc";
          result = myFunc(123);
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_body() {
      module("""
          result() = 123;
          """)
          .loadsWithSuccess();
    }

    @Test
    public void value_body() {
      module("""
          result = 123;
          """)
          .loadsWithSuccess();
    }

    @Test
    public void array_elem() {
      module("""
           result = [ 123 ];
           """)
          .loadsWithSuccess();
    }

    @Test
    public void param_default_arg() {
      module("""
        String myFunc(Int i = 123) = "abc";
        """)
          .loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression_fails() {
      module("""
          result = 123("abc");
          """)
          .loadsWithError(1, """
              mismatched input '(' expecting {';', '|'}
              result = 123("abc");
                          ^""");
    }

    @Test
    public void struct_in_select_expression_fails() {
      module(
          """
          result = 123.accessedField;
          """)
          .loadsWithError(1, """
              mismatched input '.' expecting {';', '|'}
              result = 123.accessedField;
                          ^""");
    }
  }

  @Nested
  class _string_literal_used_as {
    @Test
    public void func_arg() {
      module("""
        String myFunc(String param) = "abc";
        result = myFunc("abc");
        """)
          .loadsWithSuccess();
    }

    @Test
    public void func_body() {
      module("""
          result() = "abc";
          """)
          .loadsWithSuccess();
    }

    @Test
    public void value_body() {
      module("""
          result = "abc";
          """)
          .loadsWithSuccess();
    }

    @Test
    public void array_elem() {
      module("""
           result = [ "abc" ];
           """)
          .loadsWithSuccess();
    }

    @Test
    public void param_default_arg() {
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
          .loadsWithError(1, """
              mismatched input '(' expecting {';', '|'}
              result = "text"("abc");
                             ^""");
    }

    @Test
    public void struct_in_select_expression_fails() {
      module(
          """
          result = "abc".accessedField;
          """)
          .loadsWithError(1, """
              mismatched input '.' expecting {';', '|'}
              result = "abc".accessedField;
                            ^""");
    }
  }

  @Nested
  class _array_literal_used_as {
    @Test
    public void func_arg() {
      module("""
        myFunc([String] param) = "abc";
        result = myFunc(["abc"]);
        """)
          .loadsWithSuccess();
    }

    @Test
    public void func_body() {
      module("""
          result() = ["abc"];
          """)
          .loadsWithSuccess();
    }

    @Test
    public void value_body() {
      module("""
          result = ["abc"];
          """)
          .loadsWithSuccess();
    }

    @Test
    public void array_elem() {
      module("""
           result = [ ["abc"] ];
           """)
          .loadsWithSuccess();
    }

    @Test
    public void param_default_arg() {
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
          .loadsWithError(1, """
              mismatched input '(' expecting {';', '|'}
              result = ["text"]("abc");
                               ^""");
    }

    @Test
    public void struct_in_select_expression_fails() {
      module(
          """
          result = ["abc"].accessedField;
          """)
          .loadsWithError(1, """
              mismatched input '.' expecting {';', '|'}
              result = ["abc"].accessedField;
                              ^""");
    }
  }

  @Nested
  class _select_used_as {
    @Test
    public void func_arg() {
      module("""
        MyStruct {
          String field,
        }
        myValue = myStruct("abc");
        String myFunc(String s) = "abc";
        result = myFunc(myValue.field);
        """)
          .loadsWithSuccess();
    }

    @Test
    public void func_body() {
      module("""
          MyStruct {
            String field,
          }
          myValue = myStruct("abc");
          result() = myValue.field;
          """)
          .loadsWithSuccess();
    }

    @Test
    public void value_body() {
      module("""
          MyStruct {
            String field,
          }
          myValue = myStruct("abc");
          result = myValue.field;
          """)
          .loadsWithSuccess();
    }

    @Test
    public void array_elem() {
      module("""
           MyStruct {
             String field,
           }
           myValue = myStruct("abc");
           result = [ myValue.field ];
           """)
          .loadsWithSuccess();
    }

    @Test
    public void param_default_arg() {
      module("""
        MyStruct {
          String field,
        }
        value = myStruct("abc");
        String myFunc(String value = value.field) = "abc";
        """)
          .loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression() {
      module("""
        MyStruct {
          String() myFunc
        }
        String justAbc() = "abc";
        result = myStruct(justAbc).myFunc();
        """)
          .loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression_fails_when_field_type_is_not_a_func() {
      module("""
        MyStruct {
          String myField
        }
        result = myStruct("abc").myField();
        """)
          .loadsWithError(4, "expression cannot be called as it is not a function but `String`.");
    }

    @Test
    public void struct_in_select_expression() {
      String code = """
            S1 {
              S2 f1,
            }
            S2 {
              String f2,
            }
            String result = s1(s2("abc")).f1.f2;
            """;
      module(code)
          .loadsWithSuccess();
    }

    @Test
    public void struct_in_select_expression_fails_when_field_type_is_not_a_struct() {
      String code = """
            MyStruct {
              String myField,
            }
            String result = myStruct("abc").myField.otherField;
            """;
      module(code)
          .loadsWithError(4, "Type `String` is not a struct so it doesn't have `otherField` field.");
    }
  }

  @Nested
  class _pipe_used_as {
    @Test
    public void func_arg() {
      module("""
          A myIdentity(A a) = a;
          String myFunc(String param) = "abc";
          result = myFunc("abc" | myIdentity());
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_body() {
      module("""
          A myIdentity(A a) = a;
          String myFunc(String param) = "abc";
          result() = "abc" | myIdentity();
          """)
          .loadsWithSuccess();
    }

    @Test
    public void value_body() {
      module("""
          A myIdentity(A a) = a;
          String myFunc(String param) = "abc";
          result = "abc" | myIdentity();
          """)
          .loadsWithSuccess();
    }

    @Test
    public void array_elem() {
      module("""
             String myIdentity(String string) = string;
             result = [ "abc" | myIdentity() ];
             """)
          .loadsWithSuccess();
    }

    @Test
    public void param_default_arg() {
      module("""
          A myIdentity(A a) = a;
          String myFunc(String param = "abc" | myIdentity()) = "abc";
          """)
          .loadsWithSuccess();
    }
  }

  @Nested
  class _call_used_as {
    @Test
    public void func_arg() {
      module("""
        String otherFunc() = "abc";
        String myFunc(String param) = "abc";
        result = myFunc(otherFunc());
        """)
          .loadsWithSuccess();
    }

    @Test
    public void func_body() {
      module("""
          String otherFunc() = "abc";
          result() = otherFunc();
          """)
          .loadsWithSuccess();
    }

    @Test
    public void value_body() {
      module("""
          String otherFunc() = "abc";
          result = otherFunc();
          """)
          .loadsWithSuccess();
    }

    @Test
    public void array_elem() {
      module("""
           String myFunc() = "abc";
           result = [ myFunc() ];
           """)
          .loadsWithSuccess();
    }

    @Test
    public void param_default_arg() {
      module("""
        String myFunc() = "abc";
        String otherFunc(String value = myFunc()) = "abc";
        """)
          .loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression() {
      module("""
        String justAbc() = "abc";
        String() highOrderFunc() = justAbc;
        result = highOrderFunc()();
        """)
          .loadsWithSuccess();
    }

    @Test
    public void struct_in_select_expression() {
      String code = """
            MyStruct {
              String myField
            }
            myFunc() = myStruct("abc");
            result = myFunc().myField;
            """;
      module(code)
          .loadsWithSuccess();
    }
  }

  @Nested
  class _func_reference_used_as {
    @Test
    public void func_arg() {
      module("""
          String otherFunc() = "abc";
          String myFunc(String() param) = "abc";
          result = myFunc(otherFunc);
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_body() {
      module("""
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
           result = [ myFunc ];
           """)
          .loadsWithSuccess();
    }

    @Test
    public void param_default_arg() {
      module("""
          String myFunc() = "abc";
          String otherFunc(String() value = myFunc) = "abc";
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
      String code = """
            myFunc() = "abc";
            result = myFunc.myField;
            """;
      module(code)
          .loadsWithError(2, "Type `String()` is not a struct so it doesn't have `myField` field.");
    }
  }

  @Nested
  class _func_param_used_as {
    @Test
    public void func_arg() {
      module("""
          myIdentity(String string) = string;
          myFunc(String string) = myIdentity(string);
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_body() {
      module("""
          myFunc(String string) = string;
          """)
          .loadsWithSuccess();
    }

    @Test
    public void array_elem() {
      module("""
          myFunc(String string) = [ string ];
           """)
          .loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression() {
      module("""
          myFunc(String() param) = param();
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression_fails_when_param_type_is_not_a_func() {
      module("""
          myFunc(String param) = param();
          """)
          .loadsWithError(1, "`param` cannot be called as it is not a function but `String`.");
    }

    @Test
    public void struct_in_select_expression() {
      String code = """
            MyStruct {
              String myField
            }
            myFunc(MyStruct param) = param.myField;
            """;
      module(code)
          .loadsWithSuccess();
    }

    @Test
    public void struct_in_select_expression_fails_when_param_type_is_not_a_struct() {
      String code = """
            myFunc(String param) = param.myField;
            """;
      module(code)
          .loadsWithError(1, "Type `String` is not a struct so it doesn't have `myField` field.");
    }
  }

  @Nested
  class _value_reference_used_as {
    @Test
    public void func_arg() {
      module("""
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
           result = [ myValue ];
           """)
          .loadsWithSuccess();
    }

    @Test
    public void param_default_arg() {
      module("""
          String myValue = "abc";
          String myFunc(String value = myValue) = "abc";
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_in_call_expression() {
      module("""
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
          .loadsWithError(2, "`myValue` cannot be called as it is not a function but `String`.");
    }

    @Test
    public void struct_in_select_expression() {
      String code = """
            MyStruct {
              String myField
            }
            myValue = myStruct("abc");
            result = myValue.myField;
            """;
      module(code)
          .loadsWithSuccess();
    }

    @Test
    public void struct_in_select_expression_fails_when_its_type_is_not_struct() {
      module("""
          myValue = "abc";
          result = myValue.someField;
          """)
          .loadsWithError(
              2, "Type `String` is not a struct so it doesn't have `someField` field.");
    }
  }

  @Nested
  class _struct_type_used_as {
    @Test
    public void func_arg_fails() {
      module("""
          MyStruct {}
          String myFunc(String param) = "abc";
          result = myFunc(MyStruct);
          """)
          .loadsWithError(3, """
              extraneous input 'MyStruct' expecting {')', '[', NAME, INT, BLOB, STRING}
              result = myFunc(MyStruct);
                              ^^^^^^^^""");
    }

    @Test
    public void func_body_fails() {
      module("""
          MyStruct {}
          result() = MyStruct;
          """)
          .loadsWithError(2, """
              mismatched input 'MyStruct' expecting {'[', NAME, INT, BLOB, STRING}
              result() = MyStruct;
                         ^^^^^^^^""");
    }

    @Test
    public void value_body_fails() {
      module("""
          MyStruct {}
          result = MyStruct;
          """)
          .loadsWithError(2, """
              mismatched input 'MyStruct' expecting {'[', NAME, INT, BLOB, STRING}
              result = MyStruct;
                       ^^^^^^^^""");
    }

    @Test
    public void array_elem_fails() {
      module("""
          MyStruct {}
          result = [ MyStruct ];
          """)
          .loadsWithError(2, """
              extraneous input 'MyStruct' expecting {'[', ']', NAME, INT, BLOB, STRING}
              result = [ MyStruct ];
                         ^^^^^^^^""");
    }

    @Test
    public void param_default_arg_fails() {
      module("""
          MyStruct {}
          String myFunc(String value = MyStruct) = "abc";
          """)
          .loadsWithError(2, """
              mismatched input 'MyStruct' expecting {'[', NAME, INT, BLOB, STRING}
              String myFunc(String value = MyStruct) = "abc";
                                           ^^^^^^^^""");
    }

    @Test
    public void func_in_call_expression_fails() {
      module("""
            MyStruct {}
            result = MyStruct();
            """)
          .loadsWith(
              err(2, """
                  mismatched input 'MyStruct' expecting {'[', NAME, INT, BLOB, STRING}
                  result = MyStruct();
                           ^^^^^^^^"""),
              err(2, """
                  missing NAME at ';'
                  result = MyStruct();
                                     ^"""));
    }

    @Test
    public void struct_in_select_expression() {
      String code = """
            MyStruct {
              String myField
            }
            result = MyStruct.myField;
            """;
      module(code)
          .loadsWithError(4, """
              mismatched input 'MyStruct' expecting {'[', NAME, INT, BLOB, STRING}
              result = MyStruct.myField;
                       ^^^^^^^^""");
    }

    @Test
    public void field_type() {
      module("""
          ReferencingStruct {
           MyStruct field
          }
          MyStruct {}
          """)
          .loadsWithSuccess();
    }

    @Test
    public void field_arrayed_type() {
      module("""
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
      module("""
          @Native("Impl.met")
          MyStruct myFunc();
          MyStruct myValue = myFunc();
          MyStruct {}
          """)
          .loadsWithSuccess();
    }

    @Test
    public void value_arrayed_type() {
      module("""
          @Native("Impl.met")
          [MyStruct] myValue();
          MyStruct {}
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_param_type() {
      module("""
          String myFunc(MyStruct param) = "abc";
          MyStruct {}
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_arrayed_type() {
      module("""
          String myFunc([MyStruct] param) = "abc";
          MyStruct {}
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_result_type() {
      module("""
          @Native("Impl.met")
          MyStruct myFunc(String param);
          MyStruct {}
          """)
          .loadsWithSuccess();
    }

    @Test
    public void func_arrayed_result_type() {
      module("""
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
      module("""
          String myFunc(String param) = "abc";
          result = myFunc(A);
          """)
          .loadsWithError(2, """
              extraneous input 'A' expecting {')', '[', NAME, INT, BLOB, STRING}
              result = myFunc(A);
                              ^""");
    }

    @Test
    public void func_body_fails() {
      module("""
          result() = A;
          """)
          .loadsWithError(1, """
              mismatched input 'A' expecting {'[', NAME, INT, BLOB, STRING}
              result() = A;
                         ^""");
    }

    @Test
    public void value_body_fails() {
      module("""
          result = A;
          """)
          .loadsWithError(1, """
              mismatched input 'A' expecting {'[', NAME, INT, BLOB, STRING}
              result = A;
                       ^""");
    }

    @Test
    public void array_elem_fails() {
      module("""
          result = [ A ];
          """)
          .loadsWithError(1, """
              extraneous input 'A' expecting {'[', ']', NAME, INT, BLOB, STRING}
              result = [ A ];
                         ^""");
    }

    @Test
    public void param_default_arg_fails() {
      module("""
          String myFunc(String value = A) = "abc";
          """)
          .loadsWithError(1, """
              mismatched input 'A' expecting {'[', NAME, INT, BLOB, STRING}
              String myFunc(String value = A) = "abc";
                                           ^""");
    }

    @Test
    public void func_in_call_expression_fails() {
      module("""
            result = A();
            """)
          .loadsWith(
              err(1, """
                  mismatched input 'A' expecting {'[', NAME, INT, BLOB, STRING}
                  result = A();
                           ^"""),
              err(1, """
                  missing NAME at ';'
                  result = A();
                              ^"""));
    }

    @Test
    public void struct_in_select_expression() {
      String code = """
            result = A.myField;
            """;
      module(code)
          .loadsWithError(1, """
              mismatched input 'A' expecting {'[', NAME, INT, BLOB, STRING}
              result = A.myField;
                       ^""");
    }
  }
}
