package org.smoothbuild.parse;

import static java.util.Optional.empty;
import static org.smoothbuild.lang.type.ConstrS.constrS;
import static org.smoothbuild.lang.type.Side.LOWER;
import static org.smoothbuild.lang.type.TNamesS.isVarName;
import static org.smoothbuild.lang.type.TypeS.prefixFreeVarsWithIndex;
import static org.smoothbuild.lang.type.VarSetS.varSetS;
import static org.smoothbuild.parse.ParseError.parseError;
import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.filter;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;
import static org.smoothbuild.util.collect.NList.nList;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.inject.Inject;

import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.like.Obj;
import org.smoothbuild.lang.type.FuncTS;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.PolyTS;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.TypeFS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.TypingS;
import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.lang.type.VarSetS;
import org.smoothbuild.lang.type.solver.ConstrDecomposeExc;
import org.smoothbuild.lang.type.solver.DenormalizerS;
import org.smoothbuild.lang.type.solver.SolverS;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
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

import com.google.common.collect.ImmutableList;

public class TypeInferrer {
  private final TypeFS typeFS;
  private final TypingS typing;
  private final CallTypeInferrer callTypeInferrer;

  @Inject
  public TypeInferrer(TypeFS typeFS, TypingS typing) {
    this.typeFS = typeFS;
    this.typing = typing;
    this.callTypeInferrer = new CallTypeInferrer(typeFS, typing);
  }

  public List<Log> inferTypes(Ast ast, DefsS imported) {
    var logBuffer = new LogBuffer();

    new AstVisitor() {
      @Override
      public void visitStruct(StructP struct) {
        super.visitStruct(struct);
        var fields = pullUp(map(struct.fields(), ItemP::sig));
        struct.setTypeO(fields.map(f -> typeFS.struct(struct.name(), nList(f))));
        struct.ctor().setTypeO(
            fields.map(s -> typeFS.func(struct.typeO().get(), map(s, ItemSigS::type))));
      }

      @Override
      public void visitField(ItemP itemP) {
        super.visitField(itemP);
        var typeOpt = itemP.typeN().typeO();
        typeOpt.flatMap((t) -> {
          if (!t.vars().isEmpty()) {
            var message = "Field type cannot be polymorphic. Found field %s with type %s."
                .formatted(itemP.q(), t.q());
            logError(itemP, message);
            return empty();
          } else {
            return Optional.of(t);
          }
        });

        itemP.setTypeO(typeOpt);
      }

      @Override
      public void visitFunc(FuncP funcP) {
        funcP.resTN().ifPresent(this::visitType);
        visitParams(funcP.params());
        funcP.body().ifPresent(this::visitObj);
        var resN = funcP.resTN().orElse(null);
        funcP.setTypeO(funcTOpt(resN, evalTOfTopEval(funcP), funcP.paramTs()));
      }

      private Optional<FuncTS> funcTOpt(TypeP resN, Optional<MonoTS> result,
          Optional<ImmutableList<MonoTS>> params) {
        if (result.isEmpty() || params.isEmpty()) {
          return empty();
        }
        var ps = params.get();
        var paramVars = varSetS(params.get());
        var r = result.get();
        if (paramVars.containsAll(r.vars())) {
          return Optional.of(paramVars.isEmpty() ? typeFS.func(r, ps) :  typeFS.polyFunc(r, ps));
        }
        logError(
            resN, "Function result type has type variable(s) not present in any parameter type.");
        return empty();
      }

      @Override
      public void visitValue(ValP valP) {
        valP.typeN().ifPresent(this::visitType);
        valP.body().ifPresent(this::visitObj);
        valP.setTypeO(evalTOfTopEval(valP));
      }

      @Override
      public void visitParam(int index, ItemP param) {
        super.visitParam(index, param);
        param.setTypeO(typeOfParam(param));
      }

      private Optional<MonoTS> typeOfParam(ItemP param) {
        return evalTypeOf(
            param,
            (bodyT, targetT) -> logError(param, "Parameter " + param.q() + " is of type "
                + targetT.q() + " so it cannot have default argument of type " + bodyT.q() + "."));
      }

      private Optional<MonoTS> evalTOfTopEval(TopRefableP refableN) {
        return evalTypeOf(
            refableN,
            (bodyT, targetT) -> bodyConversionError(refableN, bodyT, targetT));
      }

      private void bodyConversionError(NamedP refableN, TypeS sourceT, MonoTS targetT) {
        logError(refableN, refableN.q() + " has body which type is " + sourceT.q()
            + " and it is not convertible to its declared type " + targetT.q() + ".");
      }

      private Optional<MonoTS> inferMonoizedBodyT(TypeS bodyT, MonoTS targetT) {
        var mappedBodyT = bodyT.mapFreeVars(v -> v.prefixed("body"));
        var solver = new SolverS(typeFS);
        try {
          solver.addConstr(constrS(mappedBodyT, targetT));
        } catch (ConstrDecomposeExc e) {
          return empty();
        }
        var constrGraph = solver.graph();
        var denormalizer = new DenormalizerS(typeFS, constrGraph);
        var typeS = denormalizeAndResolveMerges(denormalizer, mappedBodyT);
        if (typeS.includes(typeFS.any())) {
          return empty();
        }
        return Optional.of(typeS);
      }

      private MonoTS denormalizeAndResolveMerges(DenormalizerS denormalizer, MonoTS typeS) {
        var denormalizedT = denormalizer.denormalizeVars(typeS, LOWER);
        return typing.resolveMerges(denormalizedT);
      }

      private Optional<MonoTS> evalTypeOf(
          RefableP refable, BiConsumer<TypeS, MonoTS> bodyAssignmentErrorReporter) {
        if (evalTHasProblems(refable)) {
          return empty();
        }

        if (refable.body().isPresent()) {
          var body = refable.body().get();
          var bodyT = body.typeO();
          if (refable.evalT().isPresent()) {
            var evalTS = createType(refable.evalT().get());
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
                    + "%s to monomorphic function. You need to specify type of " + refable.q()
                    + " explicitly.").formatted(((RefP) body).referenced().q()));
                yield empty();
              }
              case default -> throw unexpectedCaseExc(t);
            });
          }
        } else {
          return createType(refable.evalT().get());
        }
      }

      private boolean evalTHasProblems(RefableP refable) {
        if (refable.evalT().isEmpty()) {
          return false;
        }
        TypeP typeP = refable.evalT().get();
        Optional<MonoTS> typeS = typeP.typeO();
        if (typeS.isEmpty()) {
          return true;
        }
        VarSetS evalTVars = typeS.get().vars();
        return switch (refable) {
          case ItemP itemP -> false;
          case FuncP funcP -> !evalTypeVarsArePresentInParameters(funcP, typeP, evalTVars);
          case ValP valP -> evalTypeHasVars(typeP, evalTVars);
        };
      }

      private boolean evalTypeVarsArePresentInParameters(FuncP funcP,
          TypeP typeP, VarSetS evalTVars) {
        if (funcP.paramTs().isEmpty()) {
          return false;
        }
        var paramVars = varSetS(funcP.paramTs().get());
        var unknownVars = filter(evalTVars.asList(), var -> !paramVars.contains(var));
        if (!unknownVars.isEmpty()) {
          logUnknownVars(typeP, unknownVars);
          return false;
        }
        return true;
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
        type.setTypeO(createType(type));
      }

      private Optional<MonoTS> createType(TypeP type) {
        if (isVarName(type.name())) {
          return Optional.of(typeFS.var(type.name()));
        }
        return switch (type) {
          case ArrayTP array -> createType(array.elemT()).map(typeFS::array);
          case FuncTP func -> {
            var resultOpt = createType(func.resT());
            var paramsOpt = pullUp(map(func.paramTs(), this::createType));
            if (resultOpt.isEmpty() || paramsOpt.isEmpty()) {
              yield empty();
            }
            yield Optional.of(typeFS.func(resultOpt.get(), paramsOpt.get()));
          }
          default -> Optional.of(findType(type.name()));
        };
      }

      private MonoTS findType(String name) {
        var typeDef = imported.tDefs().get(name);
        if (typeDef != null) {
          return typeDef.type();
        } else {
          return findLocalType(name);
        }
      }

      private MonoTS findLocalType(String name) {
        StructP localStruct = ast.structs().get(name);
        if (localStruct == null) {
          throw new RuntimeException(
              "Cannot find type `" + name + "`. Available types = " + ast.structs());
        } else {
          return localStruct.typeO().orElseThrow(() -> new RuntimeException(
              "Cannot find type `" + name + "`. Available types = " + ast.structs()));
        }
      }

      @Override
      public void visitSelect(SelectP select) {
        super.visitSelect(select);
        select.selectable().typeO().ifPresentOrElse(
            t -> {
              if (!(t instanceof StructTS st)) {
                select.setTypeO(empty());
                logError(select, "Type " + t.q() + " is not a struct so it doesn't have "
                    + q(select.field()) + " field.");
              } else if (!st.fields().containsName(select.field())) {
                select.setTypeO(empty());
                logError(select,
                    "Struct " + t.q() + " doesn't have field `" + select.field() + "`.");
              } else {
                select.setTypeO(((StructTS) t).fields().get(select.field()).type());
              }
            },
            () -> select.setTypeO(empty())
        );
      }

      @Override
      public void visitOrder(OrderP order) {
        super.visitOrder(order);
        order.setTypeO(findArrayT(order));
      }

      private Optional<MonoTS> findArrayT(OrderP array) {
        List<ObjP> expressions = array.elems();
        if (expressions.isEmpty()) {
          return Optional.of(typeFS.array(typeFS.nothing()));
        }

        var map = map(expressions, Parsed::typeO);
        var elemTsOpt = pullUp(map);
        if (elemTsOpt.isEmpty()) {
          return empty();
        }
        var elemTs = elemTsOpt.get();
        if (!elemTs.isEmpty() && elemTs.stream().allMatch(t -> t instanceof PolyTS)) {
          ObjP firstElem = expressions.get(0);
          String funcName = ((RefP) firstElem).referenced().q();
          logError(firstElem, "Cannot infer type parameters to convert function reference "
              +  funcName + " to monomorphic function.");
          return empty();
        }
        var prefixedElemTs = prefixFreeVarsWithIndex(elemTs);
        var solver = new SolverS(typeFS);

        var a = typeFS.var("array.A");
        for (MonoTS prefixedElemT : prefixedElemTs) {
          try {
            solver.addConstr(constrS(prefixedElemT, a));
          } catch (ConstrDecomposeExc e) {
            arrayElemError(array);
            return empty();
          }
        }

        var constrGraph = solver.graph();
        var denormalizer = new DenormalizerS(typeFS, constrGraph);
        var inferredElemT = denormalizeAndResolveMerges(denormalizer, a);
        for (int i = 0; i < prefixedElemTs.size(); i++) {
          storeActualTypeIfNeeded(expressions.get(i), inferredElemT, denormalizer);
        }
        if (inferredElemT.includes(typeFS.any())) {
          arrayElemError(array);
          return empty();
        }
        return Optional.of(typeFS.array(inferredElemT));
      }

      private void arrayElemError(OrderP array) {
        logError(array, "Array elements don't have common super type.");
      }

      private void storeActualTypeIfNeeded(Obj obj, MonoTS monoTS, DenormalizerS denormalizer) {
        if (obj instanceof RefP refP && refP.referenced().typeO().get() instanceof PolyTS) {
          refP.setInferredMonoType(denormalizeAndResolveMerges(denormalizer, monoTS));
        }
      }

      @Override
      public void visitCall(CallP call) {
        super.visitCall(call);
        call.setTypeO(callTypeInferrer.inferCallT(call, logBuffer));
      }

      @Override
      public void visitRef(RefP ref) {
        super.visitRef(ref);
        ref.setTypeO(ref.referenced().typeO());
      }

      @Override
      public void visitArg(ArgP arg) {
        super.visitArg(arg);
        arg.setTypeO(arg.obj().typeO());
      }

      private void logError(Parsed parsed, String message) {
        logBuffer.log(parseError(parsed, message));
      }
    }.visitAst(ast);
    return logBuffer.toList();
  }
}
