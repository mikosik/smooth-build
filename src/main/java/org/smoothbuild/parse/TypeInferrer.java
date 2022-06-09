package org.smoothbuild.parse;

import static java.util.Optional.empty;
import static org.smoothbuild.lang.define.ItemSigS.itemSigS;
import static org.smoothbuild.lang.type.TNamesS.isVarName;
import static org.smoothbuild.lang.type.VarSetS.toVarSetS;
import static org.smoothbuild.parse.ConstructArgList.constructArgList;
import static org.smoothbuild.parse.ParseError.parseError;
import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.inject.Inject;

import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.define.PolyFuncS;
import org.smoothbuild.lang.like.Param;
import org.smoothbuild.lang.like.Refable;
import org.smoothbuild.lang.type.FuncTS;
import org.smoothbuild.lang.type.PolyFuncTS;
import org.smoothbuild.lang.type.PolyTS;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.TKind;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.TypeSF;
import org.smoothbuild.lang.type.TypingS;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.parse.ast.ArgN;
import org.smoothbuild.parse.ast.ArrayTN;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.AstNode;
import org.smoothbuild.parse.ast.AstVisitor;
import org.smoothbuild.parse.ast.CallN;
import org.smoothbuild.parse.ast.FuncN;
import org.smoothbuild.parse.ast.FuncTN;
import org.smoothbuild.parse.ast.ItemN;
import org.smoothbuild.parse.ast.ObjN;
import org.smoothbuild.parse.ast.OrderN;
import org.smoothbuild.parse.ast.RefN;
import org.smoothbuild.parse.ast.RefableN;
import org.smoothbuild.parse.ast.RefableObjN;
import org.smoothbuild.parse.ast.SelectN;
import org.smoothbuild.parse.ast.StructN;
import org.smoothbuild.parse.ast.TypeN;
import org.smoothbuild.parse.ast.ValN;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class TypeInferrer {
  private final TypeSF typeSF;
  private final TypingS typing;
  private final CallTypeInferrer callTypeInferrer;

  @Inject
  public TypeInferrer(TypeSF typeSF, TypingS typing) {
    this.typeSF = typeSF;
    this.typing = typing;
    this.callTypeInferrer = new CallTypeInferrer(typeSF, typing);
  }

  public List<Log> inferTypes(Ast ast, DefsS imported) {
    var logBuffer = new LogBuffer();

    new AstVisitor() {
      @Override
      public void visitStruct(StructN struct) {
        super.visitStruct(struct);
        var fields = pullUp(map(struct.fields(), ItemN::sig));
        struct.setTypeO(fields.map(f -> typeSF.struct(struct.name(), nList(f))));
        struct.ctor().setTypeO(
            fields.map(s -> typeSF.polyFunc(struct.typeO().get(), map(s, ItemSigS::type))));
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
        visitParams(funcN.params());
        funcN.body().ifPresent(this::visitObj);
        var resN = funcN.resTN().orElse(null);
        funcN.setTypeO(funcTOpt(resN, evalTOfTopEval(funcN), funcN.paramTSs()));
      }

      private Optional<PolyTS> funcTOpt(TypeN resN, Optional<TypeS> result,
          Optional<ImmutableList<TypeS>> params) {
        if (result.isEmpty() || params.isEmpty()) {
          return empty();
        }
        var ps = params.get();
        var paramVars = ps.stream()
            .flatMap(t -> t.vars().stream())
            .collect(toVarSetS());
        var r = result.get();
        if (paramVars.containsAll(r.vars())) {
          return Optional.of(typeSF.polyFunc(r, ps));
        }
        logError(
            resN, "Function result type has type variable(s) not present in any parameter type.");
        return empty();
      }

      @Override
      public void visitValue(ValN valN) {
        valN.body().ifPresent(this::visitObj);
        valN.setTypeO(evalTOfTopEval(valN));
      }

      @Override
      public void visitParam(int index, ItemN param) {
        super.visitParam(index, param);
        param.setTypeO(typeOfParam(param));
      }

      private Optional<TypeS> typeOfParam(ItemN param) {
        return evalTypeOf(param, (target, source) -> {
          if (!typing.isParamAssignable(target, source)) {
            logError(param, "Parameter " + param.q() + " is of type " + target.q()
                + " so it cannot have default argument of type " + source.q() + ".");
          }
        });
      }

      private Optional<TypeS> evalTOfTopEval(RefableObjN refableN) {
        return evalTypeOf(refableN, (target, source) -> {
          if (!typing.isAssignable(target, source)) {
            logError(refableN, "`" + refableN.name() + "` has body which type is " + source.q()
                + " and it is not convertible to its declared type " + target.q() + ".");
          }
        });
      }

      private Optional<TypeS> evalTypeOf(
          RefableN refable, BiConsumer<TypeS, TypeS> assignmentChecker) {
        if (refable.body().isPresent()) {
          var exprT = refable.body().get().typeO();
          if (refable.evalTN().isPresent()) {
            var type = createType(refable.evalTN().get());
            type.ifPresent(target -> exprT.ifPresent(source -> {
              assignmentChecker.accept(target, TKind.hackyCast(source));
            }));
            return type;
          } else {
            return exprT.map(TKind::hackyCast);
          }
        } else {
          return createType(refable.evalTN().get());
        }
      }

      @Override
      public void visitType(TypeN type) {
        super.visitType(type);
        type.setTypeO(createType(type));
      }

      private Optional<TypeS> createType(TypeN type) {
        if (isVarName(type.name())) {
          return Optional.of(typeSF.var(type.name()));
        }
        return switch (type) {
          case ArrayTN array -> createType(array.elemT()).map(typeSF::array);
          case FuncTN func -> {
            var resultOpt = createType(func.resT());
            var paramsOpt = pullUp(map(func.paramTs(), this::createType));
            if (resultOpt.isEmpty() || paramsOpt.isEmpty()) {
              yield empty();
            }
            yield Optional.of(typeSF.func(resultOpt.get(), paramsOpt.get()));
          }
          default -> Optional.of(findType(type.name()));
        };
      }

      private TypeS findType(String name) {
        var typeDef = imported.tDefs().get(name);
        if (typeDef != null) {
          return typeDef.type();
        } else {
          return findLocalType(name);
        }
      }

      private TypeS findLocalType(String name) {
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

      private Optional<TypeS> findArrayT(OrderN array) {
        List<ObjN> expressions = array.elems();
        if (expressions.isEmpty()) {
          return Optional.of(typeSF.array(typeSF.nothing()));
        }
        Optional<? extends TKind> firstType = expressions.get(0).typeO();
        if (firstType.isEmpty()) {
          return empty();
        }

        TypeS type = TKind.hackyCast(firstType.get());
        for (int i = 1; i < expressions.size(); i++) {
          ObjN elem = expressions.get(i);
          Optional<? extends TKind> elemT = elem.typeO();
          if (elemT.isEmpty()) {
            return empty();
          }
          type = typing.mergeUp(type, TKind.hackyCast(elemT.get()));
          if (typing.contains(type, typeSF.any())) {
            logError(elem,
                "Array elems at indexes 0 and " + i + " doesn't have common super type."
                + "\nElement at index 0 type = " + expressions.get(0).typeO().get().q()
                + "\nElement at index " + i + " type = " + elemT.get().q());
            return empty();
          }
        }
        return Optional.of(typeSF.array(type));
      }

      @Override
      public void visitCall(CallN call) {
        super.visitCall(call);
        ObjN callee = call.callee();
        Optional<? extends TKind> calledT = callee.typeO();
        if (calledT.isEmpty()) {
          call.setTypeO(empty());
        } else if (!(calledT.get() instanceof FuncTS || calledT.get() instanceof PolyFuncTS)) {
          logError(call, description(callee) + " cannot be called as it is not a function but "
              + calledT.get().q() + ".");
          call.setTypeO(empty());
        } else {
          var funcParams = funcParams(callee);
          if (funcParams.isEmpty()) {
            call.setTypeO(empty());
          } else {
            var params = funcParams.get();
            Maybe<ImmutableList<ArgN>> args = constructArgList(call, params);
            if (args.containsProblem()) {
              logBuffer.logAll(args.logs());
              call.setTypeO(empty());
            } else if (someArgHasNotInferredType(args.value())) {
              call.setTypeO(empty());
            } else {
              call.setAssignedArgs(args.value());
              Maybe<TypeS> type = callTypeInferrer.inferCallT(call, castToFuncTS(calledT.get()).res(), params);
              logBuffer.logAll(type.logs());
              call.setTypeO(type.valueOptional());
            }
          }
        }
      }

      public static Optional<NList<Param>> funcParams(ObjN callee) {
        if (callee instanceof RefN refN) {
          Refable referenced = refN.referenced();
          if (referenced instanceof PolyFuncS polyFuncS) {
            var funcS = polyFuncS.func();
            return Optional.of(funcS.params().map(p -> new Param(p.sig(), p.body())));
          } else if (referenced instanceof FuncN funcN) {
            var params = map(
                funcN.params().list(), p -> p.sig().map(sig -> new Param(sig, p.body())));
            return pullUp(params).map(NList::nList);
          }
        }
        return callee.typeO().map(t -> funcTParams(t));
      }

      private static NList<Param> funcTParams(TKind funcType) {
        return nList(map(castToFuncTS(funcType).params(), p -> new Param(itemSigS(p), empty())));
      }

      private static boolean someArgHasNotInferredType(ImmutableList<ArgN> args) {
        return args.stream()
            .anyMatch(a -> a.typeO().isEmpty());
      }

      private static String description(ObjN node) {
        if (node instanceof RefN refN) {
          return "`" + refN.name() + "`";
        }
        return "expression";
      }

      @Override
      public void visitRef(RefN ref) {
        super.visitRef(ref);
        ref.setTypeO(ref.referencedType());
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

  private static FuncTS castToFuncTS(TKind funcType) {
    // TODO handle polymorphic correctly
    return (FuncTS) TKind.hackyCast(funcType);
  }
}
