package org.smoothbuild.registry.instantiate;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.lang.type.Path.path;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.lang.function.Function;
import org.smoothbuild.lang.function.FunctionName;
import org.smoothbuild.lang.function.Params;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.type.Path;
import org.smoothbuild.registry.exc.CreatingInstanceFailedException;
import org.smoothbuild.registry.exc.FunctionImplementationException;
import org.smoothbuild.registry.exc.MissingNameException;

public class FunctionTypeFactoryTest {
  Instantiator instantiator = mock(Instantiator.class);
  Path path = path("abc");
  Function function = mock(Function.class);

  InstantiatorFactory instantiatorFactory = mock(InstantiatorFactory.class);
  FunctionTypeFactory functionTypeFactory = new FunctionTypeFactory(instantiatorFactory);

  @Test
  public void creatingFunctionType() throws FunctionImplementationException,
      CreatingInstanceFailedException {
    when(instantiatorFactory.create(MyNamedFunction.class)).thenReturn(instantiator);
    when(instantiator.newInstance(path)).thenReturn(function);

    FunctionType functionType = functionTypeFactory.create(MyNamedFunction.class);
    Function instance = functionType.newInstance(path);

    assertThat(instance).isEqualTo(function);
  }

  @FunctionName(name = "myFunction")
  public static class MyNamedFunction implements Function {

    @Override
    public Params params() {
      return null;
    }

    @Override
    public Object execute() throws FunctionException {
      return null;
    }
  }

  @Test
  public void missingNameCausesExceptionToBeThrown() throws FunctionImplementationException {
    try {
      functionTypeFactory.create(MyMissingNameFunction.class);
      Assert.fail("exception should be thrown");
    } catch (MissingNameException e) {
      // expected
    }
  }

  public static class MyMissingNameFunction implements Function {

    @Override
    public Params params() {
      return null;
    }

    @Override
    public Object execute() throws FunctionException {
      return null;
    }
  }
}
