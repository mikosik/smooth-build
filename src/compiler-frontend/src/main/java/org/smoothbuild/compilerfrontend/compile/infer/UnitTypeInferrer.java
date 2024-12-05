package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExpr;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInstantiate;
import org.smoothbuild.compilerfrontend.lang.type.STupleType;
import org.smoothbuild.compilerfrontend.lang.type.tool.Constraint;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;

/**
 * Infers unit type (which is represented by empty tuple in smooth) for each type argument
 * to PInstantiate that has not been inferred after inferring phase. Such situation may happen when
 * type variable is used in function parameters but not in return type.
 * For example in call `concat([[]])`.
 */
public class UnitTypeInferrer extends PModuleVisitor<RuntimeException> {
  private final Unifier unifier;

  private UnitTypeInferrer(Unifier unifier) {
    this.unifier = unifier;
  }

  public static void inferUnitTypes(Unifier unifier, PExpr expr) {
    new UnitTypeInferrer(unifier).visitExpr(expr);
  }

  @Override
  public void visitInstantiateP(PInstantiate pInstantiate) {
    for (var typeArg : pInstantiate.typeArgs()) {
      for (var var : unifier.resolve(typeArg).vars()) {
        if (var.isFlexibleVar()) {
          unifier.addOrFailWithRuntimeException(new Constraint(var, new STupleType(list())));
        }
      }
    }
  }
}
