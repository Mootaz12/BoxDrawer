package com.boxdrawer.command.parser;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Turns a sentence into a structured command with simple regular expressions.
 * Supported: "draw a grid of 4 by 4 squares", "add a red circle labeled start",
 * "link square 1 to square 6 label depends on".
 */
@Component
public class CommandParser {

    private static final String COLORS = "red|blue|green|yellow|orange|purple|pink|black|gray|white";
    private static final String SHAPES = "square|circle|triangle";

    private static final Pattern GRID = Pattern.compile(
            "grid\\s+of\\s+(\\d+)\\s*(?:x|by)\\s*(\\d+)\\s+(?:(" + COLORS + ")\\s+)?(" + SHAPES + ")s?");

    private static final Pattern LINK = Pattern.compile(
            "(?:link|connect|map)\\s+(?:(?:" + SHAPES + "|shape)\\s+)?(\\d+)\\s+(?:to|with)\\s+(?:(?:"
                    + SHAPES + "|shape)\\s+)?(\\d+)(?:\\s+(?:with label|labeled|label|as)\\s+(.+))?");

    private static final Pattern SHAPE = Pattern.compile(
            "(?:draw|add|create|make)\\s+(?:an?\\s+)?(?:(" + COLORS + ")\\s+)?(" + SHAPES
                    + ")(?:\\s+(?:labeled|label|named)\\s+(.+))?");

    public ParsedCommand parse(String text) {
        String t = text == null ? "" : text.trim().toLowerCase();

        Matcher m = GRID.matcher(t);
        if (m.find()) {
            return new ParsedCommand(ParsedCommand.Action.GRID, m.group(4), m.group(3),
                    Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), null, null, null);
        }
        m = LINK.matcher(t);
        if (m.find()) {
            return new ParsedCommand(ParsedCommand.Action.LINK, null, null, null, null,
                    Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), clean(m.group(3)));
        }
        m = SHAPE.matcher(t);
        if (m.find()) {
            return new ParsedCommand(ParsedCommand.Action.ADD_SHAPE, m.group(2), m.group(1),
                    null, null, null, null, clean(m.group(3)));
        }
        return ParsedCommand.unknown();
    }

    private String clean(String s) {
        if (s == null) {
            return null;
        }
        return s.trim().replaceAll("^['\"]+|['\"]+$", "");
    }
}
