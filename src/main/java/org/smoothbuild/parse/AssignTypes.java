package org.smoothbuild.parse;

import static java.util.stream.Collectors.toMap;
import static org.smoothbuild.lang.function.base.Scope.scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.ParameterInfo;
import org.smoothbuild.lang.function.base.Scope;
import org.smoothbuild.lang.runtime.Functions;
import org.smoothbuild.lang.runtime.RuntimeTypes;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.ArrayNode;
import org.smoothbuild.parse.ast.ArrayTypeNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.ExprNode;
import org.smoothbuild.parse.ast.FieldNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.ParamNode;
import org.smoothbuild.parse.ast.RefNode;
import org.smoothbuild.parse.ast.StringNode;
import org.smoothbuild.parse.ast.StructNode;
import org.smoothbuild.parse.ast.TypeNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

public class AssignTypes {
  private final RuntimeTypes types;

  @Inject
  public AssignTypes(RuntimeTypes types) {
    this.types = types;
  }

  public List<ParseError> assignTypes(Functions functions, Ast ast) {
    List<ParseError> errors = new ArrayList<>();
    Map<String, Type> functionTypes = functions
        .all()
        .stream()
        .collect(toMap(Function::name, Function::type));
    new AstVisitor() {
      Scope<Type> scope;

      @Override
      public void visitStruct(StructNode struct) {
        super.visitStruct(struct);
        ImmutableMap.Builder<String, Type> builder = ImmutableMap.builder();
        for (FieldNode field : struct.fields()) {
          Type type = field.get(Type.class);
          if (type == null) {
            return;
          } else {
            builder.put(field.name(), type);
          }
        }
        types.struct(struct.name(), builder.build());
      }

      @Override
      public void visitField(FieldNode field) {
        super.visitField(field);
        field.set(Type.class, field.type().get(Type.class));
      }

      @Override
      public void visitFunc(FuncNode func) {
        visitParams(func.params());

        scope = scope();
        func.params()
            .stream()
            .forEach(p -> scope.add(p.name(), p.get(Type.class)));
        if (!func.isNative()) {
          visitExpr(func.expr());
        }
        scope = null;

        Type type = funcType(func);
        func.set(Type.class, type);
        functionTypes.put(func.name(), type);
        List<ParameterInfo> parameters = createParameters(func.params());
        if (parameters != null) {
          func.set(List.class, parameters);
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
              errors.add(new ParseError(func, "Type of function's '" + func.name()
                  + "' expression is " + exprType.name()
                  + " which is not convertable to function's declared result type " + type.name()
                  + "."));
            }
            return type;
          } else {
            return exprType;
          }
        }
      }

      private List<ParameterInfo> createParameters(List<ParamNode> params) {
        Builder<ParameterInfo> builder = ImmutableList.builder();
        for (ParamNode param : params) {
          if (param.has(ParameterInfo.class)) {
            builder.add(param.get(ParameterInfo.class));
          } else {
            return null;
          }
        }
        return builder.build();
      }

      @Override
      public void visitParam(ParamNode param) {
        super.visitParam(param);
        Type type = param.type().get(Type.class);
        param.set(Type.class, type);
        if (type != null) {
          ParameterInfo info = new ParameterInfo(
              param.get(Type.class), param.name(), !param.hasDefaultValue());
          param.set(ParameterInfo.class, info);
          if (param.hasDefaultValue() && param.defaultValue().get(Type.class) != null) {
            Type valueType = param.defaultValue().get((Type.class));
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
        type.set(Type.class, createType(type));
      }

      private Type createType(TypeNode type) {
        if (type instanceof ArrayTypeNode) {
          TypeNode elementType = ((ArrayTypeNode) type).elementType();
          Type inferredElemType = createType(elementType);
          return inferredElemType == null ? null : types.array(inferredElemType);
        }
        return types.getType(type.name());
      }

      @Override
      public void visitArray(ArrayNode array) {
        super.visitArray(array);
        array.set(Type.class, findArrayType(array));
      }

      private Type findArrayType(ArrayNode array) {
        List<ExprNode> expressions = array.elements();
        if (expressions.isEmpty()) {
          return types.array(types.nothing());
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
        call.set(Type.class, functionTypes.get(call.name()));
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
        string.set(Type.class, types.string());
      }
    }.visitAst(ast);
    return errors;
  }
}
