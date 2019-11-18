package org.smoothbuild.parse;

import static org.smoothbuild.lang.base.Scope.scope;
import static org.smoothbuild.parse.InferCallTypeAndParamAssignment.inferCallTypeAndParamAssignment;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.base.ParameterInfo;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.object.db.ObjectFactory;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.object.type.StructType;
import org.smoothbuild.lang.object.type.Type;
import org.smoothbuild.lang.runtime.SRuntime;
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
import com.google.common.collect.ImmutableList.Builder;

public class InferTypesAndParamAssignment {
  public static List<ParseError> inferTypesAndParamAssignment(SRuntime runtime, Ast ast) {
    ObjectFactory objectFactory = runtime.objectFactory();
    List<ParseError> errors = new ArrayList<>();
    new AstVisitor() {
      Scope<Type> scope;

      @Override
      public void visitStruct(StructNode struct) {
        super.visitStruct(struct);
        List<Field> fields = new ArrayList<>();
        for (FieldNode field : struct.fields()) {
          ConcreteType type = (ConcreteType) field.get(Type.class);
          if (type == null) {
            return;
          } else {
            fields.add(new Field(type, field.name(), field.location()));
          }
        }
        ConcreteType type = objectFactory.structType(struct.name(), fields);
        struct.set(Type.class, type);
        List<ParameterInfo> parameters = createParameters(struct.fields());
        if (parameters != null) {
          struct.setParameterInfos(parameters);
        }
      }

      @Override
      public void visitField(int index, FieldNode field) {
        super.visitField(index, field);
        field.set(Type.class, field.type().get(Type.class));
        field.set(ParameterInfo.class,
            new ParameterInfo(index, field.get(Type.class), field.name(), false));
      }

      @Override
      public void visitFunc(FuncNode func) {
        visitParams(func.params());

        scope = scope();
        func.params().forEach(p -> scope.add(p.name(), p.get(Type.class)));
        if (!func.isNative()) {
          visitExpr(func.expr());
        }
        scope = null;

        Type type = funcType(func);
        func.set(Type.class, type);
        List<ParameterInfo> parameters = createParameters(func.params());
        if (parameters != null) {
          func.setParameterInfos(parameters);
        }
      }

      private Type funcType(FuncNode func) {
        if (func.isNative()) {
          if (func.hasType()) {
            return createType(func.type());
          } else {
            errors.add(new ParseError(func, "Function '" + func.name()
                + "' is native so should have declared result type."));
            return null;
          }
        } else {
          Type exprType = func.expr().get(Type.class);
          if (func.hasType()) {
            Type type = createType(func.type());
            if (type != null && exprType != null && !type.isAssignableFrom(exprType)) {
              errors.add(new ParseError(func, "Function '" + func.name()
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
        Type type = param.type().get(Type.class);
        param.set(Type.class, type);
        if (type == null) {
          param.set(ParameterInfo.class, null);
        } else {
          ParameterInfo info = new ParameterInfo(
              index, param.get(Type.class), param.name(), param.hasDefaultValue());
          param.set(ParameterInfo.class, info);
          if (param.hasDefaultValue()) {
            if (param.type().isGeneric()) {
              errors.add(new ParseError(param, "Parameter '" + param.name()
                  + "' has generic type " + type.q() + " so it cannot have default value."));
            } else {
              Type defaultValueType = param.defaultValue().get((Type.class));
              if (defaultValueType != null && !type.isAssignableFrom(defaultValueType)) {
                errors.add(new ParseError(param, "Parameter '" + param.name()
                    + "' is of type " + type.q() + " so it cannot have default value of type "
                    + defaultValueType.q() + "."));
              }
            }
          }
        }
      }

      @Override
      public void visitType(TypeNode type) {
        super.visitType(type);
        type.set(Type.class, createType(type));
      }

      private Type createType(TypeNode type) {
        if (type.isArray()) {
          TypeNode elementType = ((ArrayTypeNode) type).elementType();
          return objectFactory.arrayType(createType(elementType));
        }
        return objectFactory.getType(type.name());
      }

      @Override
      public void visitAccessor(AccessorNode expr) {
        super.visitAccessor(expr);
        Type exprType = expr.expr().get(Type.class);
        if (exprType == null) {
          expr.set(Type.class, null);
        } else {
          if (exprType instanceof StructType
              && ((StructType) exprType).fields().containsKey(expr.fieldName())) {
            expr.set(Type.class,
                ((StructType) exprType).fields().get(expr.fieldName()).type());
          } else {
            expr.set(Type.class, null);
            errors.add(new ParseError(expr.location(), "Type " + exprType.q()
                + " doesn't have field '" + expr.fieldName() + "'."));
          }
        }
      }

      @Override
      public void visitArray(ArrayNode array) {
        super.visitArray(array);
        array.set(Type.class, findArrayType(array));
      }

      private Type findArrayType(ArrayNode array) {
        List<ExprNode> expressions = array.elements();
        if (expressions.isEmpty()) {
          return objectFactory.arrayType(objectFactory.nothingType());
        }
        Type firstType = expressions.get(0).get(Type.class);
        if (firstType == null) {
          return null;
        }
        Type elemType = firstType;
        for (int i = 1; i < expressions.size(); i++) {
          Type type = expressions.get(i).get(Type.class);
          if (type == null) {
            return null;
          }
          elemType = elemType.commonSuperType(type);

          if (elemType == null) {
            errors.add(new ParseError(array,
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
        inferCallTypeAndParamAssignment(call, runtime, ast, errors);
      }

      @Override
      public void visitRef(RefNode ref) {
        super.visitRef(ref);
        ref.set(Type.class, scope.get(ref.name()));
      }

      @Override
      public void visitArg(ArgNode arg) {
        super.visitArg(arg);
        arg.set(Type.class, arg.expr().get(Type.class));
      }

      @Override
      public void visitString(StringNode string) {
        super.visitString(string);
        string.set(Type.class, objectFactory.stringType());
      }
    }.visitAst(ast);
    return errors;
  }
}
