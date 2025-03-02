package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.compilerfrontend.compile.infer.UnitTypeInferrer.inferUnitTypes;
import static org.smoothbuild.compilerfrontend.compile.task.CompileError.compileError;

import org.smoothbuild.common.collect.List;
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
import org.smoothbuild.compilerfrontend.compile.ast.define.PSelect;
import org.smoothbuild.compilerfrontend.compile.ast.define.PString;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;
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
    var sSchema = pNamedValue.schema();
    var typeParams = resolveTypeParams(sSchema);
    pNamedValue.setSchema(new SSchema(typeParams, resolveType(sSchema)));
  }

  public static void resolveFunc(Unifier unifier, PFunc pFunc) throws TypeException {
    new TypeResolver(unifier).resolveFunc(pFunc);
  }

  private void resolveFunc(PFunc pFunc) throws TypeException {
    var sSchema = pFunc.schema();
    var typeParams = resolveTypeParams(sSchema);
    pFunc.setSchema(new SFuncSchema(typeParams, (SFuncType) resolveType(sSchema)));
    resolveBody(pFunc.body());
  }

  private List<STypeVar> resolveTypeParams(SSchema sSchema) {
    return sSchema.typeParams().map(v -> (STypeVar) unifier.resolve(v));
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
      case PLambda pLambda -> resolveLambda(pLambda);
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
    pCall.positionedArgs().foreach(this::resolveExpr);
    resolveExprType(pCall);
  }

  private void resolveInstantiate(PInstantiate pInstantiate) throws TypeException {
    var resolvedTypeArgs = pInstantiate.typeArgs().map(unifier::resolve);
    if (resolvedTypeArgs.stream().anyMatch(this::hasFlexibleVar)) {
      throw new TypeException(
          compileError(pInstantiate.location(), "Cannot infer actual type parameters."));
    }
    pInstantiate.setTypeArgs(resolvedTypeArgs);
  }

  private void resolveLambda(PLambda pFunc) throws TypeException {
    var sSchema = pFunc.schema();
    pFunc.setSchema(new SFuncSchema(list(), (SFuncType) resolveType(sSchema)));
    resolveBody(pFunc.body());
  }

  private void resolveNamedArg(PNamedArg pNamedArg) throws TypeException {
    resolveExpr(pNamedArg.expr());
    resolveExprType(pNamedArg);
  }

  private void resolveOrder(POrder pOrder) throws TypeException {
    pOrder.elements().foreach(this::resolveExpr);
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
    return sType.typeVars().stream().anyMatch(STypeVar::isFlexibleTypeVar);
  }
}
