package org.smoothbuild.lang.function.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.expr.Expr;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.TaskWorker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class AbstractFunctionTest {
  @SuppressWarnings("unchecked")
  Signature<SString> signature = mock(Signature.class);
  AbstractFunction<SString> function = new MyAbstractFunction(signature);
  ImmutableList<Param> params;

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
  public void params_returns_signature_params() {
    given(params = ImmutableList.of(param(STRING, "name", false)));
    given(signature = new Signature<>(STRING, Name.name("name"), params));
    given(function = new MyAbstractFunction(signature));
    when(function).params();
    thenReturned(params);
  }

  public static class MyAbstractFunction extends AbstractFunction<SString> {
    public MyAbstractFunction(Signature<SString> signature) {
      super(signature);
    }

    @Override
    public ImmutableList<? extends Expr<?>> dependencies(
        ImmutableMap<String, ? extends Expr<?>> args) {
      return null;
    }

    @Override
    public TaskWorker<SString> createWorker(ImmutableMap<String, ? extends Expr<?>> args,
        CodeLocation codeLocation) {
      return null;
    }
  }
}
