package org.smoothbuild.acceptance.lang;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.IllegalName;
import org.smoothbuild.acceptance.testing.NonPublicMethod;
import org.smoothbuild.acceptance.testing.NonStaticMethod;
import org.smoothbuild.acceptance.testing.SameName;
import org.smoothbuild.acceptance.testing.SameName2;
import org.smoothbuild.acceptance.testing.WithoutContainer;
import org.smoothbuild.plugin.NativeApi;

public class NativeTest extends AcceptanceTestCase {
  @Test
  public void native_jar_with_two_functions_with_same_name_causes_error() throws Exception {
    createNativeJar(SameName.class, SameName2.class);
    createUserModule("""
            result = "abc";
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(invalidFunctionProvidedBy(SameName2.class) + ".sameName: "
        + "Function with the same name is also provided by "
        + SameName.class.getCanonicalName() + ".sameName.\n");
  }

  @Test
  public void native_with_illegal_name_causes_error() throws Exception {
    createNativeJar(IllegalName.class);
    createUserModule("""
            result = "abc";
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(invalidFunctionProvidedBy(IllegalName.class)
        + ".illegalName$: Name 'illegalName$' is illegal.\n");
  }

  @Test
  public void native_provided_by_non_public_method_causes_error() throws Exception {
    createNativeJar(NonPublicMethod.class);
    createUserModule("""
            result = "abc";
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(invalidFunctionProvidedBy(NonPublicMethod.class)
        + ".function: Providing method must be public.\n");
  }

  @Test
  public void native_provided_by_non_static_method_causes_error() throws Exception {
    createNativeJar(NonStaticMethod.class);
    createUserModule("""
            result = "abc";
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(invalidFunctionProvidedBy(NonStaticMethod.class)
        + ".function: Providing method must be static.\n");
  }

  @Test
  public void native_without_container_parameter_causes_error() throws Exception {
    createNativeJar(WithoutContainer.class);
    createUserModule("""
            result = "abc";
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(invalidFunctionProvidedBy(WithoutContainer.class)
        + ".function: Providing method should have first parameter of type "
        + NativeApi.class.getCanonicalName() + ".\n");
  }

  private String invalidFunctionProvidedBy(Class<?> clazz) {
    return "Invalid native implementation in " + projectDirOption().resolve("build.jar").normalize()
        + " provided by " + clazz.getCanonicalName();
  }
}
