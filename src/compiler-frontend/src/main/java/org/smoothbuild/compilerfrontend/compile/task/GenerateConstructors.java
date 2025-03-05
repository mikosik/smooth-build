package org.smoothbuild.compilerfrontend.compile.task;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;

import java.util.ArrayList;
import java.util.List;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.compilerfrontend.compile.ast.PScopingModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PConstructor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExplicitTypeParams;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PPolyEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;

public class GenerateConstructors implements Task1<PModule, PModule> {
  @Override
  public Output<PModule> execute(PModule pModule) {
    var constructorCreator = new ConstructorCreator();
    constructorCreator.visit(pModule);
    var label = COMPILER_FRONT_LABEL.append(":generateConstructors");
    var newEvaluables = pModule.evaluables().addAll(constructorCreator.constructors);
    var newModule = new PModule(pModule.fullPath(), pModule.structs(), newEvaluables);
    return output(newModule, label, list());
  }

  private static class ConstructorCreator extends PScopingModuleVisitor<RuntimeException> {
    private final List<PPolyEvaluable> constructors = new ArrayList<>();

    @Override
    public void visitStructSignature(PStruct pStruct) throws RuntimeException {
      super.visitStructSignature(pStruct);
      var typeParams = new PExplicitTypeParams(list(), pStruct.location());
      constructors.add(new PPolyEvaluable(typeParams, new PConstructor(pStruct)));
    }
  }
}
