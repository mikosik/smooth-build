package org.smoothbuild.testing.parse;

import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;

public class TestModule extends ModuleContext {

  public static TestModule module(FunctionContext... functions) {
    TestModule module = new TestModule();
    for (FunctionContext function : functions) {
      module.addChild(function);
    }
    return module;
  }

  public TestModule() {
    super(null, 0);
  }

  public TestFunction addFunction(String name) {
    TestFunction function = TestFunction.function(this, name);
    addChild(function);
    return function;
  }
}
