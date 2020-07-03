package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Optional.empty;
import static org.smoothbuild.lang.base.Scope.scope;
import static org.smoothbuild.parse.InferCallTypeAndParamAssignment.inferCallTypeAndParamAssignment;
import static org.smoothbuild.parse.ParseError.parseError;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.cli.console.LoggerImpl;
import org.smoothbuild.lang.base.ParameterInfo;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.object.db.ObjectFactory;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.object.type.Field;
import org.smoothbuild.lang.object.type.StructType;
import org.smoothbuild.lang.object.type.Type;
import org.smoothbuild.parse.ast.AccessorNode;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.ArrayNode;
import org.smoothbuild.parse.ast.ArrayTypeNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.AstVisitor;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.ExprNode;
import org.smoothbuild.parse.ast.FieldNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.NamedNode;
import org.smoothbuild.parse.ast.ParamNode;
import org.smoothbuild.parse.ast.RefNode;
import org.smoothbuild.parse.ast.StringNode;
import org.smoothbuild.parse.ast.StructNode;
import org.smoothbuild.parse.ast.TypeNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class InferTypesAndParamAssignment {
  public static void inferTypesAndParamAssignment(Ast ast, Definitions imported,
      ObjectFactory objectFactory, LoggerImpl logger) {
    new AstVisitor() {
      Scope<Type> scope;

      @Override
      public void visitStruct(StructNode struct) {
        super.visitStruct(struct);
        if (struct.fields().stream().anyMatch(f -> f.type().isEmpty())) {
          return;
        }
        ImmutableList<Field> fields = struct.fields().stream()
            .map(f -> new Field((ConcreteType) f.type().get(), f.name(), f.location()))
            .collect(toImmutableList());
        struct.setType(objectFactory.structType(struct.name(), fields));
        List<ParameterInfo> parameters = createParameters(struct.fields());
        if (parameters != null) {
          struct.constructor().setParameterInfos(parameters);
        }
      }

      @Override
      public void visitField(int index, FieldNode fieldNode) {
        super.visitField(index, fieldNode);
        fieldNode.setType(fieldNode.typeNode().type());
        fieldNode.set(ParameterInfo.class,
            new ParameterInfo(index, fieldNode.type().get(), fieldNode.name(), false));
      }

      @Override
      public void visitFunc(FuncNode func) {
        visitParams(func.params());

        scope = scope();
        func.params().forEach(p -> scope.add(p.name(), p.type().get()));
        func.visitExpr(this);
        scope = null;

        func.setType(funcType(func));
        List<ParameterInfo> parameters = createParameters(func.params());
        if (parameters != null) {
          func.setParameterInfos(parameters);
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
        if (func.hasType()) {
          return createType(func.typeNode());
        } else {
          logger.log(parseError(func, "Function '" + func.name()
              + "' is native so should have declared result type."));
          return empty();
        }
      }

      private Optional<Type> typeOfDeclaredFunction(FuncNode func) {
        Optional<Type> exprType = func.expr().type();
        if (func.hasType()) {
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

      private List<ParameterInfo> createParameters(List<? extends NamedNode> params) {
        Builder<ParameterInfo> builder = ImmutableList.builder();
        for (NamedNode param : params) {
          if (param.get(ParameterInfo.class) == null) {
            return null;
          } else {
            builder.add(param.get(ParameterInfo.class));
          }
        }
        return builder.build();
      }

      @Override
      public void visitParam(int index, ParamNode param) {
        super.visitParam(index, param);
        Optional<Type> type = param.typeNode().type();
        param.setType(type);
        type.ifPresentOrElse(t -> {
              var info = new ParameterInfo(index, t, param.name(), param.hasDefaultValue());
              param.set(ParameterInfo.class, info);
              if (param.hasDefaultValue()) {
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
            () -> param.set(ParameterInfo.class, null));
      }

      @Override
      public void visitType(TypeNode type) {
        super.visitType(type);
        type.setType(createType(type));
      }

      private Optional<Type> createType(TypeNode type) {
        if (type.isArray()) {
          TypeNode elementType = ((ArrayTypeNode) type).elementType();
          return createType(elementType).map(objectFactory::arrayType);
        } else {
          return Optional.of(objectFactory.getType(type.name()));
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
          return Optional.of(objectFactory.arrayType(objectFactory.nothingType()));
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
          elemType = elemType.commonSuperType(type.get());

          if (elemType == null) {
            logger.log(parseError(array,
                "Array cannot contain elements of incompatible types.\n"
                    + "First element has type " + firstType.get().q()
                    + " while element at index " + i + " has type " + type.get().q() + "."));
            return empty();
          }
        }
        return Optional.of(objectFactory.arrayType(elemType));
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
        string.setType(objectFactory.stringType());
      }
    }.visitAst(ast);
  }
}
