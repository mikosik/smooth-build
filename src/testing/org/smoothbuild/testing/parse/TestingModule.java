package org.smoothbuild.testing.parse;

import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;

public class TestingModule extends ModuleContext {

  public static TestingModule testingModule(FunctionContext... functions) {
    TestingModule module = new TestingModule();
    for (FunctionContext function : functions) {
      module.addChild(function);
    }
    return module;
  }

  public TestingModule() {
    super(null, 0);
  }

  public TestingFunction addFunction(String name) {
    TestingFunction function = TestingFunction.function(this, name);
    addChild(function);
    return function;
  }
}
