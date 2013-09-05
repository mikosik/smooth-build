package org.smoothbuild.function.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;

import org.junit.Test;
import org.smoothbuild.plugin.api.Sandbox;

import com.google.common.collect.ImmutableMap;

public class PluginInvokerTest {
  Sandbox sandbox = mock(Sandbox.class);

  @Test
  public void test() throws Exception {
    String value = "stringParamValue";
    Method method = PluginInvokerTest.class.getMethod("myMethod", Sandbox.class, Parameters.class);

    PluginInvoker pluginInvoker = new PluginInvoker(method, new ArgumentsCreator(Parameters.class));
    ImmutableMap<String, Object> valuesMap = ImmutableMap.<String, Object> of("stringParam", value);
    Object result = pluginInvoker.invoke(sandbox, valuesMap);

    assertThat(result).isSameAs(value);
  }

  public interface Parameters {
    public String stringParam();
  }

  public static String myMethod(Sandbox sandbox, Parameters params) {
    return params.stringParam();
  }
}
