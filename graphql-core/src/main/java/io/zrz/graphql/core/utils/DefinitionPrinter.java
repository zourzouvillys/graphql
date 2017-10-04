package io.zrz.graphql.core.utils;

import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import io.zrz.graphql.core.doc.GQLArgument;
import io.zrz.graphql.core.doc.GQLDefinitionVisitor;
import io.zrz.graphql.core.doc.GQLDirective;
import io.zrz.graphql.core.doc.GQLFieldSelection;
import io.zrz.graphql.core.doc.GQLFragmentDefinition;
import io.zrz.graphql.core.doc.GQLFragmentSpreadSelection;
import io.zrz.graphql.core.doc.GQLInlineFragmentSelection;
import io.zrz.graphql.core.doc.GQLOperationDefinition;
import io.zrz.graphql.core.doc.GQLSelectionVisitor;
import io.zrz.graphql.core.doc.GQLVariableDefinition;
import io.zrz.graphql.core.parser.SyntaxErrorException;
import io.zrz.graphql.core.value.GQLBooleanValue;
import io.zrz.graphql.core.value.GQLEnumValueRef;
import io.zrz.graphql.core.value.GQLFloatValue;
import io.zrz.graphql.core.value.GQLIntValue;
import io.zrz.graphql.core.value.GQLListValue;
import io.zrz.graphql.core.value.GQLObjectValue;
import io.zrz.graphql.core.value.GQLStringValue;
import io.zrz.graphql.core.value.GQLValueVisitor;
import io.zrz.graphql.core.value.GQLVariableRef;

public class DefinitionPrinter implements GQLDefinitionVisitor<Void>, GQLSelectionVisitor<Void>, GQLValueVisitor<Void> {

	private PrintStream strm;
	private int nodeId;

	public DefinitionPrinter(PrintStream strm) {
		this.strm = strm;
	}

	@Override
	public Void visitOperation(GQLOperationDefinition op) {

		if (op.name() != null) {

			switch (op.type()) {
			case Mutation:
				strm.print("mutation");
				break;
			case Query:
				strm.print("query");
				break;
			case Subscription:
				strm.print("subscription");
				break;
			}

			if (op.name() != null) {
				strm.print(" ");
				strm.print(op.name());
			}

		}

		for (GQLVariableDefinition arg : op.vars()) {
			strm.print('(');
			strm.print(arg.name());
			strm.print(':');
			// arg.value().apply(this);
			strm.print(')');
		}

		printDirectives(op.directives());

		strm.print("{ ");
		this.nodeId = 0;
		op.selections().forEach(sel -> sel.apply(this));
		strm.print(" }");

		return null;
	}

	@Override
	public Void visitFragment(GQLFragmentDefinition frag) {
		// TODO Auto-generated method stub
		return null;
	}

	//

	/**
	 * Print the field selection out.
	 */

	@Override
	public Void visitFieldSelection(GQLFieldSelection selection) {

		if (this.nodeId++ > 0) {
			strm.print(",");
		}

		if (selection.alias() != null) {
			strm.print(selection.alias());
			strm.print(":");
		}

		strm.print(selection.name());

		if (!selection.args().isEmpty()) {
			strm.print('(');
			int count = 0;
			for (GQLArgument arg : selection.args()) {
				if (count++ > 0) {
					strm.print(',');
				}
				strm.print(arg.name());
				strm.print(':');
				arg.value().apply(this);
			}
			strm.print(')');
		}

		if (!selection.directives().isEmpty()) {
			strm.print(" ");
			printDirectives(selection.directives());
		}

		if (!selection.selections().isEmpty()) {
			int saved = this.nodeId;
			this.nodeId = 0;
			strm.print("{");
			selection.selections().forEach(sel -> sel.apply(this));
			strm.print("}");
			// no comma is needed after braces, but we add them anyway.
			this.nodeId = saved;
		}

		return null;

	}

	private String generateDirective(GQLDirective directive) {

		StringBuilder sb = new StringBuilder();

		sb.append("@").append(directive.name());

		if (!directive.args().isEmpty()) {
			String x = directive.args().stream()
					.map(arg -> String.format("%s: %s", arg.name(), arg.value()))
					.collect(Collectors.joining(", "));
			sb.append('(').append(x).append(')');
		}

		return sb.toString();

	}

	private void printDirectives(List<GQLDirective> directives) {
		strm.print(directives.stream().map(this::generateDirective).collect(Collectors.joining(" ")));
	}

	@Override
	public Void visitFragmentSelection(GQLFragmentSpreadSelection selection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitInlineFragment(GQLInlineFragmentSelection selection) {
		// TODO Auto-generated method stub
		return null;
	}

	//

	@Override
	public Void visitVarValue(GQLVariableRef value) {
		strm.print('$');
		strm.print(value.name());
		return null;
	}

	@Override
	public Void visitObjectValue(GQLObjectValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitListValue(GQLListValue value) {
		AtomicInteger val = new AtomicInteger(0);
		value.values().forEach(item -> {
			if (val.incrementAndGet() > 1) {
				strm.print(',');
			}
			item.apply(this);
		});
		return null;
	}

	@Override
	public Void visitBooleanValue(GQLBooleanValue value) {
		strm.print(value == GQLBooleanValue.TRUE ? "true" : "false");
		return null;
	}

	@Override
	public Void visitIntValue(GQLIntValue value) {
		strm.print(value.value());
		return null;
	}

	@Override
	public Void visitStringValue(GQLStringValue value) {
		strm.print('"');
		strm.print(value.value().replace("\\", "\\\\").replace("\"", "\\\""));
		strm.print('"');
		return null;
	}

	@Override
	public Void visitFloatValue(GQLFloatValue value) {
		strm.print(value.value());
		return null;
	}

	@Override
	public Void visitEnumValueRef(GQLEnumValueRef value) {
		strm.print(value.value());
		return null;
	}

}
