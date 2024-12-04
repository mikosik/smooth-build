package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExpr;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInstantiate;
import org.smoothbuild.compilerfrontend.lang.type.STupleType;
import org.smoothbuild.compilerfrontend.lang.type.tool.Constraint;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;

/**
 * Infers unit type (which is empty tuple) for each quantified var
 * in any call to polymorphic function when that quantified var:
 * - is not used in function result type
 * - is resolved as temp-var after inferring phase (= it is not constrained).
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
        if (var.isTemporary()) {
          unifier.addOrFailWithRuntimeException(new Constraint(var, new STupleType(list())));
        }
      }
    }
  }
}
