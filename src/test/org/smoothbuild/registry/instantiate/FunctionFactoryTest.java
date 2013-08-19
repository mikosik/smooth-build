package org.smoothbuild.registry.instantiate;

import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.type.Path.path;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.FunctionName;
import org.smoothbuild.lang.function.Params;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.type.Path;
import org.smoothbuild.registry.exc.FunctionImplementationException;
import org.smoothbuild.registry.exc.IllegalFunctionNameException;
import org.smoothbuild.registry.exc.IllegalReturnTypeException;
import org.smoothbuild.registry.exc.MissingNameException;
import org.smoothbuild.registry.exc.StrangeExecuteMethodException;

public class FunctionFactoryTest {
  Instantiator instantiator = mock(Instantiator.class);
  Path path = path("abc");
  FunctionDefinition definition = mock(FunctionDefinition.class);

  InstantiatorFactory instantiatorFactory = mock(InstantiatorFactory.class);
  FunctionFactory functionFactory = new FunctionFactory(instantiatorFactory);

  // TODO add tests for creating Function once Function is simplified

  @FunctionName("myFunction")
  public static class MyNamedFunction implements FunctionDefinition {

    @Override
    public Params params() {
      return null;
    }

    @Override
    public String execute() throws FunctionException {
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

  @Test
  public void missingExecuteMethodCausesExceptionToBeThrown()
      throws FunctionImplementationException {
    try {
      Class<?> x = FakeFunction.class;
      @SuppressWarnings("unchecked")
      Class<FunctionDefinition> klass = (Class<FunctionDefinition>) x;
      functionFactory.create(klass);
      Assert.fail("exception should be thrown");
    } catch (StrangeExecuteMethodException e) {
      // expected
    }
  }

  @FunctionName("name")
  public static class FakeFunction {

  }

  @Test
  public void illegalReturnTypeCausesExceptionToBeThrown() throws FunctionImplementationException {
    try {
      functionFactory.create(MyIllegalReturnTypeFunction.class);
      Assert.fail("exception should be thrown");
    } catch (IllegalReturnTypeException e) {
      // expected
    }
  }

  @FunctionName("name")
  public static class MyIllegalReturnTypeFunction implements FunctionDefinition {

    @Override
    public Params params() {
      return null;
    }

    @Override
    public Runnable execute() throws FunctionException {
      return null;
    }
  }

  @Test
  public void illegalFunctionNameCausesExceptionToBeThrown() throws FunctionImplementationException {
    try {
      functionFactory.create(MyIllegalFunctionNameFunction.class);
      Assert.fail("exception should be thrown");
    } catch (IllegalFunctionNameException e) {
      // expected
    }
  }

  @FunctionName("name.")
  public static class MyIllegalFunctionNameFunction implements FunctionDefinition {

    @Override
    public Params params() {
      return null;
    }

    @Override
    public Runnable execute() throws FunctionException {
      return null;
    }
  }
}
