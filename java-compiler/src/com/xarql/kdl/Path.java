package com.xarql.kdl;

import com.xarql.kdl.names.BaseType;

import java.io.Serializable;
import java.util.Arrays;

public class Path implements Serializable {
    // TODO: increment when this file is edited
    private static final long serialVersionUID = 1L;

    public static final char PATH_SEPARATOR = '/';

    private final String[] parts;

    public Path() {
        this(new String[0]);
    }

    public Path(String...parts) {
        if(parts == null || parts.length == 0)
            this.parts = new String[0];
        else
            this.parts = parts;
        if(parts == null)
            throw new IllegalArgumentException("parts may not be null");
    }

    public Path(String raw) {
        this(raw.split("" + PATH_SEPARATOR));
        System.out.println("raw: " + raw + "\nis" + Arrays.toString(raw.split("" + PATH_SEPARATOR)));
    }

    public static Path forClass(Class<?> c) {
        BaseType base = BaseType.matchClassStrict(c);
        if(base != null)
            return base.toType().name;
        else
            return new Path(c.getCanonicalName().replace(CompilationUnit.JAVA_SOURCE_SEPARATOR, PATH_SEPARATOR));
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

    public Path prepend(Path p) {
        String[] newParts = getParts(p.size());
        for(int i = 0; i < p.size(); i++)
            newParts[i] = p.part(i);
        return new Path(newParts);
    }

    public Path prepend(String s) {
        return prepend(new Path(s));
    }

    public Path append(Path p) {
        String[] newParts = getParts(-p.size());
        for(int i = 0; i < p.size(); i++)
            newParts[i + size()] = p.part(i);
        return new Path(newParts);
    }

    public Path append(String s) {
        return append(new Path(s));
    }

    public String[] getParts() {
        return getParts(0);
    }

    /**
     * Creates a copy of this StringPath's parts.
     *
     * @param offset Amount of null elements to prepend
     */
    public String[] getParts(int offset) {
        final int diff = Math.abs(offset);
        offset = Math.max(0, offset);
        String[] parts = new String[size() + diff];
        System.arraycopy(this.parts, 0, parts, offset, size());
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
        } else if(o instanceof String) {
            Path that = new Path((String) o);
            return equals(that);
        } else
            return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getParts());
    }

}
