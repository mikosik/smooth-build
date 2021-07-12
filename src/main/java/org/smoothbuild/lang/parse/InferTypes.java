package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Objects.requireNonNullElseGet;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.smoothbuild.lang.base.type.Side.UPPER;
import static org.smoothbuild.lang.base.type.Type.toItemSignatures;
import static org.smoothbuild.lang.base.type.Types.array;
import static org.smoothbuild.lang.base.type.Types.blob;
import static org.smoothbuild.lang.base.type.Types.function;
import static org.smoothbuild.lang.base.type.Types.isVariableName;
import static org.smoothbuild.lang.base.type.Types.nothing;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.lang.base.type.Types.variable;
import static org.smoothbuild.lang.parse.InferArgsToParamsAssignment.inferArgsToParamsAssignment;
import static org.smoothbuild.lang.parse.InferCallType.inferCallType;
import static org.smoothbuild.lang.parse.ParseError.parseError;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.cli.console.MemoryLogger;
import org.smoothbuild.lang.base.define.Defined;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.ModulePath;
import org.smoothbuild.lang.base.define.Struct;
import org.smoothbuild.lang.base.type.FunctionType;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.Types;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.ArrayNode;
import org.smoothbuild.lang.parse.ast.ArrayTypeNode;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.AstVisitor;
import org.smoothbuild.lang.parse.ast.BlobNode;
import org.smoothbuild.lang.parse.ast.CallNode;
import org.smoothbuild.lang.parse.ast.CallableNode;
import org.smoothbuild.lang.parse.ast.ExprNode;
import org.smoothbuild.lang.parse.ast.FieldReadNode;
import org.smoothbuild.lang.parse.ast.FuncNode;
import org.smoothbuild.lang.parse.ast.FunctionTypeNode;
import org.smoothbuild.lang.parse.ast.ItemNode;
import org.smoothbuild.lang.parse.ast.RefNode;
import org.smoothbuild.lang.parse.ast.ReferencableNode;
import org.smoothbuild.lang.parse.ast.StringNode;
import org.smoothbuild.lang.parse.ast.StructNode;
import org.smoothbuild.lang.parse.ast.TypeNode;
import org.smoothbuild.lang.parse.ast.ValueNode;

import com.google.common.collect.ImmutableList;

public class InferTypes {
  public static List<Log> inferTypes(ModulePath path, Ast ast, Definitions imported) {
    var logger = new MemoryLogger();
    new AstVisitor() {
      @Override
      public void visitStruct(StructNode struct) {
        super.visitStruct(struct);
        if (struct.fields().stream().anyMatch(f -> f.type().isEmpty())) {
          struct.setStruct(Optional.empty());
          return;
        }

        var fields = struct.fields()
            .stream()
            .map(f -> new Item(f.type().get(), f.name(), empty()))
            .collect(toImmutableList());
        struct.setStruct(Optional.of(new Struct(path, struct.name(), fields, struct.location())));
      }

      @Override
      public void visitField(ItemNode fieldNode) {
        super.visitField(fieldNode);
        fieldNode.setType(fieldNode.typeNode().get().type());
      }

      @Override
      public void visitFunc(FuncNode func) {
        visitParams(func.params());
        func.expr().ifPresent(this::visitExpr);
        func.setType(functionType(func));
      }

      private Optional<Type> functionType(FuncNode func) {
        var resultType = bodyType(func);
        var parameterSignatures = func.optParameterSignatures();
        if (resultType.isPresent() && parameterSignatures.isPresent()) {
          return Optional.of(function(resultType.get(), parameterSignatures.get()));
        } else {
          return Optional.empty();
        }
      }

      @Override
      public void visitValue(ValueNode value) {
        value.expr().ifPresent(this::visitExpr);
        value.setType(bodyType(value));
      }

      @Override
      public void visitCallable(CallableNode callable) {
        super.visitCallable(callable);
      }

      private Optional<Type> bodyType(ReferencableNode referencable) {
        if (referencable.expr().isPresent()) {
          return typeOfDeclaredBody(referencable, referencable.expr().get());
        } else {
          return typeOfNativeBody(referencable);
        }
      }

      private Optional<Type> typeOfDeclaredBody(ReferencableNode referencable, ExprNode exprNode) {
        Optional<Type> exprType = exprNode.type().map(Type::strip);
        if (referencable.typeNode().isPresent()) {
          Optional<Type> type = createType(referencable.typeNode().get());
          type.ifPresent(t -> exprType.ifPresent(et -> {
            if (!t.isAssignableFrom(et)) {
              logger.log(parseError(referencable, "`" + referencable.name()
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
          logger.log(parseError(referencable, referencable.q()
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
          var optDefaultValue = param.expr();
          if (optDefaultValue.isPresent()) {
            var optDefaultValueType = optDefaultValue.get().type();
            if (optDefaultValueType.isPresent()) {
              Type dt = optDefaultValueType.get();
              if (!type.isParamAssignableFrom(dt)) {
                logger.log(parseError(param, "Parameter " + param.q() + " is of type " + type.q()
                    + " so it cannot have default value of type " + dt.q() + "."));
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
          return Optional.of(variable(type.name()));
        } else if (type instanceof ArrayTypeNode array) {
          TypeNode elementType = array.elementType();
          return createType(elementType).map(Types::array);
        } else if (type instanceof FunctionTypeNode function) {
          Optional<Type> result = createType(function.resultType());
          var parameters = map(function.parameterTypes(), this::createType);
          if (result.isPresent() && parameters.stream().allMatch(Optional::isPresent)) {
            return Optional.of(function(
                result.get(), toItemSignatures(map(parameters, Optional::get))));
          } else {
            return Optional.empty();
          }
        } else {
          return Optional.of(findType(type.name()).type());
        }
      }

      private Defined findType(String name) {
        Defined type = imported.types().get(name);
        return requireNonNullElseGet(type, () -> findStruct(name));
      }

      private Defined findStruct(String name) {
        StructNode structNode = ast.structsMap().get(name);
        if (structNode == null) {
          throw new RuntimeException(
              "Cannot find type `" + name + "`. Available types = " + ast.structsMap());
        } else {
          return structNode.struct().orElseThrow(() -> new RuntimeException(
              "Cannot find type `" + name + "`. Available types = " + ast.structsMap()));
        }
      }

      @Override
      public void visitFieldRead(FieldReadNode expr) {
        super.visitFieldRead(expr);
        expr.expr().type().ifPresentOrElse(
            t -> {
              if (t instanceof StructType st && st.containsFieldWithName(expr.fieldName())) {
                expr.setType(((StructType) t).fieldWithName(expr.fieldName()).type());
              } else {
                expr.setType(empty());
                logger.log(parseError(expr.location(), "Type " + t.q()
                    + " doesn't have field `" + expr.fieldName() + "`."));
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
          return Optional.of(array(nothing()));
        }
        Optional<Type> firstType = expressions.get(0).type();
        if (firstType.isEmpty()) {
          return empty();
        }

        Type elemType = firstType.get();
        for (int i = 1; i < expressions.size(); i++) {
          Optional<Type> type = expressions.get(i).type();
          if (type.isEmpty()) {
            return empty();
          }
          elemType = elemType.mergeWith(type.get(), UPPER);
        }
        return Optional.of(array(elemType));
      }

      @Override
      public void visitCall(CallNode call) {
        super.visitCall(call);
        String name = call.calledName();
        Optional<Type> calledType = call.ref().referenced().inferredType();
        if (calledType.isEmpty()) {
          call.setType(Optional.empty());
        } else if (calledType.get() instanceof FunctionType functionType) {
          ImmutableList<ItemSignature> parameters = functionType.parameters();
          Maybe<List<ArgNode>> args = inferArgsToParamsAssignment(call, parameters);
          if (args.hasProblems()) {
            logger.logAllFrom(args);
            call.setType(Optional.empty());
          } else {
            call.setAssignedArgs(args.value());
            Maybe<Type> type = inferCallType(call, functionType.resultType(), parameters);
            logger.logAllFrom(type);
            call.setType(ofNullable(type.value()));
          }
        } else {
          logger.log(parseError(call.location(),
              "`" + name + "`" + " cannot be called as it is not a function."));
          call.setType(Optional.empty());
        }
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
        string.setType(string());
      }

      @Override
      public void visitBlobLiteral(BlobNode blob) {
        super.visitBlobLiteral(blob);
        blob.setType(blob());
      }
    }.visitAst(ast);
    return logger.logs();
  }
}
