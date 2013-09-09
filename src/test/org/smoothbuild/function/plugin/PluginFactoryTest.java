package org.smoothbuild.function.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.function.base.Name.qualifiedName;
import static org.smoothbuild.function.base.Param.param;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.plugin.api.Path.path;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.fs.mem.MemoryFileSystemModule;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.plugin.exc.ForbiddenParamTypeException;
import org.smoothbuild.function.plugin.exc.IllegalFunctionNameException;
import org.smoothbuild.function.plugin.exc.IllegalReturnTypeException;
import org.smoothbuild.function.plugin.exc.MoreThanOneSmoothFunctionException;
import org.smoothbuild.function.plugin.exc.NoSmoothFunctionException;
import org.smoothbuild.function.plugin.exc.NonPublicSmoothFunctionException;
import org.smoothbuild.function.plugin.exc.NonStaticSmoothFunctionException;
import org.smoothbuild.function.plugin.exc.ParamMethodHasArgumentsException;
import org.smoothbuild.function.plugin.exc.ParamsIsNotInterfaceException;
import org.smoothbuild.function.plugin.exc.WrongParamsInSmoothFunctionException;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.plugin.api.SmoothFunction;
import org.smoothbuild.task.PrecalculatedTask;
import org.smoothbuild.task.Task;
import org.smoothbuild.task.err.UnexpectedError;
import org.smoothbuild.testing.plugin.internal.TestSandbox;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;

public class PluginFactoryTest {
  TestSandbox sandbox = new TestSandbox();
  Path tempDir = path("tem/dir");
  PluginFactory pluginFactory;

  @Before
  public void before() {
    pluginFactory = Guice.createInjector(new MemoryFileSystemModule()).getInstance(
        PluginFactory.class);
  }

  @Test
  public void testSignature() throws Exception {
    Function function = pluginFactory.create(MyPlugin.class);

    assertThat(function.name()).isEqualTo(qualifiedName("my.package.myFunction"));
    Signature signature = function.signature();
    assertThat(signature.name()).isEqualTo(qualifiedName("my.package.myFunction"));
    assertThat(signature.type()).isEqualTo(Type.STRING);

    Param paramA = param(STRING, "stringA");
    Param paramB = param(STRING, "stringB");

    assertThat(signature.params()).isEqualTo(Param.params(paramA, paramB));
  }

  @Test
  public void testInvokation() throws Exception {
    Function function = pluginFactory.create(MyPlugin.class);
    ImmutableMap<String, Task> dependencies = ImmutableMap.of("stringA",
        stringReturningTask("abc"), "stringB", stringReturningTask("def"));
    Task task = function.generateTask(dependencies);
    task.execute(sandbox);
    sandbox.problems().assertNoProblems();
    assertThat(task.result()).isEqualTo("abcdef");
  }

  private Task stringReturningTask(String string) {
    return new PrecalculatedTask(string);
  }

  public interface Parameters {
    public String stringA();

    public String stringB();
  }

  public static class MyPlugin {
    @SmoothFunction("my.package.myFunction")
    public static String execute(Sandbox sandbox, Parameters params) {
      return params.stringA() + params.stringB();
    }
  }

  @Test
  public void allowedParamTypesAreAccepted() throws Exception {
    pluginFactory.create(MyPluginWithAllowedParamTypes.class);
  }

  public interface AllowedParameters {
    public String string();

    public File file();

    public FileSet fileSet();
  }

  public static class MyPluginWithAllowedParamTypes {
    @SmoothFunction("my.package.myFunction")
    public static String execute(Sandbox sandbox, Parameters params) {
      return params.stringA() + params.stringB();
    }
  }

  @Test
  public void pluginWithForbiddenParamType() throws Exception {
    assertExceptionThrown(MyPluginWithForbiddenParamType.class, ForbiddenParamTypeException.class);
  }

  public interface ForbiddenParams {
    public Runnable runnable();
  }

  public static class MyPluginWithForbiddenParamType {
    @SmoothFunction("my.package.myFunction")
    public static String execute(Sandbox sandbox, ForbiddenParams params) {
      return null;
    }
  }

  @Test
  public void emptyParamtersAreAccepted() throws Exception {
    pluginFactory.create(MyPluginWithEmptyParameters.class);
  }

  public interface EmptyParameters {}

  public static class MyPluginWithEmptyParameters {
    @SmoothFunction("my.package.myFunction")
    public static String execute(Sandbox sandbox, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void stringResultTypeIsAccepted() throws Exception {
    pluginFactory.create(MyPluginWithStringResult.class);
  }

  public static class MyPluginWithStringResult {
    @SmoothFunction("my.package.myFunction")
    public static String execute(Sandbox sandbox, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void fileResultTypeIsAccepted() throws Exception {
    pluginFactory.create(MyPluginWithFileResult.class);
  }

  public static class MyPluginWithFileResult {
    @SmoothFunction("my.package.myFunction")
    public static File execute(Sandbox sandbox, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void filesResultTypeIsAccepted() throws Exception {
    pluginFactory.create(MyPluginWithFilesResult.class);
  }

  public static class MyPluginWithFilesResult {

    @SmoothFunction("my.package.myFunction")
    public static FileSet execute(Sandbox sandbox, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void voidResultTypeIsAccepted() throws Exception {
    pluginFactory.create(MyPluginWithVoidResult.class);
  }

  public static class MyPluginWithVoidResult {
    @SmoothFunction("my.package.myFunction")
    public static void execute(Sandbox sandbox, EmptyParameters params) {}
  }

  @Test
  public void illegalReturnTypeException() throws Exception {
    assertExceptionThrown(MyPluginWithIllegalReturnType.class, IllegalReturnTypeException.class);
  }

  public static class MyPluginWithIllegalReturnType {

    @SmoothFunction("my.package.MyFunction")
    public static Runnable execute(Sandbox sandbox, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void paramsIsNotInterfaceException() throws Exception {
    assertExceptionThrown(MyPluginWithParamThatIsNotInterface.class,
        ParamsIsNotInterfaceException.class);
  }

  public static class MyPluginWithParamThatIsNotInterface {

    @SmoothFunction("my.package.myFunction")
    public static void execute(Sandbox sandbox, String string) {}
  }

  @Test
  public void illegalFunctionNameException() throws Exception {
    assertExceptionThrown(MyPluginWithIllegalFunctionName.class, IllegalFunctionNameException.class);
  }

  public static class MyPluginWithIllegalFunctionName {
    @SmoothFunction("my..package")
    public static void execute(Sandbox sandbox, EmptyParameters params) {}
  }

  @Test
  public void runtimeExceptionThrownAreReported() throws Exception {
    Function function = pluginFactory.create(MyPluginWithThrowingSmoothMethod.class);
    function.generateTask(Empty.stringTaskMap()).execute(sandbox);
    sandbox.problems().assertOnlyProblem(UnexpectedError.class);
  }

  public static class MyPluginWithThrowingSmoothMethod {
    @SmoothFunction("my.package.MyFunction")
    public static void execute(Sandbox sandbox, EmptyParameters params) {
      throw new RuntimeException();
    }
  }

  @Test
  public void moreThanOneSmoothMethodException() throws Exception {
    assertExceptionThrown(MyPluginWithTwoSmoothMethods.class,
        MoreThanOneSmoothFunctionException.class);
  }

  public static class MyPluginWithTwoSmoothMethods {
    @SmoothFunction("my.package.MyFunction")
    public static void execute(Sandbox sandbox, EmptyParameters params) {}

    @SmoothFunction("my.package.MyFunction2")
    public static void execute2(Sandbox sandbox, EmptyParameters params) {}
  }

  @Test
  public void noSmoothMethodException() throws Exception {
    assertExceptionThrown(MyPluginWithZeroSmoothMethods.class, NoSmoothFunctionException.class);
  }

  public static class MyPluginWithZeroSmoothMethods {}

  @Test
  public void nonPublicSmoothMethodException() throws Exception {
    assertExceptionThrown(MyPluginWithPrivateSmoothMethod.class,
        NonPublicSmoothFunctionException.class);
  }

  public static class MyPluginWithPrivateSmoothMethod {
    @SmoothFunction("my.package.MyFunction")
    private static void execute(Sandbox sandbox, EmptyParameters params) {}
  }

  @Test
  public void paramMethodHasArgumentsException() throws Exception {
    assertExceptionThrown(MyPluginWithParamMethodThatHasParameters.class,
        ParamMethodHasArgumentsException.class);
  }

  public interface ParametersWithMethodWithParameters {
    public String string(String notAllowed);
  }

  public static class MyPluginWithParamMethodThatHasParameters {
    @SmoothFunction("my.package.MyFunction")
    public static void execute(Sandbox sandbox, ParametersWithMethodWithParameters params) {}
  }

  @Test
  public void nonStaticSmoothMethodException() throws Exception {
    assertExceptionThrown(MyPluginWithNonStaticSmoothMethod.class,
        NonStaticSmoothFunctionException.class);
  }

  public static class MyPluginWithNonStaticSmoothMethod {
    @SmoothFunction("my.package.MyFunction")
    public void execute(Sandbox sandbox, EmptyParameters params) {}
  }

  @Test
  public void zeroParamsInSmoothMethodException() throws Exception {
    assertExceptionThrown(MyPluginWithSmoothMethodWithZeroParams.class,
        WrongParamsInSmoothFunctionException.class);
  }

  public static class MyPluginWithSmoothMethodWithZeroParams {
    @SmoothFunction("my.package.MyFunction")
    public static void execute() {}
  }

  @Test
  public void oneParamInSmoothMethodException() throws Exception {
    assertExceptionThrown(MyPluginWithSmoothMethodWithOneParam.class,
        WrongParamsInSmoothFunctionException.class);
  }

  public static class MyPluginWithSmoothMethodWithOneParam {
    @SmoothFunction("my.package.MyFunction")
    public static void execute() {}
  }

  @Test
  public void wrongFirstParamInSmoothMethodException() throws Exception {
    assertExceptionThrown(MyPluginWithSmoothMethodWithWrongFirstParam.class,
        WrongParamsInSmoothFunctionException.class);
  }

  public static class MyPluginWithSmoothMethodWithWrongFirstParam {
    @SmoothFunction("my.package.MyFunction")
    public static void execute(Parameters wrong, Parameters params) {}
  }

  @Test
  public void wrongSecondParamInSmoothMethodException() throws Exception {
    assertExceptionThrown(MyPluginWithSmoothMethodWithWrongSecondParam.class,
        ParamsIsNotInterfaceException.class);
  }

  public static class MyPluginWithSmoothMethodWithWrongSecondParam {
    @SmoothFunction("my.package.MyFunction")
    public static void execute(Sandbox sandbox, Integer wrong) {}
  }

  private void assertExceptionThrown(Class<?> klass, Class<?> exception) {
    try {
      pluginFactory.create(klass);
      fail("exception should be thrown");
    } catch (Throwable e) {
      // expected
      assertThat(e).isInstanceOf(exception);
    }
  }
}
