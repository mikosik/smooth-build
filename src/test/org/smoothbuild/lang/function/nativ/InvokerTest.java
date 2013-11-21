package org.smoothbuild.lang.function.nativ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;

import org.junit.Test;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.type.StringValue;
import org.smoothbuild.lang.type.Value;
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
    StringValue value = new FakeString("stringParamValue");
    Method method = InvokerTest.class.getMethod("myMethod", Sandbox.class, Parameters.class);

    Invoker invoker = new Invoker(method, new ArgumentsCreator(Parameters.class));
    ImmutableMap<String, Value> valuesMap = ImmutableMap.<String, Value> of("stringParam", value);
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
