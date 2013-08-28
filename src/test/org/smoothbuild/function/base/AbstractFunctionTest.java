package org.smoothbuild.function.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.base.Param.param;

import java.util.Map;

import org.junit.Test;
import org.smoothbuild.function.expr.Expression;
import org.smoothbuild.function.expr.ExpressionIdFactory;

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
    Name name = Name.qualifiedName("name");
    when(signature.name()).thenReturn(name);

    assertThat(function.name()).isEqualTo(name);
  }

  @Test
  public void params() {
    ImmutableMap<String, Param> params = Param.params(param(Type.STRING, "name"));
    when(signature.params()).thenReturn(params);

    assertThat(function.params()).isEqualTo(params);
  }

  public static class MyAbstractFunction extends AbstractFunction {
    public MyAbstractFunction(Signature signature) {
      super(signature);
    }

    @Override
    public Expression apply(ExpressionIdFactory idFactory, Map<String, Expression> arguments) {
      return null;
    }
  }
}
