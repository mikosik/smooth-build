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
import org.smoothbuild.compilerfrontend.compile.ast.define.PContainer;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExpr;
import org.smoothbuild.compilerfrontend.compile.ast.define.PImplicitType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PImplicitTypeParams;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.Id;

public class GenerateDefaultValues implements Task1<PModule, PModule> {
  @Override
  public Output<PModule> execute(PModule pModule) {
    var namedDefaultValues = new ArrayList<PNamedEvaluable>();
    generateDefaultValues(pModule, namedDefaultValues);
    var label = COMPILER_FRONT_LABEL.append(":generateDefaultValues");
    var newModule = new PModule(
        pModule.fullPath(), pModule.structs(), pModule.evaluables().addAll(namedDefaultValues));
    return output(newModule, label, list());
  }

  private static void generateDefaultValues(
      PModule pModule, ArrayList<PNamedEvaluable> namedDefaultValues) {
    new PModuleVisitor<Fqn, RuntimeException>() {
      @Override
      protected Fqn propertyOf(PContainer pContainer) {
        return pContainer.fqn();
      }

      @Override
      public void visitItem(PItem pItem) throws RuntimeException {
        super.visitItem(pItem);
        var fqn = fqn(containerProperty().toString() + "~" + pItem.name().toString());
        pItem.setDefaultValueId(pItem.defaultValue().map(e -> createNamedDefaultValue(e, fqn)));
      }

      private Id createNamedDefaultValue(PExpr expr, Fqn fqn) {
        var location = expr.location();
        var type = new PImplicitType(location);
        var typeParams = new PImplicitTypeParams();
        var name = fqn.parts().getLast().toString();
        var pNamedValue = new PNamedValue(type, name, typeParams, some(expr), none(), location);
        pNamedValue.setFqn(fqn);
        namedDefaultValues.add(pNamedValue);
        return fqn;
      }
    }.visit(pModule);
  }
}
