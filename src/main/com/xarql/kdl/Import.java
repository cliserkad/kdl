package main.com.xarql.kdl;

public class Import {
	public final String pkg;
	public final String className;

	public Import(String qualifiedName) {
		this.pkg = qualifiedName.substring(0, qualifiedName.lastIndexOf('.') + 1);
		this.className = qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
	}

	public Import(String pkg, String className) {
		this.pkg = pkg;
		this.className = className;
	}

	@Override
	public String toString() {
		return qualifiedName();
	}

	public String qualifiedName() {
		return pkg + className;
	}

	public String internalName() {
		return qualifiedName().replace('.', '/');
	}

	public String internalObjectName() {
		return "L" + internalName() + ";";
	}
}
