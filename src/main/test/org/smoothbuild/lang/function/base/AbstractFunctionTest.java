package org.smoothbuild.lang.function.base;

import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Parameter.optionalParameter;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;

public class AbstractFunctionTest {
  Name name;
  ImmutableList<Parameter> parameters;
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
    given(parameters = ImmutableList.of(optionalParameter(STRING, "name")));
    given(signature = new Signature<>(STRING, name("name"), parameters));
    given(function = new MyAbstractFunction(signature));
    when(function).parameters();
    thenReturned(parameters);
  }

  public static class MyAbstractFunction extends AbstractFunction<SString> {
    public MyAbstractFunction(Signature<SString> signature) {
      super(signature);
    }
  }
}
