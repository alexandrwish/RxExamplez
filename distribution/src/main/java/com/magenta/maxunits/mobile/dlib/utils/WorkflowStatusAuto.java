package com.magenta.maxunits.mobile.dlib.utils;

import android.annotation.SuppressLint;

import java.util.HashMap;
import java.util.HashSet;

public class WorkflowStatusAuto {

    @SuppressLint("UseSparseArrays")
    private final HashMap<Byte, Entity> entities = new HashMap<>();

    public void addEntityStatus(final byte kind, final Status status) {
        final Byte key = kind;
        Entity entity = entities.get(key);
        if (entity == null) {
            entities.put(key, entity = new Entity());
        }
        entity.addStatus(status);
    }

    public HashMap getEntities() {
        return entities;
    }

    public static final class Entity {

        public static final byte KIND_JOB = 0;
        public static final byte KIND_STOP = 1;
        private final HashSet<Status> statuses = new HashSet<Status>();

        public void addStatus(final Status status) {
            statuses.add(status);
        }

        public HashSet getStatuses() {
            return statuses;
        }
    }

    public static final class Status {

        final int value;
        final Expression expression;

        public Status(final int value, final Expression expression) {
            this.value = value;
            this.expression = expression;
        }

        public int getValue() {
            return value;
        }

        public Expression getExpression() {
            return expression;
        }
    }

    public static final class Expression {

        public static final byte GROUP_NONE = -1;
        public static final byte GROUP_STOPS = 0;

        final byte group;
        final int state;

        public Expression(final byte group, final int state) {
            this.group = group;
            this.state = state;
        }

        public byte getGroup() {
            return group;
        }

        public int getState() {
            return state;
        }
    }
}