package org.smoothbuild.lang.function.nativ;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.base.Types.BLOB;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.base.Types.STRING_ARRAY;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Parameter.optionalParameter;
import static org.smoothbuild.lang.function.nativ.NativeFunctionFactory.createNativeFunction;
import static org.smoothbuild.lang.function.nativ.NativeFunctionFactory.createNativeFunctions;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.lang.reflect.Method;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.function.nativ.err.IllegalFunctionNameException;
import org.smoothbuild.lang.function.nativ.err.IllegalParamTypeException;
import org.smoothbuild.lang.function.nativ.err.IllegalReturnTypeException;
import org.smoothbuild.lang.function.nativ.err.NativeImplementationException;
import org.smoothbuild.lang.function.nativ.err.NonPublicSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.err.NonStaticSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.err.ParamMethodHasArgumentsException;
import org.smoothbuild.lang.function.nativ.err.ParamsIsNotInterfaceException;
import org.smoothbuild.lang.function.nativ.err.WrongParamsInSmoothFunctionException;
import org.smoothbuild.lang.plugin.NotCacheable;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.util.Empty;
import org.testory.Closure;
import org.testory.common.Matcher;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;

public class NativeFunctionFactoryTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private NativeFunction function;
  private NativeFunction function2;
  private NativeFunction stringFunction;
  private HashCode hash1;
  private HashCode hash2;

  @Test
  public void function_is_created_for_each_annotated_java_method() throws Exception {
    when(createNativeFunctions(Hash.integer(33), ClassWithManyFunctions.class));
    thenReturned(new Matcher() {
      @Override
      public boolean matches(Object object) {
        List<NativeFunction> functions = (List<NativeFunction>) object;
        List<String> names = Lists.newArrayList();
        for (NativeFunction function : functions) {
          names.add(function.name().value());
        }
        return Matchers.containsInAnyOrder("aFunction", "bFunction").matches(names);
      }
    });
  }

  public static class ClassWithManyFunctions {
    @SmoothFunction
    public static SString aFunction(NativeApi nativeApi, EmptyParameters params) {
      return null;
    }

    @SmoothFunction
    public static SString bFunction(NativeApi nativeApi, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void no_function_is_created_for_not_annotated_java_method() throws Exception {
    when(createNativeFunctions(Hash.integer(33), ClassWithZeroFunctions.class));
    thenReturned(Matchers.empty());
  }

  public static class ClassWithZeroFunctions {
    public static SString execute(NativeApi nativeApi, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void function_name_is_equal_to_declared_via_annotation() throws Exception {
    given(function = function(NamedFunc.class.getMethods()[0]));
    when(function).name();
    thenReturned(name("myFunction"));
  }

  public static class NamedFunc {
    @SmoothFunction
    public static SString myFunction(NativeApi nativeApi, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void different_functions_have_different_hashes() throws Exception {
    given(function = function(FunctionHashes.class.getMethods()[0]));
    given(function2 = function(FunctionHashes.class.getMethods()[1]));
    when(function).hash();
    thenReturned(not(function2.hash()));
  }

  public static class FunctionHashes {
    @SmoothFunction
    public static SString myFunction(NativeApi nativeApi, EmptyParameters params) {
      return null;
    }

    @SmoothFunction
    public static SString myFunction2(NativeApi nativeApi, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void function_with_same_name_in_different_jars_have_different_hashes() throws Exception {
    given(hash1 = Hash.integer(33));
    given(hash2 = Hash.integer(34));
    given(function = createNativeFunction(hash1, FunctionHash.class.getMethods()[0]));
    given(function2 = createNativeFunction(hash2, FunctionHash.class.getMethods()[0]));
    when(function).hash();
    thenReturned(not(function2.hash()));
  }

  public static class FunctionHash {
    @SmoothFunction
    public static SString myFunction(NativeApi nativeApi, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void function_return_type_is_equal_to_java_method_return_type() throws Exception {
    given(function = function(FunctionReturningSString.class.getMethods()[0]));
    when(function).type();
    thenReturned(STRING);
  }

  public static class FunctionReturningSString {
    @SmoothFunction
    public static SString myFunction(NativeApi nativeApi, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void function_params_are_equal_to_params_of_java_method() throws Exception {
    given(function = function(FunctionWithDifferentParams.class.getMethods()[0]));
    when(function).parameters();
    thenReturned(asList(optionalParameter(STRING_ARRAY, "array"), optionalParameter(STRING,
        "string")));
  }

  public interface DifferentParams {
    public SString string();

    public Array<SString> array();
  }

  public static class FunctionWithDifferentParams {
    @SmoothFunction
    public static SString myFunction(NativeApi nativeApi, DifferentParams params) {
      return null;
    }
  }

  @Test
  public void function_is_cacheable_when_cacheable_is_missing_from_java_method_annotation()
      throws Exception {
    given(function = function(NonCacheableFunction.class.getMethods()[0]));
    when(function).isCacheable();
    thenReturned(true);
  }

  public static class NonCacheableFunction {
    @SmoothFunction
    public static SString myFunction(NativeApi nativeApi, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void function_is_not_cacheable_when_java_method_annotation_is_annotated_as_not_cacheable()
      throws Exception {
    given(function = function(NotCacheableFunction.class.getMethods()[0]));
    when(function).isCacheable();
    thenReturned(false);
  }

  public static class NotCacheableFunction {
    @SmoothFunction
    @NotCacheable
    public static SString myFunction(NativeApi nativeApi, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void function_signature_is_determined_by_java_method() throws Exception {
    given(function = function(SignatureTestFunction.class.getMethods()[0]));

    when(function.type());
    thenReturned(BLOB);

    when(function.parameters());
    thenReturned(asList(optionalParameter(STRING, "string")));

    when(function.name());
    thenReturned(name("func"));
  }

  public interface SignatureTestParameters {
    public SString string();
  }

  public static class SignatureTestFunction {
    @SmoothFunction
    public static Blob func(NativeApi nativeApi, SignatureTestParameters params) {
      return null;
    }
  }

  @Test
  public void testInvokation() throws Exception {
    given(stringFunction = function(ConstantStringFunction.class.getMethods()[0]));
    when(stringFunction.invoke(null, Empty.stringValueMap()));
    thenReturned(objectsDb.string("constant string"));
  }

  public static class ConstantStringFunction {
    @SmoothFunction
    public static SString constantStringFunction(NativeApi nativeApi, EmptyParameters params) {
      return new FakeObjectsDb().string("constant string");
    }
  }

  @Test
  public void all_allowed_param_types_are_accepted() throws Exception {
    when(function(FuncWithAllowedParamTypes.class.getMethods()[0]));
    thenReturned();
  }

  public interface AllowedParameters {
    public SString string();

    public Array<SString> stringArray();

    public SFile file();

    public Array<SFile> fileArray();

    public Blob blob();

    public Array<Blob> blobArray();
  }

  public static class FuncWithAllowedParamTypes {
    @SmoothFunction
    public static SString myFunction(NativeApi nativeApi, AllowedParameters params) {
      return null;
    }
  }

  @Test
  public void param_annotated_as_required_is_required() throws Exception {
    given(function = function(FuncWithRequiredParam.class.getMethods()[0]));
    when(function.parameters().get(0).isRequired());
    thenReturned(true);
  }

  public interface RequiredParam {
    @Required
    public SString param();
  }

  public static class FuncWithRequiredParam {
    @SmoothFunction
    public static SString myFunction(NativeApi nativeApi, RequiredParam params) {
      return null;
    }
  }

  @Test
  public void param_not_annotated_as_required_is_not_required() throws Exception {
    given(function = function(FuncWithNotRequiredParam.class.getMethods()[0]));
    when(function.parameters().get(0).isRequired());
    thenReturned(false);
  }

  public interface NotRequiredParam {
    public SString param();
  }

  public static class FuncWithNotRequiredParam {
    @SmoothFunction
    public static SString myFunction(NativeApi nativeApi, NotRequiredParam params) {
      return null;
    }
  }

  @Test
  public void array_of_array_is_forbidden_as_param_type() throws Exception {
    when($createNativeFunction(FuncWithArrayOfArrayParamType.class.getDeclaredMethods()[0]));
    thenThrown(IllegalParamTypeException.class);
  }

  public interface ArrayOfArrayParams {
    public Array<Array<SString>> runnable();
  }

  public static class FuncWithArrayOfArrayParamType {
    @SmoothFunction
    public static SString myFunction(NativeApi nativeApi, ArrayOfArrayParams params) {
      return null;
    }
  }

  @Test
  public void non_smooth_types_are_forbidden_as_param_types() throws Exception {
    when($createNativeFunction(FuncWithForbiddenParamType.class.getDeclaredMethods()[0]));
    thenThrown(IllegalParamTypeException.class);
  }

  public interface ForbiddenParams {
    public Runnable runnable();
  }

  public static class FuncWithForbiddenParamType {
    @SmoothFunction
    public static SString myFunction(NativeApi nativeApi, ForbiddenParams params) {
      return null;
    }
  }

  @Test
  public void empty_parameters_are_accepted() throws Exception {
    when($createNativeFunction(FuncWithEmptyParameters.class.getMethods()[0]));
    thenReturned();
  }

  public interface EmptyParameters {}

  public static class FuncWithEmptyParameters {
    @SmoothFunction
    public static SString myFunction(NativeApi nativeApi, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void string_result_type_is_accepted() throws Exception {
    when($createNativeFunction(FuncWithStringResult.class.getMethods()[0]));
    thenReturned();
  }

  public static class FuncWithStringResult {
    @SmoothFunction
    public static SString myFunction(NativeApi nativeApi, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void blob_result_type_is_accepted() throws Exception {
    when($createNativeFunction(FuncWithBlobResult.class.getMethods()[0]));
    thenReturned();
  }

  public static class FuncWithBlobResult {
    @SmoothFunction
    public static Blob myFunction(NativeApi nativeApi, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void file_result_type_is_accepted() throws Exception {
    when($createNativeFunction(FuncWithFileResult.class.getMethods()[0]));
    thenReturned();
  }

  public static class FuncWithFileResult {
    @SmoothFunction
    public static SFile myFunction(NativeApi nativeApi, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void string_array_result_type_is_accepted() throws Exception {
    when($createNativeFunction(FuncWithStringArrayResult.class.getMethods()[0]));
    thenReturned();
  }

  public static class FuncWithStringArrayResult {
    @SmoothFunction
    public static Array<SString> myFunction(NativeApi nativeApi, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void blob_array_result_type_is_accepted() throws Exception {
    when($createNativeFunction(FuncWithBlobArrayResult.class.getMethods()[0]));
    thenReturned();
  }

  public static class FuncWithBlobArrayResult {
    @SmoothFunction
    public static Array<Blob> myFunction(NativeApi nativeApi, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void file_array_result_type_is_accepted() throws Exception {
    when($createNativeFunction(FuncWithFileArrayResult.class.getMethods()[0]));
    thenReturned();
  }

  public static class FuncWithFileArrayResult {
    @SmoothFunction
    public static Array<SFile> myFunction(NativeApi nativeApi, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void non_smooth_type_is_not_allowed_as_return_type() throws Exception {
    when($createNativeFunction(FuncWithIllegalReturnType.class.getDeclaredMethods()[0]));
    thenThrown(IllegalReturnTypeException.class);
  }

  public static class FuncWithIllegalReturnType {
    @SmoothFunction
    public static Runnable myFunction(NativeApi nativeApi, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void array_of_array_result_type_is_not_allowed() throws Exception {
    when($createNativeFunction(FuncWithArrayOfArrayReturnType.class.getDeclaredMethods()[0]));
    thenThrown(IllegalReturnTypeException.class);
  }

  public static class FuncWithArrayOfArrayReturnType {
    @SmoothFunction
    public static Array<Array<SFile>> myFunction(NativeApi nativeApi, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void non_interface_is_not_allowed_as_params_interface() throws Exception {
    when($createNativeFunction(FuncWithParamThatIsNotInterface.class.getDeclaredMethods()[0]));
    thenThrown(ParamsIsNotInterfaceException.class);
  }

  public static class FuncWithParamThatIsNotInterface {
    @SmoothFunction
    public static SString myFunction(NativeApi nativeApi, String string) {
      return null;
    }
  }

  @Test
  public void illegal_smooth_function_names_are_not_allowed() throws Exception {
    when($createNativeFunction(FuncWithIllegalFunctionName.class.getDeclaredMethods()[0]));
    thenThrown(IllegalFunctionNameException.class);
  }

  public static class FuncWithIllegalFunctionName {
    @SmoothFunction
    public static SString my$function(NativeApi nativeApi, EmptyParameters params) {
      return null;
    }
  }

  @Test
  public void non_public_smooth_method_is_not_allowed() throws Exception {
    when($createNativeFunction(FuncWithPrivateSmoothMethod.class.getDeclaredMethods()[0]));
    thenThrown(NonPublicSmoothFunctionException.class);
  }

  public static class FuncWithPrivateSmoothMethod {
    @SmoothFunction
    private static void myFunction(NativeApi nativeApi, EmptyParameters params) {}
  }

  @Test
  public void method_in_params_interface_cannot_have_parameters() throws Exception {
    when($createNativeFunction(FuncWithParamMethodThatHasParameters.class.getDeclaredMethods()[0]));
    thenThrown(ParamMethodHasArgumentsException.class);
  }

  public interface ParametersWithMethodWithParameters {
    public String string(String notAllowed);
  }

  public static class FuncWithParamMethodThatHasParameters {
    @SmoothFunction
    public static SString myFunction(NativeApi nativeApi, ParametersWithMethodWithParameters params) {
      return null;
    }
  }

  @Test
  public void native_smooth_method_cannot_be_static() throws Exception {
    when($createNativeFunction(FuncWithNonStaticSmoothMethod.class.getDeclaredMethods()[0]));
    thenThrown(NonStaticSmoothFunctionException.class);
  }

  public static class FuncWithNonStaticSmoothMethod {
    @SmoothFunction
    public void myFunction(NativeApi nativeApi, EmptyParameters params) {}
  }

  @Test
  public void native_smooth_method_cannot_have_zero_parameters() throws Exception {
    when($createNativeFunction(FuncWithSmoothMethodWithZeroParams.class.getDeclaredMethods()[0]));
    thenThrown(WrongParamsInSmoothFunctionException.class);
  }

  public static class FuncWithSmoothMethodWithZeroParams {
    @SmoothFunction
    public static void myFunction() {}
  }

  @Test
  public void native_smooth_method_cannot_have_one_parameter() throws Exception {
    when($createNativeFunction(FuncWithSmoothMethodWithOneParam.class.getDeclaredMethods()[0]));
    thenThrown(WrongParamsInSmoothFunctionException.class);
  }

  public static class FuncWithSmoothMethodWithOneParam {
    @SmoothFunction
    public static void myFunction() {}
  }

  @Test
  public void wrong_first_parameter_in_native_smooth_function() throws Exception {
    when($createNativeFunction(FuncWithSmoothMethodWithWrongFirstParam.class.getDeclaredMethods()[0]));
    thenThrown(WrongParamsInSmoothFunctionException.class);
  }

  public static class FuncWithSmoothMethodWithWrongFirstParam {
    @SmoothFunction
    public static void myFunction(EmptyParameters wrong, EmptyParameters params) {}
  }

  @Test
  public void wrong_second_parameter_in_native_smooth_function() throws Exception {
    when($createNativeFunction(FuncWithSmoothMethodWithWrongSecondParam.class.getDeclaredMethods()[0]));
    thenThrown(ParamsIsNotInterfaceException.class);
  }

  public static class FuncWithSmoothMethodWithWrongSecondParam {
    @SmoothFunction
    public static SString myFunction(NativeApi nativeApi, Integer wrong) {
      return null;
    }
  }

  // helpers

  private static Closure $createNativeFunction(final Method method) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return function(method);
      }
    };
  }

  private static NativeFunction function(Method method) throws NativeImplementationException {
    return NativeFunctionFactory.createNativeFunction(Hash.integer(33), method);
  }
}
