package org.smoothbuild.cli.accept;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;

import org.junit.jupiter.api.Test;
import org.smoothbuild.evaluator.testing.EvaluatorTestContext;
import org.smoothbuild.virtualmachine.testing.func.bytecode.NonPublicMethod;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnAbc;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnIdFunc;

public class BytecodeEvaluableTest extends EvaluatorTestContext {
  @Test
  void func_call_can_be_evaluated() throws Exception {
    var userModule = format(
        """
            @Bytecode("%s")
            A myId<A>(A a);
            result = myId(77);
            """,
        ReturnIdFunc.class.getCanonicalName());
    createUserModule(userModule, ReturnIdFunc.class);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bInt(77));
  }

  @Test
  void without_native_jar_file_causes_fatal() throws Exception {
    createUserModule(
        """
        @Bytecode("MissingClass")
        String myFunc();
        result = myFunc();
        """);
    evaluate("result");
    assertThat(logs())
        .contains(userFatal(
            1,
            "Error loading native jar '{t-project}/module.jar'.\n"
                + "Cannot read '{t-project}/module.jar'. "
                + "File '{t-project}/module.jar' doesn't exist."));
  }

  @Test
  void func_with_illegal_impl_causes_fatal() throws Exception {
    var userModule = format(
        """
            @Bytecode("%s")
            Int brokenFunc();
            result = brokenFunc();
            """,
        NonPublicMethod.class.getCanonicalName());
    createUserModule(userModule, NonPublicMethod.class);
    evaluate("result");
    assertThat(logs())
        .containsExactly(userFatal(
            1,
            "Error loading bytecode for `brokenFunc`"
                + " using provider specified as `" + NonPublicMethod.class.getCanonicalName()
                + "`: Providing method is not public."));
  }

  @Test
  void value_can_be_evaluated() throws Exception {
    var userModule = format(
        """
            @Bytecode("%s")
            String result;
            """,
        ReturnAbc.class.getCanonicalName());
    createUserModule(userModule, ReturnAbc.class);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bString("abc"));
  }

  @Test
  void value_with_illegal_impl_causes_fatal() throws Exception {
    var userModule = format(
        """
            @Bytecode("%s")
            Int result;
            """,
        NonPublicMethod.class.getCanonicalName());
    createUserModule(userModule, NonPublicMethod.class);
    evaluate("result");
    assertThat(logs())
        .containsExactly(userFatal(
            1,
            "Error loading bytecode for `result` using "
                + "provider specified as `" + NonPublicMethod.class.getCanonicalName()
                + "`: Providing method is not public."));
  }
}
