package org.smoothbuild.lang.function.nativ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.function.base.Type.STRING;
import static org.smoothbuild.testing.lang.function.base.ParamTester.params;

import org.junit.Test;
import org.smoothbuild.io.cache.task.TaskDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.base.Type;
import org.smoothbuild.lang.function.nativ.exc.ForbiddenParamTypeException;
import org.smoothbuild.lang.function.nativ.exc.IllegalFunctionNameException;
import org.smoothbuild.lang.function.nativ.exc.IllegalReturnTypeException;
import org.smoothbuild.lang.function.nativ.exc.MoreThanOneSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.exc.NoSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.exc.NonPublicSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.exc.NonStaticSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.exc.ParamMethodHasArgumentsException;
import org.smoothbuild.lang.function.nativ.exc.ParamsIsNotInterfaceException;
import org.smoothbuild.lang.function.nativ.exc.WrongParamsInSmoothFunctionException;
import org.smoothbuild.lang.function.value.Array;
import org.smoothbuild.lang.function.value.File;
import org.smoothbuild.lang.function.value.StringValue;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.err.UnexpectedError;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.lang.function.value.FakeString;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.base.FakeResult;
import org.smoothbuild.testing.task.exec.FakeSandbox;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;

public class NativeFunctionFactoryTest {
  TaskGenerator taskGenerator = mock(TaskGenerator.class);
  FakeSandbox sandbox = new FakeSandbox();
  Path tempDir = path("tem/dir");
  TaskDb taskDb = mock(TaskDb.class);
  CodeLocation codeLocation = new FakeCodeLocation();

  @Test
  public void testSignature() throws Exception {
    Function function = NativeFunctionFactory.create(MyFunction.class, false);

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
    Function function = NativeFunctionFactory.create(MyFunction.class, false);
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
    @SmoothFunction(name = "myFunction")
    public static StringValue execute(Sandbox sandbox, Parameters params) {
      return new FakeString(params.stringA().value() + params.stringB().value());
    }
  }

  @Test
  public void allowedParamTypesAreAccepted() throws Exception {
    NativeFunctionFactory.create(MyFunctionWithAllowedParamTypes.class, false);
  }

  public interface AllowedParameters {
    public StringValue string();

    public Array<StringValue> stringSet();

    public File file();

    public Array<File> fileSet();
  }

  public static class MyFunctionWithAllowedParamTypes {
    @SmoothFunction(name = "myFunction")
    public static StringValue execute(Sandbox sandbox, AllowedParameters params) {
      return new FakeString("string");
    }
  }

  @Test
  public void paramsAnnotatedAsRequiredAreRequired() throws Exception {
    Function f = NativeFunctionFactory.create(MyFunctionWithAnnotatedParams.class, false);
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
    @SmoothFunction(name = "myFunction")
    public static StringValue execute(Sandbox sandbox, AnnotatedParameters params) {
      return null;
    }
  }

  @Test
  public void array_of_array_is_forbidden_as_param_type() throws Exception {
    assertExceptionThrown(MyFunctionWithArrayOfArrayParamType.class,
        ForbiddenParamTypeException.class);
  }

  public interface ArrayOfArrayParams {
    public Array<Array<StringValue>> runnable();
  }

  public static class MyFunctionWithArrayOfArrayParamType {
    @SmoothFunction(name = "myFunction")
    public static StringValue execute(Sandbox sandbox, ArrayOfArrayParams params) {
      return null;
    }
  }

  @Test
  public void functionWithForbiddenParamType() throws Exception {
    assertExceptionThrown(MyFunctionWithForbiddenParamType.class, ForbiddenParamTypeException.class);
  }

  public interface ForbiddenParams {
    public Runnable runnable();
  }

  public static class MyFunctionWithForbiddenParamType {
    @SmoothFunction(name = "myFunction")
    public static StringValue execute(Sandbox sandbox, ForbiddenParams params) {
      return null;
    }
  }

  @Test
  public void emptyParamtersAreAccepted() throws Exception {
    NativeFunctionFactory.create(MyFunctionWithEmptyParameters.class, false);
  }

  public interface EmptyParameters {}

  public static class MyFunctionWithEmptyParameters {
    @SmoothFunction(name = "myFunction")
    public static StringValue execute(Sandbox sandbox, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void stringResultTypeIsAccepted() throws Exception {
    NativeFunctionFactory.create(MyFunctionWithStringResult.class, false);
  }

  public static class MyFunctionWithStringResult {
    @SmoothFunction(name = "myFunction")
    public static StringValue execute(Sandbox sandbox, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void fileResultTypeIsAccepted() throws Exception {
    NativeFunctionFactory.create(MyFunctionWithFileResult.class, false);
  }

  public static class MyFunctionWithFileResult {
    @SmoothFunction(name = "myFunction")
    public static File execute(Sandbox sandbox, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void file_array_result_type_is_accepted() throws Exception {
    NativeFunctionFactory.create(MyFunctionWithFileArrayResult.class, false);
  }

  public static class MyFunctionWithFileArrayResult {
    @SmoothFunction(name = "myFunction")
    public static Array<File> execute(Sandbox sandbox, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void illegalReturnTypeException() throws Exception {
    assertExceptionThrown(MyFunctionWithIllegalReturnType.class, IllegalReturnTypeException.class);
  }

  public static class MyFunctionWithIllegalReturnType {
    @SmoothFunction(name = "MyFunction")
    public static Runnable execute(Sandbox sandbox, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void array_of_array_result_type_is_not_allowed() throws Exception {
    assertExceptionThrown(MyFunctionWithArrayOfArrayReturnType.class,
        IllegalReturnTypeException.class);
  }

  public static class MyFunctionWithArrayOfArrayReturnType {
    @SmoothFunction(name = "MyFunction")
    public static Array<Array<File>> execute(Sandbox sandbox, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void paramsIsNotInterfaceException() throws Exception {
    assertExceptionThrown(MyFunctionWithParamThatIsNotInterface.class,
        ParamsIsNotInterfaceException.class);
  }

  public static class MyFunctionWithParamThatIsNotInterface {

    @SmoothFunction(name = "myFunction")
    public static StringValue execute(Sandbox sandbox, String string) {
      return null;
    }
  }

  @Test
  public void illegalFunctionNameException() throws Exception {
    assertExceptionThrown(MyFunctionWithIllegalFunctionName.class,
        IllegalFunctionNameException.class);
  }

  public static class MyFunctionWithIllegalFunctionName {
    @SmoothFunction(name = "my^package")
    public static StringValue execute(Sandbox sandbox, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void runtimeExceptionThrownAreReported() throws Exception {
    Function function = NativeFunctionFactory.create(MyFunctionWithThrowingSmoothMethod.class,
        false);
    function.generateTask(taskGenerator, Empty.stringTaskResultMap(), codeLocation)
        .execute(sandbox);
    sandbox.messages().assertOnlyProblem(UnexpectedError.class);
  }

  public static class MyFunctionWithThrowingSmoothMethod {
    @SmoothFunction(name = "MyFunction")
    public static StringValue execute(Sandbox sandbox, EmptyParameters params) {
      throw new RuntimeException();
    }
  }

  @Test
  public void moreThanOneSmoothMethodException() throws Exception {
    assertExceptionThrown(MyFunctionWithTwoSmoothMethods.class,
        MoreThanOneSmoothFunctionException.class);
  }

  public static class MyFunctionWithTwoSmoothMethods {
    @SmoothFunction(name = "MyFunction")
    public static void execute(Sandbox sandbox, EmptyParameters params) {}

    @SmoothFunction(name = "MyFunction2")
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
    @SmoothFunction(name = "MyFunction")
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
    @SmoothFunction(name = "MyFunction")
    public static StringValue execute(Sandbox sandbox, ParametersWithMethodWithParameters params) {
      return null;
    }
  }

  @Test
  public void nonStaticSmoothMethodException() throws Exception {
    assertExceptionThrown(MyFunctionWithNonStaticSmoothMethod.class,
        NonStaticSmoothFunctionException.class);
  }

  public static class MyFunctionWithNonStaticSmoothMethod {
    @SmoothFunction(name = "MyFunction")
    public void execute(Sandbox sandbox, EmptyParameters params) {}
  }

  @Test
  public void zeroParamsInSmoothMethodException() throws Exception {
    assertExceptionThrown(MyFunctionWithSmoothMethodWithZeroParams.class,
        WrongParamsInSmoothFunctionException.class);
  }

  public static class MyFunctionWithSmoothMethodWithZeroParams {
    @SmoothFunction(name = "MyFunction")
    public static void execute() {}
  }

  @Test
  public void oneParamInSmoothMethodException() throws Exception {
    assertExceptionThrown(MyFunctionWithSmoothMethodWithOneParam.class,
        WrongParamsInSmoothFunctionException.class);
  }

  public static class MyFunctionWithSmoothMethodWithOneParam {
    @SmoothFunction(name = "MyFunction")
    public static void execute() {}
  }

  @Test
  public void wrongFirstParamInSmoothMethodException() throws Exception {
    assertExceptionThrown(MyFunctionWithSmoothMethodWithWrongFirstParam.class,
        WrongParamsInSmoothFunctionException.class);
  }

  public static class MyFunctionWithSmoothMethodWithWrongFirstParam {
    @SmoothFunction(name = "MyFunction")
    public static void execute(Parameters wrong, Parameters params) {}
  }

  @Test
  public void wrongSecondParamInSmoothMethodException() throws Exception {
    assertExceptionThrown(MyFunctionWithSmoothMethodWithWrongSecondParam.class,
        ParamsIsNotInterfaceException.class);
  }

  public static class MyFunctionWithSmoothMethodWithWrongSecondParam {
    @SmoothFunction(name = "MyFunction")
    public static StringValue execute(Sandbox sandbox, Integer wrong) {
      return null;
    }
  }

  public static class FunctionForHash1 {
    @SmoothFunction(name = "function1")
    public static void execute(Sandbox sandbox, EmptyParameters params) {}
  }

  public static class FunctionForHash2 {
    @SmoothFunction(name = "function2")
    public static void execute(Sandbox sandbox, EmptyParameters params) {}
  }

  private void assertExceptionThrown(Class<?> klass, Class<?> exception) {
    try {
      NativeFunctionFactory.create(klass, false);
      fail("exception should be thrown");
    } catch (Throwable e) {
      // expected
      assertThat(e).isInstanceOf(exception);
    }
  }
}
