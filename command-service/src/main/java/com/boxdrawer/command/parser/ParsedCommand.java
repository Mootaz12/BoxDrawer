package com.boxdrawer.command.parser;

public record ParsedCommand(Action action, String type, String color, Integer rows, Integer cols,
                            Integer from, Integer to, String label) {

    public enum Action { ADD_SHAPE, GRID, LINK, UNKNOWN }

    public static ParsedCommand unknown() {
        return new ParsedCommand(Action.UNKNOWN, null, null, null, null, null, null, null);
    }
}
