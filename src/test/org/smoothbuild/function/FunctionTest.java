package org.smoothbuild.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.Param.param;

import org.junit.Test;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.exc.FunctionException;

import com.google.common.collect.ImmutableMap;

public class FunctionTest {
  FunctionSignature signature = mock(FunctionSignature.class);
  FunctionInvoker invoker = mock(FunctionInvoker.class);

  Function function = new Function(signature, invoker);

  @Test
  public void type() {
    when(signature.type()).thenReturn(Type.STRING);

    assertThat(function.type()).isEqualTo(Type.STRING);
  }

  @Test
  public void name() {
    FullyQualifiedName name = FullyQualifiedName.fullyQualifiedName("name");
    when(signature.name()).thenReturn(name);

    assertThat(function.name()).isEqualTo(name);
  }

  @Test
  public void params() {
    ImmutableMap<String, Param> params = ImmutableMap.of("name", param(Type.STRING, "name"));
    when(signature.params()).thenReturn(params);

    assertThat(function.params()).isEqualTo(params);
  }

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
