package com.xarql.kdl;

import com.xarql.kdl.ir.*;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.Details;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.ToName;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable version of a custom class's metadata
 */
public class Type implements ToName, Member {
    public static final char PATH_SEPARATOR = Path.PATH_SEPARATOR;

    public final InternalName name;
    public TrackedMap<Constant, kdl.ReservationContext> constants = new TrackedMap<>();
    public TrackedMap<StaticField, kdl.ReservationContext> fields = new TrackedMap<>();
    public Set<MethodHeader> methods = new HashSet<>();

    public Type(InternalName name) {
        this.name = name;
    }

    public Type(Class<?> clazz) {
        this.name = new InternalName(clazz);
        for(Method method : clazz.getMethods()) {
            methods.add(new MethodHeader(clazz, method));
        }
        for(Field field : clazz.getFields()) {
            final Details details = new Details(field.getName(), new InternalName(field.getType()), (field.getModifiers() & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL);
            if((field.getModifiers() & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
                fields.add(new StaticField(details, new InternalName(clazz)), null);
            } else {
                fields.add(new ObjectField(details, new InternalName(clazz)), null);
            }
        }
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

    public void copyTo(Type other) {
        other.constants = constants;
        other.fields = fields;
        other.methods = methods;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o != null && o instanceof Type) {
            Type that = (Type) o;
            return name.equals(that.name);
        } else
            return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return toInternalName().arrayName();
    }

    @Override
    public InternalName toInternalName() {
        return name;
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
    public Type push(Actor actor) throws Exception {
        // do nothing
        return this;
    }

    @Override
    public Details details() {
        // mutable is false as the class file is not allowed to change during runtime
        return new Details(toInternalName().name(), toInternalName(), false);
    }

}
