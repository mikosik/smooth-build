package org.smoothbuild.lang.function.nativ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.testing.lang.function.base.ParamTester.params;
import static org.testory.Testory.mock;

import org.junit.Test;
import org.smoothbuild.io.cache.task.TaskDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.base.Signature;
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
import org.smoothbuild.lang.plugin.PluginApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.err.UnexpectedError;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.lang.type.FakeString;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.base.FakeResult;
import org.smoothbuild.testing.task.exec.FakePluginApi;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;

public class NativeFunctionFactoryTest {
  TaskGenerator taskGenerator = mock(TaskGenerator.class);
  FakePluginApi pluginApi = new FakePluginApi();
  Path tempDir = path("tem/dir");
  TaskDb taskDb = mock(TaskDb.class);
  CodeLocation codeLocation = new FakeCodeLocation();

  // signature

  @Test
  public void testSignature() throws Exception {
    Function function = NativeFunctionFactory.create(Func.class, false);

    assertThat(function.name()).isEqualTo(name("myFunction"));
    Signature signature = function.signature();
    assertThat(signature.name()).isEqualTo(name("myFunction"));
    assertThat(signature.type()).isEqualTo(STRING);

    Param paramA = param(STRING, "stringA");
    Param paramB = param(STRING, "stringB");

    assertThat(signature.params()).isEqualTo(params(paramA, paramB));
  }

  // invokation

  @Test
  public void testInvokation() throws Exception {
    Function function = NativeFunctionFactory.create(Func.class, false);
    Result result1 = new FakeResult(new FakeString("abc"));
    Result result2 = new FakeResult(new FakeString("def"));
    ImmutableMap<String, Result> dependencies =
        ImmutableMap.<String, Result> of("stringA", result1, "stringB", result2);

    Task task = function.generateTask(taskGenerator, dependencies, codeLocation);
    SString result = (SString) task.execute(pluginApi);
    pluginApi.loggedMessages().assertNoProblems();
    assertThat(result.value()).isEqualTo("abcdef");
  }

  public interface Parameters {
    public SString stringA();

    public SString stringB();
  }

  public static class Func {
    @SmoothFunction(name = "myFunction")
    public static SString execute(PluginApi pluginApi, Parameters params) {
      return new FakeString(params.stringA().value() + params.stringB().value());
    }
  }

  // allowed_param_types_are_accepted

  @Test
  public void allowed_param_types_are_accepted() throws Exception {
    NativeFunctionFactory.create(FuncWithAllowedParamTypes.class, false);
  }

  public interface AllowedParameters {
    public SString string();

    public SArray<SString> stringArray();

    public SFile file();

    public SArray<SFile> fileArray();

    public SBlob blob();

    public SArray<SBlob> blobArray();
  }

  public static class FuncWithAllowedParamTypes {
    @SmoothFunction(name = "myFunction")
    public static SString execute(PluginApi pluginApi, AllowedParameters params) {
      return new FakeString("string");
    }
  }

  // params_annotated_as_required_are_required

  @Test
  public void params_annotated_as_required_are_required() throws Exception {
    Function f = NativeFunctionFactory.create(FuncWithAnnotatedParams.class, false);
    ImmutableMap<String, Param> params = f.params();
    assertThat(params.get("string1").isRequired()).isTrue();
    assertThat(params.get("string2").isRequired()).isFalse();
  }

  public interface AnnotatedParameters {
    @Required
    public SString string1();

    public SString string2();
  }

  public static class FuncWithAnnotatedParams {
    @SmoothFunction(name = "myFunction")
    public static SString execute(PluginApi pluginApi, AnnotatedParameters params) {
      return null;
    }
  }

  // array_of_array_is_forbidden_as_param_type

  @Test
  public void array_of_array_is_forbidden_as_param_type() throws Exception {
    assertExceptionThrown(FuncWithArrayOfArrayParamType.class, ForbiddenParamTypeException.class);
  }

  public interface ArrayOfArrayParams {
    public SArray<SArray<SString>> runnable();
  }

  public static class FuncWithArrayOfArrayParamType {
    @SmoothFunction(name = "myFunction")
    public static SString execute(PluginApi pluginApi, ArrayOfArrayParams params) {
      return null;
    }
  }

  // non_smooth_types_are_forbidden_as_param_types

  @Test
  public void non_smooth_types_are_forbidden_as_param_types() throws Exception {
    assertExceptionThrown(FuncWithForbiddenParamType.class, ForbiddenParamTypeException.class);
  }

  public interface ForbiddenParams {
    public Runnable runnable();
  }

  public static class FuncWithForbiddenParamType {
    @SmoothFunction(name = "myFunction")
    public static SString execute(PluginApi pluginApi, ForbiddenParams params) {
      return null;
    }
  }

  // empty_parameters_are_accepted

  @Test
  public void empty_parameters_are_accepted() throws Exception {
    NativeFunctionFactory.create(FuncWithEmptyParameters.class, false);
  }

  public interface EmptyParameters {}

  public static class FuncWithEmptyParameters {
    @SmoothFunction(name = "myFunction")
    public static SString execute(PluginApi pluginApi, EmptyParameters params) {
      return null;
    }
  }

  // string_result_type_is_accepted

  @Test
  public void string_result_type_is_accepted() throws Exception {
    NativeFunctionFactory.create(FuncWithStringResult.class, false);
  }

  public static class FuncWithStringResult {
    @SmoothFunction(name = "myFunction")
    public static SString execute(PluginApi pluginApi, EmptyParameters params) {
      return null;
    }
  }

  // blob_result_type_is_accepted

  @Test
  public void blob_result_type_is_accepted() throws Exception {
    NativeFunctionFactory.create(FuncWithBlobResult.class, false);
  }

  public static class FuncWithBlobResult {
    @SmoothFunction(name = "myFunction")
    public static SBlob execute(PluginApi pluginApi, EmptyParameters params) {
      return null;
    }
  }

  // file_result_type_is_accepted

  @Test
  public void file_result_type_is_accepted() throws Exception {
    NativeFunctionFactory.create(FuncWithFileResult.class, false);
  }

  public static class FuncWithFileResult {
    @SmoothFunction(name = "myFunction")
    public static SFile execute(PluginApi pluginApi, EmptyParameters params) {
      return null;
    }
  }

  // string_array_result_type_is_accepted

  @Test
  public void string_array_result_type_is_accepted() throws Exception {
    NativeFunctionFactory.create(FuncWithStringArrayResult.class, false);
  }

  public static class FuncWithStringArrayResult {
    @SmoothFunction(name = "myFunction")
    public static SArray<SString> execute(PluginApi pluginApi, EmptyParameters params) {
      return null;
    }
  }

  // blob_array_result_type_is_accepted

  @Test
  public void blob_array_result_type_is_accepted() throws Exception {
    NativeFunctionFactory.create(FuncWithBlobArrayResult.class, false);
  }

  public static class FuncWithBlobArrayResult {
    @SmoothFunction(name = "myFunction")
    public static SArray<SBlob> execute(PluginApi pluginApi, EmptyParameters params) {
      return null;
    }
  }

  // file_array_result_type_is_accepted

  @Test
  public void file_array_result_type_is_accepted() throws Exception {
    NativeFunctionFactory.create(FuncWithFileArrayResult.class, false);
  }

  public static class FuncWithFileArrayResult {
    @SmoothFunction(name = "myFunction")
    public static SArray<SFile> execute(PluginApi pluginApi, EmptyParameters params) {
      return null;
    }
  }

  // non_smooth_type_is_not_allowed_as_return_type

  @Test
  public void non_smooth_type_is_not_allowed_as_return_type() throws Exception {
    assertExceptionThrown(FuncWithIllegalReturnType.class, IllegalReturnTypeException.class);
  }

  public static class FuncWithIllegalReturnType {
    @SmoothFunction(name = "myFunction")
    public static Runnable execute(PluginApi pluginApi, EmptyParameters params) {
      return null;
    }
  }

  // array_of_array_result_type_is_not_allowed

  @Test
  public void array_of_array_result_type_is_not_allowed() throws Exception {
    assertExceptionThrown(FuncWithArrayOfArrayReturnType.class, IllegalReturnTypeException.class);
  }

  public static class FuncWithArrayOfArrayReturnType {
    @SmoothFunction(name = "myFunction")
    public static SArray<SArray<SFile>> execute(PluginApi pluginApi, EmptyParameters params) {
      return null;
    }
  }

  // non_interface_is_not_allowed_as_params_interface

  @Test
  public void non_interface_is_not_allowed_as_params_interface() throws Exception {
    assertExceptionThrown(FuncWithParamThatIsNotInterface.class,
        ParamsIsNotInterfaceException.class);
  }

  public static class FuncWithParamThatIsNotInterface {
    @SmoothFunction(name = "myFunction")
    public static SString execute(PluginApi pluginApi, String string) {
      return null;
    }
  }

  // illegal_smooth_function_names_are_not_allowed

  @Test
  public void illegal_smooth_function_names_are_not_allowed() throws Exception {
    assertExceptionThrown(FuncWithIllegalFunctionName.class, IllegalFunctionNameException.class);
  }

  public static class FuncWithIllegalFunctionName {
    @SmoothFunction(name = "my^package")
    public static SString execute(PluginApi pluginApi, EmptyParameters params) {
      return null;
    }
  }

  // runtime_exception_thrown_from_native_function_is_logged

  @Test
  public void runtime_exception_thrown_from_native_function_is_logged() throws Exception {
    Function function = NativeFunctionFactory.create(FuncWithThrowingSmoothMethod.class, false);
    function.generateTask(taskGenerator, Empty.stringTaskResultMap(), codeLocation).execute(
        pluginApi);
    pluginApi.loggedMessages().assertContainsOnly(UnexpectedError.class);
  }

  public static class FuncWithThrowingSmoothMethod {
    @SmoothFunction(name = "myFunction")
    public static SString execute(PluginApi pluginApi, EmptyParameters params) {
      throw new RuntimeException();
    }
  }

  // only_one_smooth_function_pre_class_is_allowed

  @Test
  public void only_one_smooth_function_per_class_is_allowed() throws Exception {
    assertExceptionThrown(FuncWithTwoSmoothMethods.class, MoreThanOneSmoothFunctionException.class);
  }

  public static class FuncWithTwoSmoothMethods {
    @SmoothFunction(name = "myFunction")
    public static void execute(PluginApi pluginApi, EmptyParameters params) {}

    @SmoothFunction(name = "myFunction2")
    public static void execute2(PluginApi pluginApi, EmptyParameters params) {}
  }

  @Test
  public void zero_smooth_method_per_class_is_forbidden() throws Exception {
    assertExceptionThrown(FuncWithZeroSmoothMethods.class, NoSmoothFunctionException.class);
  }

  public static class FuncWithZeroSmoothMethods {}

  // non_public_smooth_method_is_not_allowed

  @Test
  public void non_public_smooth_method_is_not_allowed() throws Exception {
    assertExceptionThrown(FuncWithPrivateSmoothMethod.class, NonPublicSmoothFunctionException.class);
  }

  public static class FuncWithPrivateSmoothMethod {
    @SmoothFunction(name = "myFunction")
    private static void execute(PluginApi pluginApi, EmptyParameters params) {}
  }

  // method_in_params_interface_cannot_have_parameters

  @Test
  public void method_in_params_interface_cannot_have_parameters() throws Exception {
    assertExceptionThrown(FuncWithParamMethodThatHasParameters.class,
        ParamMethodHasArgumentsException.class);
  }

  public interface ParametersWithMethodWithParameters {
    public String string(String notAllowed);
  }

  public static class FuncWithParamMethodThatHasParameters {
    @SmoothFunction(name = "myFunction")
    public static SString execute(PluginApi pluginApi, ParametersWithMethodWithParameters params) {
      return null;
    }
  }

  // native_smooth_method_cannot_be_static

  @Test
  public void native_smooth_method_cannot_be_static() throws Exception {
    assertExceptionThrown(FuncWithNonStaticSmoothMethod.class,
        NonStaticSmoothFunctionException.class);
  }

  public static class FuncWithNonStaticSmoothMethod {
    @SmoothFunction(name = "myFunction")
    public void execute(PluginApi pluginApi, EmptyParameters params) {}
  }

  // native_smooth_method_cannot_have_zero_parameters

  @Test
  public void native_smooth_method_cannot_have_zero_parameters() throws Exception {
    assertExceptionThrown(FuncWithSmoothMethodWithZeroParams.class,
        WrongParamsInSmoothFunctionException.class);
  }

  public static class FuncWithSmoothMethodWithZeroParams {
    @SmoothFunction(name = "myFunction")
    public static void execute() {}
  }

  // native_smooth_method_cannot_have_one_parameter

  @Test
  public void native_smooth_method_cannot_have_one_parameter() throws Exception {
    assertExceptionThrown(FuncWithSmoothMethodWithOneParam.class,
        WrongParamsInSmoothFunctionException.class);
  }

  public static class FuncWithSmoothMethodWithOneParam {
    @SmoothFunction(name = "myFunction")
    public static void execute() {}
  }

  // wrong_first_parameter_in_native_smooth_function

  @Test
  public void wrong_first_parameter_in_native_smooth_function() throws Exception {
    assertExceptionThrown(FuncWithSmoothMethodWithWrongFirstParam.class,
        WrongParamsInSmoothFunctionException.class);
  }

  public static class FuncWithSmoothMethodWithWrongFirstParam {
    @SmoothFunction(name = "myFunction")
    public static void execute(Parameters wrong, Parameters params) {}
  }

  // wrong_second_parameter_in_native_smooth_function

  @Test
  public void wrong_second_parameter_in_native_smooth_function() throws Exception {
    assertExceptionThrown(FuncWithSmoothMethodWithWrongSecondParam.class,
        ParamsIsNotInterfaceException.class);
  }

  public static class FuncWithSmoothMethodWithWrongSecondParam {
    @SmoothFunction(name = "myFunction")
    public static SString execute(PluginApi pluginApi, Integer wrong) {
      return null;
    }
  }

  // helpers

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
