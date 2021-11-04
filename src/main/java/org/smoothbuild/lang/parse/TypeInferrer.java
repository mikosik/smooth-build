package org.smoothbuild.lang.parse;

import static java.util.Optional.empty;
import static org.smoothbuild.lang.base.define.ItemSignature.toItemSignatures;
import static org.smoothbuild.lang.base.type.api.TypeNames.isVariableName;
import static org.smoothbuild.lang.parse.InferArgsToParamsAssignment.inferArgsToParamsAssignment;
import static org.smoothbuild.lang.parse.ParseError.parseError;
import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NamedList.namedList;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.LogBuffer;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.lang.base.define.Defined;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.Function;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.ItemSignature;
import org.smoothbuild.lang.base.like.ReferencableLike;
import org.smoothbuild.lang.base.type.Typing;
import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.ArrayNode;
import org.smoothbuild.lang.parse.ast.ArrayTypeNode;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.AstVisitor;
import org.smoothbuild.lang.parse.ast.BlobNode;
import org.smoothbuild.lang.parse.ast.CallNode;
import org.smoothbuild.lang.parse.ast.ExprNode;
import org.smoothbuild.lang.parse.ast.FunctionNode;
import org.smoothbuild.lang.parse.ast.FunctionTypeNode;
import org.smoothbuild.lang.parse.ast.IntNode;
import org.smoothbuild.lang.parse.ast.ItemNode;
import org.smoothbuild.lang.parse.ast.RealFuncNode;
import org.smoothbuild.lang.parse.ast.RefNode;
import org.smoothbuild.lang.parse.ast.ReferencableNode;
import org.smoothbuild.lang.parse.ast.SelectNode;
import org.smoothbuild.lang.parse.ast.StringNode;
import org.smoothbuild.lang.parse.ast.StructNode;
import org.smoothbuild.lang.parse.ast.TypeNode;
import org.smoothbuild.lang.parse.ast.ValueNode;
import org.smoothbuild.util.collect.Named;
import org.smoothbuild.util.collect.Optionals;

import com.google.common.collect.ImmutableList;

public class TypeInferrer {
  private final TypeFactoryS factory;
  private final Typing typing;
  private final CallTypeInferrer callTypeInferrer;

  @Inject
  public TypeInferrer(TypeFactoryS factory, Typing typing) {
    this.factory = factory;
    this.typing = typing;
    this.callTypeInferrer = new CallTypeInferrer(factory, typing);
  }

  public List<Log> inferTypes(Ast ast, Definitions imported) {
    var logBuffer = new LogBuffer();
    new AstVisitor() {
      @Override
      public void visitStruct(StructNode struct) {
        super.visitStruct(struct);
        var fields = Optionals.pullUp(
            map(struct.fields(), f -> f.type().map(t -> f.toNamedType())));
        struct.setType(fields.map(s -> factory.struct(struct.name(), namedList(s))));
        struct.constructor().setType(
            fields.map(s -> factory.function(struct.type().get(), map(s, Named::object))));
      }

      @Override
      public void visitField(ItemNode fieldNode) {
        super.visitField(fieldNode);
        fieldNode.setType(fieldNode.typeNode().get().type());
      }

      @Override
      public void visitRealFunc(RealFuncNode func) {
        visitParams(func.params());
        func.body().ifPresent(this::visitExpr);
        func.setType(optionalFunctionType(evaluationTypeOfGlobalReferencable(func), func.optParameterTypes()));
      }

      @Override
      public void visitValue(ValueNode value) {
        value.body().ifPresent(this::visitExpr);
        value.setType(evaluationTypeOfGlobalReferencable(value));
      }

      @Override
      public void visitFunction(FunctionNode function) {
        super.visitFunction(function);
      }

      @Override
      public void visitParam(int index, ItemNode param) {
        super.visitParam(index, param);
        param.setType(typeOfParameter(param));
      }

      private Optional<Type> typeOfParameter(ItemNode param) {
        return evaluationTypeOf(param, (target, source) -> {
          if (!typing.isParamAssignable(target, source)) {
            logBuffer.log(parseError(param, "Parameter " + param.q() + " is of type " + target.q()
                + " so it cannot have default argument of type " + source.q() + "."));
          }
        });
      }

      private Optional<Type> evaluationTypeOfGlobalReferencable(ReferencableNode referencable) {
        return evaluationTypeOf(referencable, (target, source) -> {
          if (!typing.isAssignable(target, source)) {
            logBuffer.log(parseError(referencable, "`" + referencable.name()
                + "` has body which type is " + source.q()
                + " and it is not convertible to its declared type " + target.q()
                + "."));
          }
        });
      }

      private Optional<Type> evaluationTypeOf(ReferencableNode referencable,
          BiConsumer<Type, Type> assignmentChecker) {
        if (referencable.body().isPresent()) {
          Optional<Type> exprType = referencable.body().get().type();
          if (referencable.typeNode().isPresent()) {
            Optional<Type> type = createType(referencable.typeNode().get());
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
      public void visitType(TypeNode type) {
        super.visitType(type);
        type.setType(createType(type));
      }

      private Optional<Type> createType(TypeNode type) {
        if (isVariableName(type.name())) {
          return Optional.of(factory.variable(type.name()));
        } else if (type instanceof ArrayTypeNode array) {
          TypeNode elementType = array.elementType();
          return createType(elementType).map(factory::array);
        } else if (type instanceof FunctionTypeNode function) {
          Optional<Type> result = createType(function.resultType());
          var parameters = Optionals.pullUp(map(function.parameterTypes(), this::createType));
          return optionalFunctionType(result, parameters);
        } else {
          return Optional.of(findType(type.name()));
        }
      }

      private Optional<Type> optionalFunctionType(
          Optional<Type> result, Optional<ImmutableList<Type>> parameters) {
        if (result.isPresent() && parameters.isPresent()) {
          return Optional.of(factory.function(result.get(), parameters.get()));
        } else {
          return empty();
        }
      }

      private Type findType(String name) {
        Defined type = imported.types().get(name);
        if (type != null) {
          return type.type();
        } else {
          return findStruct(name);
        }
      }

      private Type findStruct(String name) {
        StructNode structNode = ast.structsMap().get(name);
        if (structNode == null) {
          throw new RuntimeException(
              "Cannot find type `" + name + "`. Available types = " + ast.structsMap());
        } else {
          return structNode.type().orElseThrow(() -> new RuntimeException(
              "Cannot find type `" + name + "`. Available types = " + ast.structsMap()));
        }
      }

      @Override
      public void visitSelect(SelectNode expr) {
        super.visitSelect(expr);
        expr.expr().type().ifPresentOrElse(
            t -> {
              if (!(t instanceof StructType st)) {
                expr.setType(empty());
                logBuffer.log(parseError(expr.location(), "Type " + t.q()
                    + " is not a struct so it doesn't have " + q(expr.fieldName()) + " field."));
              } else if (!st.fields().contains(expr.fieldName())) {
                expr.setType(empty());
                logBuffer.log(parseError(expr.location(), "Struct " + t.q()
                    + " doesn't have field `" + expr.fieldName() + "`."));
              } else {
                expr.setType(((StructType) t).fields().map().get(expr.fieldName()));
              }
            },
            () -> expr.setType(empty())
        );
      }

      @Override
      public void visitArray(ArrayNode array) {
        super.visitArray(array);
        array.setType(findArrayType(array));
      }

      private Optional<Type> findArrayType(ArrayNode array) {
        List<ExprNode> expressions = array.elements();
        if (expressions.isEmpty()) {
          return Optional.of(factory.array(factory.nothing()));
        }
        Optional<Type> firstType = expressions.get(0).type();
        if (firstType.isEmpty()) {
          return empty();
        }

        Type type = firstType.get();
        for (int i = 1; i < expressions.size(); i++) {
          ExprNode elem = expressions.get(i);
          Optional<Type> elemType = elem.type();
          if (elemType.isEmpty()) {
            return empty();
          }
          type = typing.mergeUp(type, elemType.get());
          if (typing.contains(type, factory.any())) {
            logBuffer.log(parseError(elem.location(),
                "Array elements at indexes 0 and " + i + " doesn't have common super type."
                + "\nElement at index 0 type = " + expressions.get(0).type().get().q()
                + "\nElement at index " + i + " type = " + elemType.get().q()));
            return empty();
          }
        }
        return Optional.of(factory.array(type));
      }

      @Override
      public void visitCall(CallNode call) {
        super.visitCall(call);
        ExprNode called = call.function();
        Optional<Type> calledType = called.type();
        if (calledType.isEmpty()) {
          call.setType(empty());
        } else if (!(calledType.get() instanceof FunctionType functionType)) {
          logBuffer.log(parseError(call.location(), description(called)
              + " cannot be called as it is not a function but " + calledType.get().q() + "."));
          call.setType(empty());
        } else {
          var functionParameters = functionParameters(called);
          if (functionParameters.isEmpty()) {
            call.setType(empty());
          } else {
            var parameters = functionParameters.get();
            Maybe<List<Optional<ArgNode>>> args = inferArgsToParamsAssignment(call, parameters);
            if (args.containsProblem()) {
              logBuffer.logAll(args.logs());
              call.setType(empty());
            } else if (someArgumentHasNotInferredType(args.value())) {
              call.setType(empty());
            } else {
              call.setAssignedArgs(args.value());
              Maybe<Type> type = callTypeInferrer.inferCallType(
                  call, functionType.result(), parameters);
              logBuffer.logAll(type.logs());
              call.setType(type.valueOptional());
            }
          }
        }
      }

      public static Optional<ImmutableList<ItemSignature>> functionParameters(ExprNode called) {
        if (called instanceof RefNode refNode) {
          ReferencableLike referenced = refNode.referenced();
          if (referenced instanceof Function function) {
            return Optional.of(map(function.parameters(), Item::signature));
          } else if (referenced instanceof FunctionNode functionNode) {
            return Optionals.pullUp(map(functionNode.params(), ItemNode::itemSignature));
          } else {
            return Optional.of(map(((FunctionType) referenced.inferredType().get()).parameters(),
                ItemSignature::itemSignature));
          }
        } else {
          return called.type().map(t -> toItemSignatures(((FunctionType) t).parameters()));
        }
      }

      private static boolean someArgumentHasNotInferredType(List<Optional<ArgNode>> assignedArgs) {
        return assignedArgs.stream()
            .flatMap(Optional::stream)
            .anyMatch(a -> a.type().isEmpty());
      }

      private static String description(ExprNode node) {
        if (node instanceof RefNode refNode) {
          return "`" + refNode.name() + "`";
        }
        return "expression";
      }

      @Override
      public void visitRef(RefNode ref) {
        super.visitRef(ref);
        ref.setType(ref.referenced().inferredType());
      }

      @Override
      public void visitArg(ArgNode arg) {
        super.visitArg(arg);
        arg.setType(arg.expr().type());
      }

      @Override
      public void visitStringLiteral(StringNode string) {
        super.visitStringLiteral(string);
        string.setType(factory.string());
      }

      @Override
      public void visitBlobLiteral(BlobNode blob) {
        super.visitBlobLiteral(blob);
        blob.setType(factory.blob());
      }

      @Override
      public void visitIntLiteral(IntNode intNode) {
        super.visitIntLiteral(intNode);
        intNode.setType(factory.int_());
      }
    }.visitAst(ast);
    return logBuffer.toList();
  }
}
