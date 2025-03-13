package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.compilerfrontend.compile.ast.PScopingModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInstantiate;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedEvaluable;
import org.smoothbuild.compilerfrontend.lang.type.STupleType;
import org.smoothbuild.compilerfrontend.lang.type.tool.Constraint;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;

/**
 * Infers unit type (which is represented by empty tuple in smooth) for each type argument
 * to PInstantiate that has not been inferred after inferring phase. Such situation may happen when
 * type variable is used in function parameters but not in return type.
 * For example in call `size([])`.
 */
public class UnitTypeInferrer extends PScopingModuleVisitor<RuntimeException> {
  private final Unifier unifier;

  private UnitTypeInferrer(Unifier unifier) {
    this.unifier = unifier;
  }

  public static void collectUnitTypesConstraints(Unifier unifier, PNamedEvaluable expr) {
    new UnitTypeInferrer(unifier).visit(expr);
  }

  @Override
  public void visitInstantiate(PInstantiate pInstantiate) {
    for (var typeArg : pInstantiate.typeArgs()) {
      for (var var : unifier.resolve(typeArg).typeVars()) {
        if (var.isFlexibleTypeVar()) {
          unifier.addOrFailWithRuntimeException(new Constraint(var, new STupleType(list())));
        }
      }
    }
  }
}
