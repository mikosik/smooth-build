package org.smoothbuild.parse;

import static java.util.Optional.empty;
import static org.smoothbuild.lang.type.ConstrS.constrS;
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
import static org.smoothbuild.util.type.Side.LOWER;

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
import org.smoothbuild.parse.ast.ArgN;
import org.smoothbuild.parse.ast.ArrayTN;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.AstNode;
import org.smoothbuild.parse.ast.AstVisitor;
import org.smoothbuild.parse.ast.CallN;
import org.smoothbuild.parse.ast.FuncN;
import org.smoothbuild.parse.ast.FuncTN;
import org.smoothbuild.parse.ast.ItemN;
import org.smoothbuild.parse.ast.NamedN;
import org.smoothbuild.parse.ast.ObjN;
import org.smoothbuild.parse.ast.OrderN;
import org.smoothbuild.parse.ast.RefN;
import org.smoothbuild.parse.ast.RefableN;
import org.smoothbuild.parse.ast.SelectN;
import org.smoothbuild.parse.ast.StructN;
import org.smoothbuild.parse.ast.TopRefableN;
import org.smoothbuild.parse.ast.TypeN;
import org.smoothbuild.parse.ast.ValN;

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
      public void visitStruct(StructN struct) {
        super.visitStruct(struct);
        var fields = pullUp(map(struct.fields(), ItemN::sig));
        struct.setTypeO(fields.map(f -> typeFS.struct(struct.name(), nList(f))));
        struct.ctor().setTypeO(
            fields.map(s -> typeFS.func(struct.typeO().get(), map(s, ItemSigS::type))));
      }

      @Override
      public void visitField(ItemN itemN) {
        super.visitField(itemN);
        var typeOpt = itemN.typeN().typeO();
        typeOpt.flatMap((t) -> {
          if (!t.vars().isEmpty()) {
            var message = "Field type cannot be polymorphic. Found field %s with type %s."
                .formatted(itemN.q(), t.q());
            logError(itemN, message);
            return empty();
          } else {
            return Optional.of(t);
          }
        });

        itemN.setTypeO(typeOpt);
      }

      @Override
      public void visitFunc(FuncN funcN) {
        funcN.resTN().ifPresent(this::visitType);
        visitParams(funcN.params());
        funcN.body().ifPresent(this::visitObj);
        var resN = funcN.resTN().orElse(null);
        funcN.setTypeO(funcTOpt(resN, evalTOfTopEval(funcN), funcN.paramTs()));
      }

      private Optional<FuncTS> funcTOpt(TypeN resN, Optional<MonoTS> result,
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
      public void visitValue(ValN valN) {
        valN.typeN().ifPresent(this::visitType);
        valN.body().ifPresent(this::visitObj);
        valN.setTypeO(evalTOfTopEval(valN));
      }

      @Override
      public void visitParam(int index, ItemN param) {
        super.visitParam(index, param);
        param.setTypeO(typeOfParam(param));
      }

      private Optional<MonoTS> typeOfParam(ItemN param) {
        return evalTypeOf(
            param,
            (bodyT, targetT) -> logError(param, "Parameter " + param.q() + " is of type "
                + targetT.q() + " so it cannot have default argument of type " + bodyT.q() + "."));
      }

      private Optional<MonoTS> evalTOfTopEval(TopRefableN refableN) {
        return evalTypeOf(
            refableN,
            (bodyT, targetT) -> bodyConversionError(refableN, bodyT, targetT));
      }

      private void bodyConversionError(NamedN refableN, TypeS sourceT, MonoTS targetT) {
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
          RefableN refable, BiConsumer<TypeS, MonoTS> bodyAssignmentErrorReporter) {
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
              } else if (body instanceof RefN refN && bodyT.get() instanceof PolyTS) {
                refN.setInferredMonoType(inferredT.get());
              }
            }));
            return evalTS;
          } else {
            return bodyT.flatMap((TypeS t) -> switch (t) {
              case MonoTS monoTS -> Optional.of(monoTS);
              case PolyTS polyTS -> {
                logError(refable, ("Cannot infer type parameters to convert function reference "
                    + "%s to monomorphic function. You need to specify type of " + refable.q()
                    + " explicitly.").formatted(((RefN) body).referenced().q()));
                yield empty();
              }
              case default -> throw unexpectedCaseExc(t);
            });
          }
        } else {
          return createType(refable.evalT().get());
        }
      }

      private boolean evalTHasProblems(RefableN refable) {
        if (refable.evalT().isEmpty()) {
          return false;
        }
        TypeN typeN = refable.evalT().get();
        Optional<MonoTS> typeS = typeN.typeO();
        if (typeS.isEmpty()) {
          return true;
        }
        VarSetS evalTVars = typeS.get().vars();
        return switch (refable) {
          case ItemN itemN -> false;
          case FuncN funcN -> !evalTypeVarsArePresentInParameters(funcN, typeN, evalTVars);
          case ValN valN -> evalTypeHasVars(typeN, evalTVars);
        };
      }

      private boolean evalTypeVarsArePresentInParameters(FuncN funcN,
          TypeN typeN, VarSetS evalTVars) {
        if (funcN.paramTs().isEmpty()) {
          return false;
        }
        var paramVars = varSetS(funcN.paramTs().get());
        var unknownVars = filter(evalTVars.asList(), var -> !paramVars.contains(var));
        if (!unknownVars.isEmpty()) {
          logUnknownVars(typeN, unknownVars);
          return false;
        }
        return true;
      }

      private boolean evalTypeHasVars(TypeN typeN, VarSetS evalTVars) {
        if (evalTVars.isEmpty()) {
          return false;
        }
        logUnknownVars(typeN, evalTVars.asList());
        return false;
      }

      private void logUnknownVars(TypeN typeN, ImmutableList<VarS> unknownVars) {
        logError(typeN, "Unknown type variable(s): " + toCommaSeparatedString(unknownVars));
      }

      @Override
      public void visitType(TypeN type) {
        super.visitType(type);
        type.setTypeO(createType(type));
      }

      private Optional<MonoTS> createType(TypeN type) {
        if (isVarName(type.name())) {
          return Optional.of(typeFS.var(type.name()));
        }
        return switch (type) {
          case ArrayTN array -> createType(array.elemT()).map(typeFS::array);
          case FuncTN func -> {
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
        StructN localStruct = ast.structs().get(name);
        if (localStruct == null) {
          throw new RuntimeException(
              "Cannot find type `" + name + "`. Available types = " + ast.structs());
        } else {
          return localStruct.typeO().orElseThrow(() -> new RuntimeException(
              "Cannot find type `" + name + "`. Available types = " + ast.structs()));
        }
      }

      @Override
      public void visitSelect(SelectN select) {
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
      public void visitOrder(OrderN order) {
        super.visitOrder(order);
        order.setTypeO(findArrayT(order));
      }

      private Optional<MonoTS> findArrayT(OrderN array) {
        List<ObjN> expressions = array.elems();
        if (expressions.isEmpty()) {
          return Optional.of(typeFS.array(typeFS.nothing()));
        }

        var map = map(expressions, AstNode::typeO);
        var elemTsOpt = pullUp(map);
        if (elemTsOpt.isEmpty()) {
          return empty();
        }
        var elemTs = elemTsOpt.get();
        if (!elemTs.isEmpty() && elemTs.stream().allMatch(t -> t instanceof PolyTS)) {
          ObjN firstElem = expressions.get(0);
          String funcName = ((RefN) firstElem).referenced().q();
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

      private void arrayElemError(OrderN array) {
        logError(array, "Array elements don't have common super type.");
      }

      private void storeActualTypeIfNeeded(Obj obj, MonoTS monoTS, DenormalizerS denormalizer) {
        if (obj instanceof RefN refN && refN.referenced().typeO().get() instanceof PolyTS) {
          refN.setInferredMonoType(denormalizeAndResolveMerges(denormalizer, monoTS));
        }
      }

      @Override
      public void visitCall(CallN call) {
        super.visitCall(call);
        call.setTypeO(callTypeInferrer.inferCallT(call, logBuffer));
      }

      @Override
      public void visitRef(RefN ref) {
        super.visitRef(ref);
        ref.setTypeO(ref.referenced().typeO());
      }

      @Override
      public void visitArg(ArgN arg) {
        super.visitArg(arg);
        arg.setTypeO(arg.obj().typeO());
      }

      private void logError(AstNode astNode, String message) {
        logBuffer.log(parseError(astNode, message));
      }
    }.visitAst(ast);
    return logBuffer.toList();
  }
}
