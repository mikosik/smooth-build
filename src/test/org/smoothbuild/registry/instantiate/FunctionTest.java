package org.smoothbuild.registry.instantiate;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.smoothbuild.lang.function.FullyQualifiedName;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.type.Path;

import com.google.common.collect.ImmutableMap;

public class FunctionTest {
  FunctionSignature signature = mock(FunctionSignature.class);
  FunctionInvoker invoker = mock(FunctionInvoker.class);

  Function function = new Function(signature, invoker);

  @Test
  public void name() {
    FullyQualifiedName name = FullyQualifiedName.fullyQualifiedName("name");
    when(signature.name()).thenReturn(name);

    assertThat(function.name()).isEqualTo(name);
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
