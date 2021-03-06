package com.marvin.component.templating.extension;

import com.marvin.component.templating.node.operator.BinaryOperator;
import com.marvin.component.templating.node.operator.UnaryOperator;
import com.marvin.component.templating.tokenParser.TokenParser;
import java.util.List;
import java.util.Map;

public interface Extension {

    /**
     * Use this method to provide custom filters.
     *
     * @return A list of filters. It is okay to return null.
     */
    Map<String, Filter> getFilters();

    /**
     * Use this method to provide custom tests.
     *
     * @return A list of tests. It is okay to return null.
     */
    Map<String, Test> getTests();

    /**
     * Use this method to provide custom functions.
     *
     * @return A list of functions. It is okay to return null.
     */
    Map<String, Function> getFunctions();

    /**
     * Use this method to provide custom tags.
     *
     * A TokenParser is used to parse a stream of tokens into Nodes which are
     * then responsible for compiling themselves into Java.
     *
     * @return A list of TokenParsers. It is okay to return null.
     */
    List<TokenParser> getTokenParsers();

    /**
     * Use this method to provide custom binary operators.
     *
     * @return A list of Operators. It is okay to return null;
     */
    List<BinaryOperator> getBinaryOperators();

    /**
     * Use this method to provide custom unary operators.
     *
     * @return A list of Operators. It is okay to return null;
     */
    List<UnaryOperator> getUnaryOperators();

    /**
     * Use this method to provide variables available to all templates
     *
     * @return Map of global variables available to all templates
     */
    Map<String, Object> getGlobalVariables();

    /**
     * Node visitors will travel the AST tree during the compilation phase.
     *
     * @return a list of node visitors
     */
    List<NodeVisitorFactory> getNodeVisitors();
}
