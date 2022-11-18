package org.smoothbuild.accept;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.testing.accept.AcceptanceTestCase;
import org.smoothbuild.testing.func.nativ.StringIdentity;
import org.smoothbuild.testing.func.nativ.ThrowException;

import okio.ByteString;

public class EvaluateTest extends AcceptanceTestCase {
  @Nested
  class _literal {
    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "12",
        "1234",
        "123456",
        "ABCDEF",
        "abcdef",
        "ABCDEFabcdef"})
    public void blob_literal_value_is_decoded(String hexDigits) throws Exception {
      createUserModule("result = 0x" + hexDigits + ";");
      evaluate("result");
      assertThat(artifact())
          .isEqualTo(blobB(ByteString.decodeHex(hexDigits)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "0",
        "1",
        "-1",
        "1234",
        "-123456",
        "123456789000000"})
    public void int_literal_value_is_decoded(String intLiteral) throws Exception {
      createUserModule("result = " + intLiteral + ";");
      evaluate("result");
      assertThat(artifact())
          .isEqualTo(intB(new BigInteger(intLiteral, 10)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "abc",
        "abcdefghijklmnopqrstuvwxyz",
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
        "0123456789",  // digits
        "abc←",        // unicode character
        "#",           // smooth language comment opening character
        "#abc",        // smooth language comment opening character with additional characters
        "'",           // single quote
        "\\\\",        // escaped backslash
        "\\t",         // escaped tab
        "\\b",         // escaped backspace
        "\\n",         // escaped new line
        "\\r",         // escaped carriage return
        "\\f",         // escaped form feed
        "\\\""         // escaped double quotes
    })
    public void string_literal_value_is_decoded(String string) throws Exception {
      createUserModule("result = \"" + string + "\";");
      evaluate("result");
      assertThat(artifact())
          .isEqualTo(stringB(string.translateEscapes()));
    }
  }

  @Nested
  class _expr {
    @Nested
    class _select {
      @Test
      public void select() throws Exception {
        createUserModule("""
            MyStruct {
              String field,
            }
            String result = myStruct("abc").field;
            """);
        evaluate("result");
        assertThat(artifact())
            .isEqualTo(stringB("abc"));
      }

      @Test
      public void select_doesnt_consume_piped_value() throws Exception {
        createUserModule("""
            MyStruct {
              (String)->()->Int field,
            }
            Int return7() = 7;
            ()->Int returnReturn7(String s) = return7;
            aStruct = myStruct(returnReturn7);
            # TODO
            # smooth-lang design problems:
            # - "abc" is passed to first call but this is not intuitive
            # - "abc" skips selection: `aStruct.field`
            Int result = "abc" | aStruct.field()();
            """);
        evaluate("result");
        assertThat(artifact())
            .isEqualTo(intB(7));
      }
    }

    @Nested
    class _order {
      @Test
      public void order() throws Exception {
        createUserModule("""
            [Int] result = [1, 2, 3];
            """);
        evaluate("result");
        assertThat(artifact())
            .isEqualTo(arrayB(intB(1), intB(2), intB(3)));
      }

      @Test
      public void order_consumes_piped_value() throws Exception {
        createUserModule("""
            [Int] result = 1 | [2, 3];
            """);
        evaluate("result");
        assertThat(artifact())
            .isEqualTo(arrayB(intB(1), intB(2), intB(3)));
      }
    }

    @Nested
    class _call {
      @Test
      public void const_func() throws Exception {
        createUserModule("""
            myFunc() = 7;
            result = myFunc();
            """);
        evaluate("result");
        assertThat(artifact())
            .isEqualTo(intB(7));
      }

      @Test
      public void func_returning_its_param() throws Exception {
        createUserModule("""
            myFunc(Int int) = int;
            result = myFunc(7);
            """);
        evaluate("result");
        assertThat(artifact())
            .isEqualTo(intB(7));
      }

      @Test
      public void func_that_does_not_use_its_param_will_not_evaluate_matching_arg()
          throws Exception {
        createUserNativeJar(ThrowException.class);
        createUserModule("""
            @Native("impl")
            A throwException();
            func(String notUsedParameter) = "abc";
            result = func(throwException());
            """);
        evaluate("result");
        assertThat(artifact())
            .isEqualTo(stringB("abc"));
      }

      @Test
      public void func_passed_as_argument() throws Exception {
        createUserNativeJar(ThrowException.class);
        createUserModule("""
            String returnAbc() = "abc";
            A invokeProducer(()->A producer) = producer();
            result = invokeProducer(returnAbc);
            """);
        evaluate("result");
        assertThat(artifact())
            .isEqualTo(stringB("abc"));
      }

      @Test
      public void func_returned_by_other_func() throws Exception {
        createUserNativeJar(ThrowException.class);
        createUserModule("""
            String returnAbc() = "abc";
            ()->String createProducer() = returnAbc;
            result = createProducer()();
            """);
        evaluate("result");
        assertThat(artifact())
            .isEqualTo(stringB("abc"));
      }

      @Test
      public void call_consumes_piped_value() throws Exception {
        createUserModule("""
            myFunc(Int int) = int;
            result = 7 | myFunc();
            """);
        evaluate("result");
        assertThat(artifact())
            .isEqualTo(intB(7));
      }
    }

    @Nested
    class _param {
      @Nested
      class _default_value {
        @Nested
        class _in_def_func {
          @Test
          public void is_used_when_param_has_no_value_assigned_in_call() throws Exception {
            createUserModule("""
          func(String withDefault = "abc") = withDefault;
          result = func();
          """);
            evaluate("result");
            assertThat(artifact())
                .isEqualTo(stringB("abc"));
          }

          @Test
          public void is_ignored_when_param_is_assigned_in_a_call() throws Exception {
            createUserModule("""
              func(String withDefault = "abc") = withDefault;
              result = func("def");
              """);
            evaluate("result");
            assertThat(artifact())
                .isEqualTo(stringB("def"));
          }

          @Test
          public void is_not_evaluated_when_not_needed() throws Exception {
            createUserNativeJar(ThrowException.class);
            createUserModule(format("""
          @Native("%s")
          A throwException();
          func(String withDefault = throwException()) = withDefault;
          result = func("def");
          """, ThrowException.class.getCanonicalName()));
            evaluate("result");
            assertThat(artifact())
                .isEqualTo(stringB("def"));
          }
        }

        @Nested
        class _in_nat_func {
          @Test
          public void is_used_when_param_has_no_value_assigned_in_call() throws Exception {
            createUserNativeJar(StringIdentity.class);
            createUserModule(format("""
            @Native("%s")
            String stringIdentity(String value = "abc");
            result = stringIdentity();
            """, StringIdentity.class.getCanonicalName()));
            evaluate("result");
            assertThat(artifact())
                .isEqualTo(stringB("abc"));
          }

          @Test
          public void is_ignored_when_param_is_assigned_in_a_call() throws Exception {
            createUserNativeJar(StringIdentity.class);
            createUserModule(format("""
            @Native("%s")
            String stringIdentity(String value = "abc");
            result = stringIdentity("def");
            """, StringIdentity.class.getCanonicalName()));
            evaluate("result");
            assertThat(artifact())
                .isEqualTo(stringB("def"));
          }

          @Test
          public void is_not_evaluated_when_not_needed() throws Exception {
            createUserNativeJar(StringIdentity.class, ThrowException.class);
            createUserModule(format("""
            @Native("%s")
            String stringIdentity(String value = throwException());
            @Native("%s")
            A throwException();
            result = stringIdentity("def");
            """, StringIdentity.class.getCanonicalName(), ThrowException.class.getCanonicalName()));
            evaluate("result");
            assertThat(artifact())
                .isEqualTo(stringB("def"));
          }
        }
      }

      @Nested
      class _that_shadows {
        @Nested
        class _imported {
          @Test
          public void value_makes_it_inaccessible() throws IOException {
            createUserModule("""
              String myFunc(String true) = true;
              result = myFunc("abc");
              """);
            evaluate("result");
            assertThat(artifact())
                .isEqualTo(stringB("abc"));
          }

          @Test
          public void func_makes_it_inaccessible() throws IOException {
            createUserModule("""
              String myFunc(String and) = and;
              result = myFunc("abc");
              """);
            evaluate("result");
            assertThat(artifact())
                .isEqualTo(stringB("abc"));
          }
        }

        @Nested
        class _local {
          @Test
          public void value_makes_it_inaccessible() throws IOException {
            createUserModule("""
              localValue = 7;
              String myFunc(String localValue) = localValue;
              result = myFunc("abc");
              """);
            evaluate("result");
            assertThat(artifact())
                .isEqualTo(stringB("abc"));
          }

          @Test
          public void func_makes_it_inaccessible() throws IOException {
            createUserModule("""
              localFunc() = 7;
              String myFunc(String localFunc) = localFunc;
              result = myFunc("abc");
              """);
            evaluate("result");
            assertThat(artifact())
                .isEqualTo(stringB("abc"));
          }
        }
      }
    }
  }
}
