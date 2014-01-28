package org.smoothbuild.lang.function.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.testing.lang.function.base.ParamTester.params;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.willReturn;

import java.util.Map;

import org.junit.Test;
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
    given(willReturn(STRING), signature).type();
    assertThat(function.type()).isEqualTo(STRING);
  }

  @Test
  public void name() {
    Name name = Name.name("name");
    given(willReturn(name), signature).name();

    assertThat(function.name()).isEqualTo(name);
  }

  @Test
  public void testParams() {
    ImmutableMap<String, Param> params = params(param(STRING, "name"));
    given(willReturn(params), signature).params();

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
