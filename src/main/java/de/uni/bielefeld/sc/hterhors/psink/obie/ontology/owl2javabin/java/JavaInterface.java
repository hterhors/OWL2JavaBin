package de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.java;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.enums.EAccessType;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.enums.EAnnotation;

import java.util.Set;

public class JavaInterface {

	final boolean isStatic;
	final EAccessType accessType;
	final private Set<String> extendsClasses;
	final private String interfaceName;
	final private Set<JavaMethod> methods;
	final private Set<String> imports;
	final private String packageName;
	final private Set<JavaInterface> innerInterfaces;
	final private String additionalContent;

	final private Set<JavaAnnotation> annotations;
	final private Map<String, List<String>> documentation;

	public JavaInterface(Map<String, List<String>> documentation, Set<JavaAnnotation> annotations,
			final String additionalContent, boolean isStatic, EAccessType accessType, Set<String> extendsClasses,
			String interfaceName, Set<JavaMethod> methods, Set<String> imports, String classPackageName,
			Set<JavaInterface> innerinterfaces) {
		this.documentation = documentation;
		this.isStatic = isStatic;
		this.accessType = accessType;
		this.extendsClasses = extendsClasses;
		this.interfaceName = interfaceName;
		this.methods = methods;
		this.packageName = classPackageName;
		this.innerInterfaces = innerinterfaces;
		this.imports = imports;
		this.annotations = annotations;
		this.additionalContent = additionalContent;

	}

	public JavaInterface(Map<String, List<String>> documentation, Set<JavaAnnotation> annotations, boolean isStatic,
			EAccessType accessType, Set<String> extendsClasses, String interfaceName, Set<JavaMethod> methods,
			Set<String> imports, String classPackageName, Set<JavaInterface> innerinterfaces) {
		this.documentation = documentation;
		this.isStatic = isStatic;
		this.accessType = accessType;
		this.extendsClasses = extendsClasses;
		this.interfaceName = interfaceName;
		this.methods = methods;
		this.packageName = classPackageName;
		this.innerInterfaces = innerinterfaces;
		this.imports = imports;
		this.additionalContent = null;
		this.annotations = annotations;

	}

	public boolean isStatic() {
		return isStatic;
	}

	public EAccessType getAccessType() {
		return accessType;
	}

	public Set<String> getExtendsClasses() {
		return extendsClasses;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public Set<JavaMethod> getMethods() {
		return methods;
	}

	public Set<String> getImports() {
		return imports;
	}

	public String getClassPackageName() {
		return packageName;
	}

	public Set<JavaInterface> getInnerinterfaces() {
		return innerInterfaces;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessType == null) ? 0 : accessType.hashCode());
		result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
		result = prime * result + ((extendsClasses == null) ? 0 : extendsClasses.hashCode());
		result = prime * result + ((imports == null) ? 0 : imports.hashCode());
		result = prime * result + ((innerInterfaces == null) ? 0 : innerInterfaces.hashCode());
		result = prime * result + ((interfaceName == null) ? 0 : interfaceName.hashCode());
		result = prime * result + (isStatic ? 1231 : 1237);
		result = prime * result + ((methods == null) ? 0 : methods.hashCode());
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
		JavaInterface other = (JavaInterface) obj;
		if (accessType != other.accessType)
			return false;
		if (packageName == null) {
			if (other.packageName != null)
				return false;
		} else if (!packageName.equals(other.packageName))
			return false;
		if (extendsClasses == null) {
			if (other.extendsClasses != null)
				return false;
		} else if (!extendsClasses.equals(other.extendsClasses))
			return false;
		if (imports == null) {
			if (other.imports != null)
				return false;
		} else if (!imports.equals(other.imports))
			return false;
		if (innerInterfaces == null) {
			if (other.innerInterfaces != null)
				return false;
		} else if (!innerInterfaces.equals(other.innerInterfaces))
			return false;
		if (interfaceName == null) {
			if (other.interfaceName != null)
				return false;
		} else if (!interfaceName.equals(other.interfaceName))
			return false;
		if (isStatic != other.isStatic)
			return false;
		if (methods == null) {
			if (other.methods != null)
				return false;
		} else if (!methods.equals(other.methods))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Interface [isStatic=" + isStatic + ", accessType=" + accessType + ", extendsClasses=" + extendsClasses
				+ ", interfaceName=" + interfaceName + ", methods=" + methods + ", imports=" + imports
				+ ", classPackageName=" + packageName + ", innerinterfaces=" + innerInterfaces + "]";
	}

	public StringBuilder toJavaString() {
		StringBuilder builder = new StringBuilder();
		builder.append("package " + packageName + ";");
		builder.append("\n\n");

		List<String> is = new ArrayList<>(imports);
		Collections.sort(is);

		for (String importClass : is) {
			builder.append("import ");
			builder.append(importClass);
			builder.append(";");
			builder.append("\n");
		}

		builder.append("/**");
		if (documentation != null)
			for (Entry<String, List<String>> doc : documentation.entrySet()) {
				for (String d : doc.getValue()) {
					builder.append("\n<p><b>");
					builder.append(doc.getKey());
					builder.append("</b>\n<p>");
					builder.append(d);
					builder.append("\n<p>");
				}
			}
		String now = DateFormat.getDateInstance().format(new Date());
		builder.append("\n" + "*\n" + "* @author hterhors\n" + "*\n" + "*\n*" + now + "\n");
		builder.append("*/");
		builder.append("\n");

		for (JavaAnnotation annotation : annotations) {
			builder.append("\n");
			builder.append("@");
			builder.append(annotation.annotation.annotationClassName);
			if (annotation.annotation == EAnnotation.ASSIGNABLE_SUB_INTERFACES
					|| annotation.annotation == EAnnotation.SUPER_ROOT_CLASSES
					|| annotation.annotation == EAnnotation.DIRECT_SIBLINGS) {
				builder.append("(");
				builder.append("get=");
				builder.append("{");
				for (String parameterClass : annotation.parameter) {
					builder.append(parameterClass + ".class, ");
				}
				builder.append("}");
				builder.append(")\n");
			}

			if (annotation.annotation == EAnnotation.IMPLEMENTATION_CLASS) {
				builder.append("(");
				builder.append("get=");
				for (String parameterClass : annotation.parameter) {
					builder.append(parameterClass + ".class");
				}
				builder.append(")\n");
			}
		}
		if (isStatic)
			builder.append(" static ");
		builder.append(" " + accessType.name().toLowerCase());
		builder.append(" interface ");
		builder.append(interfaceName);
		builder.append("\n");

		List<String> ec = new ArrayList<>(extendsClasses);
		Collections.sort(ec);

		if (!ec.isEmpty()) {
			builder.append(" extends");

			for (int i = 0; i < ec.size(); i++) {
				builder.append(" ");
				builder.append(ec.get(i));
				if (i != ec.size() - 1)
					builder.append(",");
			}
		}

		builder.append("{\n");
		builder.append("\n");

		if (additionalContent != null) {
			builder.append(additionalContent);
		}

		List<JavaMethod> ms = new ArrayList<>(methods);
		Collections.sort(ms);

		for (JavaMethod method : ms) {
			builder.append(method.toJavaString());
			builder.append("\n\n\n");
		}

		builder.append("}");

		return builder;
	}

}
