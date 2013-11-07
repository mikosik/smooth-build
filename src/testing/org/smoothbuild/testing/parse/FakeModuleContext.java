package org.smoothbuild.testing.parse;

import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;

public class FakeModuleContext extends ModuleContext {

  public static FakeModuleContext moduleCtx(FunctionContext... functions) {
    FakeModuleContext module = new FakeModuleContext();
    for (FunctionContext function : functions) {
      module.addChild(function);
    }
    return module;
  }

  public FakeModuleContext() {
    super(null, 0);
  }

  public FakeFunctionContext addFunctionCtx(String name) {
    FakeFunctionContext function = FakeFunctionContext.functionCtx(this, name);
    addChild(function);
    return function;
  }
}
