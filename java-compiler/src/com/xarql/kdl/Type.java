package com.xarql.kdl;

import com.xarql.kdl.ir.*;
import com.xarql.kdl.names.*;
import com.xarql.kdl.antlr.kdl;
import com.xarql.smp.Path;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Stores a Type's metadata
 */
public class Type implements ToDetails  {
    public static final char PATH_SEPARATOR = Path.PATH_SEPARATOR;
    public static final Type OBJECT = Type.get(Object.class);

    private static HashMap<Path, Type> knownTypes;

    public Path name;
    public TrackedMap<Constant, kdl.ReservationContext> constants = new TrackedMap<>();
    public TrackedMap<StaticField, kdl.ReservationContext> fields = new TrackedMap<>();
    public Set<MethodHeader> methods = new HashSet<>();

    private Type() {
        // do nothing
    }

    public Type init(Path name) {
        this.name = name;
        return this;
    }

    public static Path pathFor(Class<?> c) {
        BaseType base = BaseType.matchClassStrict(c);
        if(base != null)
            return base.path;
        else
            return new Path(c.getCanonicalName().replace(CompilationUnit.JAVA_SOURCE_SEPARATOR, PATH_SEPARATOR));
    }

    public Type init(Class<?> c) {
        init(pathFor(c));
        for(Method method : c.getMethods()) {
            methods.add(new MethodHeader(this, method));
        }
        for(Field field : c.getFields()) {
            final Details details = new Details(field.getName(), new TypeDescriptor(field.getType()), (field.getModifiers() & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL);
            if((field.getModifiers() & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
                fields.add(new StaticField(details, new TypeDescriptor(c)), null);
            } else {
                fields.add(new ObjectField(details, new TypeDescriptor(c)), null);
            }
        }
        return this;
    }

    public static Type get(Class<?> c) {
        final Path p = pathFor(c);
        if(getKnownTypes().containsKey(p))
            return getKnownTypes().get(p);
        else {
            Type t = new Type();
            getKnownTypes().put(p, t);
            t.init(c);
            return t;
        }
    }

    public static Type get(Path name) {
        if(getKnownTypes().containsKey(name))
            return getKnownTypes().get(name);
        else {
            Type t = new Type();
            getKnownTypes().put(name, t);
            t.init(name);
            return t;
        }
    }

    private static HashMap<Path, Type> getKnownTypes() {
        if(knownTypes == null)
            knownTypes = new HashMap<>();
        return knownTypes;
    }

    public TrackedMap<Identifier, Member> members() {
        TrackedMap<Identifier, Member> out = new TrackedMap<>();
        for(Constant c : constants.keys())
            out.add(c.name, c);
        for(StaticField f : fields.keys())
            out.add(f.name, f);
        for(MethodHeader m : methods)
            out.add(m.details().name, m);
        return out;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o != null && o instanceof Type) {
            Type that = (Type) o;
            return name.equals(that.name);
        } else if(o instanceof Path) {
            Path p = (Path) o;
            return name.equals(p);
        } else if(o instanceof Class) {
            Class<?> c = (Class<?>) o;
            return name.equals(pathFor(c));
        } else
            return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name.toString();
    }

    @Override
    public Type toType() {
        return this;
    }

    @Override
    public boolean isBaseType() {
        return false;
    }

    @Override
    public BaseType toBaseType() {
        return null;
    }

    @Override
    public Details toDetails() {
        // mutable is false as the class file is not allowed to change during runtime
        return new Details(name.last(), toTypeDescriptor(), false);
    }

    @Override
    public TypeDescriptor toTypeDescriptor() {
        return new TypeDescriptor(this);
    }

}
