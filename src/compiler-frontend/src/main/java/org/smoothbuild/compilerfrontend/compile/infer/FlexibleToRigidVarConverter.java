package org.smoothbuild.compilerfrontend.compile.infer;

import org.smoothbuild.compilerfrontend.compile.ast.PScopingModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;
import org.smoothbuild.compilerfrontend.lang.type.tool.AlphabeticalTypeNameGenerator;
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
    var nameGenerator = new AlphabeticalTypeNameGenerator();
    var mapping = resolvedType
        .typeVars()
        .filter(STypeVar::isFlexibleTypeVar)
        .toList()
        .toMap(v -> (SType) new STypeVar(evaluable.fqn().append(nameGenerator.next())));
    var renamedVarsType = resolvedType.mapTypeVars(mapping);
    unifier.addOrFailWithRuntimeException(new Constraint(renamedVarsType, resolvedType));
    evaluable.body().ifPresent(this::visitExpr);
  }

  @Override
  public void visitLambda(PLambda pLambda) {
    renameFlexibleVarsToRigid(pLambda);
  }
}
