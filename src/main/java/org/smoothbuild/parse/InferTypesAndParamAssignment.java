package org.smoothbuild.parse;

import static org.smoothbuild.lang.base.Scope.scope;
import static org.smoothbuild.parse.InferCallTypeAndParamAssignment.inferCallTypeAndParamAssignment;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.base.ParameterInfo;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.runtime.RuntimeTypes;
import org.smoothbuild.lang.runtime.SRuntime;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.ConcreteType;
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
    RuntimeTypes types = runtime.types();
    List<ParseError> errors = new ArrayList<>();
    new AstVisitor() {
      Scope<ConcreteType> scope;

      @Override
      public void visitStruct(StructNode struct) {
        super.visitStruct(struct);
        List<Field> fields = new ArrayList<>();
        for (FieldNode field : struct.fields()) {
          ConcreteType type = field.get(ConcreteType.class);
          if (type == null) {
            return;
          } else {
            fields.add(new Field(type, field.name(), field.location()));
          }
        }
        ConcreteType type = types.struct(struct.name(), fields);
        struct.set(ConcreteType.class, type);
        List<ParameterInfo> parameters = createParameters(struct.fields());
        if (parameters != null) {
          struct.set(List.class, parameters);
        }
      }

      @Override
      public void visitField(FieldNode field) {
        super.visitField(field);
        field.set(ConcreteType.class, field.type().get(ConcreteType.class));
        field.set(ParameterInfo.class,
            new ParameterInfo(field.get(ConcreteType.class), field.name(), true));
      }

      @Override
      public void visitFunc(FuncNode func) {
        visitParams(func.params());

        scope = scope();
        func.params()
            .stream()
            .forEach(p -> scope.add(p.name(), p.get(ConcreteType.class)));
        if (!func.isNative()) {
          visitExpr(func.expr());
        }
        scope = null;

        ConcreteType type = funcType(func);
        func.set(ConcreteType.class, type);
        List<ParameterInfo> parameters = createParameters(func.params());
        if (parameters != null) {
          func.set(List.class, parameters);
        }
      }

      private ConcreteType funcType(FuncNode func) {
        if (func.isNative()) {
          if (func.hasType()) {
            return createType(func.type());
          } else {
            errors.add(new ParseError(func, "Function '" + func.name()
                + "' is native so should have declared result type."));
            return null;
          }
        } else {
          ConcreteType exprType = func.expr().get(ConcreteType.class);
          if (func.hasType()) {
            ConcreteType type = createType(func.type());
            if (type != null && exprType != null && !type.isAssignableFrom(exprType)) {
              errors.add(new ParseError(func, "Type of function's '" + func.name()
                  + "' expression is " + exprType.name()
                  + " which is not convertible to function's declared result type " + type.name()
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
      public void visitParam(ParamNode param) {
        super.visitParam(param);
        ConcreteType type = param.type().get(ConcreteType.class);
        param.set(ConcreteType.class, type);
        if (type == null) {
          param.set(ParameterInfo.class, null);
        } else {
          ParameterInfo info = new ParameterInfo(
              param.get(ConcreteType.class), param.name(), !param.hasDefaultValue());
          param.set(ParameterInfo.class, info);
          if (param.hasDefaultValue() && param.defaultValue().get(ConcreteType.class) != null) {
            ConcreteType valueType = param.defaultValue().get((ConcreteType.class));
            if (!type.isAssignableFrom(valueType)) {
              errors.add(new ParseError(param, "Parameter '" + param.name()
                  + "' is of type " + type.name() + " so it cannot have default value of type "
                  + valueType.name() + "."));
            }
          }
        }
      }

      @Override
      public void visitType(TypeNode type) {
        super.visitType(type);
        type.set(ConcreteType.class, createType(type));
      }

      private ConcreteType createType(TypeNode type) {
        if (type.isArray()) {
          TypeNode elementType = ((ArrayTypeNode) type).elementType();
          return types.array(createType(elementType));
        }
        return types.getType(type.name());
      }

      @Override
      public void visitAccessor(AccessorNode expr) {
        super.visitAccessor(expr);
        ConcreteType exprType = expr.expr().get(ConcreteType.class);
        if (exprType == null) {
          expr.set(ConcreteType.class, null);
        } else {
          if (exprType instanceof StructType
              && ((StructType) exprType).fields().containsKey(expr.fieldName())) {
            expr.set(ConcreteType.class, ((StructType) exprType).fields().get(expr.fieldName()).type());
          } else {
            expr.set(ConcreteType.class, null);
            errors.add(new ParseError(expr.location(), "Type '" + exprType.name()
                + "' doesn't have field '" + expr.fieldName() + "'."));
          }
        }
      }

      @Override
      public void visitArray(ArrayNode array) {
        super.visitArray(array);
        array.set(ConcreteType.class, findArrayType(array));
      }

      private ConcreteType findArrayType(ArrayNode array) {
        List<ExprNode> expressions = array.elements();
        if (expressions.isEmpty()) {
          return types.array(types.nothing());
        }
        ConcreteType firstType = expressions.get(0).get(ConcreteType.class);
        if (firstType == null) {
          return null;
        }
        ConcreteType elemType = firstType;
        for (int i = 1; i < expressions.size(); i++) {
          ConcreteType type = expressions.get(i).get(ConcreteType.class);
          if (type == null) {
            return null;
          }
          elemType = elemType.commonSuperType(type);

          if (elemType == null) {
            errors.add(new ParseError(array,
                "Array cannot contain elements of incompatible types.\n"
                    + "First element has type '" + firstType.name()
                    + "' while element at index " + i + " has type '" + type.name() + "'."));
            return null;
          }
        }
        return types.array(elemType);
      }

      @Override
      public void visitCall(CallNode call) {
        super.visitCall(call);
        inferCallTypeAndParamAssignment(call, runtime, ast, errors);
      }

      @Override
      public void visitRef(RefNode ref) {
        super.visitRef(ref);
        ref.set(ConcreteType.class, scope.get(ref.name()));
      }

      @Override
      public void visitArg(ArgNode arg) {
        super.visitArg(arg);
        arg.set(ConcreteType.class, arg.expr().get(ConcreteType.class));
      }

      @Override
      public void visitString(StringNode string) {
        super.visitString(string);
        string.set(ConcreteType.class, types.string());
      }
    }.visitAst(ast);
    return errors;
  }
}
