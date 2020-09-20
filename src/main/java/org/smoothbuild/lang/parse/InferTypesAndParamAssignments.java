package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Objects.requireNonNullElseGet;
import static java.util.Optional.empty;
import static org.smoothbuild.lang.base.type.Types.array;
import static org.smoothbuild.lang.base.type.Types.blob;
import static org.smoothbuild.lang.base.type.Types.isGenericTypeName;
import static org.smoothbuild.lang.base.type.Types.nothing;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.lang.base.type.Types.struct;
import static org.smoothbuild.lang.parse.InferCallTypeAndParamAssignment.inferCallTypeAndParamAssignment;
import static org.smoothbuild.lang.parse.ParseError.parseError;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.lang.base.Definitions;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.base.ItemSignature;
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
import org.smoothbuild.lang.parse.ast.EvaluableNode;
import org.smoothbuild.lang.parse.ast.ExprNode;
import org.smoothbuild.lang.parse.ast.FieldReadNode;
import org.smoothbuild.lang.parse.ast.FuncNode;
import org.smoothbuild.lang.parse.ast.ItemNode;
import org.smoothbuild.lang.parse.ast.RefNode;
import org.smoothbuild.lang.parse.ast.StringNode;
import org.smoothbuild.lang.parse.ast.StructNode;
import org.smoothbuild.lang.parse.ast.TypeNode;
import org.smoothbuild.lang.parse.ast.ValueNode;

public class InferTypesAndParamAssignments {
  public static void inferTypesAndParamAssignment(
      Ast ast, Definitions imported, Logger logger) {
    new AstVisitor() {
      @Override
      public void visitStruct(StructNode struct) {
        super.visitStruct(struct);
        if (struct.fields().stream().anyMatch(f -> f.type().isEmpty())) {
          return;
        }

        var fields = struct.fields()
            .stream()
            .map(f -> new Field(f.type().get(), f.name(), f.location()))
            .collect(toImmutableList());
        struct.setType(struct(struct.name(), struct.location(), fields));
        struct.constructor().initializeParameterInfos();
      }

      @Override
      public void visitField(ItemNode fieldNode) {
        super.visitField(fieldNode);
        fieldNode.setType(fieldNode.typeNode().type());
        fieldNode.setItemInfo(Optional.of(new ItemSignature(
            fieldNode.type().get(), fieldNode.name(), false, fieldNode.location())));
      }

      @Override
      public void visitFunc(FuncNode func) {
        visitParams(func.params());
        func.visitExpr(this);
        func.setType(codeType(func));
        setParametersInfo(func);
      }

      @Override
      public void visitValue(ValueNode value) {
        value.visitExpr(this);
        value.setType(codeType(value));
      }

      @Override
      public void visitCallable(CallableNode callable) {
        super.visitCallable(callable);
        setParametersInfo(callable);
      }

      private void setParametersInfo(CallableNode callable) {
        var infos = map(callable.params(), ItemNode::itemInfo);
        if (infos.stream().noneMatch(Optional::isEmpty)) {
           callable.setParameterInfos(map(infos, Optional::get));
        }
      }

      private Optional<Type> codeType(EvaluableNode evaluable) {
        if (evaluable.isNative()) {
          return typeOfNativeCode(evaluable);
        } else {
          return typeOfDeclaredCode(evaluable);
        }
      }

      private Optional<Type> typeOfNativeCode(EvaluableNode evaluable) {
        if (evaluable.declaresType()) {
          return createType(evaluable.typeNode());
        } else {
          logger.log(parseError(
              evaluable, evaluable.q() + " is native so it should have type declaration."));
          return empty();
        }
      }

      private Optional<Type> typeOfDeclaredCode(EvaluableNode evaluable) {
        Optional<Type> exprType = evaluable.expr().type();
        if (evaluable.declaresType()) {
          Optional<Type> type = createType(evaluable.typeNode());
          type.ifPresent(t -> exprType.ifPresent(et -> {
            if (!t.isAssignableFrom(et)) {
              logger.log(parseError(evaluable, "`" + evaluable.name()
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

      @Override
      public void visitParam(int index, ItemNode param) {
        super.visitParam(index, param);
        Optional<Type> type = param.typeNode().type();
        param.setType(type);
        type.ifPresentOrElse(t -> {
              var info = new ItemSignature(
                  t, param.name(), param.defaultValue().isPresent(), param.location());
              param.setItemInfo(Optional.of(info));
              if (param.defaultValue().isPresent()) {
                Optional<Type> defaultValueType = param.defaultValue().get().type();
                defaultValueType.ifPresent(dt -> {
                  if (!t.isAssignableFrom(dt)) {
                    logger.log(parseError(param, "Parameter " + param.q() + " is of type " + t.q()
                        + " so it cannot have default value of type " + dt.q() + "."));
                  }
                });
              }
            },
            () -> param.setItemInfo(empty()));
      }

      @Override
      public void visitType(TypeNode type) {
        super.visitType(type);
        type.setType(createType(type));
      }

      private Optional<Type> createType(TypeNode type) {
        if (isGenericTypeName(type.name())) {
          return Optional.of(Types.generic(type.name()));
        } else if (type.isArray()) {
          TypeNode elementType = ((ArrayTypeNode) type).elementType();
          return createType(elementType).map(Types::array);
        } else {
          return Optional.of(findType(type.name()));
        }
      }

      private Type findType(String name) {
        Type type = imported.types().get(name);
        return requireNonNullElseGet(type, () -> findStructType(name));
      }

      private Type findStructType(String name) {
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
          Optional<Type> common = elemType.commonSuperType(type.get());
          if (common.isEmpty()) {
            logger.log(parseError(array,
                "Array cannot contain elements of incompatible types. "
                    + "First element has type " + firstType.get().q()
                    + " while element at index " + i + " has type " + type.get().q() + "."));
            return empty();
          }
          elemType = common.get();
        }
        return Optional.of(array(elemType));
      }

      @Override
      public void visitCall(CallNode call) {
        super.visitCall(call);
        inferCallTypeAndParamAssignment(call, imported, ast.callablesMap(), logger);
      }

      @Override
      public void visitRef(RefNode ref) {
        super.visitRef(ref);
        ref.setType(ref.target().inferredType());
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
  }
}
