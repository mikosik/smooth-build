package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVar;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;
import org.smoothbuild.compilerfrontend.lang.type.tool.Constraint;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;
import org.smoothbuild.compilerfrontend.lang.type.tool.UnusedVarsGenerator;

public class FlexibleToRigidVarConverter extends PModuleVisitor<RuntimeException> {
  private final Unifier unifier;
  private final SVarSet outerScopeVars;

  private FlexibleToRigidVarConverter(Unifier unifier, SVarSet outerScopeVars) {
    this.unifier = unifier;
    this.outerScopeVars = outerScopeVars;
  }

  public static void convertFlexibleVarsToRigid(Unifier unifier, PEvaluable pEvaluable) {
    new FlexibleToRigidVarConverter(unifier, varSetS()).nameVarsInEvaluable(pEvaluable);
  }

  @Override
  public void visitLambda(PLambda pLambda) {
    nameVarsInEvaluable(pLambda);
  }

  private void nameVarsInEvaluable(PEvaluable evaluable) {
    var resolvedT = unifier.resolve(evaluable.sType());
    var body = evaluable.body();
    var localScopeVars = resolvedT.vars().filter(v -> !v.isFlexibleVar());
    var fullScopeVars = outerScopeVars.withAddedAll(localScopeVars);
    body.ifPresent(b -> new FlexibleToRigidVarConverter(unifier, fullScopeVars).visitExpr(b));
    var resolvedAndRenamedT = renameFlexibleVars(resolvedT, localScopeVars);
    unifier.addOrFailWithRuntimeException(new Constraint(resolvedAndRenamedT, resolvedT));
  }

  private static SType renameFlexibleVars(SType resolvedT, SVarSet reservedVars) {
    var vars = resolvedT.vars();
    var varsToRename = vars.filter(SVar::isFlexibleVar);
    var varGenerator = new UnusedVarsGenerator(reservedVars);
    var mapping = varsToRename.toList().toMap(v -> (SType) varGenerator.next());
    return resolvedT.mapVars(mapping);
  }
}
