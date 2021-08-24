package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.TestModuleLoader.err;
import static org.smoothbuild.lang.TestModuleLoader.module;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ExpressionUsageTest {
  @Nested
  class _blob_literal_used_as {
    @Test
    public void function_argument() {
      module("""
          String myFunction(Blob b) = "abc";
          result = myFunction(0x01);
          """)
          .loadsSuccessfully();
    }

    @Test
    public void function_body() {
      module("""
          result() = 0x01;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      module("""
          result = 0x01;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_element() {
      module("""
           result = [ 0x01 ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void parameter_default_argument() {
      module("""
        String myFunction(Blob b = 0x01) = "abc";
        """)
          .loadsSuccessfully();
    }

    @Test
    public void function_in_call_expression_fails() {
      module("""
          result = 0x01("abc");
          """)
          .loadsWithError(1, """
              mismatched input '(' expecting {';', '|'}
              result = 0x01("abc");
                           ^""");
    }

    @Test
    public void struct_in_field_read_expression_fails() {
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
    public void function_argument() {
      module("""
          String myFunction(Int i) = "abc";
          result = myFunction(123);
          """)
          .loadsSuccessfully();
    }

    @Test
    public void function_body() {
      module("""
          result() = 123;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      module("""
          result = 123;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_element() {
      module("""
           result = [ 123 ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void parameter_default_argument() {
      module("""
        String myFunction(Int i = 123) = "abc";
        """)
          .loadsSuccessfully();
    }

    @Test
    public void function_in_call_expression_fails() {
      module("""
          result = 123("abc");
          """)
          .loadsWithError(1, """
              mismatched input '(' expecting {';', '|'}
              result = 123("abc");
                          ^""");
    }

    @Test
    public void struct_in_field_read_expression_fails() {
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
    public void function_argument() {
      module("""
        String myFunction(String param) = "abc";
        result = myFunction("abc");
        """)
          .loadsSuccessfully();
    }

    @Test
    public void function_body() {
      module("""
          result() = "abc";
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      module("""
          result = "abc";
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_element() {
      module("""
           result = [ "abc" ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void parameter_default_argument() {
      module("""
        String myFunction(String s = "abc") = "abc";
        """)
          .loadsSuccessfully();
    }

    @Test
    public void function_in_call_expression_fails() {
      module("""
          result = "text"("abc");
          """)
          .loadsWithError(1, """
              mismatched input '(' expecting {';', '|'}
              result = "text"("abc");
                             ^""");
    }

    @Test
    public void struct_in_field_read_expression_fails() {
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
    public void function_argument() {
      module("""
        myFunction([String] param) = "abc";
        result = myFunction(["abc"]);
        """)
          .loadsSuccessfully();
    }

    @Test
    public void function_body() {
      module("""
          result() = ["abc"];
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      module("""
          result = ["abc"];
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_element() {
      module("""
           result = [ ["abc"] ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void parameter_default_argument() {
      module("""
        String myFunction([String] s = ["abc"]) = "abc";
        """)
          .loadsSuccessfully();
    }

    @Test
    public void function_in_call_expression_fails() {
      module("""
          result = ["text"]("abc");
          """)
          .loadsWithError(1, """
              mismatched input '(' expecting {';', '|'}
              result = ["text"]("abc");
                               ^""");
    }

    @Test
    public void struct_in_field_read_expression_fails() {
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
  class _field_read_used_as {
    @Test
    public void function_argument() {
      module("""
        MyStruct {
          String field,
        }
        myValue = myStruct("abc");
        String myFunction(String s) = "abc";
        result = myFunction(myValue.field);
        """)
          .loadsSuccessfully();
    }

    @Test
    public void function_body() {
      module("""
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
      module("""
          MyStruct {
            String field,
          }
          myValue = myStruct("abc");
          result = myValue.field;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_element() {
      module("""
           MyStruct {
             String field,
           }
           myValue = myStruct("abc");
           result = [ myValue.field ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void parameter_default_argument() {
      module("""
        MyStruct {
          String field,
        }
        value = myStruct("abc");
        String myFunction(String value = value.field) = "abc";
        """)
          .loadsSuccessfully();
    }

    @Test
    public void function_in_call_expression() {
      module("""
        MyStruct {
          String() myFunction
        }
        String justAbc() = "abc";
        result = myStruct(justAbc).myFunction();
        """)
          .loadsSuccessfully();
    }

    @Test
    public void function_in_call_expression_fails_when_field_type_is_not_a_function() {
      module("""
        MyStruct {
          String myField
        }
        result = myStruct("abc").myField();
        """)
          .loadsWithError(4, "expression cannot be called as it is not a function but `String`.");
    }

    @Test
    public void struct_in_field_read_expression() {
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
          .loadsSuccessfully();
    }

    @Test
    public void struct_in_field_read_expression_fails_when_field_type_is_not_a_struct() {
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
    public void function_argument() {
      module("""
          A myIdentity(A a) = a;
          String myFunction(String param) = "abc";
          result = myFunction("abc" | myIdentity());
          """)
          .loadsSuccessfully();
    }

    @Test
    public void function_body() {
      module("""
          A myIdentity(A a) = a;
          String myFunction(String param) = "abc";
          result() = "abc" | myIdentity();
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      module("""
          A myIdentity(A a) = a;
          String myFunction(String param) = "abc";
          result = "abc" | myIdentity();
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_element() {
      module("""
             String myIdentity(String string) = string;
             result = [ "abc" | myIdentity() ];
             """)
          .loadsSuccessfully();
    }

    @Test
    public void parameter_default_argument() {
      module("""
          A myIdentity(A a) = a;
          String myFunction(String param = "abc" | myIdentity()) = "abc";
          """)
          .loadsSuccessfully();
    }
  }

  @Nested
  class _call_used_as {
    @Test
    public void function_argument() {
      module("""
        String otherFunction() = "abc";
        String myFunction(String param) = "abc";
        result = myFunction(otherFunction());
        """)
          .loadsSuccessfully();
    }

    @Test
    public void function_body() {
      module("""
          String otherFunction() = "abc";
          result() = otherFunction();
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      module("""
          String otherFunction() = "abc";
          result = otherFunction();
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_element() {
      module("""
           String myFunction() = "abc";
           result = [ myFunction() ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void parameter_default_argument() {
      module("""
        String myFunction() = "abc";
        String otherFunction(String value = myFunction()) = "abc";
        """)
          .loadsSuccessfully();
    }

    @Test
    public void function_in_call_expression() {
      module("""
        String justAbc() = "abc";
        String() highOrderFunction() = justAbc;
        result = highOrderFunction()();
        """)
          .loadsSuccessfully();
    }

    @Test
    public void struct_in_field_read_expression() {
      String code = """
            MyStruct {
              String myField
            }
            myFunction() = myStruct("abc");
            result = myFunction().myField;
            """;
      module(code)
          .loadsSuccessfully();
    }
  }

  @Nested
  class _function_reference_used_as {
    @Test
    public void function_argument() {
      module("""
          String otherFunction() = "abc";
          String myFunction(String() param) = "abc";
          result = myFunction(otherFunction);
          """)
          .loadsSuccessfully();
    }

    @Test
    public void function_body() {
      module("""
          String otherFunction() = "abc";
          result() = otherFunction;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      module("""
          String otherFunction() = "abc";
          result = otherFunction;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_element() {
      module("""
           String myFunction() = "abc";
           result = [ myFunction ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void parameter_default_argument() {
      module("""
          String myFunction() = "abc";
          String otherFunction(String() value = myFunction) = "abc";
          """)
          .loadsSuccessfully();
    }

    @Test
    public void function_in_call_expression() {
      module("""
          String justAbc() = "abc";
          result = justAbc();
          """)
          .loadsSuccessfully();
    }

    @Test
    public void struct_in_field_read_expression_fails() {
      String code = """
            myFunction() = "abc";
            result = myFunction.myField;
            """;
      module(code)
          .loadsWithError(2, "Type `String()` is not a struct so it doesn't have `myField` field.");
    }
  }

  @Nested
  class _function_parameter_used_as {
    @Test
    public void function_argument() {
      module("""
          myIdentity(String string) = string;
          myFunction(String string) = myIdentity(string);
          """)
          .loadsSuccessfully();
    }

    @Test
    public void function_body() {
      module("""
          myFunction(String string) = string;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_element() {
      module("""
          myFunction(String string) = [ string ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void function_in_call_expression() {
      module("""
          myFunction(String() param) = param();
          """)
          .loadsSuccessfully();
    }

    @Test
    public void function_in_call_expression_fails_when_parameter_type_is_not_a_function() {
      module("""
          myFunction(String param) = param();
          """)
          .loadsWithError(1, "`param` cannot be called as it is not a function but `String`.");
    }

    @Test
    public void struct_in_field_read_expression() {
      String code = """
            MyStruct {
              String myField
            }
            myFunction(MyStruct param) = param.myField;
            """;
      module(code)
          .loadsSuccessfully();
    }

    @Test
    public void struct_in_field_read_expression_fails_when_parameter_type_is_not_a_struct() {
      String code = """
            myFunction(String param) = param.myField;
            """;
      module(code)
          .loadsWithError(1, "Type `String` is not a struct so it doesn't have `myField` field.");
    }
  }

  @Nested
  class _value_reference_used_as {
    @Test
    public void function_argument() {
      module("""
          String myValue = "abc";
          String myFunction(String param) = "abc";
          result = myFunction(myValue);
          """)
          .loadsSuccessfully();
    }

    @Test
    public void function_body() {
      module("""
          String myValue = "abc";
          result() = myValue;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      module("""
          String myValue = "abc";
          result = myValue;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_element() {
      module("""
           String myValue = "abc";
           result = [ myValue ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void parameter_default_argument() {
      module("""
          String myValue = "abc";
          String myFunction(String value = myValue) = "abc";
          """)
          .loadsSuccessfully();
    }

    @Test
    public void function_in_call_expression() {
      module("""
        String justAbc() = "abc";
        myValue = justAbc;
        result = myValue();
        """)
          .loadsSuccessfully();
    }

    @Test
    public void function_in_call_expression_fails_when_value_type_is_not_a_function() {
      module("""
        myValue = "abc";
        result = myValue();
        """)
          .loadsWithError(2, "`myValue` cannot be called as it is not a function but `String`.");
    }

    @Test
    public void struct_in_field_read_expression() {
      String code = """
            MyStruct {
              String myField
            }
            myValue = myStruct("abc");
            result = myValue.myField;
            """;
      module(code)
          .loadsSuccessfully();
    }

    @Test
    public void struct_in_field_read_expression_fails_when_its_type_is_not_struct() {
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
    public void function_argument_fails() {
      module("""
          MyStruct {}
          String myFunction(String param) = "abc";
          result = myFunction(MyStruct);
          """)
          .loadsWithError(3, """
              extraneous input 'MyStruct' expecting {')', '[', NAME, INT, BLOB, STRING}
              result = myFunction(MyStruct);
                                  ^^^^^^^^""");
    }

    @Test
    public void function_body_fails() {
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
    public void array_element_fails() {
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
    public void parameter_default_argument_fails() {
      module("""
          MyStruct {}
          String myFunction(String value = MyStruct) = "abc";
          """)
          .loadsWithError(2, """
              mismatched input 'MyStruct' expecting {'[', NAME, INT, BLOB, STRING}
              String myFunction(String value = MyStruct) = "abc";
                                               ^^^^^^^^""");
    }

    @Test
    public void function_in_call_expression_fails() {
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
    public void struct_in_field_read_expression() {
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
          .loadsSuccessfully();
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
          .loadsSuccessfully();
    }

    @Test
    public void value_type() {
      module("""
          @Native("Impl.met")
          MyStruct myValue;
          MyStruct {}
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_arrayed_type() {
      module("""
          @Native("Impl.met")
          [MyStruct] myValue;
          MyStruct {}
          """)
          .loadsSuccessfully();
    }

    @Test
    public void function_parameter_type() {
      module("""
          String myFunction(MyStruct param) = "abc";
          MyStruct {}
          """)
          .loadsSuccessfully();
    }

    @Test
    public void function_arrayed_type() {
      module("""
          String myFunction([MyStruct] param) = "abc";
          MyStruct {}
          """)
          .loadsSuccessfully();
    }

    @Test
    public void function_result_type() {
      module("""
          @Native("Impl.met")
          MyStruct myFunction(String param);
          MyStruct {}
          """)
          .loadsSuccessfully();
    }

    @Test
    public void function_arrayed_result_type() {
      module("""
          [MyStruct] myFunction(String param) = [];
          MyStruct {}
          """)
          .loadsSuccessfully();
    }
  }

  @Nested
  class _type_variable_used_as {
    @Test
    public void function_argument_fails() {
      module("""
          String myFunction(String param) = "abc";
          result = myFunction(A);
          """)
          .loadsWithError(2, """
              extraneous input 'A' expecting {')', '[', NAME, INT, BLOB, STRING}
              result = myFunction(A);
                                  ^""");
    }

    @Test
    public void function_body_fails() {
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
    public void array_element_fails() {
      module("""
          result = [ A ];
          """)
          .loadsWithError(1, """
              extraneous input 'A' expecting {'[', ']', NAME, INT, BLOB, STRING}
              result = [ A ];
                         ^""");
    }

    @Test
    public void parameter_default_argument_fails() {
      module("""
          String myFunction(String value = A) = "abc";
          """)
          .loadsWithError(1, """
              mismatched input 'A' expecting {'[', NAME, INT, BLOB, STRING}
              String myFunction(String value = A) = "abc";
                                               ^""");
    }

    @Test
    public void function_in_call_expression_fails() {
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
    public void struct_in_field_read_expression() {
      String code = """
            result = A.myField;
            """;
      module(code)
          .loadsWithError(1, """
              mismatched input 'A' expecting {'[', NAME, INT, BLOB, STRING}
              result = A.myField;
                       ^""");
    }

    @Test
    public void field_type_fails() {
      module("""
          MyStruct {
           A field
          }
          """)
          .loadsWithError(2, "Type variable(s) `A` are used once in declaration of `field`. "
              + "This means each one can be replaced with `Any`.");
    }

    @Test
    public void field_arrayed_type() {
      module("""
          MyStruct {
           [A] field
          }
          """)
          .loadsWithError(2, "Type variable(s) `A` are used once in declaration of `field`. "
              + "This means each one can be replaced with `Any`.");
    }
  }

  @Nested
  class _undefined {
    @Nested
    class _referencable_cannot_be_used_as {
      @Test
      public void function_argument() {
        module("""
            String myFunction(Blob b) = "abc";
            result = myFunction(undefined);
            """)
            .loadsWithError(2, "`undefined` is undefined.");
      }

      @Test
      public void function_body() {
        module("""
            result() = undefined;
            """)
            .loadsWithError(1, "`undefined` is undefined.");
      }

      @Test
      public void function_in_call_expression() {
        module("""
            result = undefined();
            """)
            .loadsWithError(1, "`undefined` is undefined.");
      }

      @Test
      public void value_body() {
        module("""
            result = undefined;
            """)
            .loadsWithError(1, "`undefined` is undefined.");
      }

      @Test
      public void array_element() {
        module("""
           result = [ undefined ];
           """)
            .loadsWithError(1, "`undefined` is undefined.");
      }

      @Test
      public void parameter_default_argument() {
        module("""
        String myFunction(Blob b = undefined) = "abc";
        """)
            .loadsWithError(1, "`undefined` is undefined.");
      }
    }

    @Nested
    class _type_cannot_be_used_as_type_of {
      @Test
      public void value() {
        module("""
             @Native("Impl.met")
             Undefined myValue;
             """)
            .loadsWithError(2, "`Undefined` type is undefined.");
      }

      @Test
      public void function_result() {
        module("""
             @Native("Impl.met")
             Undefined myFunction();
             """)
            .loadsWithError(2, "`Undefined` type is undefined.");
      }

      @Test
      public void parameter() {
        module("""
             String myFunction(Undefined param) = "abc";
             """)
            .loadsWithError(1, "`Undefined` type is undefined.");
      }

      @Test
      public void field() {
        module("""
             MyStruct {
               Undefined field
             }
             """)
            .loadsWithError(2, "`Undefined` type is undefined.");
      }
    }
  }
}
