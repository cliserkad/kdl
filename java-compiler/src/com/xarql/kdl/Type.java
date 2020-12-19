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
    public static final char SOURCE_SEPARATOR = '/';

    public final String pkg;
    public final String name;

    public TrackedMap<Constant, kdl.ConstantDefContext> constants = new TrackedMap<>();
    public TrackedMap<StaticField, kdl.FieldDefContext> fields = new TrackedMap<>();
    public Set<MethodHeader> methods = new HashSet<>();
    public Set<Type> imports = new HashSet<>();

    public Type(String pkg, String name) {
        this.pkg = pkg;
        this.name = name;
    }

    public Type() {
        this(null, null);
    }

    public Type(Class<?> clazz) {
        pkg = clazz.getPackageName();
        name = clazz.getSimpleName();
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
        for(Type t : imports)
            out.add(t.details().name, t);
        return out;
    }

    public Type resolveImport(String name) {
        for(Type t : imports)
            if(t.name.equals(name))
                return t;
        return null;
    }

    public boolean isTypeImported(String name) {
        return resolveImport(name) != null;
    }

    public Type resolveImportOrFail(String name) throws SymbolResolutionException {
        final Type out = resolveImport(name);
        if(out != null)
            return out;
        else
            throw new SymbolResolutionException("Couldn't recognize type: " + name);
    }

    /**
     * Returns this Type's name appended to its package path, with appropriate addition of forward slashes.
     * This is how the JVM expects its names internally, and is equivalent to calling toInternalName().nameString()
     * @return JVM compatible name
     */
    public String qualifiedName() {
        if(name == null)
            throw new NullPointerException("A CustomClass' name must not be null");
        if(name.trim().isEmpty())
            throw new IllegalStateException("A CustomClass' name must not be empty");

        if(isPackaged())
            return pkg + SOURCE_SEPARATOR + name;
        else
            return name;
    }

    public boolean isNamed() {
        return name != null && !name.trim().isEmpty();
    }

    public boolean isPackaged() {
        return pkg != null && !pkg.trim().isEmpty();
    }

    public void copyTo(Type other) {
        other.constants = constants;
        other.fields = fields;
        other.methods = methods;
        other.imports = imports;
    }

    /**
     * Creates a new DynamicClass, using the name of this DynamicClass
     * @param pkg new pkg
     * @return new DynamicClass
     */
    public Type withPkg(String pkg) {
        Type out = new Type(pkg, name);
        copyTo(out);
        return out;
    }

    /**
     * Creates a new DynamicClass, using the pkg of this DynamicClass
     * @param name new name
     * @return new DynamicClass
     */
    public Type withName(String name) {
        Type out = new Type(pkg, name);
        copyTo(out);
        return out;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o != null && o instanceof Type) {
            Type that = (Type) o;
            return Objects.equals(pkg, that.pkg) &&
                    Objects.equals(name, that.name);
        } else
            return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pkg, name);
    }

    @Override
    public String toString() {
        return toInternalName().objectString();
    }

    @Override
    public InternalName toInternalName() {
        return new InternalName(this);
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
        return new Details(qualifiedName(), toInternalName(), false);
    }

}
