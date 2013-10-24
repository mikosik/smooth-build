package org.smoothbuild.function.nativ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.function.base.Name.qualifiedName;
import static org.smoothbuild.function.base.Param.param;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.testing.function.base.ParamTester.params;
import static org.smoothbuild.testing.task.exec.HashedTasksTester.hashedTasks;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.mem.MemoryFileSystemModule;
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
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.err.UnexpectedError;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.task.base.FakeTask;
import org.smoothbuild.testing.task.exec.FakeSandbox;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import com.google.inject.Guice;

public class NativeFunctionFactoryTest {
  FakeSandbox sandbox = new FakeSandbox();
  Path tempDir = path("tem/dir");
  NativeFunctionFactory nativeFunctionFactory;
  CodeLocation codeLocation = codeLocation(1, 2, 4);

  @Before
  public void before() {
    nativeFunctionFactory = Guice.createInjector(new MemoryFileSystemModule()).getInstance(
        NativeFunctionFactory.class);
  }

  @Test
  public void testSignature() throws Exception {
    Function function = NativeFunctionFactory.create(MyFunction.class, false);

    assertThat(function.name()).isEqualTo(qualifiedName("my.package.myFunction"));
    Signature signature = function.signature();
    assertThat(signature.name()).isEqualTo(qualifiedName("my.package.myFunction"));
    assertThat(signature.type()).isEqualTo(Type.STRING);

    Param paramA = param(STRING, "stringA");
    Param paramB = param(STRING, "stringB");

    assertThat(signature.params()).isEqualTo(params(paramA, paramB));
  }

  @Test
  public void testInvokation() throws Exception {
    Function function = NativeFunctionFactory.create(MyFunction.class, false);
    FakeTask task1 = new FakeTask("abc");
    FakeTask task2 = new FakeTask("def");
    ImmutableMap<String, HashCode> dependencies = ImmutableMap.of("stringA", task1.hash(),
        "stringB", task2.hash());
    TaskGenerator taskGenerator = mock(TaskGenerator.class);

    Task task = function.generateTask(taskGenerator, dependencies, codeLocation);
    task.execute(sandbox, hashedTasks(task1, task2));
    sandbox.messages().assertNoProblems();
    assertThat(task.result()).isEqualTo("abcdef");
  }

  public interface Parameters {
    public String stringA();

    public String stringB();
  }

  public static class MyFunction {
    @SmoothFunction("my.package.myFunction")
    public static String execute(Sandbox sandbox, Parameters params) {
      return params.stringA() + params.stringB();
    }
  }

  @Test
  public void allowedParamTypesAreAccepted() throws Exception {
    NativeFunctionFactory.create(MyFunctionWithAllowedParamTypes.class, false);
  }

  public interface AllowedParameters {
    public String string();

    public File file();

    public FileSet fileSet();
  }

  public static class MyFunctionWithAllowedParamTypes {
    @SmoothFunction("my.package.myFunction")
    public static String execute(Sandbox sandbox, Parameters params) {
      return params.stringA() + params.stringB();
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
    public String string1();

    public String string2();
  }

  public static class MyFunctionWithAnnotatedParams {
    @SmoothFunction("my.package.myFunction")
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
    @SmoothFunction("my.package.myFunction")
    public static String execute(Sandbox sandbox, ForbiddenParams params) {
      return null;
    }
  }

  @Test
  public void emptyParamtersAreAccepted() throws Exception {
    NativeFunctionFactory.create(MyFunctionWithEmptyParameters.class, false);
  }

  public interface EmptyParameters {}

  public static class MyFunctionWithEmptyParameters {
    @SmoothFunction("my.package.myFunction")
    public static String execute(Sandbox sandbox, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void stringResultTypeIsAccepted() throws Exception {
    NativeFunctionFactory.create(MyFunctionWithStringResult.class, false);
  }

  public static class MyFunctionWithStringResult {
    @SmoothFunction("my.package.myFunction")
    public static String execute(Sandbox sandbox, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void fileResultTypeIsAccepted() throws Exception {
    NativeFunctionFactory.create(MyFunctionWithFileResult.class, false);
  }

  public static class MyFunctionWithFileResult {
    @SmoothFunction("my.package.myFunction")
    public static File execute(Sandbox sandbox, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void filesResultTypeIsAccepted() throws Exception {
    NativeFunctionFactory.create(MyFunctionWithFilesResult.class, false);
  }

  public static class MyFunctionWithFilesResult {

    @SmoothFunction("my.package.myFunction")
    public static FileSet execute(Sandbox sandbox, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void voidResultTypeIsAccepted() throws Exception {
    NativeFunctionFactory.create(MyFunctionWithVoidResult.class, false);
  }

  public static class MyFunctionWithVoidResult {
    @SmoothFunction("my.package.myFunction")
    public static void execute(Sandbox sandbox, EmptyParameters params) {}
  }

  @Test
  public void illegalReturnTypeException() throws Exception {
    assertExceptionThrown(MyFunctionWithIllegalReturnType.class, IllegalReturnTypeException.class);
  }

  public static class MyFunctionWithIllegalReturnType {

    @SmoothFunction("my.package.MyFunction")
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

    @SmoothFunction("my.package.myFunction")
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
    Function function = NativeFunctionFactory.create(MyFunctionWithThrowingSmoothMethod.class,
        false);
    TaskGenerator taskGenerator = mock(TaskGenerator.class);
    function.generateTask(taskGenerator, Empty.stringHashMap(), codeLocation).execute(sandbox,
        hashedTasks());
    sandbox.messages().assertOnlyProblem(UnexpectedError.class);
  }

  public static class MyFunctionWithThrowingSmoothMethod {
    @SmoothFunction("my.package.MyFunction")
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
    @SmoothFunction("my.package.MyFunction")
    public static void execute(Sandbox sandbox, EmptyParameters params) {}

    @SmoothFunction("my.package.MyFunction2")
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
    @SmoothFunction("my.package.MyFunction")
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
    @SmoothFunction("my.package.MyFunction")
    public static void execute(Sandbox sandbox, ParametersWithMethodWithParameters params) {}
  }

  @Test
  public void nonStaticSmoothMethodException() throws Exception {
    assertExceptionThrown(MyFunctionWithNonStaticSmoothMethod.class,
        NonStaticSmoothFunctionException.class);
  }

  public static class MyFunctionWithNonStaticSmoothMethod {
    @SmoothFunction("my.package.MyFunction")
    public void execute(Sandbox sandbox, EmptyParameters params) {}
  }

  @Test
  public void zeroParamsInSmoothMethodException() throws Exception {
    assertExceptionThrown(MyFunctionWithSmoothMethodWithZeroParams.class,
        WrongParamsInSmoothFunctionException.class);
  }

  public static class MyFunctionWithSmoothMethodWithZeroParams {
    @SmoothFunction("my.package.MyFunction")
    public static void execute() {}
  }

  @Test
  public void oneParamInSmoothMethodException() throws Exception {
    assertExceptionThrown(MyFunctionWithSmoothMethodWithOneParam.class,
        WrongParamsInSmoothFunctionException.class);
  }

  public static class MyFunctionWithSmoothMethodWithOneParam {
    @SmoothFunction("my.package.MyFunction")
    public static void execute() {}
  }

  @Test
  public void wrongFirstParamInSmoothMethodException() throws Exception {
    assertExceptionThrown(MyFunctionWithSmoothMethodWithWrongFirstParam.class,
        WrongParamsInSmoothFunctionException.class);
  }

  public static class MyFunctionWithSmoothMethodWithWrongFirstParam {
    @SmoothFunction("my.package.MyFunction")
    public static void execute(Parameters wrong, Parameters params) {}
  }

  @Test
  public void wrongSecondParamInSmoothMethodException() throws Exception {
    assertExceptionThrown(MyFunctionWithSmoothMethodWithWrongSecondParam.class,
        ParamsIsNotInterfaceException.class);
  }

  public static class MyFunctionWithSmoothMethodWithWrongSecondParam {
    @SmoothFunction("my.package.MyFunction")
    public static void execute(Sandbox sandbox, Integer wrong) {}
  }

  @Test
  public void differentFunctionHaveDifferentHashes() throws Exception {
    NativeFunction f1 = NativeFunctionFactory.create(FunctionForHash1.class, false);
    NativeFunction f2 = NativeFunctionFactory.create(FunctionForHash2.class, false);
    assertThat(f1.hash()).isNotEqualTo(f2.hash());
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
      NativeFunctionFactory.create(klass, false);
      fail("exception should be thrown");
    } catch (Throwable e) {
      // expected
      assertThat(e).isInstanceOf(exception);
    }
  }
}
