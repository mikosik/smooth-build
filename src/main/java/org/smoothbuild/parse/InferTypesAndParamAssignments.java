package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Objects.requireNonNullElseGet;
import static java.util.Optional.empty;
import static org.smoothbuild.lang.base.Scope.scope;
import static org.smoothbuild.lang.base.type.Types.array;
import static org.smoothbuild.lang.base.type.Types.isGenericTypeName;
import static org.smoothbuild.lang.base.type.Types.nothing;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.lang.base.type.Types.struct;
import static org.smoothbuild.parse.InferCallTypeAndParamAssignment.inferCallTypeAndParamAssignment;
import static org.smoothbuild.parse.ParseError.parseError;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.cli.console.LoggerImpl;
import org.smoothbuild.lang.base.Item;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.base.type.ConcreteType;
import org.smoothbuild.lang.base.type.Field;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.Types;
import org.smoothbuild.parse.ast.AccessorNode;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.ArrayNode;
import org.smoothbuild.parse.ast.ArrayTypeNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.AstVisitor;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.CallableNode;
import org.smoothbuild.parse.ast.ExprNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.ItemNode;
import org.smoothbuild.parse.ast.RefNode;
import org.smoothbuild.parse.ast.StringNode;
import org.smoothbuild.parse.ast.StructNode;
import org.smoothbuild.parse.ast.TypeNode;
import org.smoothbuild.util.Lists;

import com.google.common.collect.ImmutableList;

public class InferTypesAndParamAssignments {
  public static void inferTypesAndParamAssignment(
      Ast ast, Definitions imported, LoggerImpl logger) {
    new AstVisitor() {
      Scope<Type> scope;

      @Override
      public void visitStruct(StructNode struct) {
        super.visitStruct(struct);
        if (struct.fields().stream().anyMatch(f -> f.type().isEmpty())) {
          return;
        }

        List<ItemNode> fieldNodes = struct.fields();
        var builder = ImmutableList.<Field>builder();
        for (int i = 0; i < fieldNodes.size(); i++) {
          ItemNode fieldNode = fieldNodes.get(i);
          Field field = new Field(i, (ConcreteType) fieldNode.type().get(), fieldNode.name(),
              fieldNode.location());
          builder.add(field);
        }
        struct.setType(struct(struct.name(), struct.location(), builder.build()));
      }

      @Override
      public void visitField(int index, ItemNode fieldNode) {
        super.visitField(index, fieldNode);
        fieldNode.setType(fieldNode.typeNode().type());
        fieldNode.setItemInfo(Optional.of(new Item(
            index, fieldNode.type().get(), fieldNode.name(), false, fieldNode.location())));
      }

      @Override
      public void visitFunc(FuncNode func) {
        visitParams(func.params());

        scope = scope();
        func.params().forEach(p -> scope.add(p.name(), p.type().get()));
        func.visitExpr(this);
        scope = null;

        func.setType(funcType(func));
        visitCallable(func);
      }

      @Override
      public void visitCallable(CallableNode callable) {
        super.visitCallable(callable);
        var infos = Lists.map(callable.params(), ItemNode::itemInfo);
        if (infos.stream().noneMatch(Optional::isEmpty)) {
           callable.setParameterInfos(infos.stream().map(Optional::get).collect(toImmutableList()));
        }
      }

      private Optional<Type> funcType(FuncNode func) {
        if (func.isNative()) {
          return typeOfNativeFunction(func);
        } else {
          return typeOfDeclaredFunction(func);
        }
      }

      private Optional<Type> typeOfNativeFunction(FuncNode func) {
        if (func.declaresType()) {
          return createType(func.typeNode());
        } else {
          logger.log(parseError(func, "Function '" + func.name()
              + "' is native so should have declared result type."));
          return empty();
        }
      }

      private Optional<Type> typeOfDeclaredFunction(FuncNode func) {
        Optional<Type> exprType = func.expr().type();
        if (func.declaresType()) {
          Optional<Type> type = createType(func.typeNode());
          type.ifPresent(t -> exprType.ifPresent(et -> {
            if (!t.isAssignableFrom(et)) {
              logger.log(parseError(func, "Function '" + func.name()
                  + "' has body which type is " + et.q()
                  + " and it is not convertible to function's declared result type " + t.q()
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
              var info = new Item(
                  index, t, param.name(), param.declaresDefaultValue(), param.location());
              param.setItemInfo(Optional.of(info));
              if (param.declaresDefaultValue()) {
                Optional<Type> defaultValueType = param.defaultValue().type();
                defaultValueType.ifPresent(dt -> {
                  if (!t.isAssignableFrom(dt)) {
                    logger.log(parseError(param, "Parameter '" + param.name()
                        + "' is of type " + t.q() + " so it cannot have default value of type "
                        + dt.q() + "."));
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
              "Cannot find type '" + name + "'. Available types = " + ast.structsMap());
        } else {
          return structNode.type().orElseThrow(() -> new RuntimeException(
              "Cannot find type '" + name + "'. Available types = " + ast.structsMap()));
        }
      }

      @Override
      public void visitAccessor(AccessorNode expr) {
        super.visitAccessor(expr);
        expr.expr().type().ifPresentOrElse(
            t -> {
              if (t instanceof StructType && ((StructType) t).fields().containsKey(expr.fieldName())) {
                expr.setType(((StructType) t).fields().get(expr.fieldName()).type());
              } else {
                expr.setType(empty());
                logger.log(parseError(expr.location(), "Type " + t.q()
                    + " doesn't have field '" + expr.fieldName() + "'."));
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
                "Array cannot contain elements of incompatible types.\n"
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
        ref.setType(scope.get(ref.name()));
      }

      @Override
      public void visitArg(ArgNode arg) {
        super.visitArg(arg);
        arg.setType(arg.expr().type());
      }

      @Override
      public void visitString(StringNode string) {
        super.visitString(string);
        string.setType(string());
      }
    }.visitAst(ast);
  }
}
