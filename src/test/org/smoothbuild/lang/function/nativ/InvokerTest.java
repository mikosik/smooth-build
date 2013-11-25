package org.smoothbuild.lang.function.nativ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;

import org.junit.Test;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.testing.lang.type.FakeString;

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
    SString value = new FakeString("stringParamValue");
    Method method = InvokerTest.class.getMethod("myMethod", Sandbox.class, Parameters.class);

    Invoker invoker = new Invoker(method, new ArgumentsCreator(Parameters.class));
    ImmutableMap<String, SValue> valuesMap = ImmutableMap.<String, SValue> of("stringParam", value);
    Object result = invoker.invoke(sandbox, valuesMap);

    assertThat(result).isSameAs(value);
  }

  public interface Parameters {
    public SString stringParam();
  }

  public static SString myMethod(Sandbox sandbox, Parameters params) {
    return params.stringParam();
  }
}
