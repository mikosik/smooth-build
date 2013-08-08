package org.smoothbuild.registry.instantiate;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.lang.type.Path.path;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.FunctionName;
import org.smoothbuild.lang.function.Params;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.type.Path;
import org.smoothbuild.registry.exc.CreatingInstanceFailedException;
import org.smoothbuild.registry.exc.FunctionImplementationException;
import org.smoothbuild.registry.exc.MissingNameException;

public class FunctionFactoryTest {
  Instantiator instantiator = mock(Instantiator.class);
  Path path = path("abc");
  FunctionDefinition definition = mock(FunctionDefinition.class);

  InstantiatorFactory instantiatorFactory = mock(InstantiatorFactory.class);
  FunctionFactory functionFactory = new FunctionFactory(instantiatorFactory);

  @Test
  public void creatingFunctionType() throws FunctionImplementationException,
      CreatingInstanceFailedException {
    when(instantiatorFactory.create(MyNamedFunction.class)).thenReturn(instantiator);
    when(instantiator.newInstance(path)).thenReturn(definition);

    Function function = functionFactory.create(MyNamedFunction.class);
    FunctionDefinition newDefinition = function.newInstance(path);

    assertThat(newDefinition).isEqualTo(definition);
  }

  @FunctionName("myFunction")
  public static class MyNamedFunction implements FunctionDefinition {

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
      functionFactory.create(MyMissingNameFunction.class);
      Assert.fail("exception should be thrown");
    } catch (MissingNameException e) {
      // expected
    }
  }

  public static class MyMissingNameFunction implements FunctionDefinition {

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
