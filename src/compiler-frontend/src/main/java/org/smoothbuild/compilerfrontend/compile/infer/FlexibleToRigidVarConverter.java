package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.smoothbuild.compilerfrontend.compile.ast.PScopingModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExpr;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVar;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;
import org.smoothbuild.compilerfrontend.lang.type.tool.Constraint;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;
import org.smoothbuild.compilerfrontend.lang.type.tool.UnusedVarsGenerator;

public class FlexibleToRigidVarConverter extends PScopingModuleVisitor<RuntimeException> {
  private final Unifier unifier;
  private final SVarSet outerScopeVars;

  private FlexibleToRigidVarConverter(Unifier unifier, SVarSet outerScopeVars) {
    this.unifier = unifier;
    this.outerScopeVars = outerScopeVars;
  }

  public static void convertFlexibleVarsToRigid(Unifier unifier, PEvaluable pEvaluable) {
    new FlexibleToRigidVarConverter(unifier, varSetS()).renameFlexibleVarsToRigid(pEvaluable);
  }

  @Override
  public void visitLambda(PLambda pLambda) {
    renameFlexibleVarsToRigid(pLambda);
  }

  private void renameFlexibleVarsToRigid(PEvaluable evaluable) {
    var resolvedType = unifier.resolve(evaluable.sType());
    evaluable.body().ifPresent(b -> nameVarsInBody(resolvedType, b));
    var renamedVarsType = renameFlexibleVarsToRigid(resolvedType, outerScopeVars);
    unifier.addOrFailWithRuntimeException(new Constraint(renamedVarsType, resolvedType));
  }

  private void nameVarsInBody(SType evaluableResolvedType, PExpr body) {
    var rigidVars = evaluableResolvedType.vars().filter(var -> !var.isFlexibleVar());
    var reservedVars = outerScopeVars.addAll(rigidVars);
    new FlexibleToRigidVarConverter(unifier, reservedVars).visitExpr(body);
  }

  private static SType renameFlexibleVarsToRigid(SType sType, SVarSet reservedVars) {
    var vars = sType.vars();
    var rigidVars = vars.filter(var -> !var.isFlexibleVar());
    var varGenerator = new UnusedVarsGenerator(reservedVars.addAll(rigidVars));
    var flexibleVars = vars.filter(SVar::isFlexibleVar);
    var mapping = flexibleVars.toList().toMap(v -> (SType) varGenerator.next());
    return sType.mapVars(mapping);
  }
}
