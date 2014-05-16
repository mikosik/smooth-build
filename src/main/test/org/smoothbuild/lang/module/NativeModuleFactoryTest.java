package org.smoothbuild.lang.module;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.util.Classes.binaryPath;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Params;
import org.smoothbuild.lang.function.nativ.err.ForbiddenParamTypeException;
import org.smoothbuild.lang.function.nativ.err.IllegalFunctionNameException;
import org.smoothbuild.lang.function.nativ.err.IllegalReturnTypeException;
import org.smoothbuild.lang.function.nativ.err.NonPublicSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.err.NonStaticSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.err.ParamMethodHasArgumentsException;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

import com.google.common.io.ByteStreams;

public class NativeModuleFactoryTest {
  private Module module;
  private Function<?> function;

  @Test
  public void module_available_names_contains_smooth_function_names() throws Exception {
    given(module = createNativeModule(ModuleWithOneFunction.class));
    when(module.availableNames());
    thenReturned(contains(name("func")));
  }

  public static class ModuleWithOneFunction {
    public interface Parameters {}

    @SmoothFunction(name = "func")
    public static SString execute(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test
  public void module_with_more_than_one_function_is_allowed() throws Exception {
    given(module = createNativeModule(ModuleWithTwoFunctions.class));
    when(module.availableNames());
    thenReturned(contains(name("func1"), name("func2")));
  }

  public static class ModuleWithTwoFunctions {
    public interface Parameters {}

    @SmoothFunction(name = "func1")
    public static SString execute1(NativeApi nativeApi, Parameters params) {
      return null;
    }

    @SmoothFunction(name = "func2")
    public static SString execute2(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void two_functions_with_same_name_are_forbidden() throws Exception {
    createNativeModule(ModuleWithTwoFunctionsWithSameName.class);
  }

  public static class ModuleWithTwoFunctionsWithSameName {
    public interface Parameters {}

    @SmoothFunction(name = "func")
    public static SString execute1(NativeApi nativeApi, Parameters params) {
      return null;
    }

    @SmoothFunction(name = "func")
    public static SString execute2(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test
  public void function_type_equals_java_function_type() throws Exception {
    given(module = createNativeModule(ModuleWithSStringFunction.class));
    when(module.getFunction(name("func")).type());
    thenReturned(STRING);
  }

  public static class ModuleWithSStringFunction {
    public interface Parameters {}

    @SmoothFunction(name = "func")
    public static SString execute(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test
  public void function_signature_contains_all_params() throws Exception {
    given(module = createNativeModule(ModuleWithTwoParamFunction.class));
    when(module.getFunction(name("func")).params());
    thenReturned(Params.map(param(STRING, "param1"), param(STRING, "param2")));
  }

  public static class ModuleWithTwoParamFunction {
    public interface Parameters {
      SString param1();

      SString param2();
    }

    @SmoothFunction(name = "func")
    public static SString execute(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test
  public void allowed_param_types_are_accepted() throws Exception {
    given(module = createNativeModule(ModuleWithFunctionWithParametersOfAllTypes.class));
    when(module.getFunction(name("func")));
    thenReturned();
  }

  public static class ModuleWithFunctionWithParametersOfAllTypes {
    public interface Parameters {
      public SString string();

      public SArray<SString> stringArray();

      public SFile file();

      public SArray<SFile> fileArray();

      public SBlob blob();

      public SArray<SBlob> blobArray();
    }

    @SmoothFunction(name = "func")
    public static SString execute(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test
  public void params_annotated_as_required_are_required() throws Exception {
    given(module = createNativeModule(ModuleWithFunctionWithRequiredParam.class));
    given(function = module.getFunction(name("func")));
    when(function.params().get("param").isRequired());
    thenReturned(true);
  }

  public static class ModuleWithFunctionWithRequiredParam {
    public interface Parameters {
      @Required
      public SString param();
    }

    @SmoothFunction(name = "func")
    public static SString execute(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test
  public void params_not_annotated_as_required_are_not_required() throws Exception {
    given(module = createNativeModule(ModuleWithFunctionWithNotRequiredParam.class));
    given(function = module.getFunction(name("func")));
    when(function.params().get("param").isRequired());
    thenReturned(false);
  }

  public static class ModuleWithFunctionWithNotRequiredParam {
    public interface Parameters {
      public SString param();
    }

    @SmoothFunction(name = "func")
    public static SString execute(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test(expected = ForbiddenParamTypeException.class)
  public void array_of_array_is_forbidden_as_param_type() throws Exception {
    module = createNativeModule(ModuleWithFunctionWithArrayOfArraysParameter.class);
  }

  public static class ModuleWithFunctionWithArrayOfArraysParameter {
    public interface Parameters {
      public SArray<SArray<SString>> param();
    }

    @SmoothFunction(name = "func")
    public static SString execute(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test(expected = ForbiddenParamTypeException.class)
  public void non_smooth_type_is_forbidden_as_param_type() throws Exception {
    module = createNativeModule(ModuleWithFunctionWithNonSmoothParameter.class);
  }

  public static class ModuleWithFunctionWithNonSmoothParameter {
    public interface Parameters {
      public String param();
    }

    @SmoothFunction(name = "func")
    public static SString execute(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test
  public void function_without_parameters_is_allowed() throws Exception {
    when(createNativeModule(ModuleWithFunctionWithNoParameter.class));
    thenReturned();
  }

  public static class ModuleWithFunctionWithNoParameter {
    public interface Parameters {}

    @SmoothFunction(name = "func")
    public static SString execute(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test
  public void function_with_string_result_type_is_allowed() throws Exception {
    given(module = createNativeModule(ModuleWithFunctionReturningString.class));
    given(function = module.getFunction(name("func")));
    when(function).type();
    thenReturned(STRING);
  }

  public static class ModuleWithFunctionReturningString {
    public interface Parameters {}

    @SmoothFunction(name = "func")
    public static SString execute(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test
  public void function_with_blob_result_type_is_allowed() throws Exception {
    given(module = createNativeModule(ModuleWithFunctionReturningBlob.class));
    given(function = module.getFunction(name("func")));
    when(function).type();
    thenReturned(BLOB);
  }

  public static class ModuleWithFunctionReturningBlob {
    public interface Parameters {}

    @SmoothFunction(name = "func")
    public static SBlob execute(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test
  public void function_with_file_result_type_is_allowed() throws Exception {
    given(module = createNativeModule(ModuleWithFunctionReturningFile.class));
    given(function = module.getFunction(name("func")));
    when(function).type();
    thenReturned(FILE);
  }

  public static class ModuleWithFunctionReturningFile {
    public interface Parameters {}

    @SmoothFunction(name = "func")
    public static SFile execute(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test
  public void function_with_string_aray_result_type_is_allowed() throws Exception {
    given(module = createNativeModule(ModuleWithFunctionReturningStringArray.class));
    given(function = module.getFunction(name("func")));
    when(function).type();
    thenReturned(STRING_ARRAY);
  }

  public static class ModuleWithFunctionReturningStringArray {
    public interface Parameters {}

    @SmoothFunction(name = "func")
    public static SArray<SString> execute(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test
  public void function_with_blob_aray_result_type_is_allowed() throws Exception {
    given(module = createNativeModule(ModuleWithFunctionReturningBlobArray.class));
    given(function = module.getFunction(name("func")));
    when(function).type();
    thenReturned(BLOB_ARRAY);
  }

  public static class ModuleWithFunctionReturningBlobArray {
    public interface Parameters {}

    @SmoothFunction(name = "func")
    public static SArray<SBlob> execute(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test
  public void function_with_file_aray_result_type_is_allowed() throws Exception {
    given(module = createNativeModule(ModuleWithFunctionReturningFileArray.class));
    given(function = module.getFunction(name("func")));
    when(function).type();
    thenReturned(FILE_ARRAY);
  }

  public static class ModuleWithFunctionReturningFileArray {
    public interface Parameters {}

    @SmoothFunction(name = "func")
    public static SArray<SFile> execute(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test(expected = IllegalReturnTypeException.class)
  public void array_of_arrays_is_not_allowed_for_return_type() throws Exception {
    createNativeModule(ModuleWithFunctionReturningArrayOfArrays.class);
  }

  public static class ModuleWithFunctionReturningArrayOfArrays {
    public interface Parameters {}

    @SmoothFunction(name = "func")
    public static SArray<SArray<SString>> execute(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test(expected = IllegalReturnTypeException.class)
  public void non_smooth_type_is_not_allowed_for_return_type() throws Exception {
    createNativeModule(ModuleWithFunctionReturningNonSmoothType.class);
  }

  public static class ModuleWithFunctionReturningNonSmoothType {
    public interface Parameters {}

    @SmoothFunction(name = "func")
    public static String execute(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test(expected = IllegalFunctionNameException.class)
  public void function_with_illegal_smooth_names_are_not_allowed() throws Exception {
    createNativeModule(ModuleWithFunctionWithIllegalName.class);
  }

  public static class ModuleWithFunctionWithIllegalName {
    public interface Parameters {}

    @SmoothFunction(name = "my^package")
    public static SString execute(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test(expected = NonPublicSmoothFunctionException.class)
  public void non_public_functions_are_forbidden() throws Exception {
    createNativeModule(ModuleWithNonPublicFunction.class);
  }

  public static class ModuleWithNonPublicFunction {
    public interface Parameters {}

    @SmoothFunction(name = "func")
    protected static SString execute(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test(expected = NonStaticSmoothFunctionException.class)
  public void non_static_function_is_forbidden() throws Exception {
    createNativeModule(ModuleWithNonStaticFunction.class);
  }

  public static class ModuleWithNonStaticFunction {
    public interface Parameters {}

    @SmoothFunction(name = "func")
    public SString execute(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test(expected = ParamMethodHasArgumentsException.class)
  public void method_in_params_interface_cannot_have_parameters() throws Exception {
    createNativeModule(ModuleWithFunctionWhithParamsInterfaceWithMethodWithParams.class);
  }

  public static class ModuleWithFunctionWhithParamsInterfaceWithMethodWithParams {
    public interface Parameters {
      SString param(SString notAllowed);
    }

    @SmoothFunction(name = "func")
    public static SString execute(NativeApi nativeApi, Parameters params) {
      return null;
    }
  }

  @Test
  public void module_with_zero_functions_is_allowed() throws Exception {
    given(module = createNativeModule(ModuleWithNoFunctions.class));
    when(module).availableNames();
    thenReturned(Matchers.emptyIterable());
  }

  public static class ModuleWithNoFunctions {}

  public static Module createNativeModule(Class<?> clazz) throws Exception {
    File tempJarFile = File.createTempFile("tmp", ".jar");
    try (JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tempJarFile))) {
      jarOutputStream.putNextEntry(new ZipEntry(binaryPath(clazz)));
      try (InputStream byteCodeInputStream = classByteCodeInputStream(clazz)) {
        ByteStreams.copy(byteCodeInputStream, jarOutputStream);
      }
    }

    return NativeModuleFactory.createNativeModule(tempJarFile);
  }

  private static InputStream classByteCodeInputStream(Class<?> clazz) {
    return clazz.getClassLoader().getResourceAsStream(binaryPath(clazz));
  }
}
