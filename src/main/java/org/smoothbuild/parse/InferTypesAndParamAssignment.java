package org.smoothbuild.parse;

import static org.smoothbuild.lang.base.Scope.scope;
import static org.smoothbuild.lang.object.type.MissingType.MISSING_TYPE;
import static org.smoothbuild.parse.InferCallTypeAndParamAssignment.inferCallTypeAndParamAssignment;
import static org.smoothbuild.parse.ParseError.parseError;

import java.util.ArrayList;
import java.util.List;

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

public class InferTypesAndParamAssignment {
  public static void inferTypesAndParamAssignment(Ast ast, Definitions imported,
      ObjectFactory objectFactory, LoggerImpl logger) {
    new AstVisitor() {
      Scope<Type> scope;

      @Override
      public void visitStruct(StructNode struct) {
        super.visitStruct(struct);
        List<Field> fields = new ArrayList<>();
        for (FieldNode field : struct.fields()) {
          Type type = field.type();
          if (type == MISSING_TYPE) {
            return;
          } else {
            fields.add(new Field((ConcreteType) type, field.name(), field.location()));
          }
        }
        struct.setType(objectFactory.structType(struct.name(), fields));
        List<ParameterInfo> parameters = createParameters(struct.fields());
        if (parameters != null) {
          struct.setParameterInfos(parameters);
        }
      }

      @Override
      public void visitField(int index, FieldNode fieldNode) {
        super.visitField(index, fieldNode);
        fieldNode.setType(fieldNode.typeNode().type());
        fieldNode.set(ParameterInfo.class,
            new ParameterInfo(index, fieldNode.type(), fieldNode.name(), false));
      }

      @Override
      public void visitFunc(FuncNode func) {
        visitParams(func.params());

        scope = scope();
        func.params().forEach(p -> scope.add(p.name(), p.type()));
        func.visitExpr(this);
        scope = null;

        Type type = funcType(func);
        func.setType(type);
        List<ParameterInfo> parameters = createParameters(func.params());
        if (parameters != null) {
          func.setParameterInfos(parameters);
        }
      }

      private Type funcType(FuncNode func) {
        if (func.isNative()) {
          if (func.hasType()) {
            return createType(func.typeNode());
          } else {
            logger.log(parseError(func, "Function '" + func.name()
                + "' is native so should have declared result type."));
            return null;
          }
        } else {
          Type exprType = func.expr().type();
          if (func.hasType()) {
            Type type = createType(func.typeNode());
            if (type != MISSING_TYPE
                && exprType != MISSING_TYPE
                && !type.isAssignableFrom(exprType)) {
              logger.log(parseError(func, "Function '" + func.name()
                  + "' has body which type is " + exprType.q()
                  + " and it is not convertible to function's declared result type " + type.q()
                  + "."));
            }
            return type;
          } else {
            return exprType;
          }
        }
      }

      private List<ParameterInfo> createParameters(List<? extends NamedNode> params) {
        ImmutableList.Builder<ParameterInfo> builder = ImmutableList.builder();
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
        Type type = param.typeNode().type();
        param.setType(type);
        if (type == MISSING_TYPE) {
          param.set(ParameterInfo.class, null);
        } else {
          ParameterInfo info = new ParameterInfo(
              index, param.type(), param.name(), param.hasDefaultValue());
          param.set(ParameterInfo.class, info);
          if (param.hasDefaultValue()) {
            Type defaultValueType = param.defaultValue().type();
            if (defaultValueType != MISSING_TYPE && !type.isAssignableFrom(defaultValueType)) {
              logger.log(parseError(param, "Parameter '" + param.name()
                  + "' is of type " + type.q() + " so it cannot have default value of type "
                  + defaultValueType.q() + "."));
            }
          }
        }
      }

      @Override
      public void visitType(TypeNode type) {
        super.visitType(type);
        type.setType(createType(type));
      }

      private Type createType(TypeNode type) {
        if (type.isArray()) {
          TypeNode elementType = ((ArrayTypeNode) type).elementType();
          return objectFactory.arrayType(createType(elementType));
        } else {
          return objectFactory.getType(type.name());
        }
      }

      @Override
      public void visitAccessor(AccessorNode expr) {
        super.visitAccessor(expr);
        Type exprType = expr.expr().type();
        if (exprType == MISSING_TYPE) {
          expr.setType(MISSING_TYPE);
        } else {
          if (exprType instanceof StructType
              && ((StructType) exprType).fields().containsKey(expr.fieldName())) {
            expr.setType(
                ((StructType) exprType).fields().get(expr.fieldName()).type());
          } else {
            expr.setType(MISSING_TYPE);
            logger.log(parseError(expr.location(), "Type " + exprType.q()
                + " doesn't have field '" + expr.fieldName() + "'."));
          }
        }
      }

      @Override
      public void visitArray(ArrayNode array) {
        super.visitArray(array);
        array.setType(findArrayType(array));
      }

      private Type findArrayType(ArrayNode array) {
        List<ExprNode> expressions = array.elements();
        if (expressions.isEmpty()) {
          return objectFactory.arrayType(objectFactory.nothingType());
        }
        Type firstType = expressions.get(0).type();
        if (firstType == MISSING_TYPE) {
          return MISSING_TYPE;
        }
        Type elemType = firstType;
        for (int i = 1; i < expressions.size(); i++) {
          Type type = expressions.get(i).type();
          if (type == MISSING_TYPE) {
            return MISSING_TYPE;
          }
          elemType = elemType.commonSuperType(type);

          if (elemType == null) {
            logger.log(parseError(array,
                "Array cannot contain elements of incompatible types.\n"
                    + "First element has type " + firstType.q()
                    + " while element at index " + i + " has type " + type.q() + "."));
            return null;
          }
        }
        return objectFactory.arrayType(elemType);
      }

      @Override
      public void visitCall(CallNode call) {
        super.visitCall(call);
        inferCallTypeAndParamAssignment(call, imported, ast.functionsAndConstructorsMap(), logger);
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
