package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.compilerfrontend.compile.ast.define.PBlob;
import org.smoothbuild.compilerfrontend.compile.ast.define.PCall;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExpr;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInstantiate;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInt;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedArg;
import org.smoothbuild.compilerfrontend.compile.ast.define.POrder;
import org.smoothbuild.compilerfrontend.compile.ast.define.PPolymorphic;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReference;
import org.smoothbuild.compilerfrontend.compile.ast.define.PSelect;
import org.smoothbuild.compilerfrontend.compile.ast.define.PString;
import org.smoothbuild.compilerfrontend.lang.type.STupleType;
import org.smoothbuild.compilerfrontend.lang.type.tool.EqualityConstraint;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;

/**
 * Infers unit type (which is empty tuple) for each quantified var
 * in any call to polymorphic function when that quantified var:
 * - is not used in function result type
 * - is resolved as temp-var after inferring phase (= it is not constrained).
 */
public class UnitTypeInferrer {
  private final Unifier unifier;

  public UnitTypeInferrer(Unifier unifier) {
    this.unifier = unifier;
  }

  public void infer(PExpr expr) {
    switch (expr) {
      case PCall call -> inferCall(call);
      case PInstantiate pInstantiate -> inferInstantiate(pInstantiate);
      case PNamedArg namedArg -> inferNamedArg(namedArg);
      case POrder order -> inferOrder(order);
      case PSelect select -> inferSelect(select);
      case PString string -> {}
      case PInt int_ -> {}
      case PBlob blob -> {}
    }
  }

  private void inferCall(PCall call) {
    infer(call.callee());
    call.args().forEach(this::infer);
  }

  private void inferInstantiate(PInstantiate pInstantiate) {
    inferPolymorphic(pInstantiate.polymorphic());
    inferInstantiateTypeArgs(pInstantiate);
  }

  private void inferInstantiateTypeArgs(PInstantiate pInstantiate) {
    for (var typeArg : pInstantiate.typeArgs()) {
      var resolvedTypeArg = unifier.resolve(typeArg);
      for (var var : resolvedTypeArg.vars()) {
        if (var.isTemporary()) {
          unifier.addOrFailWithRuntimeException(
              new EqualityConstraint(var, new STupleType(list())));
        }
      }
    }
  }

  private void inferPolymorphic(PPolymorphic pPolymorphic) {
    switch (pPolymorphic) {
      case PLambda pLambda -> inferLambda(pLambda);
      case PReference pReference -> {}
    }
  }

  private void inferLambda(PLambda lambda) {
    infer(lambda.bodyGet());
  }

  private void inferNamedArg(PNamedArg namedArg) {
    infer(namedArg.expr());
  }

  private void inferOrder(POrder order) {
    order.elements().forEach(this::infer);
  }

  private void inferSelect(PSelect select) {
    infer(select.selectable());
  }
}
