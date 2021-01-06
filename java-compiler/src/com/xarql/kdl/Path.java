package com.xarql.kdl;

import com.xarql.kdl.names.BaseType;

import java.util.Arrays;

public class Path {
    public static final char PATH_SEPARATOR = '/';

    private final String[] parts;

    public Path() {
        this.parts = new String[0];
    }

    public Path(String...parts) {
        if(parts == null || parts.length == 0)
            this.parts = new String[0];
        else
            this.parts = parts;
    }

    public Path(String raw) {
        this(raw.split("" + PATH_SEPARATOR));
    }

    public static Path forClass(Class<?> c) {
        BaseType base = BaseType.matchClassStrict(c);
        if(base != null)
            return base.rep;
        else
            return new Path(c.getName().replace(CompilationUnit.JAVA_SOURCE_SEPARATOR, PATH_SEPARATOR));
    }

    public String part(int index) {
        return parts[index];
    }

    public String first() {
        return parts[0];
    }

    public String last() {
        return parts[size() - 1];
    }

    public int size() {
        return parts.length;
    }

    public Path prepend(String s) {
        String[] parts = getParts(1, size() + 1);
        parts[0] = s;
        return new Path(parts);
    }

    public Path append(String s) {
        String[] parts = getParts(0, size() + 1);
        parts[parts.length - 1] = s;
        return new Path(parts);
    }

    public String[] getParts() {
        return getParts(0, size());
    }

    /**
     * Creates a copy of this StringPath's parts.
     *
     * @param offset Amount of null elements to prepend
     * @param length Desired length of the destination array
     */
    public String[] getParts(int offset, int length) {
        String[] parts = new String[length];
        System.arraycopy(this.parts, 0, parts, offset, length);
        return parts;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < parts.length; i++) {
            builder.append(parts[i]);
            if(i < parts.length - 1)
                builder.append(PATH_SEPARATOR);
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        else if(o instanceof Path) {
            Path that = (Path) o;
            return Arrays.equals(getParts(), that.getParts());
        } else
            return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getParts());
    }
}
