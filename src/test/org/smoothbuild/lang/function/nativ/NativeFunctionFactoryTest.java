package org.smoothbuild.lang.function.nativ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;

import org.junit.Test;
import org.smoothbuild.db.taskoutputs.TaskOutput;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.expr.ConstantExpr;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.base.Params;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.err.ForbiddenParamTypeException;
import org.smoothbuild.lang.function.nativ.err.IllegalFunctionNameException;
import org.smoothbuild.lang.function.nativ.err.IllegalReturnTypeException;
import org.smoothbuild.lang.function.nativ.err.MoreThanOneSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.err.NoSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.err.NonPublicSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.err.NonStaticSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.err.ParamMethodHasArgumentsException;
import org.smoothbuild.lang.function.nativ.err.ParamsIsNotInterfaceException;
import org.smoothbuild.lang.function.nativ.err.WrongParamsInSmoothFunctionException;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.message.base.Messages;
import org.smoothbuild.task.work.TaskWorker;
import org.smoothbuild.task.work.err.UnexpectedError;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.task.exec.FakeNativeApi;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class NativeFunctionFactoryTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private final FakeNativeApi nativeApi = new FakeNativeApi();

  // signature

  @Test
  public void testSignature() throws Exception {
    Function<?> function = NativeFunctionFactory.create(Func.class, false);

    assertThat(function.name()).isEqualTo(name("myFunction"));
    Signature<?> signature = function.signature();
    assertThat(signature.name()).isEqualTo(name("myFunction"));
    assertThat(signature.type()).isEqualTo(STRING);

    Param paramA = param(STRING, "stringA");
    Param paramB = param(STRING, "stringB");

    assertThat(signature.params()).isEqualTo(Params.map(paramA, paramB));
  }

  // invokation

  @Test
  public void testInvokation() throws Exception {
    @SuppressWarnings("unchecked")
    Function<SString> function =
        (Function<SString>) NativeFunctionFactory.create(Func.class, false);
    SString string1 = objectsDb.string("abc");
    SString string2 = objectsDb.string("def");
    ConstantExpr<?> arg1 = new ConstantExpr<>(STRING, string1, codeLocation(1));
    ConstantExpr<?> arg2 = new ConstantExpr<>(STRING, string2, codeLocation(1));

    ImmutableMap<String, ConstantExpr<?>> args = ImmutableMap.of("stringA", arg1, "stringB", arg2);
    TaskWorker<SString> task = function.createWorker(args, codeLocation(1));
    TaskOutput<SString> output = task.execute(ImmutableList.of(string1, string2), nativeApi);
    assertThat(Messages.containsProblems(output.messages())).isFalse();
    assertThat(output.returnValue().value()).isEqualTo("abcdef");
  }

  public interface Parameters {
    public SString stringA();

    public SString stringB();
  }

  public static class Func {
    @SmoothFunction(name = "myFunction")
    public static SString execute(NativeApi nativeApi, Parameters params) {
      return new FakeObjectsDb().string(params.stringA().value() + params.stringB().value());
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
    public static SString execute(NativeApi nativeApi, AllowedParameters params) {
      return new FakeObjectsDb().string("string");
    }
  }

  // params_annotated_as_required_are_required

  @Test
  public void params_annotated_as_required_are_required() throws Exception {
    Function<?> f = NativeFunctionFactory.create(FuncWithAnnotatedParams.class, false);
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
    public static SString execute(NativeApi nativeApi, AnnotatedParameters params) {
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
    public static SString execute(NativeApi nativeApi, ArrayOfArrayParams params) {
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
    public static SString execute(NativeApi nativeApi, ForbiddenParams params) {
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
    public static SString execute(NativeApi nativeApi, EmptyParameters params) {
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
    public static SString execute(NativeApi nativeApi, EmptyParameters params) {
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
    public static SBlob execute(NativeApi nativeApi, EmptyParameters params) {
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
    public static SFile execute(NativeApi nativeApi, EmptyParameters params) {
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
    public static SArray<SString> execute(NativeApi nativeApi, EmptyParameters params) {
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
    public static SArray<SBlob> execute(NativeApi nativeApi, EmptyParameters params) {
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
    public static SArray<SFile> execute(NativeApi nativeApi, EmptyParameters params) {
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
    public static Runnable execute(NativeApi nativeApi, EmptyParameters params) {
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
    public static SArray<SArray<SFile>> execute(NativeApi nativeApi, EmptyParameters params) {
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
    public static SString execute(NativeApi nativeApi, String string) {
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
    public static SString execute(NativeApi nativeApi, EmptyParameters params) {
      return null;
    }
  }

  // runtime_exception_thrown_from_native_function_is_logged

  @Test
  public void runtime_exception_thrown_from_native_function_is_logged() throws Exception {
    Function<?> function = NativeFunctionFactory.create(FuncWithThrowingSmoothMethod.class, false);
    function.createWorker(Empty.stringExprMap(), codeLocation(1)).execute(Empty.svalueList(),
        nativeApi);
    nativeApi.loggedMessages().assertContainsOnly(UnexpectedError.class);
  }

  public static class FuncWithThrowingSmoothMethod {
    @SmoothFunction(name = "myFunction")
    public static SString execute(NativeApi nativeApi, EmptyParameters params) {
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
    public static void execute(NativeApi nativeApi, EmptyParameters params) {}

    @SmoothFunction(name = "myFunction2")
    public static void execute2(NativeApi nativeApi, EmptyParameters params) {}
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
    private static void execute(NativeApi nativeApi, EmptyParameters params) {}
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
    public static SString execute(NativeApi nativeApi, ParametersWithMethodWithParameters params) {
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
    public void execute(NativeApi nativeApi, EmptyParameters params) {}
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
    public static SString execute(NativeApi nativeApi, Integer wrong) {
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
