package org.lxly.blog.util;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public final class MarkdownUtil {
    private static final Parser PARSER = Parser.builder().build();
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder().build();

    private MarkdownUtil() {}

    public static String toHtml(String markdown) {
        if (markdown == null) {
            return null;
        }
        Node document = PARSER.parse(markdown);
        return RENDERER.render(document);
    }
}
