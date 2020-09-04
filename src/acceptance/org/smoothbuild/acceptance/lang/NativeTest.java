package org.smoothbuild.acceptance.lang;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.IllegalName;
import org.smoothbuild.acceptance.testing.NonPublicMethod;
import org.smoothbuild.acceptance.testing.NonStaticMethod;
import org.smoothbuild.acceptance.testing.ReturnAbc;
import org.smoothbuild.acceptance.testing.SameName;
import org.smoothbuild.acceptance.testing.SameName2;
import org.smoothbuild.acceptance.testing.WithoutContainer;
import org.smoothbuild.plugin.NativeApi;

public class NativeTest extends AcceptanceTestCase {
  @Test
  public void native_jar_with_two_native_implementations_with_same_name_causes_error() throws Exception {
    createNativeJar(SameName.class, SameName2.class);
    createUserModule("""
            String sameName;
            result = sameName;
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(
        "Error loading native implementation for `sameName`. Invalid native implementation in "
            + projectDirOption().resolve("build.jar").normalize()
            + " provided by " + SameName2.class.getCanonicalName()
            + ".sameName: Implementation for the same name is also provided by "
            + SameName.class.getCanonicalName() + ".sameName.");
  }

  @Test
  public void native_with_illegal_name_causes_error() throws Exception {
    createNativeJar(IllegalName.class, ReturnAbc.class);
    createUserModule("""
            String returnAbc;
            result = returnAbc;
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(invalidFunctionProvidedBy(IllegalName.class)
        + ".illegalName$: Name 'illegalName$' is illegal.\n");
  }

  @Test
  public void native_provided_by_non_public_method_causes_error() throws Exception {
    createNativeJar(NonPublicMethod.class, ReturnAbc.class);
    createUserModule("""
            String returnAbc;
            result = returnAbc;
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(invalidFunctionProvidedBy(NonPublicMethod.class)
        + ".function: Providing method must be public.\n");
  }

  @Test
  public void native_provided_by_non_static_method_causes_error() throws Exception {
    createNativeJar(NonStaticMethod.class, ReturnAbc.class);
    createUserModule("""
            String returnAbc;
            result = returnAbc;
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(invalidFunctionProvidedBy(NonStaticMethod.class)
        + ".function: Providing method must be static.\n");
  }

  @Test
  public void native_without_container_parameter_causes_error() throws Exception {
    createNativeJar(WithoutContainer.class, ReturnAbc.class);
    createUserModule("""
            String returnAbc;
            result = returnAbc;
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
