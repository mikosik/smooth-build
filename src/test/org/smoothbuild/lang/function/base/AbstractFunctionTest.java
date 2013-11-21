package org.smoothbuild.lang.function.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.testing.lang.function.base.ParamTester.params;

import java.util.Map;

import org.junit.Test;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

import com.google.common.collect.ImmutableMap;

public class AbstractFunctionTest {
  Signature signature = mock(Signature.class);
  AbstractFunction function = new MyAbstractFunction(signature);

  @Test(expected = NullPointerException.class)
  public void nullSignatureIsForbidden() throws Exception {
    new MyAbstractFunction(null);
  }

  @Test
  public void type() {
    when(signature.type()).thenReturn(Type.STRING);
    assertThat(function.type()).isEqualTo(Type.STRING);
  }

  @Test
  public void name() {
    Name name = Name.name("name");
    when(signature.name()).thenReturn(name);

    assertThat(function.name()).isEqualTo(name);
  }

  @Test
  public void testParams() {
    ImmutableMap<String, Param> params = params(param(Type.STRING, "name"));
    when(signature.params()).thenReturn(params);

    assertThat(function.params()).isEqualTo(params);
  }

  public static class MyAbstractFunction extends AbstractFunction {
    public MyAbstractFunction(Signature signature) {
      super(signature);
    }

    @Override
    public Task generateTask(TaskGenerator taskGenerator, Map<String, Result> arguments,
        CodeLocation codeLocation) {
      return null;
    }
  }
}
