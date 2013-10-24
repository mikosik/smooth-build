package org.smoothbuild.function.nativ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;

import org.junit.Test;
import org.smoothbuild.plugin.Sandbox;

import com.google.common.collect.ImmutableMap;

public class InvokerTest {
  Sandbox sandbox = mock(Sandbox.class);

  @Test(expected = NullPointerException.class)
  public void null_method_is_forbidden() throws Exception {
    new Invoker(null, mock(ArgumentsCreator.class));
  }

  @Test(expected = NullPointerException.class)
  public void null_ArgumentsCreator_is_forbidden() throws Exception {
    new Invoker(InvokerTest.class.getMethod("null_ArgumentsCreator_is_forbidden"), null);
  }

  @Test
  public void test() throws Exception {
    String value = "stringParamValue";
    Method method = InvokerTest.class.getMethod("myMethod", Sandbox.class, Parameters.class);

    Invoker invoker = new Invoker(method, new ArgumentsCreator(Parameters.class));
    ImmutableMap<String, Object> valuesMap = ImmutableMap.<String, Object> of("stringParam", value);
    Object result = invoker.invoke(sandbox, valuesMap);

    assertThat(result).isSameAs(value);
  }

  public interface Parameters {
    public String stringParam();
  }

  public static String myMethod(Sandbox sandbox, Parameters params) {
    return params.stringParam();
  }
}
