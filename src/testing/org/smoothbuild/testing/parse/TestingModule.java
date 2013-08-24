package org.smoothbuild.testing.parse;

import org.smoothbuild.antlr.SmoothParser.ModuleContext;

public class TestingModule extends ModuleContext {

  public static TestingModule testingModule() {
    return new TestingModule();
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
