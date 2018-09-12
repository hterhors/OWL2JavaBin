package de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.java;

import java.util.ArrayList;
import java.util.List;

public class JavaMethodParameter {

	final private List<String> imports;
	final private String type;
	final private String name;

	public JavaMethodParameter(String type, String name) {
		this.imports = new ArrayList<>();
		this.imports.add(type);
		this.type = type;
		this.name = name;
	}

	public List<String> getImports() {
		return imports;
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JavaMethodParameter other = (JavaMethodParameter) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Argument [imports=" + imports + ", type=" + type + ", name=" + name + "]";
	}

	public StringBuilder toJavaString() {
		StringBuilder builder = new StringBuilder();
		builder.append(type);
		builder.append(" ");
		builder.append(name);
		return builder;
	}

}
