package org.smoothbuild.compilerfrontend.compile.task;

import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;

import java.util.ArrayList;
import java.util.List;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PConstructor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;

public class GenerateConstructors extends PModuleVisitor<RuntimeException>
    implements Task1<PModule, PModule> {
  @Override
  public Output<PModule> execute(PModule pModule) {
    var logger = new Logger();
    var constructors = new ArrayList<PConstructor>();
    new ScopeCreator(constructors).visitModule(pModule);
    var label = COMPILER_FRONT_LABEL.append(":generateConstructors");
    var newEvaluables = pModule.evaluables().addAll(constructors);
    var newModule = new PModule(pModule.fileName(), pModule.structs(), newEvaluables);
    return output(newModule, label, logger.toList());
  }

  private static class ScopeCreator extends PModuleVisitor<RuntimeException> {
    private final List<PConstructor> constructors;

    public ScopeCreator(ArrayList<PConstructor> constructors) {
      this.constructors = constructors;
    }

    @Override
    public void visitStruct(PStruct pStruct) throws RuntimeException {
      super.visitStruct(pStruct);
      constructors.add(new PConstructor(pStruct));
    }
  }
}
