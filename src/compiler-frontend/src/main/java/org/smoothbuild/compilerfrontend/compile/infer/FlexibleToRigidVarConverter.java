package org.smoothbuild.compilerfrontend.compile.infer;

import org.smoothbuild.compilerfrontend.compile.ast.PScopingModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVar;
import org.smoothbuild.compilerfrontend.lang.type.tool.AlphabeticalVarsGenerator;
import org.smoothbuild.compilerfrontend.lang.type.tool.Constraint;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;

public class FlexibleToRigidVarConverter extends PScopingModuleVisitor<RuntimeException> {
  private final Unifier unifier;

  private FlexibleToRigidVarConverter(Unifier unifier) {
    this.unifier = unifier;
  }

  public static void convertFlexibleVarsToRigid(Unifier unifier, PEvaluable pEvaluable) {
    new FlexibleToRigidVarConverter(unifier).renameFlexibleVarsToRigid(pEvaluable);
  }

  private void renameFlexibleVarsToRigid(PEvaluable evaluable) {
    var resolvedType = unifier.resolve(evaluable.sType());
    evaluable.body().ifPresent(this::visitExpr);
    var varGenerator = new AlphabeticalVarsGenerator();
    var mapping = resolvedType.vars().filter(SVar::isFlexibleVar).toList().toMap(v ->
        (SType) new SVar(evaluable.fqn().append(varGenerator.next().fqn())));
    var renamedVarsType = resolvedType.mapVars(mapping);
    unifier.addOrFailWithRuntimeException(new Constraint(renamedVarsType, resolvedType));
  }

  @Override
  public void visitLambda(PLambda pLambda) {
    renameFlexibleVarsToRigid(pLambda);
  }
}
