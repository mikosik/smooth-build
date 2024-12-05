package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExpr;
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
    evaluable.body().ifPresent(b -> nameVarsInBody(evaluable, b));
    var resolvedType = unifier.resolve(evaluable.sType());
    var renamedVarsType = renameFlexibleVarsToRigid(resolvedType, outerScopeVars);
    unifier.addOrFailWithRuntimeException(new Constraint(renamedVarsType, resolvedType));
  }

  private void nameVarsInBody(PEvaluable evaluable, PExpr body) {
    var resolvedType = unifier.resolve(evaluable.sType());
    var rigidVars = resolvedType.vars().filter(var -> !var.isFlexibleVar());
    var reservedVars = outerScopeVars.withAddedAll(rigidVars);
    new FlexibleToRigidVarConverter(unifier, reservedVars).visitExpr(body);
  }

  private static SType renameFlexibleVarsToRigid(SType sType, SVarSet reservedVars) {
    var vars = sType.vars();
    var rigidVars = vars.filter(var -> !var.isFlexibleVar());
    var varGenerator = new UnusedVarsGenerator(reservedVars.withAddedAll(rigidVars));
    var flexibleVars = vars.filter(SVar::isFlexibleVar);
    var mapping = flexibleVars.toList().toMap(v -> (SType) varGenerator.next());
    return sType.mapVars(mapping);
  }
}
