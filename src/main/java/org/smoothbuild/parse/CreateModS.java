package org.smoothbuild.parse;

import static java.util.Optional.empty;
import static org.smoothbuild.lang.type.ConstrS.constrS;
import static org.smoothbuild.lang.type.Side.LOWER;
import static org.smoothbuild.lang.type.TNamesS.isVarName;
import static org.smoothbuild.lang.type.TypeS.prefixFreeVarsWithIndex;
import static org.smoothbuild.lang.type.VarSetS.varSetS;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.out.log.Maybe.maybeLogs;
import static org.smoothbuild.parse.ParseError.parseError;
import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.bindings.OptionalBindings.newOptionalBindings;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;
import static org.smoothbuild.util.collect.NList.nlist;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.smoothbuild.lang.base.Tapanal;
import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.define.ModFiles;
import org.smoothbuild.lang.define.ModPath;
import org.smoothbuild.lang.define.ModS;
import org.smoothbuild.lang.define.MonoFuncS;
import org.smoothbuild.lang.define.ObjS;
import org.smoothbuild.lang.define.RefableS;
import org.smoothbuild.lang.define.StructDefS;
import org.smoothbuild.lang.define.SyntCtorS;
import org.smoothbuild.lang.define.TDefS;
import org.smoothbuild.lang.define.TopRefableS;
import org.smoothbuild.lang.type.FuncTS;
import org.smoothbuild.lang.type.MonoFuncTS;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.PolyTS;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.TypeFS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.lang.type.VarSetS;
import org.smoothbuild.lang.type.solver.ConstrDecomposeExc;
import org.smoothbuild.lang.type.solver.ConstrSolver;
import org.smoothbuild.lang.type.solver.Denormalizer;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.parse.ast.ArgP;
import org.smoothbuild.parse.ast.ArrayTP;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.AstVisitor;
import org.smoothbuild.parse.ast.CallP;
import org.smoothbuild.parse.ast.FuncP;
import org.smoothbuild.parse.ast.FuncTP;
import org.smoothbuild.parse.ast.ItemP;
import org.smoothbuild.parse.ast.NamedP;
import org.smoothbuild.parse.ast.ObjP;
import org.smoothbuild.parse.ast.OrderP;
import org.smoothbuild.parse.ast.Parsed;
import org.smoothbuild.parse.ast.RefP;
import org.smoothbuild.parse.ast.RefableP;
import org.smoothbuild.parse.ast.SelectP;
import org.smoothbuild.parse.ast.StructP;
import org.smoothbuild.parse.ast.TopRefableP;
import org.smoothbuild.parse.ast.TypeP;
import org.smoothbuild.parse.ast.ValP;
import org.smoothbuild.util.bindings.ImmutableBindings;
import org.smoothbuild.util.bindings.OptionalBindings;

import com.google.common.collect.ImmutableList;

public class CreateModS {
  public static Maybe<ModS> createModS(ModPath path, ModFiles modFiles, Ast ast, DefsS imported) {
    var logBuffer = new LogBuffer();
    OptionalBindings<RefableS> topRefables = newOptionalBindings(imported.topRefables());
    OptionalBindings<TDefS> topTypes = imported.tDefs().newInnerScope();

    new CreateTopObjsVisitor(path, topTypes, topRefables, logBuffer)
        .visitAst(ast);
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    } else {
      var cast = (ImmutableBindings<TopRefableS>)(Object) topRefables.innerScopeBindings();
      var modS = new ModS(path, modFiles, topTypes.innerScopeBindings(), cast);
      return maybe(modS, logBuffer);
    }
  }

  private static class CreateTopObjsVisitor extends AstVisitor {
    private final ModPath path;
    private final OptionalBindings<TDefS> types;
    private final OptionalBindings<RefableS> bindings;
    private final LogBuffer logBuffer;

    public CreateTopObjsVisitor(ModPath path, OptionalBindings<TDefS> types,
        OptionalBindings<RefableS> bindings, LogBuffer logBuffer) {
      this.path = path;
      this.types = types;
      this.bindings = bindings;
      this.logBuffer = logBuffer;
    }

    @Override
    public void visitStruct(StructP struct) {
      super.visitStruct(struct);
      var fieldSigs = pullUp(map(struct.fields(), ItemP::sig));
      var structTS = fieldSigs.map(f -> TypeFS.struct(struct.name(), nlist(f)));
      Optional<TDefS> structDefS = structTS.map(s -> new StructDefS(s, path, struct.loc()));
      types.add(struct.name(), structDefS);
      Optional<FuncTS> ctorT = structTS.map(s -> TypeFS.func(s, map(s.fields(), ItemSigS::type)));
      struct.ctor().setTypeS(ctorT);
      Optional<RefableS> ctorS = ctorT.map(t -> loadSyntCtor(path, struct));
      bindings.add(struct.ctor().name(), ctorS);
    }

    private static MonoFuncS loadSyntCtor(ModPath path, StructP structP) {
      var ctorP = structP.ctor();
      var type = (MonoFuncTS) ctorP.typeS().get();
      var name = ctorP.name();
      var params = structP.fields().map(ItemP::toItemS);
      var loc = structP.loc();
      return new SyntCtorS(type, path, name, params, loc);
    }

    @Override
    public void visitField(ItemP itemP) {
      super.visitField(itemP);
      var typeOpt = itemP.type().typeS();

      itemP.setTypeS(typeOpt.flatMap((t) -> {
        if (!t.vars().isEmpty()) {
          var message = "Field type cannot be polymorphic. Found field %s with type %s."
              .formatted(itemP.q(), t.q());
          logError(itemP, message);
          return empty();
        } else {
          return Optional.of(t);
        }
      }));
    }

    @Override
    public void visitFunc(FuncP funcP) {
      funcP.resTP().ifPresent(this::visitType);
      visitParams(funcP.params());

      funcP.body().ifPresent(body -> {
        var objLoader = new PsConverter(bindings);
        OptionalBindings<RefableS> bindingsInFuncBody = newOptionalBindings(bindings);
        funcP.params().list().forEach(p -> bindingsInFuncBody.add(p.name(), objLoader.convertParam(p)));
        new CreateTopObjsVisitor(path, types, bindingsInFuncBody, logBuffer)
            .visitObj(body);
      });
      var resT = evalTOfTopEval(funcP);
      var paramTs = funcP.paramTs();
      var funcT = resT.flatMap(r -> paramTs.map(ps -> newFuncTS(r, ps)));
      funcP.setTypeS(funcT);
      var funcS = funcT.flatMap(t -> new PsConverter(bindings).convertFunc(path, funcP));
      bindings.add(funcP.name(), funcS);
    }

    private static FuncTS newFuncTS(MonoTS res, ImmutableList<MonoTS> params) {
      return varSetS(params).isEmpty() ? TypeFS.func(res, params) : TypeFS.polyFunc(res, params);
    }

    @Override
    public void visitValue(ValP valP) {
      valP.typeP().ifPresent(this::visitType);
      valP.body().ifPresent(this::visitObj);
      var valT = evalTOfTopEval(valP);
      valP.setTypeS(valT);
      var funcS = valT.flatMap(t -> new PsConverter(bindings).convertVal(path, valP));
      bindings.add(valP.name(), funcS);
    }

    @Override
    public void visitParam(int index, ItemP param) {
      super.visitParam(index, param);
      param.setTypeS(typeOfParam(param));
    }

    private Optional<MonoTS> typeOfParam(ItemP param) {
      return evalTypeOf(
          param,
          (bodyT, targetT) -> logError(param, "Parameter " + param.q() + " is of type "
              + targetT.q() + " so it cannot have default argument of type " + bodyT.q() + "."));
    }

    private Optional<MonoTS> evalTOfTopEval(TopRefableP refableP) {
      return evalTypeOf(
          refableP,
          (bodyT, targetT) -> bodyConversionError(refableP, bodyT, targetT));
    }

    private void bodyConversionError(NamedP refableP, TypeS sourceT, MonoTS targetT) {
      logError(refableP, refableP.q() + " has body which type is " + sourceT.q()
          + " and it is not convertible to its declared type " + targetT.q() + ".");
    }

    private Optional<MonoTS> inferMonoizedBodyT(TypeS bodyT, MonoTS targetT) {
      var mappedBodyT = bodyT.mapFreeVars(v -> v.prefixed("body"));
      var solver = new ConstrSolver();
      try {
        solver.addConstr(constrS(mappedBodyT, targetT));
      } catch (ConstrDecomposeExc e) {
        return empty();
      }
      var constrGraph = solver.graph();
      var denormalizer = new Denormalizer(constrGraph);
      var typeS = denormalize(denormalizer, mappedBodyT);
      if (typeS.includes(TypeFS.any())) {
        return empty();
      }
      return Optional.of(typeS);
    }

    private MonoTS denormalize(Denormalizer denormalizer, MonoTS typeS) {
      return denormalizer.denormalizeVars(typeS, LOWER);
    }

    private Optional<MonoTS> evalTypeOf(
        RefableP refable, BiConsumer<TypeS, MonoTS> bodyAssignmentErrorReporter) {
      if (evalTHasProblems(refable)) {
        return empty();
      }

      if (refable.body().isPresent()) {
        var body = refable.body().get();
        var bodyT = body.typeS();
        if (refable.evalT().isPresent()) {
          var evalTS = convertTypePToS(refable.evalT().get());
          evalTS.ifPresent(targetT -> bodyT.ifPresent(sourceT -> {
            var inferredT = inferMonoizedBodyT(sourceT, targetT);
            if (inferredT.isEmpty()) {
              bodyAssignmentErrorReporter.accept(sourceT, targetT);
            } else if (body instanceof RefP refP && bodyT.get() instanceof PolyTS) {
              refP.setInferredMonoType(inferredT.get());
            }
          }));
          return evalTS;
        } else {
          return bodyT.flatMap((TypeS t) -> switch (t) {
            case MonoTS monoTS -> Optional.of(monoTS);
            case PolyTS polyTS -> {
              logError(refable, ("Cannot infer type parameters to convert function reference "
                  + "%s to monomorphic function. You need to specify type of %s explicitly.")
                  .formatted(q(((RefP) body).name()), refable.q()));
              yield empty();
            }
            case default -> throw unexpectedCaseExc(t);
          });
        }
      } else {
        return convertTypePToS(refable.evalT().get());
      }
    }

    private boolean evalTHasProblems(RefableP refable) {
      if (refable.evalT().isEmpty()) {
        return false;
      }
      TypeP typeP = refable.evalT().get();
      Optional<? extends MonoTS> typeS = typeP.typeS();
      if (typeS.isEmpty()) {
        return true;
      }
      VarSetS evalTVars = typeS.get().vars();
      return switch (refable) {
        case ItemP itemP -> false;
        case FuncP funcP -> !funcP.paramTs().isPresent();
        case ValP valP -> evalTypeHasVars(typeP, evalTVars);
      };
    }

    private boolean evalTypeHasVars(TypeP typeP, VarSetS evalTVars) {
      if (evalTVars.isEmpty()) {
        return false;
      }
      logUnknownVars(typeP, evalTVars.asList());
      return false;
    }

    private void logUnknownVars(TypeP typeP, ImmutableList<VarS> unknownVars) {
      logError(typeP, "Unknown type variable(s): " + toCommaSeparatedString(unknownVars));
    }

    @Override
    public void visitType(TypeP type) {
      super.visitType(type);
      type.setTypeS(convertTypePToS(type));
    }

    public Optional<MonoTS> convertTypePToS(TypeP type) {
      if (isVarName(type.name())) {
        return Optional.of(TypeFS.var(type.name()));
      }
      return switch (type) {
        case ArrayTP array -> convertTypePToS(array.elemT()).map(TypeFS::array);
        case FuncTP func -> {
          var resultOpt = convertTypePToS(func.resT());
          var paramsOpt = pullUp(map(func.paramTs(), this::convertTypePToS));
          if (resultOpt.isEmpty() || paramsOpt.isEmpty()) {
            yield empty();
          }
          yield Optional.of(TypeFS.func(resultOpt.get(), paramsOpt.get()));
        }
        default -> types.get(type.name()).value().map(Tapanal::type);
      };
    }

    @Override
    public void visitSelect(SelectP select) {
      super.visitSelect(select);
      select.selectable().typeS().ifPresentOrElse(
          t -> {
            if (!(t instanceof StructTS st)) {
              select.setTypeS(empty());
              logError(select, "Type " + t.q() + " is not a struct so it doesn't have "
                  + q(select.field()) + " field.");
            } else if (!st.fields().containsName(select.field())) {
              select.setTypeS(empty());
              logError(select,
                  "Struct " + t.q() + " doesn't have field `" + select.field() + "`.");
            } else {
              select.setTypeS(((StructTS) t).fields().get(select.field()).type());
            }
          },
          () -> select.setTypeS(empty())
      );
    }

    @Override
    public void visitOrder(OrderP order) {
      super.visitOrder(order);
      order.setTypeS(findArrayT(order));
    }

    private Optional<MonoTS> findArrayT(OrderP array) {
      List<ObjP> expressions = array.elems();
      if (expressions.isEmpty()) {
        return Optional.of(TypeFS.array(TypeFS.nothing()));
      }

      var map = map(expressions, Parsed::typeS);
      var elemTsOpt = pullUp(map);
      if (elemTsOpt.isEmpty()) {
        return empty();
      }
      var elemTs = elemTsOpt.get();
      if (!elemTs.isEmpty() && elemTs.stream().allMatch(t -> t instanceof PolyTS)) {
        ObjP firstElem = expressions.get(0);
        String funcName = q(((RefP) firstElem).name());
        logError(firstElem, "Cannot infer type parameters to convert function reference "
            +  funcName + " to monomorphic function.");
        return empty();
      }
      var prefixedElemTs = prefixFreeVarsWithIndex(elemTs);
      var solver = new ConstrSolver();

      var a = TypeFS.var("array.A");
      for (MonoTS prefixedElemT : prefixedElemTs) {
        try {
          solver.addConstr(constrS(prefixedElemT, a));
        } catch (ConstrDecomposeExc e) {
          arrayElemError(array);
          return empty();
        }
      }

      var constrGraph = solver.graph();
      var denormalizer = new Denormalizer(constrGraph);
      var inferredElemT = denormalize(denormalizer, a);
      for (int i = 0; i < prefixedElemTs.size(); i++) {
        storeActualTypeIfNeeded(expressions.get(i), inferredElemT, denormalizer);
      }
      if (inferredElemT.includes(TypeFS.any())) {
        arrayElemError(array);
        return empty();
      }
      return Optional.of(TypeFS.array(inferredElemT));
    }

    private void arrayElemError(OrderP array) {
      logError(array, "Array elements don't have common super type.");
    }

    private void storeActualTypeIfNeeded(ObjP objP, MonoTS monoTS, Denormalizer denormalizer) {
      if (objP instanceof RefP refP && referenced(refP).get().type() instanceof PolyTS) {
        refP.setInferredMonoType(denormalize(denormalizer, monoTS));
      }
    }

    @Override
    public void visitCall(CallP call) {
      super.visitCall(call);
      call.setTypeS(new CallTypeInferrer(call, bindings, logBuffer).inferCallT());
    }

    @Override
    public void visitRef(RefP ref) {
      super.visitRef(ref);
      ref.setTypeS(referenced(ref).map(ObjS::type));
    }

    @Override
    public void visitArg(ArgP arg) {
      super.visitArg(arg);
      arg.setTypeS(arg.obj().typeS());
    }

    private Optional<? extends RefableS> referenced(RefP ref) {
      var bound = bindings.get(ref.name());
      if (bound.isMissing()) {
        logError(ref, "`" + ref.name() + "` is undefined.");
      }
      return bound.toOptional();
    }

    private void logError(Parsed parsed, String message) {
      logBuffer.log(parseError(parsed, message));
    }
  }
}
