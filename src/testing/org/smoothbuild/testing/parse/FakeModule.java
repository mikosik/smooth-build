package org.smoothbuild.testing.parse;

import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;

public class FakeModule extends ModuleContext {

  public static FakeModule module(FunctionContext... functions) {
    FakeModule module = new FakeModule();
    for (FunctionContext function : functions) {
      module.addChild(function);
    }
    return module;
  }

  public FakeModule() {
    super(null, 0);
  }

  public FakeFunction addFunction(String name) {
    FakeFunction function = FakeFunction.function(this, name);
    addChild(function);
    return function;
  }
}
