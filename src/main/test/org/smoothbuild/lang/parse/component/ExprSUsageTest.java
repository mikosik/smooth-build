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
      mod("""
          String myFunc(Blob b) = "abc";
          result = myFunc(0x01);
          """)
          .loadsSuccessfully();
    }

    @Test
    public void func_body() {
      mod("""
          result() = 0x01;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      mod("""
          result = 0x01;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_elem() {
      mod("""
           result = [ 0x01 ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void param_default_arg() {
      mod("""
        String myFunc(Blob b = 0x01) = "abc";
        """)
          .loadsSuccessfully();
    }

    @Test
    public void func_in_call_expression_fails() {
      mod("""
          result = 0x01("abc");
          """)
          .loadsWithError(1, """
              mismatched input '(' expecting {';', '|'}
              result = 0x01("abc");
                           ^""");
    }

    @Test
    public void struct_in_select_expression_fails() {
      mod(
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
      mod("""
          String myFunc(Int i) = "abc";
          result = myFunc(123);
          """)
          .loadsSuccessfully();
    }

    @Test
    public void func_body() {
      mod("""
          result() = 123;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      mod("""
          result = 123;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_elem() {
      mod("""
           result = [ 123 ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void param_default_arg() {
      mod("""
        String myFunc(Int i = 123) = "abc";
        """)
          .loadsSuccessfully();
    }

    @Test
    public void func_in_call_expression_fails() {
      mod("""
          result = 123("abc");
          """)
          .loadsWithError(1, """
              mismatched input '(' expecting {';', '|'}
              result = 123("abc");
                          ^""");
    }

    @Test
    public void struct_in_select_expression_fails() {
      mod(
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
      mod("""
        String myFunc(String param) = "abc";
        result = myFunc("abc");
        """)
          .loadsSuccessfully();
    }

    @Test
    public void func_body() {
      mod("""
          result() = "abc";
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      mod("""
          result = "abc";
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_elem() {
      mod("""
           result = [ "abc" ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void param_default_arg() {
      mod("""
        String myFunc(String s = "abc") = "abc";
        """)
          .loadsSuccessfully();
    }

    @Test
    public void func_in_call_expression_fails() {
      mod("""
          result = "text"("abc");
          """)
          .loadsWithError(1, """
              mismatched input '(' expecting {';', '|'}
              result = "text"("abc");
                             ^""");
    }

    @Test
    public void struct_in_select_expression_fails() {
      mod(
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
      mod("""
        myFunc([String] param) = "abc";
        result = myFunc(["abc"]);
        """)
          .loadsSuccessfully();
    }

    @Test
    public void func_body() {
      mod("""
          result() = ["abc"];
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      mod("""
          result = ["abc"];
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_elem() {
      mod("""
           result = [ ["abc"] ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void param_default_arg() {
      mod("""
        String myFunc([String] s = ["abc"]) = "abc";
        """)
          .loadsSuccessfully();
    }

    @Test
    public void func_in_call_expression_fails() {
      mod("""
          result = ["text"]("abc");
          """)
          .loadsWithError(1, """
              mismatched input '(' expecting {';', '|'}
              result = ["text"]("abc");
                               ^""");
    }

    @Test
    public void struct_in_select_expression_fails() {
      mod(
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
      mod("""
        MyStruct {
          String field,
        }
        myValue = myStruct("abc");
        String myFunc(String s) = "abc";
        result = myFunc(myValue.field);
        """)
          .loadsSuccessfully();
    }

    @Test
    public void func_body() {
      mod("""
          MyStruct {
            String field,
          }
          myValue = myStruct("abc");
          result() = myValue.field;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      mod("""
          MyStruct {
            String field,
          }
          myValue = myStruct("abc");
          result = myValue.field;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_elem() {
      mod("""
           MyStruct {
             String field,
           }
           myValue = myStruct("abc");
           result = [ myValue.field ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void param_default_arg() {
      mod("""
        MyStruct {
          String field,
        }
        value = myStruct("abc");
        String myFunc(String value = value.field) = "abc";
        """)
          .loadsSuccessfully();
    }

    @Test
    public void func_in_call_expression() {
      mod("""
        MyStruct {
          String() myFunc
        }
        String justAbc() = "abc";
        result = myStruct(justAbc).myFunc();
        """)
          .loadsSuccessfully();
    }

    @Test
    public void func_in_call_expression_fails_when_field_type_is_not_a_func() {
      mod("""
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
      mod(code)
          .loadsSuccessfully();
    }

    @Test
    public void struct_in_select_expression_fails_when_field_type_is_not_a_struct() {
      String code = """
            MyStruct {
              String myField,
            }
            String result = myStruct("abc").myField.otherField;
            """;
      mod(code)
          .loadsWithError(4, "Type `String` is not a struct so it doesn't have `otherField` field.");
    }
  }

  @Nested
  class _pipe_used_as {
    @Test
    public void func_arg() {
      mod("""
          A myIdentity(A a) = a;
          String myFunc(String param) = "abc";
          result = myFunc("abc" | myIdentity());
          """)
          .loadsSuccessfully();
    }

    @Test
    public void func_body() {
      mod("""
          A myIdentity(A a) = a;
          String myFunc(String param) = "abc";
          result() = "abc" | myIdentity();
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      mod("""
          A myIdentity(A a) = a;
          String myFunc(String param) = "abc";
          result = "abc" | myIdentity();
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_elem() {
      mod("""
             String myIdentity(String string) = string;
             result = [ "abc" | myIdentity() ];
             """)
          .loadsSuccessfully();
    }

    @Test
    public void param_default_arg() {
      mod("""
          A myIdentity(A a) = a;
          String myFunc(String param = "abc" | myIdentity()) = "abc";
          """)
          .loadsSuccessfully();
    }
  }

  @Nested
  class _call_used_as {
    @Test
    public void func_arg() {
      mod("""
        String otherFunc() = "abc";
        String myFunc(String param) = "abc";
        result = myFunc(otherFunc());
        """)
          .loadsSuccessfully();
    }

    @Test
    public void func_body() {
      mod("""
          String otherFunc() = "abc";
          result() = otherFunc();
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      mod("""
          String otherFunc() = "abc";
          result = otherFunc();
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_elem() {
      mod("""
           String myFunc() = "abc";
           result = [ myFunc() ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void param_default_arg() {
      mod("""
        String myFunc() = "abc";
        String otherFunc(String value = myFunc()) = "abc";
        """)
          .loadsSuccessfully();
    }

    @Test
    public void func_in_call_expression() {
      mod("""
        String justAbc() = "abc";
        String() highOrderFunc() = justAbc;
        result = highOrderFunc()();
        """)
          .loadsSuccessfully();
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
      mod(code)
          .loadsSuccessfully();
    }
  }

  @Nested
  class _func_reference_used_as {
    @Test
    public void func_arg() {
      mod("""
          String otherFunc() = "abc";
          String myFunc(String() param) = "abc";
          result = myFunc(otherFunc);
          """)
          .loadsSuccessfully();
    }

    @Test
    public void func_body() {
      mod("""
          String otherFunc() = "abc";
          result() = otherFunc;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      mod("""
          String otherFunc() = "abc";
          result = otherFunc;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_elem() {
      mod("""
           String myFunc() = "abc";
           result = [ myFunc ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void param_default_arg() {
      mod("""
          String myFunc() = "abc";
          String otherFunc(String() value = myFunc) = "abc";
          """)
          .loadsSuccessfully();
    }

    @Test
    public void func_in_call_expression() {
      mod("""
          String justAbc() = "abc";
          result = justAbc();
          """)
          .loadsSuccessfully();
    }

    @Test
    public void struct_in_select_expression_fails() {
      String code = """
            myFunc() = "abc";
            result = myFunc.myField;
            """;
      mod(code)
          .loadsWithError(2, "Type `String()` is not a struct so it doesn't have `myField` field.");
    }
  }

  @Nested
  class _func_param_used_as {
    @Test
    public void func_arg() {
      mod("""
          myIdentity(String string) = string;
          myFunc(String string) = myIdentity(string);
          """)
          .loadsSuccessfully();
    }

    @Test
    public void func_body() {
      mod("""
          myFunc(String string) = string;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_elem() {
      mod("""
          myFunc(String string) = [ string ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void func_in_call_expression() {
      mod("""
          myFunc(String() param) = param();
          """)
          .loadsSuccessfully();
    }

    @Test
    public void func_in_call_expression_fails_when_param_type_is_not_a_func() {
      mod("""
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
      mod(code)
          .loadsSuccessfully();
    }

    @Test
    public void struct_in_select_expression_fails_when_param_type_is_not_a_struct() {
      String code = """
            myFunc(String param) = param.myField;
            """;
      mod(code)
          .loadsWithError(1, "Type `String` is not a struct so it doesn't have `myField` field.");
    }
  }

  @Nested
  class _value_reference_used_as {
    @Test
    public void func_arg() {
      mod("""
          String myValue = "abc";
          String myFunc(String param) = "abc";
          result = myFunc(myValue);
          """)
          .loadsSuccessfully();
    }

    @Test
    public void func_body() {
      mod("""
          String myValue = "abc";
          result() = myValue;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      mod("""
          String myValue = "abc";
          result = myValue;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_elem() {
      mod("""
           String myValue = "abc";
           result = [ myValue ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void param_default_arg() {
      mod("""
          String myValue = "abc";
          String myFunc(String value = myValue) = "abc";
          """)
          .loadsSuccessfully();
    }

    @Test
    public void func_in_call_expression() {
      mod("""
        String justAbc() = "abc";
        myValue = justAbc;
        result = myValue();
        """)
          .loadsSuccessfully();
    }

    @Test
    public void func_in_call_expression_fails_when_value_type_is_not_a_func() {
      mod("""
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
      mod(code)
          .loadsSuccessfully();
    }

    @Test
    public void struct_in_select_expression_fails_when_its_type_is_not_struct() {
      mod("""
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
      mod("""
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
      mod("""
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
      mod("""
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
      mod("""
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
      mod("""
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
      mod("""
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
      mod(code)
          .loadsWithError(4, """
              mismatched input 'MyStruct' expecting {'[', NAME, INT, BLOB, STRING}
              result = MyStruct.myField;
                       ^^^^^^^^""");
    }

    @Test
    public void field_type() {
      mod("""
          ReferencingStruct {
           MyStruct field
          }
          MyStruct {}
          """)
          .loadsSuccessfully();
    }

    @Test
    public void field_arrayed_type() {
      mod("""
          ReferencingStruct {
           String firstField,
           [MyStruct] field
          }
          MyStruct {}
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_type() {
      mod("""
          @Native("Impl.met")
          MyStruct myFunc();
          MyStruct myValue = myFunc();
          MyStruct {}
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_arrayed_type() {
      mod("""
          @Native("Impl.met")
          [MyStruct] myValue();
          MyStruct {}
          """)
          .loadsSuccessfully();
    }

    @Test
    public void func_param_type() {
      mod("""
          String myFunc(MyStruct param) = "abc";
          MyStruct {}
          """)
          .loadsSuccessfully();
    }

    @Test
    public void func_arrayed_type() {
      mod("""
          String myFunc([MyStruct] param) = "abc";
          MyStruct {}
          """)
          .loadsSuccessfully();
    }

    @Test
    public void func_result_type() {
      mod("""
          @Native("Impl.met")
          MyStruct myFunc(String param);
          MyStruct {}
          """)
          .loadsSuccessfully();
    }

    @Test
    public void func_arrayed_result_type() {
      mod("""
          [MyStruct] myFunc(String param) = [];
          MyStruct {}
          """)
          .loadsSuccessfully();
    }
  }

  @Nested
  class _type_var_used_as {
    @Test
    public void func_arg_fails() {
      mod("""
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
      mod("""
          result() = A;
          """)
          .loadsWithError(1, """
              mismatched input 'A' expecting {'[', NAME, INT, BLOB, STRING}
              result() = A;
                         ^""");
    }

    @Test
    public void value_body_fails() {
      mod("""
          result = A;
          """)
          .loadsWithError(1, """
              mismatched input 'A' expecting {'[', NAME, INT, BLOB, STRING}
              result = A;
                       ^""");
    }

    @Test
    public void array_elem_fails() {
      mod("""
          result = [ A ];
          """)
          .loadsWithError(1, """
              extraneous input 'A' expecting {'[', ']', NAME, INT, BLOB, STRING}
              result = [ A ];
                         ^""");
    }

    @Test
    public void param_default_arg_fails() {
      mod("""
          String myFunc(String value = A) = "abc";
          """)
          .loadsWithError(1, """
              mismatched input 'A' expecting {'[', NAME, INT, BLOB, STRING}
              String myFunc(String value = A) = "abc";
                                           ^""");
    }

    @Test
    public void func_in_call_expression_fails() {
      mod("""
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
      mod(code)
          .loadsWithError(1, """
              mismatched input 'A' expecting {'[', NAME, INT, BLOB, STRING}
              result = A.myField;
                       ^""");
    }
  }

  @Nested
  class _undefined {
    @Nested
    class _eval_cannot_be_used_as {
      @Test
      public void func_arg() {
        mod("""
            String myFunc(Blob b) = "abc";
            result = myFunc(undefined);
            """)
            .loadsWithError(2, "`undefined` is undefined.");
      }

      @Test
      public void func_body() {
        mod("""
            result() = undefined;
            """)
            .loadsWithError(1, "`undefined` is undefined.");
      }

      @Test
      public void func_in_call_expression() {
        mod("""
            result = undefined();
            """)
            .loadsWithError(1, "`undefined` is undefined.");
      }

      @Test
      public void value_body() {
        mod("""
            result = undefined;
            """)
            .loadsWithError(1, "`undefined` is undefined.");
      }

      @Test
      public void array_elem() {
        mod("""
           result = [ undefined ];
           """)
            .loadsWithError(1, "`undefined` is undefined.");
      }

      @Test
      public void param_default_arg() {
        mod("""
        String myFunc(Blob b = undefined) = "abc";
        """)
            .loadsWithError(1, "`undefined` is undefined.");
      }
    }

    @Nested
    class _type_cannot_be_used_as_type_of {
      @Test
      public void value() {
        mod("""
             @Native("Impl.met")
             Nothing nothingFunc();
             Undefined myValue = nothingFunc();
             """)
            .loadsWithError(3, "`Undefined` type is undefined.");
      }

      @Test
      public void func_result() {
        mod("""
             @Native("Impl.met")
             Undefined myFunc();
             """)
            .loadsWithError(2, "`Undefined` type is undefined.");
      }

      @Test
      public void param() {
        mod("""
             String myFunc(Undefined param) = "abc";
             """)
            .loadsWithError(1, "`Undefined` type is undefined.");
      }

      @Test
      public void field() {
        mod("""
             MyStruct {
               Undefined field
             }
             """)
            .loadsWithError(2, "`Undefined` type is undefined.");
      }
    }
  }
}
