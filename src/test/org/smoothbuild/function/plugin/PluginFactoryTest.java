package org.smoothbuild.function.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.function.base.Name.qualifiedName;
import static org.smoothbuild.function.base.Param.param;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.plugin.Path.path;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.mem.MemoryFileSystemModule;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.plugin.exc.ForbiddenParamTypeException;
import org.smoothbuild.function.plugin.exc.IllegalConstructorParamException;
import org.smoothbuild.function.plugin.exc.IllegalFunctionNameException;
import org.smoothbuild.function.plugin.exc.IllegalReturnTypeException;
import org.smoothbuild.function.plugin.exc.MissingConstructorException;
import org.smoothbuild.function.plugin.exc.MoreThanOneExecuteMethodException;
import org.smoothbuild.function.plugin.exc.NoExecuteMethodException;
import org.smoothbuild.function.plugin.exc.NonPublicExecuteMethodException;
import org.smoothbuild.function.plugin.exc.ParamMethodHasArgumentsException;
import org.smoothbuild.function.plugin.exc.ParamsIsNotInterfaceException;
import org.smoothbuild.function.plugin.exc.StaticExecuteMethodException;
import org.smoothbuild.function.plugin.exc.TooManyConstructorParamsException;
import org.smoothbuild.function.plugin.exc.TooManyConstructorsException;
import org.smoothbuild.function.plugin.exc.TooManyParamsInExecuteMethodException;
import org.smoothbuild.function.plugin.exc.ZeroParamsInExecuteMethodException;
import org.smoothbuild.plugin.ExecuteMethod;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileList;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.run.err.FunctionError;
import org.smoothbuild.task.PrecalculatedTask;
import org.smoothbuild.task.Task;
import org.smoothbuild.testing.problem.TestingProblemsListener;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;

public class PluginFactoryTest {
  TestingProblemsListener problems = new TestingProblemsListener();
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
    task.calculateResult(problems, tempDir);
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

    @ExecuteMethod("my.package.myFunction")
    public String execute(Parameters params) {
      return params.stringA() + params.stringB();
    }
  }

  @Test
  public void pluginWithFaultyConstructor() throws Exception {
    Function function = pluginFactory.create(MyPluginWithFaultyConstructor.class);
    function.generateTask(Empty.stringTaskMap()).calculateResult(problems, tempDir);
    problems.assertOnlyProblem(FunctionError.class);
  }

  public static class MyPluginWithFaultyConstructor {

    public MyPluginWithFaultyConstructor() {
      throw new RuntimeException();
    }

    @ExecuteMethod("my.package.myFunction")
    public String execute(Parameters params) {
      return null;
    }
  }

  @Test
  public void allowedParamTypesAreAccepted() throws Exception {
    pluginFactory.create(MyPluginWithAllowedParamTypes.class);
  }

  public interface AllowedParameters {
    public String string();

    public File file();

    public FileList fileList();
  }

  public static class MyPluginWithAllowedParamTypes {

    @ExecuteMethod("my.package.myFunction")
    public String execute(Parameters params) {
      return params.stringA() + params.stringB();
    }
  }

  @Test
  public void pluginWithForbiddenParamType() throws Exception {
    try {
      pluginFactory.create(MyPluginWithForbiddenParamType.class);
      fail("exception shoulde be thrown");
    } catch (ForbiddenParamTypeException e) {
      // expected
    }
  }

  public interface ForbiddenParams {
    public Runnable runnable();
  }

  public static class MyPluginWithForbiddenParamType {

    @ExecuteMethod("my.package.myFunction")
    public String execute(ForbiddenParams params) {
      return null;
    }
  }

  @Test
  public void emptyParamtersAreAccepted() throws Exception {
    pluginFactory.create(MyPluginWithEmptyParameters.class);
  }

  public interface EmptyParameters {}

  public static class MyPluginWithEmptyParameters {
    @ExecuteMethod("my.package.myFunction")
    public String execute(EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void stringResultTypeIsAccepted() throws Exception {
    pluginFactory.create(MyPluginWithStringResult.class);
  }

  public static class MyPluginWithStringResult {

    @ExecuteMethod("my.package.myFunction")
    public String execute(EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void fileResultTypeIsAccepted() throws Exception {
    pluginFactory.create(MyPluginWithFileResult.class);
  }

  public static class MyPluginWithFileResult {

    @ExecuteMethod("my.package.myFunction")
    public File execute(EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void filesResultTypeIsAccepted() throws Exception {
    pluginFactory.create(MyPluginWithFilesResult.class);
  }

  public static class MyPluginWithFilesResult {

    @ExecuteMethod("my.package.myFunction")
    public FileList execute(EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void voidResultTypeIsAccepted() throws Exception {
    pluginFactory.create(MyPluginWithVoidResult.class);
  }

  public static class MyPluginWithVoidResult {

    @ExecuteMethod("my.package.myFunction")
    public void execute(EmptyParameters params) {}
  }

  @Test
  public void illegalReturnTypeException() throws Exception {
    try {
      pluginFactory.create(MyPluginWithIllegalReturnType.class);
      fail("exception should be thrown");
    } catch (IllegalReturnTypeException e) {
      // expected
    }
  }

  public static class MyPluginWithIllegalReturnType {

    @ExecuteMethod("my.package.MyFunction")
    public Runnable execute(EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void paramsIsNotInterfaceException() throws Exception {
    try {
      pluginFactory.create(MyPluginWithParamThatIsNotInterface.class);
      fail("exception should be thrown");
    } catch (ParamsIsNotInterfaceException e) {
      // expected
    }
  }

  public static class MyPluginWithParamThatIsNotInterface {

    @ExecuteMethod("my.package.myFunction")
    public void execute(String string) {}
  }

  @Test
  public void illegalConstructorParamException() throws Exception {
    try {
      pluginFactory.create(MyPluginWithIllegalConstructorParam.class);
      fail("exception should be thrown");
    } catch (IllegalConstructorParamException e) {
      // expected
    }
  }

  public static class MyPluginWithIllegalConstructorParam {
    public MyPluginWithIllegalConstructorParam(String illegal) {}

    @ExecuteMethod("my.package.myFunction")
    public void execute(EmptyParameters param) {}
  }

  @Test
  public void illegalFunctionNameException() throws Exception {
    try {
      pluginFactory.create(MyPluginWithIllegalFunctionName.class);
      fail("exception should be thrown");
    } catch (IllegalFunctionNameException e) {
      // expected
    }
  }

  public static class MyPluginWithIllegalFunctionName {
    @ExecuteMethod("my..package")
    public void execute(EmptyParameters params) {}
  }

  @Test
  public void invokingMethodFailedException() throws Exception {
    Function function = pluginFactory.create(MyPluginWithThrowingExecuteMethod.class);
    function.generateTask(Empty.stringTaskMap()).calculateResult(problems, tempDir);
    problems.assertOnlyProblem(FunctionError.class);
  }

  public static class MyPluginWithThrowingExecuteMethod {

    @ExecuteMethod("my.package.MyFunction")
    public void execute(EmptyParameters params) {
      throw new RuntimeException();
    }
  }

  @Test
  public void missingConstructorException() throws Exception {
    try {
      pluginFactory.create(MyPluginWithPrivateConstructor.class);
      fail("exception should be thrown");
    } catch (MissingConstructorException e) {
      // expected
    }
  }

  public static class MyPluginWithPrivateConstructor {
    private MyPluginWithPrivateConstructor() {}

    @ExecuteMethod("my.package.MyFunction")
    public void execute(EmptyParameters params) {}
  }

  @Test
  public void moreThanOneExecuteMethodException() throws Exception {
    try {
      pluginFactory.create(MyPluginWithTwoExecuteMethods.class);
      fail("exception should be thrown");
    } catch (MoreThanOneExecuteMethodException e) {
      // expected
    }
  }

  public static class MyPluginWithTwoExecuteMethods {
    @ExecuteMethod("my.package.MyFunction")
    public void execute(EmptyParameters params) {}

    @ExecuteMethod("my.package.MyFunction2")
    public void execute2(EmptyParameters params) {}
  }

  @Test
  public void noExecuteMethodException() throws Exception {
    try {
      pluginFactory.create(MyPluginWithZeroExecuteMethods.class);
      fail("exception should be thrown");
    } catch (NoExecuteMethodException e) {
      // expected
    }
  }

  public static class MyPluginWithZeroExecuteMethods {}

  @Test
  public void nonPublicExecuteMethodException() throws Exception {
    try {
      pluginFactory.create(MyPluginWithPrivateExecuteMethod.class);
      fail("exception should be thrown");
    } catch (NonPublicExecuteMethodException e) {
      // expected
    }
  }

  public static class MyPluginWithPrivateExecuteMethod {

    @ExecuteMethod("my.package.MyFunction")
    private void execute(EmptyParameters params) {}
  }

  @Test
  public void paramMethodHasArgumentsException() throws Exception {
    try {
      pluginFactory.create(MyPluginWithParamMethodThatHasParameters.class);
      fail("exception should be thrown");
    } catch (ParamMethodHasArgumentsException e) {
      // expected
    }
  }

  public interface ParametersWithMethodWithParameters {
    public String string(String notAllowed);
  }

  public static class MyPluginWithParamMethodThatHasParameters {

    @ExecuteMethod("my.package.MyFunction")
    public void execute(ParametersWithMethodWithParameters params) {}
  }

  @Test
  public void staticExecuteMethodException() throws Exception {
    try {
      pluginFactory.create(MyPluginWithStaticExecuteMethod.class);
      fail("exception should be thrown");
    } catch (StaticExecuteMethodException e) {
      // expected
    }
  }

  public static class MyPluginWithStaticExecuteMethod {
    @ExecuteMethod("my.package.MyFunction")
    public static void execute(EmptyParameters params) {}
  }

  @Test
  public void tooManyConstructorParamsException() throws Exception {
    try {
      pluginFactory.create(MyPluginWithConstructorWithTooManyParams.class);
      fail("exception should be thrown");
    } catch (TooManyConstructorParamsException e) {
      // expected
    }
  }

  public static class MyPluginWithConstructorWithTooManyParams {
    public MyPluginWithConstructorWithTooManyParams(FileSystem fileSystem, FileSystem fileSystem2) {}

    @ExecuteMethod("my.package.MyFunction")
    public void execute(EmptyParameters params) {}
  }

  @Test
  public void tooManyConstructorsException() throws Exception {
    try {
      pluginFactory.create(MyPluginWithTwoConstructors.class);
      fail("exception should be thrown");
    } catch (TooManyConstructorsException e) {
      // expected
    }
  }

  public static class MyPluginWithTwoConstructors {
    public MyPluginWithTwoConstructors() {}

    public MyPluginWithTwoConstructors(FileSystem fileSystem) {}

    @ExecuteMethod("my.package.MyFunction")
    public void execute(EmptyParameters params) {}
  }

  @Test
  public void tooManyParamsInExecuteMethodException() throws Exception {
    try {
      pluginFactory.create(MyPluginWithExecuteMethodWithTwoParams.class);
      fail("exception should be thrown");
    } catch (TooManyParamsInExecuteMethodException e) {
      // expected
    }
  }

  public static class MyPluginWithExecuteMethodWithTwoParams {
    @ExecuteMethod("my.package.MyFunction")
    public void execute(EmptyParameters params, EmptyParameters params2) {}
  }

  @Test
  public void zeroParamsInExecuteMethodException() throws Exception {
    try {
      pluginFactory.create(MyPluginWithExecuteMethodWithZeroParams.class);
      fail("exception should be thrown");
    } catch (ZeroParamsInExecuteMethodException e) {
      // expected
    }
  }

  public static class MyPluginWithExecuteMethodWithZeroParams {
    @ExecuteMethod("my.package.MyFunction")
    public void execute() {}
  }
}
