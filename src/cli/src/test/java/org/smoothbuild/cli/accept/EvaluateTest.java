package org.smoothbuild.cli.accept;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;

import java.math.BigInteger;
import okio.ByteString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.evaluator.testing.EvaluatorTestContext;
import org.smoothbuild.virtualmachine.testing.func.nativ.StringIdentity;
import org.smoothbuild.virtualmachine.testing.func.nativ.ThrowException;

public class EvaluateTest extends EvaluatorTestContext {
  @Nested
  class _literal {
    @ParameterizedTest
    @ValueSource(strings = {"", "12", "1234", "123456", "ABCDEF", "abcdef", "ABCDEFabcdef"})
    public void blob_literal_value_is_decoded(String hexDigits) throws Exception {
      createUserModule("result = 0x" + hexDigits + ";");
      evaluate("result");
      assertThat(artifact()).isEqualTo(bBlob(ByteString.decodeHex(hexDigits)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "1", "-1", "1234", "-123456", "123456789000000"})
    public void int_literal_value_is_decoded(String intLiteral) throws Exception {
      createUserModule("result = " + intLiteral + ";");
      evaluate("result");
      assertThat(artifact()).isEqualTo(bInt(new BigInteger(intLiteral, 10)));
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
          "",
          "abc",
          "abcdefghijklmnopqrstuvwxyz",
          "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
          "0123456789", // digits
          "abc←", // unicode character
          "#", // smooth language comment opening character
          "#abc", // smooth language comment opening character with additional characters
          "'", // single quote
          "\\\\", // escaped backslash
          "\\t", // escaped tab
          "\\b", // escaped backspace
          "\\n", // escaped new line
          "\\r", // escaped carriage return
          "\\f", // escaped form feed
          "\\\"" // escaped double quotes
        })
    public void string_literal_value_is_decoded(String string) throws Exception {
      createUserModule("result = \"" + string + "\";");
      evaluate("result");
      assertThat(artifact()).isEqualTo(bString(string.translateEscapes()));
    }
  }

  @Nested
  class _expr {
    @Nested
    class _select {
      @Test
      void select() throws Exception {
        createUserModule(
            """
            MyStruct {
              String field,
            }
            String result = MyStruct("abc").field;
            """);
        evaluate("result");
        assertThat(artifact()).isEqualTo(bString("abc"));
      }

      @Test
      void select_does_not_consume_piped_value() throws Exception {
        createUserModule(
            """
            MyStruct {
              (String)->Int field,
            }
            Int return7(String s) = 7;
            aStruct = MyStruct(return7);
            Int result = "abc" > aStruct.field();
            """);
        evaluate("result");
        assertThat(artifact()).isEqualTo(bInt(7));
      }
    }

    @Nested
    class _order {
      @Test
      void order() throws Exception {
        createUserModule("""
            [Int] result = [1, 2, 3];
            """);
        evaluate("result");
        assertThat(artifact()).isEqualTo(bArray(bInt(1), bInt(2), bInt(3)));
      }

      @Test
      void order_consumes_piped_value() throws Exception {
        createUserModule("""
            [Int] result = 1 > [2, 3];
            """);
        evaluate("result");
        assertThat(artifact()).isEqualTo(bArray(bInt(1), bInt(2), bInt(3)));
      }
    }

    @Nested
    class _call {
      @Nested
      class _lambda {
        @Test
        void const_func() throws Exception {
          createUserModule(
              """
            myFunc() = 7;
            result = (() -> 7)();
            """);
          evaluate("result");
          assertThat(artifact()).isEqualTo(bInt(7));
        }

        @Test
        void func_returning_its_param() throws Exception {
          createUserModule("""
            result = ((Int int) -> int)(7);
            """);
          evaluate("result");
          assertThat(artifact()).isEqualTo(bInt(7));
        }

        @Test
        void func_that_does_not_use_its_param_will_not_evaluate_matching_arg() throws Exception {
          var userModule =
              """
              @Native("impl")
              A throwException();
              result = ((String notUsedParameter) -> "abc")(throwException());
              """;
          createUserModule(userModule, ThrowException.class);
          evaluate("result");
          assertThat(artifact()).isEqualTo(bString("abc"));
        }

        @Test
        void func_passed_as_argument() throws Exception {
          var userModule =
              """
              A invokeProducer(()->A producer) = producer();
              result = invokeProducer(() -> "abc");
              """;
          createUserModule(userModule, ThrowException.class);
          evaluate("result");
          assertThat(artifact()).isEqualTo(bString("abc"));
        }

        @Test
        void func_returned_by_other_func() throws Exception {
          var userModule =
              """
              ()->String createProducer() = () -> "abc";
              result = createProducer()();
              """;
          createUserModule(userModule, ThrowException.class);
          evaluate("result");
          assertThat(artifact()).isEqualTo(bString("abc"));
        }

        @Test
        void call_consumes_piped_value() throws Exception {
          createUserModule(
              """
            myFunc(Int int) = int;
            result = 7 > ((Int int) -> int)();
            """);
          evaluate("result");
          assertThat(artifact()).isEqualTo(bInt(7));
        }
      }

      @Nested
      class _expression_function {
        @Test
        void const_func() throws Exception {
          createUserModule(
              """
            myFunc() = 7;
            result = myFunc();
            """);
          evaluate("result");
          assertThat(artifact()).isEqualTo(bInt(7));
        }

        @Test
        void func_returning_its_param() throws Exception {
          createUserModule(
              """
            myFunc(Int int) = int;
            result = myFunc(7);
            """);
          evaluate("result");
          assertThat(artifact()).isEqualTo(bInt(7));
        }

        @Test
        void func_that_does_not_use_its_param_will_not_evaluate_matching_arg() throws Exception {
          var userModule =
              """
              @Native("impl")
              A throwException();
              func(String notUsedParameter) = "abc";
              result = func(throwException());
              """;
          createUserModule(userModule, ThrowException.class);
          evaluate("result");
          assertThat(artifact()).isEqualTo(bString("abc"));
        }

        @Test
        void func_passed_as_argument() throws Exception {
          var userModule =
              """
              String returnAbc() = "abc";
              A invokeProducer(()->A producer) = producer();
              result = invokeProducer(returnAbc);
              """;
          createUserModule(userModule, ThrowException.class);
          evaluate("result");
          assertThat(artifact()).isEqualTo(bString("abc"));
        }

        @Test
        void func_returned_by_other_func() throws Exception {
          var userModule =
              """
              String returnAbc() = "abc";
              ()->String createProducer() = returnAbc;
              result = createProducer()();
              """;
          createUserModule(userModule, ThrowException.class);
          evaluate("result");
          assertThat(artifact()).isEqualTo(bString("abc"));
        }

        @Test
        void call_consumes_piped_value() throws Exception {
          createUserModule(
              """
            myFunc(Int int) = int;
            result = 7 > myFunc();
            """);
          evaluate("result");
          assertThat(artifact()).isEqualTo(bInt(7));
        }
      }
    }

    @Nested
    class _param {
      @Nested
      class _default_value {
        @Nested
        class _in_expr_func {
          @Test
          void is_used_when_param_has_no_value_assigned_in_call() throws Exception {
            createUserModule(
                """
          func(String withDefault = "abc") = withDefault;
          result = func();
          """);
            evaluate("result");
            assertThat(artifact()).isEqualTo(bString("abc"));
          }

          @Test
          void is_ignored_when_param_is_assigned_in_a_call() throws Exception {
            createUserModule(
                """
              func(String withDefault = "abc") = withDefault;
              result = func("def");
              """);
            evaluate("result");
            assertThat(artifact()).isEqualTo(bString("def"));
          }

          @Test
          void is_not_evaluated_when_not_needed() throws Exception {
            var userModule = format(
                """
                    @Native("%s")
                    A throwException();
                    func(String withDefault = throwException()) = withDefault;
                    result = func("def");
                    """,
                ThrowException.class.getCanonicalName());
            createUserModule(userModule, ThrowException.class);
            evaluate("result");
            assertThat(artifact()).isEqualTo(bString("def"));
          }
        }

        @Nested
        class _in_native_func {
          @Test
          void is_used_when_param_has_no_value_assigned_in_call() throws Exception {
            var userModule = format(
                """
                    @Native("%s")
                    String stringIdentity(String value = "abc");
                    result = stringIdentity();
                    """,
                StringIdentity.class.getCanonicalName());
            createUserModule(userModule, StringIdentity.class);
            evaluate("result");
            assertThat(artifact()).isEqualTo(bString("abc"));
          }

          @Test
          void is_ignored_when_param_is_assigned_in_a_call() throws Exception {
            var userModule = format(
                """
                    @Native("%s")
                    String stringIdentity(String value = "abc");
                    result = stringIdentity("def");
                    """,
                StringIdentity.class.getCanonicalName());
            createUserModule(userModule, StringIdentity.class);
            evaluate("result");
            assertThat(artifact()).isEqualTo(bString("def"));
          }

          @Test
          void is_not_evaluated_when_not_needed() throws Exception {
            var userModule = format(
                """
                    @Native("%s")
                    String stringIdentity(String value = throwException());
                    @Native("%s")
                    A throwException();
                    result = stringIdentity("def");
                    """,
                StringIdentity.class.getCanonicalName(), ThrowException.class.getCanonicalName());
            createUserModule(userModule, StringIdentity.class, ThrowException.class);
            evaluate("result");
            assertThat(artifact()).isEqualTo(bString("def"));
          }
        }
      }

      @Nested
      class _that_shadows {
        @Nested
        class _imported {
          @Test
          void value_makes_it_inaccessible() throws Exception {
            createUserModule(
                """
              String myFunc(String true) = true;
              result = myFunc("abc");
              """);
            evaluate("result");
            assertThat(artifact()).isEqualTo(bString("abc"));
          }

          @Test
          void func_makes_it_inaccessible() throws Exception {
            createUserModule(
                """
              String myFunc(String and) = and;
              result = myFunc("abc");
              """);
            evaluate("result");
            assertThat(artifact()).isEqualTo(bString("abc"));
          }
        }

        @Nested
        class _local {
          @Test
          void value_makes_it_inaccessible() throws Exception {
            createUserModule(
                """
              localValue = 7;
              String myFunc(String localValue) = localValue;
              result = myFunc("abc");
              """);
            evaluate("result");
            assertThat(artifact()).isEqualTo(bString("abc"));
          }

          @Test
          void func_makes_it_inaccessible() throws Exception {
            createUserModule(
                """
              localFunc() = 7;
              String myFunc(String localFunc) = localFunc;
              result = myFunc("abc");
              """);
            evaluate("result");
            assertThat(artifact()).isEqualTo(bString("abc"));
          }
        }
      }
    }
  }
}
