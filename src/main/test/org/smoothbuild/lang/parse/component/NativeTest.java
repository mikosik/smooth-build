package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.parse.component.TestModuleLoader.module;

import org.junit.jupiter.api.Test;

public class NativeTest {
  @Test
  public void native_without_declared_result_type_causes_error() {
    module("myFunction();")
        .loadsWithError(1, "`myFunction` is native so it should have type declaration.");
  }
}
