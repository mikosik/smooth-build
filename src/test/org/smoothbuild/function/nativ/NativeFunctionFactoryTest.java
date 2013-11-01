package org.smoothbuild.function.nativ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.function.base.Name.name;
import static org.smoothbuild.function.base.Param.param;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.testing.function.base.ParamTester.params;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.result.ResultDb;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.nativ.exc.ForbiddenParamTypeException;
import org.smoothbuild.function.nativ.exc.IllegalFunctionNameException;
import org.smoothbuild.function.nativ.exc.IllegalReturnTypeException;
import org.smoothbuild.function.nativ.exc.MoreThanOneSmoothFunctionException;
import org.smoothbuild.function.nativ.exc.NoSmoothFunctionException;
import org.smoothbuild.function.nativ.exc.NonPublicSmoothFunctionException;
import org.smoothbuild.function.nativ.exc.NonStaticSmoothFunctionException;
import org.smoothbuild.function.nativ.exc.ParamMethodHasArgumentsException;
import org.smoothbuild.function.nativ.exc.ParamsIsNotInterfaceException;
import org.smoothbuild.function.nativ.exc.WrongParamsInSmoothFunctionException;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.Required;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.plugin.StringSet;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.err.UnexpectedError;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.plugin.FakeString;
import org.smoothbuild.testing.task.base.FakeResult;
import org.smoothbuild.testing.task.exec.FakeSandbox;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;

public class NativeFunctionFactoryTest {
  TaskGenerator taskGenerator = mock(TaskGenerator.class);
  FakeSandbox sandbox = new FakeSandbox();
  Path tempDir = path("tem/dir");
  ResultDb resultDb = mock(ResultDb.class);
  NativeFunctionFactory nativeFunctionFactory = new NativeFunctionFactory(resultDb);
  CodeLocation codeLocation = new FakeCodeLocation();

  @Before
  public void before() {
    nativeFunctionFactory = Guice.createInjector(new IntegrationTestModule()).getInstance(
        NativeFunctionFactory.class);
  }

  @Test
  public void testSignature() throws Exception {
    Function function = nativeFunctionFactory.create(MyFunction.class, false);

    assertThat(function.name()).isEqualTo(name("myFunction"));
    Signature signature = function.signature();
    assertThat(signature.name()).isEqualTo(name("myFunction"));
    assertThat(signature.type()).isEqualTo(Type.STRING);

    Param paramA = param(STRING, "stringA");
    Param paramB = param(STRING, "stringB");

    assertThat(signature.params()).isEqualTo(params(paramA, paramB));
  }

  @Test
  public void testInvokation() throws Exception {
    Function function = nativeFunctionFactory.create(MyFunction.class, false);
    Result result1 = new FakeResult(new FakeString("abc"));
    Result result2 = new FakeResult(new FakeString("def"));
    ImmutableMap<String, Result> dependencies = ImmutableMap.<String, Result> of("stringA",
        result1, "stringB", result2);

    Task task = function.generateTask(taskGenerator, dependencies, codeLocation);
    StringValue result = (StringValue) task.execute(sandbox);
    sandbox.messages().assertNoProblems();
    assertThat(result.value()).isEqualTo("abcdef");
  }

  public interface Parameters {
    public StringValue stringA();

    public StringValue stringB();
  }

  public static class MyFunction {
    @SmoothFunction("myFunction")
    public static StringValue execute(Sandbox sandbox, Parameters params) {
      return new FakeString(params.stringA().value() + params.stringB().value());
    }
  }

  @Test
  public void allowedParamTypesAreAccepted() throws Exception {
    nativeFunctionFactory.create(MyFunctionWithAllowedParamTypes.class, false);
  }

  public interface AllowedParameters {
    public StringValue string();

    public StringSet stringSet();

    public File file();

    public FileSet fileSet();
  }

  public static class MyFunctionWithAllowedParamTypes {
    @SmoothFunction("myFunction")
    public static StringValue execute(Sandbox sandbox, AllowedParameters params) {
      return new FakeString("string");
    }
  }

  @Test
  public void paramsAnnotatedAsRequiredAreRequired() throws Exception {
    Function f = nativeFunctionFactory.create(MyFunctionWithAnnotatedParams.class, false);
    ImmutableMap<String, Param> params = f.params();
    assertThat(params.get("string1").isRequired()).isTrue();
    assertThat(params.get("string2").isRequired()).isFalse();
  }

  public interface AnnotatedParameters {
    @Required
    public StringValue string1();

    public StringValue string2();
  }

  public static class MyFunctionWithAnnotatedParams {
    @SmoothFunction("myFunction")
    public static void execute(Sandbox sandbox, AnnotatedParameters params) {}
  }

  @Test
  public void functionWithForbiddenParamType() throws Exception {
    assertExceptionThrown(MyFunctionWithForbiddenParamType.class, ForbiddenParamTypeException.class);
  }

  public interface ForbiddenParams {
    public Runnable runnable();
  }

  public static class MyFunctionWithForbiddenParamType {
    @SmoothFunction("myFunction")
    public static StringValue execute(Sandbox sandbox, ForbiddenParams params) {
      return null;
    }
  }

  @Test
  public void emptyParamtersAreAccepted() throws Exception {
    nativeFunctionFactory.create(MyFunctionWithEmptyParameters.class, false);
  }

  public interface EmptyParameters {}

  public static class MyFunctionWithEmptyParameters {
    @SmoothFunction("myFunction")
    public static StringValue execute(Sandbox sandbox, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void stringResultTypeIsAccepted() throws Exception {
    nativeFunctionFactory.create(MyFunctionWithStringResult.class, false);
  }

  public static class MyFunctionWithStringResult {
    @SmoothFunction("myFunction")
    public static StringValue execute(Sandbox sandbox, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void fileResultTypeIsAccepted() throws Exception {
    nativeFunctionFactory.create(MyFunctionWithFileResult.class, false);
  }

  public static class MyFunctionWithFileResult {
    @SmoothFunction("myFunction")
    public static File execute(Sandbox sandbox, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void filesResultTypeIsAccepted() throws Exception {
    nativeFunctionFactory.create(MyFunctionWithFilesResult.class, false);
  }

  public static class MyFunctionWithFilesResult {

    @SmoothFunction("myFunction")
    public static FileSet execute(Sandbox sandbox, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void voidResultTypeIsAccepted() throws Exception {
    nativeFunctionFactory.create(MyFunctionWithVoidResult.class, false);
  }

  public static class MyFunctionWithVoidResult {
    @SmoothFunction("myFunction")
    public static void execute(Sandbox sandbox, EmptyParameters params) {}
  }

  @Test
  public void illegalReturnTypeException() throws Exception {
    assertExceptionThrown(MyFunctionWithIllegalReturnType.class, IllegalReturnTypeException.class);
  }

  public static class MyFunctionWithIllegalReturnType {

    @SmoothFunction("MyFunction")
    public static Runnable execute(Sandbox sandbox, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void paramsIsNotInterfaceException() throws Exception {
    assertExceptionThrown(MyFunctionWithParamThatIsNotInterface.class,
        ParamsIsNotInterfaceException.class);
  }

  public static class MyFunctionWithParamThatIsNotInterface {

    @SmoothFunction("myFunction")
    public static void execute(Sandbox sandbox, String string) {}
  }

  @Test
  public void illegalFunctionNameException() throws Exception {
    assertExceptionThrown(MyFunctionWithIllegalFunctionName.class,
        IllegalFunctionNameException.class);
  }

  public static class MyFunctionWithIllegalFunctionName {
    @SmoothFunction("my..package")
    public static void execute(Sandbox sandbox, EmptyParameters params) {}
  }

  @Test
  public void runtimeExceptionThrownAreReported() throws Exception {
    Function function = nativeFunctionFactory.create(MyFunctionWithThrowingSmoothMethod.class,
        false);
    function.generateTask(taskGenerator, Empty.stringTaskResultMap(), codeLocation)
        .execute(sandbox);
    sandbox.messages().assertOnlyProblem(UnexpectedError.class);
  }

  public static class MyFunctionWithThrowingSmoothMethod {
    @SmoothFunction("MyFunction")
    public static void execute(Sandbox sandbox, EmptyParameters params) {
      throw new RuntimeException();
    }
  }

  @Test
  public void moreThanOneSmoothMethodException() throws Exception {
    assertExceptionThrown(MyFunctionWithTwoSmoothMethods.class,
        MoreThanOneSmoothFunctionException.class);
  }

  public static class MyFunctionWithTwoSmoothMethods {
    @SmoothFunction("MyFunction")
    public static void execute(Sandbox sandbox, EmptyParameters params) {}

    @SmoothFunction("MyFunction2")
    public static void execute2(Sandbox sandbox, EmptyParameters params) {}
  }

  @Test
  public void noSmoothMethodException() throws Exception {
    assertExceptionThrown(MyFunctionWithZeroSmoothMethods.class, NoSmoothFunctionException.class);
  }

  public static class MyFunctionWithZeroSmoothMethods {}

  @Test
  public void nonPublicSmoothMethodException() throws Exception {
    assertExceptionThrown(MyFunctionWithPrivateSmoothMethod.class,
        NonPublicSmoothFunctionException.class);
  }

  public static class MyFunctionWithPrivateSmoothMethod {
    @SmoothFunction("MyFunction")
    private static void execute(Sandbox sandbox, EmptyParameters params) {}
  }

  @Test
  public void paramMethodHasArgumentsException() throws Exception {
    assertExceptionThrown(MyFunctionWithParamMethodThatHasParameters.class,
        ParamMethodHasArgumentsException.class);
  }

  public interface ParametersWithMethodWithParameters {
    public String string(String notAllowed);
  }

  public static class MyFunctionWithParamMethodThatHasParameters {
    @SmoothFunction("MyFunction")
    public static void execute(Sandbox sandbox, ParametersWithMethodWithParameters params) {}
  }

  @Test
  public void nonStaticSmoothMethodException() throws Exception {
    assertExceptionThrown(MyFunctionWithNonStaticSmoothMethod.class,
        NonStaticSmoothFunctionException.class);
  }

  public static class MyFunctionWithNonStaticSmoothMethod {
    @SmoothFunction("MyFunction")
    public void execute(Sandbox sandbox, EmptyParameters params) {}
  }

  @Test
  public void zeroParamsInSmoothMethodException() throws Exception {
    assertExceptionThrown(MyFunctionWithSmoothMethodWithZeroParams.class,
        WrongParamsInSmoothFunctionException.class);
  }

  public static class MyFunctionWithSmoothMethodWithZeroParams {
    @SmoothFunction("MyFunction")
    public static void execute() {}
  }

  @Test
  public void oneParamInSmoothMethodException() throws Exception {
    assertExceptionThrown(MyFunctionWithSmoothMethodWithOneParam.class,
        WrongParamsInSmoothFunctionException.class);
  }

  public static class MyFunctionWithSmoothMethodWithOneParam {
    @SmoothFunction("MyFunction")
    public static void execute() {}
  }

  @Test
  public void wrongFirstParamInSmoothMethodException() throws Exception {
    assertExceptionThrown(MyFunctionWithSmoothMethodWithWrongFirstParam.class,
        WrongParamsInSmoothFunctionException.class);
  }

  public static class MyFunctionWithSmoothMethodWithWrongFirstParam {
    @SmoothFunction("MyFunction")
    public static void execute(Parameters wrong, Parameters params) {}
  }

  @Test
  public void wrongSecondParamInSmoothMethodException() throws Exception {
    assertExceptionThrown(MyFunctionWithSmoothMethodWithWrongSecondParam.class,
        ParamsIsNotInterfaceException.class);
  }

  public static class MyFunctionWithSmoothMethodWithWrongSecondParam {
    @SmoothFunction("MyFunction")
    public static void execute(Sandbox sandbox, Integer wrong) {}
  }

  public static class FunctionForHash1 {
    @SmoothFunction("function1")
    public static void execute(Sandbox sandbox, EmptyParameters params) {}
  }

  public static class FunctionForHash2 {
    @SmoothFunction("function2")
    public static void execute(Sandbox sandbox, EmptyParameters params) {}
  }

  private void assertExceptionThrown(Class<?> klass, Class<?> exception) {
    try {
      nativeFunctionFactory.create(klass, false);
      fail("exception should be thrown");
    } catch (Throwable e) {
      // expected
      assertThat(e).isInstanceOf(exception);
    }
  }
}
