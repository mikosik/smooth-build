package org.smoothbuild.lang.function.base;

import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.expr.Expr;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.TaskWorker;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class AbstractFunctionTest {
  Name name;
  ImmutableList<Param> params;
  Signature<SString> signature;
  AbstractFunction<SString> function;

  @Test(expected = NullPointerException.class)
  public void nullSignatureIsForbidden() throws Exception {
    new MyAbstractFunction(null);
  }

  @Test
  public void type_returns_signature_type() {
    given(signature = new Signature<>(STRING, name("name"), Empty.paramList()));
    given(function = new MyAbstractFunction(signature));
    when(function).type();
    thenReturned(STRING);
  }

  @Test
  public void name_returns_signature_name() {
    given(name = name("name"));
    given(signature = new Signature<>(STRING, name, Empty.paramList()));
    given(function = new MyAbstractFunction(signature));
    when(function).type();
    thenReturned(STRING);
  }

  @Test
  public void params_returns_signature_params() {
    given(params = ImmutableList.of(param(STRING, "name", false)));
    given(signature = new Signature<>(STRING, name("name"), params));
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
        boolean isInternal, CodeLocation codeLocation) {
      return null;
    }
  }
}
