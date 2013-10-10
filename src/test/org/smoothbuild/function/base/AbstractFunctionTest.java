package org.smoothbuild.function.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.testing.function.base.ParamTester.param;
import static org.smoothbuild.testing.function.base.ParamTester.params;

import java.util.Map;

import org.junit.Test;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.Task;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class AbstractFunctionTest {
  Signature signature = mock(Signature.class);
  HashCode hash = HashCode.fromInt(33);
  AbstractFunction function = new MyAbstractFunction(signature, hash);

  @Test(expected = NullPointerException.class)
  public void nullSignatureIsForbidden() throws Exception {
    new MyAbstractFunction(null, hash);
  }

  @Test(expected = NullPointerException.class)
  public void nullHashIsForbidden() throws Exception {
    new MyAbstractFunction(signature, null);
  }

  @Test
  public void type() {
    when(signature.type()).thenReturn(Type.STRING);
    assertThat(function.type()).isEqualTo(Type.STRING);
  }

  @Test
  public void name() {
    Name name = Name.qualifiedName("name");
    when(signature.name()).thenReturn(name);

    assertThat(function.name()).isEqualTo(name);
  }

  @Test
  public void hash() {
    assertThat(function.hash()).isSameAs(hash);
  }

  @Test
  public void testParams() {
    ImmutableMap<String, Param> params = params(param(Type.STRING, "name"));
    when(signature.params()).thenReturn(params);

    assertThat(function.params()).isEqualTo(params);
  }

  public static class MyAbstractFunction extends AbstractFunction {
    public MyAbstractFunction(Signature signature, HashCode hash) {
      super(signature, hash);
    }

    @Override
    public Task generateTask(Map<String, Task> dependencies, CodeLocation codeLocation) {
      return null;
    }
  }
}
