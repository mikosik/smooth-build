package org.smoothbuild.compile.fs.ps.infer;

import static org.smoothbuild.compile.fs.lang.type.VarSetS.varSetS;
import static org.smoothbuild.compile.fs.ps.CompileError.compileError;
import static org.smoothbuild.util.collect.Lists.generate;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.zip;
import static org.smoothbuild.util.collect.Optionals.flatMapPair;
import static org.smoothbuild.util.collect.Optionals.mapPair;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.Optional;
import java.util.function.Function;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.ArrayTS;
import org.smoothbuild.compile.fs.lang.type.FuncSchemaS;
import org.smoothbuild.compile.fs.lang.type.FuncTS;
import org.smoothbuild.compile.fs.lang.type.SchemaS;
import org.smoothbuild.compile.fs.lang.type.StructTS;
import org.smoothbuild.compile.fs.lang.type.TypeFS;
import org.smoothbuild.compile.fs.lang.type.TypeS;
import org.smoothbuild.compile.fs.lang.type.VarSetS;
import org.smoothbuild.compile.fs.lang.type.tool.Unifier;
import org.smoothbuild.compile.fs.lang.type.tool.UnifierExc;
import org.smoothbuild.compile.fs.ps.CompileError;
import org.smoothbuild.compile.fs.ps.ast.define.AnonymousFuncP;
import org.smoothbuild.compile.fs.ps.ast.define.BlobP;
import org.smoothbuild.compile.fs.ps.ast.define.CallP;
import org.smoothbuild.compile.fs.ps.ast.define.EvaluableP;
import org.smoothbuild.compile.fs.ps.ast.define.ExprP;
import org.smoothbuild.compile.fs.ps.ast.define.FuncP;
import org.smoothbuild.compile.fs.ps.ast.define.ImplicitTP;
import org.smoothbuild.compile.fs.ps.ast.define.IntP;
import org.smoothbuild.compile.fs.ps.ast.define.ItemP;
import org.smoothbuild.compile.fs.ps.ast.define.MonoizableP;
import org.smoothbuild.compile.fs.ps.ast.define.MonoizeP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedArgP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedFuncP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedValueP;
import org.smoothbuild.compile.fs.ps.ast.define.OrderP;
import org.smoothbuild.compile.fs.ps.ast.define.ReferenceP;
import org.smoothbuild.compile.fs.ps.ast.define.SelectP;
import org.smoothbuild.compile.fs.ps.ast.define.StringP;
import org.smoothbuild.compile.fs.ps.ast.define.TypeP;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class ExprTypeUnifier {
  private final Unifier unifier;
  private final TypeTeller typeTeller;
  private final VarSetS outerScopeVars;
  private final Logger logger;

  public ExprTypeUnifier(Unifier unifier, TypeTeller typeTeller, Logger logger) {
    this(unifier, typeTeller, varSetS(), logger);
  }

  private ExprTypeUnifier(
      Unifier unifier,
      TypeTeller typeTeller,
      VarSetS outerScopeVars,
      Logger logger) {
    this.unifier = unifier;
    this.typeTeller = typeTeller;
    this.outerScopeVars = outerScopeVars;
    this.logger = logger;
  }

  public boolean unifyNamedValue(NamedValueP namedValue) {
    return unifyEvaluableAndSetSchema(namedValue);
  }

  public boolean unifyNamedFunc(NamedFuncP namedFunc) {
    return unifyEvaluableAndSetSchema(namedFunc);
  }

  private boolean unifyEvaluableAndSetSchema(EvaluableP evaluableP) {
    return unifyEvaluable(evaluableP) && setEvaluableSchema(evaluableP);
  }

  private boolean unifyEvaluable(EvaluableP evaluableP) {
    return switch (evaluableP) {
      case FuncP funcP -> unifyFunc(funcP);
      case NamedValueP namedValueP -> unifyValue(namedValueP);
    };
  }

  private boolean setEvaluableSchema(EvaluableP evaluableP) {
    var resolvedT = resolveType(evaluableP);
    var vars = resolveQuantifiedVars(resolvedT);
    // @formatter:off
    switch (evaluableP) {
      case NamedValueP valueP -> valueP.setSchemaS(new SchemaS(vars, resolvedT));
      case FuncP       funcP  -> funcP.setSchemaS(new FuncSchemaS(vars, (FuncTS) resolvedT));
    }
    // @formatter:on
    return true;
  }

  private VarSetS resolveQuantifiedVars(TypeS typeS) {
    return typeS.vars().withRemoved(outerScopeVars.map(unifier::resolve));
  }

  private TypeS resolveType(EvaluableP evaluableP) {
    return unifier.resolve(evaluableP.typeS());
  }

  private boolean unifyValue(NamedValueP namedValueP) {
    return translateOrGenerateTempVar(namedValueP.evalT())
        .map(evalT -> {
          namedValueP.setTypeS(evalT);
          return unifyEvaluableBody(namedValueP, evalT, evalT, typeTeller);
        })
        .orElse(false);
  }

  private boolean unifyFunc(FuncP funcP) {
    var paramTs = inferParamTs(funcP.params());
    var resT = translateOrGenerateTempVar(funcP.resT());
    return mapPair(paramTs, resT, (p, r) -> unifyFunc(funcP, p, r))
        .orElse(false);
  }

  private boolean unifyFunc(FuncP funcP, ImmutableList<TypeS> paramTs, TypeS resT) {
    var typeTellerForBody = typeTeller.withScope(funcP.scope());
    var funcTS = new FuncTS(paramTs, resT);
    funcP.setTypeS(funcTS);
    return unifyEvaluableBody(funcP, resT, funcTS, typeTellerForBody);
  }

  private Optional<ImmutableList<TypeS>> inferParamTs(NList<ItemP> params) {
    var paramTs = pullUp(map(params, p -> typeTeller.translate(p.type())));
    paramTs.ifPresent(types -> zip(params, types, ItemP::setTypeS));
    return paramTs;
  }

  private Boolean unifyEvaluableBody(
      EvaluableP evaluableP, TypeS evalT, TypeS type, TypeTeller typeTeller) {
    var vars = outerScopeVars.withAdded(type.vars());
    return new ExprTypeUnifier(unifier, typeTeller, vars, logger)
        .unifyEvaluableBody(evaluableP, evalT);
  }

  private Boolean unifyEvaluableBody(EvaluableP evaluableP, TypeS evalT) {
    return evaluableP.body()
        .map(body -> unifyBodyExprAndEvaluationType(evaluableP, evalT, body))
        .orElse(true);
  }

  private boolean unifyBodyExprAndEvaluationType(EvaluableP evaluableP, TypeS typeS, ExprP bodyP) {
    return unifyExpr(bodyP)
        .map(bodyT -> unifyEvaluationTypeWithBodyType(evaluableP, typeS, bodyT))
        .orElse(false);
  }

  private boolean unifyEvaluationTypeWithBodyType(EvaluableP evaluableP, TypeS typeS, TypeS bodyT) {
    try {
      unifier.unify(typeS, bodyT);
      return true;
    } catch (UnifierExc e) {
      logger.log(compileError(
          evaluableP.location(), evaluableP.q() + " body type is not equal to declared type."));
      return false;
    }
  }

  private Optional<TypeS> unifyExpr(ExprP exprP) {
    // @formatter:off
    return switch (exprP) {
      case CallP          callP          -> unifyAndMemoize(this::unifyCall, callP);
      case MonoizeP       monoizeP       -> unifyAndMemoize(this::unifyMonoize, monoizeP);
      case NamedArgP      namedArgP      -> unifyAndMemoize(this::unifyNamedArg, namedArgP);
      case OrderP         orderP         -> unifyAndMemoize(this::unifyOrder, orderP);
      case SelectP        selectP        -> unifyAndMemoize(this::unifySelect, selectP);
      case StringP        stringP        -> setAndMemoize(TypeFS.STRING, stringP);
      case IntP           intP           -> setAndMemoize(TypeFS.INT, intP);
      case BlobP          blobP          -> setAndMemoize(TypeFS.BLOB, blobP);
    };
    // @formatter:on
  }

  private Optional<TypeS> setAndMemoize(TypeS typeS, ExprP exprP) {
    exprP.setTypeS(typeS);
    return Optional.of(typeS);
  }

  private <T extends ExprP> Optional<TypeS> unifyAndMemoize(
      Function<T, Optional<TypeS>> unifier, T exprP) {
    var type = unifier.apply(exprP);
    type.ifPresent(exprP::setTypeS);
    return type;
  }

  private Optional<TypeS> unifyCall(CallP callP) {
    var calleeT = unifyExpr(callP.callee());
    var positionedArgs = callP.positionedArgs();
    var argTs = pullUp(map(positionedArgs, this::unifyExpr));
    return flatMapPair(calleeT, argTs, (c, a) -> unifyCall(c, a, callP.location()));
  }

  private Optional<TypeS> unifyCall(TypeS calleeT, ImmutableList<TypeS> argTs, Location location) {
    var resT = unifier.newTempVar();
    var funcT = new FuncTS(argTs, resT);
    try {
      unifier.unify(funcT, calleeT);
      return Optional.of(resT);
    } catch (UnifierExc e) {
      logger.log(CompileError.compileError(location, "Illegal call."));
      return Optional.empty();
    }
  }

  private Optional<TypeS> unifyMonoize(MonoizeP monoizeP) {
    var monoizableP = monoizeP.monoizable();
    if (unifyMonoizable(monoizableP)) {
      var schema = monoizableP.schemaS();
      monoizeP.setTypeArgs(generate(schema.quantifiedVars().size(), unifier::newTempVar));
      return Optional.of(schema.monoize(monoizeP.typeArgs()));
    } else {
      return Optional.empty();
    }
  }

  private boolean unifyMonoizable(MonoizableP monoizableP) {
    return switch (monoizableP) {
      case AnonymousFuncP anonymousFuncP -> unifyAnonymousFunc(anonymousFuncP);
      case ReferenceP referenceP -> unifyReference(referenceP);
    };
  }

  private boolean unifyAnonymousFunc(AnonymousFuncP anonymousFuncP) {
    return unifyEvaluableAndSetSchema(anonymousFuncP);
  }

  private Optional<TypeS> unifyNamedArg(NamedArgP namedArgP) {
    return unifyExpr(namedArgP.expr());
  }

  private Optional<TypeS> unifyOrder(OrderP orderP) {
    var elems = orderP.elems();
    var elemTs = pullUp(map(elems, this::unifyExpr));
    return elemTs.flatMap(types -> unifyElemsWithArray(types, orderP.location()));
  }

  private Optional<TypeS> unifyElemsWithArray(ImmutableList<TypeS> elemTs, Location location) {
    var elemVar = unifier.newTempVar();
    for (TypeS elemT : elemTs) {
      try {
        unifier.unify(elemVar, elemT);
      } catch (UnifierExc e) {
        logger.log(CompileError.compileError(location,
            "Cannot infer type for array literal. Its element types are not compatible."));
        return Optional.empty();
      }
    }
    return Optional.of(new ArrayTS(elemVar));
  }

  private boolean unifyReference(ReferenceP referenceP) {
    Optional<SchemaS> schemaS = typeTeller.schemaFor(referenceP.name());
    if (schemaS.isPresent()) {
      referenceP.setSchemaS(schemaS.get());
      return true;
    } else {
      return false;
    }
  }

  private Optional<TypeS> unifySelect(SelectP selectP) {
    var selectableT = unifyExpr(selectP.selectable());
    return selectableT.flatMap(t -> {
      if (unifier.resolve(t) instanceof StructTS structTS) {
        var itemSigS = structTS.fields().get(selectP.field());
        if (itemSigS == null) {
          logger.log(compileError(selectP.location(), "Unknown field `" + selectP.field() + "`."));
          return Optional.empty();
        } else {
          return Optional.of(itemSigS.type());
        }
      } else {
        logger.log(compileError(selectP.location(), "Illegal field access."));
        return Optional.empty();
      }
    });
  }

  private Optional<TypeS> translateOrGenerateTempVar(TypeP typeP) {
    if (typeP instanceof ImplicitTP) {
      return Optional.of(unifier.newTempVar());
    } else {
      return typeTeller.translate(typeP);
    }
  }
}
