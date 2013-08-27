package org.smoothbuild.function.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.smoothbuild.function.base.FunctionSignature;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.exc.FunctionException;

import com.google.common.collect.ImmutableMap;

public class PluginFunctionTest {
  FunctionSignature signature = mock(FunctionSignature.class);
  PluginInvoker invoker = mock(PluginInvoker.class);

  PluginFunction function = new PluginFunction(signature, invoker);

  @Test
  public void execute() throws FunctionException {
    Path resultDir = Path.path("my/path");
    @SuppressWarnings("unchecked")
    ImmutableMap<String, Object> map = mock(ImmutableMap.class);

    Object result = "result string";
    when(invoker.invoke(resultDir, map)).thenReturn(result);

    assertThat(function.execute(resultDir, map)).isEqualTo(result);
  }
}
