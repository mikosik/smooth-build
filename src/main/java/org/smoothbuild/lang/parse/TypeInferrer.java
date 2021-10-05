package org.smoothbuild.lang.parse;

import static java.util.Optional.empty;
import static org.smoothbuild.lang.base.type.TypeNames.isVariableName;
import static org.smoothbuild.lang.parse.InferArgsToParamsAssignment.inferArgsToParamsAssignment;
import static org.smoothbuild.lang.parse.ParseError.parseError;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Strings.q;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.LogBuffer;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.lang.base.define.Defined;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.ModulePath;
import org.smoothbuild.lang.base.type.FunctionType;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.Typing;
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

import com.google.common.collect.ImmutableList;

public class TypeInferrer {
  private final Typing typing;
  private final CallTypeInferrer callTypeInferrer;

  @Inject
  public TypeInferrer(Typing typing) {
    this.typing = typing;
    this.callTypeInferrer = new CallTypeInferrer(typing);
  }

  public List<Log> inferTypes(ModulePath path, Ast ast, Definitions imported) {
    var logBuffer = new LogBuffer();
    new AstVisitor() {
      @Override
      public void visitStruct(StructNode struct) {
        super.visitStruct(struct);
        if (struct.fields().stream().anyMatch(f -> f.type().isEmpty())) {
          struct.setType(empty());
          struct.constructor().setType(empty());
          return;
        }
        var fieldSignatures = map(struct.fields(), ItemNode::toItemSignature);
        struct.setType(typing.structT(struct.name(), fieldSignatures));
        struct.constructor().setType(
            typing.functionT(typing.structT(struct.name(), fieldSignatures), fieldSignatures));
      }

      @Override
      public void visitField(ItemNode fieldNode) {
        super.visitField(fieldNode);
        fieldNode.setType(fieldNode.typeNode().get().type());
      }

      @Override
      public void visitFunc(RealFuncNode func) {
        visitParams(func.params());
        func.body().ifPresent(this::visitExpr);
        func.setType(functionType(func));
      }

      private Optional<Type> functionType(RealFuncNode func) {
        var resultType = bodyType(func);
        var parameterSignatures = func.optParameterSignatures();
        if (resultType.isPresent() && parameterSignatures.isPresent()) {
          return Optional.of(typing.functionT(resultType.get(), parameterSignatures.get()));
        } else {
          return empty();
        }
      }

      @Override
      public void visitValue(ValueNode value) {
        value.body().ifPresent(this::visitExpr);
        value.setType(bodyType(value));
      }

      @Override
      public void visitFunction(FunctionNode function) {
        super.visitFunction(function);
      }

      private Optional<Type> bodyType(ReferencableNode referencable) {
        if (referencable.body().isPresent()) {
          return typeOfDeclaredBody(referencable, referencable.body().get());
        } else {
          return typeOfNativeBody(referencable);
        }
      }

      private Optional<Type> typeOfDeclaredBody(ReferencableNode referencable, ExprNode exprNode) {
        Optional<Type> exprType = exprNode.type().map(typing::strip);
        if (referencable.typeNode().isPresent()) {
          Optional<Type> type = createType(referencable.typeNode().get());
          type.ifPresent(t -> exprType.ifPresent(et -> {
            if (!typing.isAssignable(t, et)) {
              logBuffer.log(parseError(referencable, "`" + referencable.name()
                  + "` has body which type is " + et.q()
                  + " and it is not convertible to its declared type " + t.q()
                  + "."));
            }
          }));
          return type;
        } else {
          return exprType;
        }
      }

      private Optional<Type> typeOfNativeBody(ReferencableNode referencable) {
        if (referencable.typeNode().isPresent()) {
          return createType(referencable.typeNode().get());
        } else {
          logBuffer.log(parseError(referencable, referencable.q()
              + " is native so it should have declared result type."));
          return empty();
        }
      }

      @Override
      public void visitParam(int index, ItemNode param) {
        super.visitParam(index, param);
        Optional<Type> optType = param.typeNode().get().type();
        param.setType(optType);
        if (optType.isPresent()) {
          Type type = optType.get();
          var optDefaultArgument = param.body();
          if (optDefaultArgument.isPresent()) {
            var optDefaultArgumentType = optDefaultArgument.get().type();
            if (optDefaultArgumentType.isPresent()) {
              Type dt = optDefaultArgumentType.get();
              if (!typing.isParamAssignable(type, dt)) {
                logBuffer.log(parseError(param, "Parameter " + param.q() + " is of type " + type.q()
                    + " so it cannot have default argument of type " + dt.q() + "."));
              }
            }
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
          return Optional.of(typing.variable(type.name()));
        } else if (type instanceof ArrayTypeNode array) {
          TypeNode elementType = array.elementType();
          return createType(elementType).map(typing::arrayT);
        } else if (type instanceof FunctionTypeNode function) {
          Optional<Type> result = createType(function.resultType());
          var parameters = map(function.parameterTypes(), this::createType);
          if (result.isPresent() && parameters.stream().allMatch(Optional::isPresent)) {
            return Optional.of(typing.functionT(
                result.get(), Type.toItemSignatures(map(parameters, Optional::get))));
          } else {
            return empty();
          }
        } else {
          return Optional.of(findType(type.name()));
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
              } else if (!st.containsFieldWithName(expr.fieldName())) {
                expr.setType(empty());
                logBuffer.log(parseError(expr.location(), "Struct " + t.q()
                    + " doesn't have field `" + expr.fieldName() + "`."));
              } else {
                expr.setType(((StructType) t).fieldWithName(expr.fieldName()).type());
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
          return Optional.of(typing.arrayT(typing.nothingT()));
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
          type = typing.merge(type, elemType.get(), typing.upper());
          if (type.contains(typing.anyT())) {
            logBuffer.log(parseError(elem.location(),
                "Array elements at indexes 0 and " + i + " doesn't have common super type."
                + "\nElement at index 0 type = " + expressions.get(0).type().get().q()
                + "\nElement at index " + i + " type = " + elemType.get().q()));
            return empty();
          }
        }
        return Optional.of(typing.arrayT(type));
      }

      @Override
      public void visitCall(CallNode call) {
        super.visitCall(call);
        ExprNode called = call.function();
        Optional<Type> calledType = called.type();
        if (calledType.isEmpty()) {
          call.setType(empty());
        } else if (calledType.get() instanceof FunctionType functionType) {
          ImmutableList<ItemSignature> parameters = functionType.parameters();
          Maybe<List<Optional<ArgNode>>> args = inferArgsToParamsAssignment(call, parameters);
          if (args.containsProblem()) {
            logBuffer.logAll(args.logs());
            call.setType(empty());
          } else if (someArgumentHasNotInferredType(args.value())) {
            call.setType(empty());
          } else {
            call.setAssignedArgs(args.value());
            Maybe<Type> type = callTypeInferrer.inferCallType(
                call, functionType.resultType(), parameters);
            logBuffer.logAll(type.logs());
            call.setType(type.valueOptional());
          }
        } else {
          logBuffer.log(parseError(call.location(), description(called)
              + " cannot be called as it is not a function but " + calledType.get().q() + "."));
          call.setType(empty());
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
        string.setType(typing.stringT());
      }

      @Override
      public void visitBlobLiteral(BlobNode blob) {
        super.visitBlobLiteral(blob);
        blob.setType(typing.blobT());
      }

      @Override
      public void visitIntLiteral(IntNode intNode) {
        super.visitIntLiteral(intNode);
        intNode.setType(typing.intT());
      }
    }.visitAst(ast);
    return logBuffer.toList();
  }
}
