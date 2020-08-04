package org.smoothbuild.lang.parse.ast;

import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.lang.parse.LocationHelpers.locationOf;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.sane;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.antlr.lang.SmoothBaseVisitor;
import org.smoothbuild.antlr.lang.SmoothParser.AccessorContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArgContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArgListContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArrayTypeContext;
import org.smoothbuild.antlr.lang.SmoothParser.CallContext;
import org.smoothbuild.antlr.lang.SmoothParser.ExprContext;
import org.smoothbuild.antlr.lang.SmoothParser.FieldContext;
import org.smoothbuild.antlr.lang.SmoothParser.FieldListContext;
import org.smoothbuild.antlr.lang.SmoothParser.FuncContext;
import org.smoothbuild.antlr.lang.SmoothParser.ModuleContext;
import org.smoothbuild.antlr.lang.SmoothParser.NameContext;
import org.smoothbuild.antlr.lang.SmoothParser.ParamContext;
import org.smoothbuild.antlr.lang.SmoothParser.ParamListContext;
import org.smoothbuild.antlr.lang.SmoothParser.PipeContext;
import org.smoothbuild.antlr.lang.SmoothParser.StructContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeIdentifierContext;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.ModulePath;

public class AstCreator {
  public static Ast fromParseTree(ModulePath path, ModuleContext module) {
    List<FuncNode> nodes = new ArrayList<>();
    List<StructNode> structs = new ArrayList<>();
    new SmoothBaseVisitor<Void>() {
      private Set<String> visibleParams = new HashSet<>();

      @Override
      public Void visitStruct(StructContext struct) {
        String name = struct.TYPE_IDENTIFIER().getText();
        Location location = locationOf(path, struct.TYPE_IDENTIFIER().getSymbol());
        List<ItemNode> fields = createFields(struct.fieldList());
        structs.add(new StructNode(name, fields, location));
        return null;
      }

      private List<ItemNode> createFields(FieldListContext fieldList) {
        var result = new ArrayList<ItemNode>();
        if (fieldList != null) {
          List<FieldContext> saneList = sane(fieldList.field());
          for (int i = 0; i < saneList.size(); i++) {
            result.add(createField(i, saneList.get(i)));
          }
        }
        return result;
      }

      private ItemNode createField(int index, FieldContext field) {
        TypeNode type = createType(field.type());
        NameContext nameContext = field.name();
        String name = nameContext.getText();
        Location location = locationOf(path, nameContext);
        return new ItemNode(index, type, name, null, location);
      }

      @Override
      public Void visitFunc(FuncContext func) {
        TypeNode type = func.type() == null ? null : createType(func.type());
        NameContext nameContext = func.name();
        String name = nameContext.getText();
        List<ItemNode> params = createParams(func.paramList());
        visibleParams = paramNames(params);
        ExprNode pipe = func.pipe() == null ? null : createPipe(func.pipe());
        visitChildren(func);
        visibleParams = new HashSet<>();
        nodes.add(new FuncNode(type, name, params, pipe, locationOf(path, nameContext)));
        return null;
      }

      private Set<String> paramNames(List<ItemNode> params) {
        return params
            .stream()
            .map(NamedNode::name)
            .collect(toSet());
      }

      private List<ItemNode> createParams(ParamListContext paramList) {
        ArrayList<ItemNode> result = new ArrayList<>();
        if (paramList != null) {
          List<ParamContext> paramContexts = sane(paramList.param());
          for (int i = 0; i < paramContexts.size(); i++) {
            result.add(createParam(i, paramContexts.get(i)));
          }
        }
        return result;
      }

      private ItemNode createParam(int index, ParamContext param) {
        TypeNode type = createType(param.type());
        String name = param.name().getText();
        Location location = locationOf(path, param);
        ExprNode defaultValue = param.expr() != null
            ? createExpr(param.expr())
            : null;
        return new ItemNode(index, type, name, defaultValue, location);
      }

      private ExprNode createPipe(PipeContext pipe) {
        ExprContext initialExpression = pipe.expr();
        ExprNode result = createExpr(initialExpression);
        List<CallContext> calls = pipe.call();
        for (int i = 0; i < calls.size(); i++) {
          CallContext call = calls.get(i);
          // Location of nameless piped argument is set to the location of pipe character '|'.
          Location location = locationOf(path, pipe.p.get(i));
          List<ArgNode> args = new ArrayList<>();
          args.add(new ArgNode(null, result, location));
          args.addAll(createArgList(call.argList()));
          String name = call.name().getText();
          result = new CallNode(name, args, locationOf(path, call.name()));
        }
        return result;
      }

      private ExprNode createExpr(ExprContext expr) {
        if (expr.accessor() != null) {
          ExprNode structExpr = createExpr(expr.expr());
          AccessorContext accessor = expr.accessor();
          String name = accessor.name().getText();
          return new AccessorNode(structExpr, name, locationOf(path, accessor));
        }
        if (expr.array() != null) {
          List<ExprNode> elements = map(expr.array().pipe(), this::createPipe);
          return new ArrayNode(elements, locationOf(path, expr));
        }
        if (expr.call() != null) {
          CallContext call = expr.call();
          String name = call.name().getText();
          Location location = locationOf(path, call.name());
          if (visibleParams.contains(name)) {
            boolean hasParentheses = call.p != null;
            return new RefNode(name, hasParentheses, location);
          } else {
            List<ArgNode> args = createArgList(call.argList());
            return new CallNode(name, args, location);
          }
        }
        if (expr.STRING() != null) {
          String quotedString = expr.STRING().getText();
          return new StringNode(quotedString.substring(1, quotedString.length() - 1),
              locationOf(path, expr));
        }
        if (expr.BLOB() != null) {
          return new BlobNode(
              expr.BLOB().getText().substring(2), locationOf(path, expr));
        }
        throw new RuntimeException("Illegal parse tree: " + ExprContext.class.getSimpleName()
            + " without children.");
      }

      private List<ArgNode> createArgList(ArgListContext argList) {
        List<ArgNode> result = new ArrayList<>();
        if (argList != null) {
          List<ArgContext> args = argList.arg();
          for (int i = 0; i < args.size(); i++) {
            ArgContext arg = args.get(i);
            PipeContext pipe = arg.pipe();
            NameContext nameContext = arg.name();
            String name = nameContext == null ? null : nameContext.getText();
            ExprNode exprNode = createPipe(pipe);
            result.add(new ArgNode(name, exprNode, locationOf(path, arg)));
          }
        }
        return result;
      }

      private TypeNode createType(TypeContext type) {
        if (type instanceof TypeIdentifierContext typeIdentifier) {
          return createType(typeIdentifier);
        }
        if (type instanceof ArrayTypeContext arrayType) {
          return createArrayType(arrayType);
        }
        throw new RuntimeException("Illegal parse tree: " + TypeContext.class.getSimpleName()
            + " without children.");
      }

      private TypeNode createType(TypeIdentifierContext type) {
        return new TypeNode(type.getText(), locationOf(path, type.TYPE_IDENTIFIER().getSymbol()));
      }

      private TypeNode createArrayType(ArrayTypeContext arrayType) {
        TypeNode elementType = createType(arrayType.type());
        return new ArrayTypeNode(elementType, locationOf(path, arrayType));
      }
    }.visit(module);
    return new Ast(structs, nodes);
  }
}
