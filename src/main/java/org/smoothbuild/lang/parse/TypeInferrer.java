package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Optional.empty;
import static org.smoothbuild.lang.base.type.api.TypeNames.isVarName;
import static org.smoothbuild.lang.parse.InferArgsToParamsAssignment.inferArgsToParamsAssignment;
import static org.smoothbuild.lang.parse.ParseError.parseError;
import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.LogBuffer;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.lang.base.define.DefinedS;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.lang.base.define.FuncS;
import org.smoothbuild.lang.base.define.ItemS;
import org.smoothbuild.lang.base.define.ItemSigS;
import org.smoothbuild.lang.base.like.EvalLike;
import org.smoothbuild.lang.base.type.impl.FuncTS;
import org.smoothbuild.lang.base.type.impl.StructTS;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.base.type.impl.TypingS;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.ArrayN;
import org.smoothbuild.lang.parse.ast.ArrayTN;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.AstVisitor;
import org.smoothbuild.lang.parse.ast.BlobN;
import org.smoothbuild.lang.parse.ast.CallN;
import org.smoothbuild.lang.parse.ast.EvalN;
import org.smoothbuild.lang.parse.ast.ExprN;
import org.smoothbuild.lang.parse.ast.FuncN;
import org.smoothbuild.lang.parse.ast.FuncTN;
import org.smoothbuild.lang.parse.ast.IntN;
import org.smoothbuild.lang.parse.ast.ItemN;
import org.smoothbuild.lang.parse.ast.Node;
import org.smoothbuild.lang.parse.ast.RefN;
import org.smoothbuild.lang.parse.ast.SelectN;
import org.smoothbuild.lang.parse.ast.StringN;
import org.smoothbuild.lang.parse.ast.StructN;
import org.smoothbuild.lang.parse.ast.TypeN;
import org.smoothbuild.lang.parse.ast.ValN;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.util.collect.Optionals;

import com.google.common.collect.ImmutableList;

public class TypeInferrer {
  private final TypeFactoryS factory;
  private final TypingS typing;
  private final CallTypeInferrer callTypeInferrer;

  @Inject
  public TypeInferrer(TypeFactoryS factory, TypingS typing) {
    this.factory = factory;
    this.typing = typing;
    this.callTypeInferrer = new CallTypeInferrer(factory, typing);
  }

  public List<Log> inferTypes(Ast ast, DefsS imported) {
    var logBuffer = new LogBuffer();

    new AstVisitor() {
      @Override
      public void visitStruct(StructN struct) {
        super.visitStruct(struct);
        var fields = Optionals.pullUp(map(struct.fields(), ItemN::sig));
        struct.setType(fields.map(f -> factory.struct(struct.name(), nList(f))));
        struct.ctor().setType(
            fields.map(s -> factory.func(struct.type().get(), map(s, ItemSigS::type))));
      }

      @Override
      public void visitField(ItemN itemN) {
        super.visitField(itemN);
        var typeOpt = itemN.typeNode().get().type();
        typeOpt.flatMap((t) -> {
          if (t.isPolytype()) {
            var message = "Field type cannot be polymorphic. Found field %s with type %s."
                .formatted(itemN.q(), t.q());
            logError(itemN, message);
            return Optional.empty();
          } else {
            return Optional.of(t);
          }
        });

        itemN.setType(typeOpt);
      }

      @Override
      public void visitFunc(FuncN funcN) {
        visitParams(funcN.params());
        funcN.body().ifPresent(this::visitExpr);
        var resN = funcN.typeNode().orElse(null);
        funcN.setType(funcTOpt(resN, evalTOfTopEval(funcN), funcN.optParamTs()));
      }

      private Optional<TypeS> funcTOpt(TypeN resN, Optional<TypeS> result,
          Optional<ImmutableList<TypeS>> params) {
        if (result.isEmpty() || params.isEmpty()) {
          return empty();
        }
        var ps = params.get();
        var paramOpenVars = ps.stream()
            .flatMap(t -> t.openVars().stream())
            .collect(toImmutableSet());
        var r = result.get();
        if (paramOpenVars.containsAll(r.openVars())) {
          return Optional.of(factory.func(r, ps));
        }
        logError(
            resN, "Function result type has type variable(s) not present in any parameter type.");
        return empty();
      }

      @Override
      public void visitValue(ValN valN) {
        valN.body().ifPresent(this::visitExpr);
        valN.setType(evalTOfTopEval(valN));
      }

      @Override
      public void visitParam(int index, ItemN param) {
        super.visitParam(index, param);
        param.setType(typeOfParam(param));
      }

      private Optional<TypeS> typeOfParam(ItemN param) {
        return evalTypeOf(param, (target, source) -> {
          if (!typing.isParamAssignable(target, source)) {
            logError(param, "Parameter " + param.q() + " is of type " + target.q()
                + " so it cannot have default argument of type " + source.q() + ".");
          }
        });
      }

      private Optional<TypeS> evalTOfTopEval(EvalN evalN) {
        return evalTypeOf(evalN, (target, source) -> {
          if (!typing.isAssignable(target, source)) {
            logError(evalN, "`" + evalN.name() + "` has body which type is " + source.q()
                + " and it is not convertible to its declared type " + target.q() + ".");
          }
        });
      }

      private Optional<TypeS> evalTypeOf(EvalN eval, BiConsumer<TypeS, TypeS> assignmentChecker) {
        if (eval.body().isPresent()) {
          var exprT = eval.body().get().type();
          if (eval.typeNode().isPresent()) {
            var type = createType(eval.typeNode().get());
            type.ifPresent(target -> exprT.ifPresent(source -> {
              var targetInAssignment = eval instanceof FuncN ? typing.closeVars(target) : target;
              assignmentChecker.accept(targetInAssignment, source);
            }));
            return type;
          } else {
            return exprT.map(typing::openVars);
          }
        } else {
          if (eval.typeNode().isPresent()) {
            return createType(eval.typeNode().get());
          } else {
            logError(eval, eval.q() + " is native so it should have declared result type.");
            return empty();
          }
        }
      }

      @Override
      public void visitType(TypeN type) {
        super.visitType(type);
        type.setType(createType(type));
      }

      private Optional<TypeS> createType(TypeN type) {
        if (isVarName(type.name())) {
          return Optional.of(factory.oVar(type.name()));
        }
        return switch (type) {
          case ArrayTN array -> createType(array.elemT()).map(factory::array);
          case FuncTN func -> {
            var resultOpt = createType(func.resT());
            var paramsOpt = Optionals.pullUp(map(func.paramTs(), this::createType));
            if (resultOpt.isEmpty() || paramsOpt.isEmpty()) {
              yield empty();
            }
            yield Optional.of(factory.func(resultOpt.get(), paramsOpt.get()));
          }
          default -> Optional.of(findType(type.name()));
        };
      }

      private TypeS findType(String name) {
        DefinedS type = imported.types().get(name);
        if (type != null) {
          return type.type();
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
          return localStruct.type().orElseThrow(() -> new RuntimeException(
              "Cannot find type `" + name + "`. Available types = " + ast.structs()));
        }
      }

      @Override
      public void visitSelect(SelectN select) {
        super.visitSelect(select);
        select.selectable().type().ifPresentOrElse(
            t -> {
              if (!(t instanceof StructTS st)) {
                select.setType(empty());
                logError(select, "Type " + t.q() + " is not a struct so it doesn't have "
                    + q(select.field()) + " field.");
              } else if (!st.fields().containsName(select.field())) {
                select.setType(empty());
                logError(select,
                    "Struct " + t.q() + " doesn't have field `" + select.field() + "`.");
              } else {
                select.setType(((StructTS) t).fields().get(select.field()).type());
              }
            },
            () -> select.setType(empty())
        );
      }

      @Override
      public void visitArray(ArrayN array) {
        super.visitArray(array);
        array.setType(findArrayT(array));
      }

      private Optional<TypeS> findArrayT(ArrayN array) {
        List<ExprN> expressions = array.elems();
        if (expressions.isEmpty()) {
          return Optional.of(factory.array(factory.nothing()));
        }
        Optional<TypeS> firstType = expressions.get(0).type();
        if (firstType.isEmpty()) {
          return empty();
        }

        TypeS type = firstType.get();
        for (int i = 1; i < expressions.size(); i++) {
          ExprN elem = expressions.get(i);
          Optional<TypeS> elemT = elem.type();
          if (elemT.isEmpty()) {
            return empty();
          }
          type = typing.mergeUp(type, elemT.get());
          if (typing.contains(type, factory.any())) {
            logError(elem,
                "Array elems at indexes 0 and " + i + " doesn't have common super type."
                + "\nElement at index 0 type = " + expressions.get(0).type().get().q()
                + "\nElement at index " + i + " type = " + elemT.get().q());
            return empty();
          }
        }
        return Optional.of(factory.array(type));
      }

      @Override
      public void visitCall(CallN call) {
        super.visitCall(call);
        ExprN called = call.callable();
        Optional<TypeS> calledT = called.type();
        if (calledT.isEmpty()) {
          call.setType(empty());
        } else if (!(calledT.get() instanceof FuncTS funcT)) {
          logError(call, description(called) + " cannot be called as it is not a function but "
              + calledT.get().q() + ".");
          call.setType(empty());
        } else {
          var funcParams = funcParams(called);
          if (funcParams.isEmpty()) {
            call.setType(empty());
          } else {
            var params = funcParams.get();
            Maybe<List<Optional<ArgNode>>> args = inferArgsToParamsAssignment(call, params);
            if (args.containsProblem()) {
              logBuffer.logAll(args.logs());
              call.setType(empty());
            } else if (someArgHasNotInferredType(args.value())) {
              call.setType(empty());
            } else {
              call.setAssignedArgs(args.value());
              Maybe<TypeS> type = callTypeInferrer.inferCallT(call, funcT.res(), params);
              logBuffer.logAll(type.logs());
              call.setType(type.valueOptional());
            }
          }
        }
      }

      public static Optional<NList<ItemSigS>> funcParams(ExprN called) {
        if (called instanceof RefN refN) {
          EvalLike referenced = refN.referenced();
          if (referenced instanceof FuncS func) {
            return Optional.of(func.params().map(ItemS::sig));
          } else if (referenced instanceof FuncN funcN) {
            var itemSignatures = Optionals.pullUp(
                map(funcN.params(), ItemN::sig));
            return itemSignatures.map(NList::nList);
          } else {
            var params = ((FuncTS) referenced.inferredType().get()).params();
            return Optional.of(nList(map(params, ItemSigS::itemSigS)));
          }
        } else {
          return called.type().map(
              t -> nList(map(((FuncTS) t).params(), ItemSigS::itemSigS)));
        }
      }

      private static boolean someArgHasNotInferredType(List<Optional<ArgNode>> assignedArgs) {
        return assignedArgs.stream()
            .flatMap(Optional::stream)
            .anyMatch(a -> a.type().isEmpty());
      }

      private static String description(ExprN node) {
        if (node instanceof RefN refN) {
          return "`" + refN.name() + "`";
        }
        return "expression";
      }

      @Override
      public void visitRef(RefN ref) {
        super.visitRef(ref);
        ref.setType(referencedType(ref));
      }

      private Optional<TypeS> referencedType(RefN ref) {
        EvalLike referenced = ref.referenced();
        if (referenced instanceof ItemN) {
          // Closing vars here is a hack because we lose some important information.
          // It is worked around by opening vars it in evalTypeOf() method in this class but
          // workaround works only because here we are referencing parameter of most
          // inner function. Once we allow referencing outer function parameters (which
          // have closed vars to most inner function) then opening them in evalTypeOf()
          // won't work. We would need to pass more information from here.
          return referenced.inferredType().map(typing::closeVars);
        } else {
          return referenced.inferredType();
        }
      }

      @Override
      public void visitArg(ArgNode arg) {
        super.visitArg(arg);
        arg.setType(arg.expr().type());
      }

      @Override
      public void visitString(StringN string) {
        super.visitString(string);
        string.setType(factory.string());
      }

      @Override
      public void visitBlob(BlobN blob) {
        super.visitBlob(blob);
        blob.setType(factory.blob());
      }

      @Override
      public void visitInt(IntN intN) {
        super.visitInt(intN);
        intN.setType(factory.int_());
      }

      private void logError(Node node, String message) {
        logBuffer.log(parseError(node, message));
      }
    }.visitAst(ast);
    return logBuffer.toList();
  }
}
