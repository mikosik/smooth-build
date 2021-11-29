package org.smoothbuild.lang.parse;

import static java.util.Optional.empty;
import static org.smoothbuild.lang.base.type.api.TypeNames.isVariableName;
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
import org.smoothbuild.lang.base.define.Defined;
import org.smoothbuild.lang.base.define.DefinitionsS;
import org.smoothbuild.lang.base.define.FuncS;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.ItemSignature;
import org.smoothbuild.lang.base.like.EvalLike;
import org.smoothbuild.lang.base.type.impl.FuncTypeS;
import org.smoothbuild.lang.base.type.impl.StructTypeS;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.base.type.impl.TypingS;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.ArrayN;
import org.smoothbuild.lang.parse.ast.ArrayTypeN;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.AstVisitor;
import org.smoothbuild.lang.parse.ast.BlobN;
import org.smoothbuild.lang.parse.ast.CallN;
import org.smoothbuild.lang.parse.ast.EvalN;
import org.smoothbuild.lang.parse.ast.ExprN;
import org.smoothbuild.lang.parse.ast.FuncN;
import org.smoothbuild.lang.parse.ast.FuncTypeN;
import org.smoothbuild.lang.parse.ast.IntN;
import org.smoothbuild.lang.parse.ast.ItemN;
import org.smoothbuild.lang.parse.ast.RealFuncN;
import org.smoothbuild.lang.parse.ast.RefN;
import org.smoothbuild.lang.parse.ast.SelectN;
import org.smoothbuild.lang.parse.ast.StringN;
import org.smoothbuild.lang.parse.ast.StructN;
import org.smoothbuild.lang.parse.ast.TypeN;
import org.smoothbuild.lang.parse.ast.ValueN;
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

  public List<Log> inferTypes(Ast ast, DefinitionsS imported) {
    var logBuffer = new LogBuffer();

    new AstVisitor() {
      @Override
      public void visitStruct(StructN struct) {
        super.visitStruct(struct);
        var fields = Optionals.pullUp(map(struct.fields(), ItemN::itemSignature));
        struct.setType(fields.map(f -> factory.struct(struct.name(), nList(f))));
        struct.ctor().setType(
            fields.map(s -> factory.func(struct.type().get(), map(s, ItemSignature::type))));
      }

      @Override
      public void visitField(ItemN fieldNode) {
        super.visitField(fieldNode);
        fieldNode.setType(fieldNode.typeNode().get().type());
      }

      @Override
      public void visitRealFunc(RealFuncN func) {
        visitParams(func.params());
        func.body().ifPresent(this::visitExpr);
        func.setType(optionalFuncType(evaluationTypeOfTopEvaluables(func), func.optParamTypes()));
      }

      @Override
      public void visitValue(ValueN value) {
        value.body().ifPresent(this::visitExpr);
        value.setType(evaluationTypeOfTopEvaluables(value));
      }

      @Override
      public void visitParam(int index, ItemN param) {
        super.visitParam(index, param);
        param.setType(typeOfParam(param));
      }

      private Optional<TypeS> typeOfParam(ItemN param) {
        return evaluationTypeOf(param, (target, source) -> {
          if (!typing.isParamAssignable(target, source)) {
            logBuffer.log(parseError(param, "Parameter " + param.q() + " is of type " + target.q()
                + " so it cannot have default argument of type " + source.q() + "."));
          }
        });
      }

      private Optional<TypeS> evaluationTypeOfTopEvaluables(EvalN evaluable) {
        return evaluationTypeOf(evaluable, (target, source) -> {
          if (!typing.isAssignable(target, source)) {
            logBuffer.log(parseError(evaluable, "`" + evaluable.name()
                + "` has body which type is " + source.q()
                + " and it is not convertible to its declared type " + target.q()
                + "."));
          }
        });
      }

      private Optional<TypeS> evaluationTypeOf(EvalN referencable,
          BiConsumer<TypeS, TypeS> assignmentChecker) {
        if (referencable.body().isPresent()) {
          Optional<TypeS> exprType = referencable.body().get().type();
          if (referencable.typeNode().isPresent()) {
            Optional<TypeS> type = createType(referencable.typeNode().get());
            type.ifPresent(target -> exprType.ifPresent(
                source -> assignmentChecker.accept(target, source)));
            return type;
          } else {
            return exprType;
          }
        } else {
          if (referencable.typeNode().isPresent()) {
            return createType(referencable.typeNode().get());
          } else {
            logBuffer.log(parseError(referencable, referencable.q()
                + " is native so it should have declared result type."));
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
        if (isVariableName(type.name())) {
          return Optional.of(factory.variable(type.name()));
        }
        return switch (type) {
          case ArrayTypeN array -> createType(array.elemType()).map(factory::array);
          case FuncTypeN func -> {
            Optional<TypeS> result = createType(func.resultType());
            var params = Optionals.pullUp(map(func.paramTypes(), this::createType));
            yield optionalFuncType(result, params);
          }
          default -> Optional.of(findType(type.name()));
        };
      }

      private Optional<TypeS> optionalFuncType(
          Optional<TypeS> result, Optional<ImmutableList<TypeS>> params) {
        if (result.isPresent() && params.isPresent()) {
          return Optional.of(factory.func(result.get(), params.get()));
        } else {
          return empty();
        }
      }

      private TypeS findType(String name) {
        Defined type = imported.types().get(name);
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
      public void visitSelect(SelectN expr) {
        super.visitSelect(expr);
        expr.expr().type().ifPresentOrElse(
            t -> {
              if (!(t instanceof StructTypeS st)) {
                expr.setType(empty());
                logBuffer.log(parseError(expr.location(), "Type " + t.q()
                    + " is not a struct so it doesn't have " + q(expr.fieldName()) + " field."));
              } else if (!st.fields().containsName(expr.fieldName())) {
                expr.setType(empty());
                logBuffer.log(parseError(expr.location(), "Struct " + t.q()
                    + " doesn't have field `" + expr.fieldName() + "`."));
              } else {
                expr.setType(((StructTypeS) t).fields().get(expr.fieldName()).type());
              }
            },
            () -> expr.setType(empty())
        );
      }

      @Override
      public void visitArray(ArrayN array) {
        super.visitArray(array);
        array.setType(findArrayType(array));
      }

      private Optional<TypeS> findArrayType(ArrayN array) {
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
          Optional<TypeS> elemType = elem.type();
          if (elemType.isEmpty()) {
            return empty();
          }
          type = typing.mergeUp(type, elemType.get());
          if (typing.contains(type, factory.any())) {
            logBuffer.log(parseError(elem.location(),
                "Array elems at indexes 0 and " + i + " doesn't have common super type."
                + "\nElement at index 0 type = " + expressions.get(0).type().get().q()
                + "\nElement at index " + i + " type = " + elemType.get().q()));
            return empty();
          }
        }
        return Optional.of(factory.array(type));
      }

      @Override
      public void visitCall(CallN call) {
        super.visitCall(call);
        ExprN called = call.func();
        Optional<TypeS> calledType = called.type();
        if (calledType.isEmpty()) {
          call.setType(empty());
        } else if (!(calledType.get() instanceof FuncTypeS funcType)) {
          logBuffer.log(parseError(call.location(), description(called)
              + " cannot be called as it is not a function but " + calledType.get().q() + "."));
          call.setType(empty());
        } else {
          var funcParameters = funcParams(called);
          if (funcParameters.isEmpty()) {
            call.setType(empty());
          } else {
            var params = funcParameters.get();
            Maybe<List<Optional<ArgNode>>> args = inferArgsToParamsAssignment(call, params);
            if (args.containsProblem()) {
              logBuffer.logAll(args.logs());
              call.setType(empty());
            } else if (someArgHasNotInferredType(args.value())) {
              call.setType(empty());
            } else {
              call.setAssignedArgs(args.value());
              Maybe<TypeS> type = callTypeInferrer.inferCallType(
                  call, funcType.result(), params);
              logBuffer.logAll(type.logs());
              call.setType(type.valueOptional());
            }
          }
        }
      }

      public static Optional<NList<ItemSignature>> funcParams(ExprN called) {
        if (called instanceof RefN refN) {
          EvalLike referenced = refN.referenced();
          if (referenced instanceof FuncS func) {
            return Optional.of(func.params().map(Item::signature));
          } else if (referenced instanceof FuncN funcN) {
            var itemSignatures = Optionals.pullUp(
                map(funcN.params(), ItemN::itemSignature));
            return itemSignatures.map(NList::nList);
          } else {
            var params = ((FuncTypeS) referenced.inferredType().get()).params();
            return Optional.of(nList(map(params, ItemSignature::itemSignature)));
          }
        } else {
          return called.type().map(
              t -> nList(map(((FuncTypeS) t).params(), ItemSignature::itemSignature)));
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
        ref.setType(ref.referenced().inferredType());
      }

      @Override
      public void visitArg(ArgNode arg) {
        super.visitArg(arg);
        arg.setType(arg.expr().type());
      }

      @Override
      public void visitStringLiteral(StringN string) {
        super.visitStringLiteral(string);
        string.setType(factory.string());
      }

      @Override
      public void visitBlobLiteral(BlobN blob) {
        super.visitBlobLiteral(blob);
        blob.setType(factory.blob());
      }

      @Override
      public void visitIntLiteral(IntN intN) {
        super.visitIntLiteral(intN);
        intN.setType(factory.int_());
      }
    }.visitAst(ast);
    return logBuffer.toList();
  }
}
