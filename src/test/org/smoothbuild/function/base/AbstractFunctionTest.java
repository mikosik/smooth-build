package org.smoothbuild.function.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.base.Param.param;

import java.util.Map;

import org.junit.Test;
import org.smoothbuild.expression.Expression;
import org.smoothbuild.expression.ExpressionIdFactory;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.exc.FunctionException;

import com.google.common.collect.ImmutableMap;

public class AbstractFunctionTest {
  FunctionSignature signature = mock(FunctionSignature.class);
  AbstractFunction function = new MyAbstractFunction(signature);

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
    ImmutableMap<String, Param> params = Param.params(param(Type.STRING, "name"));
    when(signature.params()).thenReturn(params);

    assertThat(function.params()).isEqualTo(params);
  }

  public static class MyAbstractFunction extends AbstractFunction {
    public MyAbstractFunction(FunctionSignature signature) {
      super(signature);
    }

    @Override
    public Expression apply(ExpressionIdFactory idFactory, Map<String, Expression> arguments) {
      return null;
    }

    @Override
    public Object execute(Path resultDir, ImmutableMap<String, Object> arguments)
        throws FunctionException {
      return null;
    }
  }
}
