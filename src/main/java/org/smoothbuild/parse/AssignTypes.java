package org.smoothbuild.parse;

import static java.util.stream.Collectors.toMap;
import static org.smoothbuild.lang.function.base.Scope.scope;
import static org.smoothbuild.lang.type.Types.NIL;
import static org.smoothbuild.lang.type.Types.NON_INFERABLE;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.lang.type.Types.commonSuperType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.function.base.Scope;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.ArrayNode;
import org.smoothbuild.parse.ast.ArrayTypeNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.CallNode.ParamRefFlag;
import org.smoothbuild.parse.ast.ExprNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.ParamNode;
import org.smoothbuild.parse.ast.StringNode;
import org.smoothbuild.parse.ast.TypeNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class AssignTypes {
  public static List<ParseError> assignTypes(Functions functions, Ast ast) {
    List<ParseError> errors = new ArrayList<>();
    Map<Name, Type> functionTypes = functions
        .nameToFunctionMap()
        .entrySet()
        .stream()
        .collect(toMap(e -> e.getKey(), e -> e.getValue().type()));
    new AstVisitor() {
      Scope<Type> scope;

      public void visitFunction(FuncNode function) {
        visitName(function.name());
        visitParams(function.params());

        scope = scope();
        function.params()
            .stream()
            .forEach(p -> scope.add(p.name(), p.get(Type.class)));
        visitExpr(function.expr());
        scope = null;

        Type type = function.expr().get(Type.class);
        function.set(Type.class, type);
        functionTypes.put(function.name(), type);
        List<Parameter> parameters = createParameters(function.params());
        if (parameters != null) {
          function.set(List.class, parameters);
        }
      }

      private List<Parameter> createParameters(List<ParamNode> params) {
        Builder<Parameter> builder = ImmutableList.builder();
        for (ParamNode param : params) {
          if (param.has(Parameter.class)) {
            builder.add(param.get(Parameter.class));
          } else {
            return null;
          }
        }
        return builder.build();
      }

      public void visitParam(ParamNode param) {
        super.visitParam(param);
        Type type = param.type().get(Type.class);
        param.set(Type.class, type);
        if (type != NON_INFERABLE) {
          param.set(Parameter.class, new Parameter(param.get(Type.class), param.name(), null));
        }
      }

      public void visitType(TypeNode type) {
        super.visitType(type);
        Type inferredType = createType(type);
        type.set(Type.class, inferredType == null ? NON_INFERABLE : inferredType);
      }

      private Type createType(TypeNode type) {
        if (type instanceof ArrayTypeNode) {
          TypeNode elementType = ((ArrayTypeNode) type).elementType();
          return Types.arrayOf(createType(elementType));
        }
        Type result = Types.basicTypeFromString(type.name());
        if (result == null) {
          errors.add(new ParseError(type.location(), "Unknown type '" + type.name() + "'."));
        }
        return result;
      }

      public void visitArray(ArrayNode array) {
        super.visitArray(array);
        array.set(Type.class, findArrayType(array));
      }

      private Type findArrayType(ArrayNode array) {
        List<ExprNode> expressions = array.elements();
        if (expressions.isEmpty()) {
          return NIL;
        }
        Type firstType = expressions.get(0).get(Type.class);
        if (firstType == NON_INFERABLE) {
          return NON_INFERABLE;
        }
        Type superType = firstType;
        for (int i = 1; i < expressions.size(); i++) {
          Type type = expressions.get(i).get(Type.class);
          if (type == NON_INFERABLE) {
            return NON_INFERABLE;
          }
          superType = commonSuperType(superType, type);

          if (superType == null) {
            errors.add(new ParseError(array,
                "Array cannot contain elements of incompatible types.\n"
                    + "First element has type '" + firstType + "' while element at index " + i
                    + " has type '" + type + "'."));
            return NON_INFERABLE;
          }
        }
        ArrayType arrayType = Types.arrayOf(superType);
        if (arrayType == null) {
          errors.add(new ParseError(array, "Array cannot contain element with type '"
              + superType + "'. Only following types are allowed: " + Types.basicTypes()
              + "."));
          return NON_INFERABLE;
        }
        return arrayType;
      }

      public void visitCall(CallNode call) {
        super.visitCall(call);
        if (call.has(ParamRefFlag.class)) {
          call.set(Type.class, scope.get(call.name()));
        } else {
          call.set(Type.class, functionTypes.get(call.name()));
        }
      }

      public void visitArg(ArgNode arg) {
        super.visitArg(arg);
        arg.set(Type.class, arg.expr().get(Type.class));
      }

      public void visitString(StringNode string) {
        super.visitString(string);
        string.set(Type.class, STRING);
      }
    }.visitAst(ast);
    return errors;
  }
}
