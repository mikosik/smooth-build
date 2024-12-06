package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.compile.infer.UnitTypeInferrer.inferUnitTypes;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.compilerfrontend.compile.ast.define.PBlob;
import org.smoothbuild.compilerfrontend.compile.ast.define.PCall;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExpr;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInstantiate;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInt;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedArg;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.POrder;
import org.smoothbuild.compilerfrontend.compile.ast.define.PPolymorphic;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReference;
import org.smoothbuild.compilerfrontend.compile.ast.define.PSelect;
import org.smoothbuild.compilerfrontend.compile.ast.define.PString;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVar;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;

public class TypeResolver {
  private final Unifier unifier;

  private TypeResolver(Unifier unifier) {
    this.unifier = unifier;
  }

  public static void resolveNamedValue(Unifier unifier, PNamedValue pNamedValue)
      throws TypeException {
    new TypeResolver(unifier).resolveNamedValue(pNamedValue);
  }

  private void resolveNamedValue(PNamedValue pNamedValue) throws TypeException {
    resolveBody(pNamedValue.body());
    var sSchema = pNamedValue.sSchema();
    var quantifiedVars = resolveQuantifiedVars(sSchema);
    pNamedValue.setSSchema(new SSchema(quantifiedVars, resolveType(sSchema)));
  }

  public static void resolveFunc(Unifier unifier, PFunc pFunc) throws TypeException {
    new TypeResolver(unifier).resolveFunc(pFunc);
  }

  private void resolveFunc(PFunc pFunc) throws TypeException {
    resolveBody(pFunc.body());
    var sSchema = pFunc.sSchema();
    var quantifiedVars = resolveQuantifiedVars(sSchema);
    pFunc.setSSchema(new SFuncSchema(quantifiedVars, (SFuncType) resolveType(sSchema)));
  }

  private SVarSet resolveQuantifiedVars(SSchema sSchema) {
    return varSetS(
        sSchema.quantifiedVars().stream().map(v -> (SVar) unifier.resolve(v)).toList());
  }

  private SType resolveType(SSchema sSchema) {
    return unifier.resolve(sSchema.type());
  }

  private void resolveBody(Maybe<PExpr> body) throws TypeException {
    body.ifPresent(this::resolveBody);
  }

  private void resolveBody(PExpr body) throws TypeException {
    inferUnitTypes(unifier, body);
    resolveExpr(body);
  }

  private void resolveExpr(PExpr expr) throws TypeException {
    switch (expr) {
      case PCall pCall -> resolveCall(pCall);
      case PInstantiate pInstantiate -> resolveInstantiate(pInstantiate);
      case PNamedArg pNamedArg -> resolveNamedArg(pNamedArg);
      case POrder pOrder -> resolveOrder(pOrder);
      case PSelect pSelect -> resolveSelect(pSelect);
      case PString pString -> resolveExprType(pString);
      case PInt pInt -> resolveExprType(pInt);
      case PBlob pBlob -> resolveExprType(pBlob);
    }
  }

  private void resolveCall(PCall pCall) throws TypeException {
    resolveExpr(pCall.callee());
    pCall.positionedArgs().withEach(this::resolveExpr);
    resolveExprType(pCall);
  }

  private void resolveInstantiate(PInstantiate pInstantiate) throws TypeException {
    resolvePolymorphic(pInstantiate.polymorphic());
    resolveInstantiateTypeArgs(pInstantiate);
  }

  private void resolveInstantiateTypeArgs(PInstantiate pInstantiate) throws TypeException {
    var resolvedTypeArgs = pInstantiate.typeArgs().map(unifier::resolve);
    if (resolvedTypeArgs.stream().anyMatch(this::hasFlexibleVar)) {
      throw new TypeException(
          compileError(pInstantiate.location(), "Cannot infer actual type parameters."));
    }
    pInstantiate.setTypeArgs(resolvedTypeArgs);
  }

  private void resolvePolymorphic(PPolymorphic pPolymorphic) throws TypeException {
    switch (pPolymorphic) {
      case PLambda pLambda -> resolveFunc(pLambda);
      case PReference pReference -> {}
    }
  }

  private void resolveNamedArg(PNamedArg pNamedArg) throws TypeException {
    resolveExpr(pNamedArg.expr());
    resolveExprType(pNamedArg);
  }

  private void resolveOrder(POrder pOrder) throws TypeException {
    pOrder.elements().withEach(this::resolveExpr);
    resolveExprType(pOrder);
  }

  private void resolveSelect(PSelect pSelect) throws TypeException {
    resolveExpr(pSelect.selectable());
    resolveExprType(pSelect);
  }

  private void resolveExprType(PExpr pExpr) {
    pExpr.setSType(unifier.resolve(pExpr.sType()));
  }

  private boolean hasFlexibleVar(SType sType) {
    return sType.vars().stream().anyMatch(SVar::isFlexibleVar);
  }
}
