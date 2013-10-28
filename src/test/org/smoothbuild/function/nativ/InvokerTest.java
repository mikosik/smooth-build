package org.smoothbuild.function.nativ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;

import org.junit.Test;
import org.smoothbuild.object.Hashed;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.testing.plugin.FakeString;

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
    StringValue value = new FakeString("stringParamValue");
    Method method = InvokerTest.class.getMethod("myMethod", Sandbox.class, Parameters.class);

    Invoker invoker = new Invoker(method, new ArgumentsCreator(Parameters.class));
    ImmutableMap<String, Hashed> valuesMap = ImmutableMap.<String, Hashed> of("stringParam", value);
    Object result = invoker.invoke(sandbox, valuesMap);

    assertThat(result).isSameAs(value);
  }

  public interface Parameters {
    public StringValue stringParam();
  }

  public static StringValue myMethod(Sandbox sandbox, Parameters params) {
    return params.stringParam();
  }
}
