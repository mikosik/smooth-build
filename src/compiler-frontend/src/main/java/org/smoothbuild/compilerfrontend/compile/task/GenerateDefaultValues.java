package org.smoothbuild.compilerfrontend.compile.task;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;

import java.util.ArrayList;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExpr;
import org.smoothbuild.compilerfrontend.compile.ast.define.PImplicitType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.lang.name.Id;

public class GenerateDefaultValues implements Task1<PModule, PModule> {
  @Override
  public Output<PModule> execute(PModule pModule) {
    var namedDefaultValues = new ArrayList<PNamedEvaluable>();
    generateDefaultValues(pModule, namedDefaultValues);
    var label = COMPILER_FRONT_LABEL.append(":generateDefaultValues");
    var newModule = new PModule(
        pModule.fileName(), pModule.structs(), pModule.evaluables().addAll(namedDefaultValues));
    return output(newModule, label, list());
  }

  private static void generateDefaultValues(
      PModule pModule, ArrayList<PNamedEvaluable> namedDefaultValues) {
    new PModuleVisitor<RuntimeException>() {
      private Id scopeId;

      @Override
      public void visitNamedEvaluable(PNamedEvaluable pNamedEvaluable) throws RuntimeException {
        runWithScopeId(pNamedEvaluable.id(), () -> super.visitNamedEvaluable(pNamedEvaluable));
      }

      @Override
      public void visitLambda(PLambda pLambda) throws RuntimeException {
        runWithScopeId(pLambda.id(), () -> super.visitLambda(pLambda));
      }

      @Override
      public void visitStruct(PStruct pStruct) throws RuntimeException {
        runWithScopeId(pStruct.id(), () -> super.visitStruct(pStruct));
      }

      @Override
      public void visitItem(PItem pItem) throws RuntimeException {
        super.visitItem(pItem);
        var id = fqn(scopeId.toString() + "~" + pItem.name().toString());
        pItem.setDefaultValueId(pItem.defaultValue().map(e -> createNamedDefaultValue(e, id)));
      }

      private Id createNamedDefaultValue(PExpr expr, Id id) {
        var type = new PImplicitType(expr.location());
        var name = id.parts().getLast().toString();
        var pNamedValue = new PNamedValue(type, name, some(expr), none(), expr.location());
        pNamedValue.setId(id);
        namedDefaultValues.add(pNamedValue);
        return id;
      }

      private void runWithScopeId(Id id, Runnable runnable) {
        var old = scopeId;
        scopeId = id;
        try {
          runnable.run();
        } finally {
          scopeId = old;
        }
      }
    }.visitModule(pModule);
  }
}
