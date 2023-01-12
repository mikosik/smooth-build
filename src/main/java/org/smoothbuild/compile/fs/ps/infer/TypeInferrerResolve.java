package org.smoothbuild.compile.fs.ps.infer;

import static org.smoothbuild.compile.fs.lang.type.VarSetS.varSetS;
import static org.smoothbuild.compile.fs.ps.CompileError.compileError;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.Optional;

import org.smoothbuild.compile.fs.lang.type.FuncSchemaS;
import org.smoothbuild.compile.fs.lang.type.FuncTS;
import org.smoothbuild.compile.fs.lang.type.SchemaS;
import org.smoothbuild.compile.fs.lang.type.TypeS;
import org.smoothbuild.compile.fs.lang.type.VarS;
import org.smoothbuild.compile.fs.lang.type.VarSetS;
import org.smoothbuild.compile.fs.lang.type.tool.Unifier;
import org.smoothbuild.compile.fs.ps.ast.define.AnonymousFuncP;
import org.smoothbuild.compile.fs.ps.ast.define.BlobP;
import org.smoothbuild.compile.fs.ps.ast.define.CallP;
import org.smoothbuild.compile.fs.ps.ast.define.EvaluableP;
import org.smoothbuild.compile.fs.ps.ast.define.ExprP;
import org.smoothbuild.compile.fs.ps.ast.define.FuncP;
import org.smoothbuild.compile.fs.ps.ast.define.IntP;
import org.smoothbuild.compile.fs.ps.ast.define.MonoizableP;
import org.smoothbuild.compile.fs.ps.ast.define.MonoizeP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedArgP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedFuncP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedValueP;
import org.smoothbuild.compile.fs.ps.ast.define.OrderP;
import org.smoothbuild.compile.fs.ps.ast.define.ReferenceP;
import org.smoothbuild.compile.fs.ps.ast.define.SelectP;
import org.smoothbuild.compile.fs.ps.ast.define.StringP;
import org.smoothbuild.out.log.Logger;

public class TypeInferrerResolve {
  private final Unifier unifier;
  private final Logger logger;

  public TypeInferrerResolve(Unifier unifier, Logger logger) {
    this.unifier = unifier;
    this.logger = logger;
  }

  public boolean resolveNamedValue(NamedValueP namedValueP) {
    return resolveEvaluable(namedValueP);
  }

  public boolean resolveNamedFunc(NamedFuncP namedFuncP) {
    return resolveEvaluable(namedFuncP);
  }

  private boolean resolveEvaluable(EvaluableP evaluableP) {
    return resolveBody(evaluableP.body()) && resolveSchema(evaluableP);
  }

  private boolean resolveSchema(EvaluableP evaluableP) {
    // @formatter:off
    switch (evaluableP) {
      case NamedValueP valueP -> valueP.setSchemaS(resolveSchema(valueP.schemaS()));
      case FuncP       funcP  -> funcP.setSchemaS(resolveSchema(funcP.schemaS()));
    }
    // @formatter:off
    return true;
  }

  private FuncSchemaS resolveSchema(FuncSchemaS funcSchemaS) {
    return new FuncSchemaS(resolveQuantifiedVars(funcSchemaS), (FuncTS) resolveType(funcSchemaS));
  }

  private SchemaS resolveSchema(SchemaS schemaS) {
    return new SchemaS(resolveQuantifiedVars(schemaS), resolveType(schemaS));
  }

  private VarSetS resolveQuantifiedVars(SchemaS schemaS) {
    return varSetS(schemaS.quantifiedVars().stream().map(v -> (VarS) unifier.resolve(v)).toList());
  }

  private TypeS resolveType(SchemaS schemaS) {
    return unifier.resolve(schemaS.type());
  }

  private boolean resolveBody(Optional<ExprP> body) {
    return body.map(this::resolveBody).orElse(true);
  }

  private boolean resolveBody(ExprP body) {
    inferUnitTypes(body);
    return resolveExpr(body);
  }

  private void inferUnitTypes(ExprP expr) {
    new UnitTypeInferrer(unifier).infer(expr);
  }

  private boolean resolveExpr(ExprP expr) {
    // @formatter:off
    return switch (expr) {
      case CallP          callP          -> resolveCall(callP);
      case MonoizeP       monoizeP       -> resolveMonoize(monoizeP);
      case NamedArgP      namedArgP      -> resolveNamedArg(namedArgP);
      case OrderP         orderP         -> resolveOrder(orderP);
      case SelectP        selectP        -> resolveSelect(selectP);
      case StringP        stringP        -> resolveExprType(stringP);
      case IntP           intP           -> resolveExprType(intP);
      case BlobP          blobP          -> resolveExprType(blobP);
    };
    // @formatter:on
  }

  private boolean resolveCall(CallP callP) {
    return resolveExpr(callP.callee())
        && callP.positionedArgs().stream().allMatch(this::resolveExpr)
        && resolveExprType(callP);
  }

  private boolean resolveMonoize(MonoizeP monoizeP) {
    return resolveMonoizable(monoizeP.monoizable()) && resolveMonoizeTypeArgs(monoizeP);
  }

  private boolean resolveMonoizeTypeArgs(MonoizeP monoizeP) {
    var resolvedTypeArgs = map(monoizeP.typeArgs(), unifier::resolve);
    if (resolvedTypeArgs.stream().anyMatch(this::hasTempVar)) {
      logger.log(compileError(monoizeP.location(), "Cannot infer actual type parameters."));
      return false;
    }
    monoizeP.setTypeArgs(resolvedTypeArgs);
    return true;
  }

  private boolean resolveMonoizable(MonoizableP monoizableP) {
    return switch (monoizableP) {
      case AnonymousFuncP anonymousFuncP -> resolveAnonymousFunc(anonymousFuncP);
      case ReferenceP referenceP -> true;
    };
  }


  private boolean resolveAnonymousFunc(AnonymousFuncP anonymousFuncP) {
    return resolveEvaluable(anonymousFuncP);
  }

  private boolean resolveNamedArg(NamedArgP namedArgP) {
    return resolveExpr(namedArgP.expr())
        && resolveExprType(namedArgP);
  }

  private boolean resolveOrder(OrderP orderP) {
    return orderP.elems().stream().allMatch(this::resolveExpr)
        && resolveExprType(orderP);
  }

  private boolean resolveSelect(SelectP selectP) {
    return resolveExpr(selectP.selectable())
        && resolveExprType(selectP);
  }

  private boolean resolveExprType(ExprP exprP) {
    exprP.setTypeS(unifier.resolve(exprP.typeS()));
    return true;
  }

  private boolean hasTempVar(TypeS t) {
    return t.vars().stream().anyMatch(VarS::isTemporary);
  }
}
