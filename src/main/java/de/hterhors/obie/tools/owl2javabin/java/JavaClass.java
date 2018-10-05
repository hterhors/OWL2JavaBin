package de.hterhors.obie.tools.owl2javabin.java;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.hterhors.obie.tools.owl2javabin.enums.EAccessType;
import de.hterhors.obie.tools.owl2javabin.enums.EAnnotation;
import de.hterhors.obie.tools.owl2javabin.enums.EField;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.AbstractOBIEIndividual;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.IndividualFactory;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.JavaClassNamingTools;

public class JavaClass implements Comparable<JavaClass> {

	public static class Builder {

		private boolean isStatic = false;
		private String className = null;
		private EAccessType accessType = EAccessType.NONE;
		private Set<String> interfaces = Collections.emptySet();
		private Set<JavaMethod> methods = Collections.emptySet();
		private Set<JavaField> fields = Collections.emptySet();
		private Set<String> imports = Collections.emptySet();
		private Set<JavaConstructor> constructors = Collections.emptySet();
		private String packageName = null;
		private Set<JavaAnnotation> annotations = Collections.emptySet();
		private Map<String, List<String>> documentation = Collections.emptyMap();

		public boolean isStatic() {
			return isStatic;
		}

		public Builder setStatic(boolean isStatic) {
			this.isStatic = isStatic;
			return this;
		}

		public String getClassName() {
			return className;
		}

		public Builder setClassName(String className) {
			this.className = className;
			return this;
		}

		public EAccessType getAccessType() {
			return accessType;
		}

		public Builder setAccessType(EAccessType accessType) {
			this.accessType = accessType;
			return this;
		}

		public Set<String> getInterfaces() {
			return interfaces;
		}

		public Builder setInterfaces(Set<String> interfaces) {
			this.interfaces = interfaces;
			return this;
		}

		public Set<JavaMethod> getMethods() {
			return methods;
		}

		public Builder setMethods(Set<JavaMethod> methods) {
			this.methods = methods;
			return this;
		}

		public Set<JavaField> getFields() {
			return fields;
		}

		public Builder setFields(Set<JavaField> fields) {
			this.fields = fields;
			return this;
		}

		public Set<String> getImports() {
			return imports;
		}

		public Builder setImports(Set<String> imports) {
			this.imports = imports;
			return this;
		}

		public Set<JavaConstructor> getConstructors() {
			return constructors;
		}

		public Builder setConstructors(Set<JavaConstructor> constructors) {
			this.constructors = constructors;
			return this;
		}

		public String getPackageName() {
			return packageName;
		}

		public Builder setPackageName(String packageName) {
			this.packageName = packageName;
			return this;
		}

		public Set<JavaAnnotation> getAnnotations() {
			return annotations;
		}

		public Builder setAnnotations(Set<JavaAnnotation> annotations) {
			this.annotations = annotations;
			return this;
		}

		public Map<String, List<String>> getDocumentation() {
			return documentation;
		}

		public Builder setDocumentation(Map<String, List<String>> documentation) {
			this.documentation = documentation;
			return this;
		}

		public JavaClass build() {
			java.util.Objects.requireNonNull(className);
			java.util.Objects.requireNonNull(packageName);
			return new JavaClass(documentation, annotations, isStatic, className, accessType, interfaces, methods,
					fields, imports, constructors, packageName);
		}

	}

	final public boolean isStatic;
	final public String className;
	final public EAccessType accessType;
	final public Set<String> interfaces;
	final public Set<JavaMethod> methods;
	final public Set<JavaField> fields;
	final public Set<String> imports;
	final public Set<JavaConstructor> constructors;
	final public String packageName;
	final public Set<JavaAnnotation> annotations;
	final public Map<String, List<String>> documentation;

	private JavaClass(Map<String, List<String>> documentation, Set<JavaAnnotation> annotations, boolean isStatic,
			String className, EAccessType accessType, Set<String> interfaces, Set<JavaMethod> methods,
			Set<JavaField> fields, Set<String> imports, Set<JavaConstructor> constructors, String classPackageName) {
		this.documentation = documentation;
		this.constructors = constructors;
		this.isStatic = isStatic;
		this.className = className;
		this.accessType = accessType;
		this.interfaces = interfaces;
		this.methods = methods;
		this.fields = fields;
		this.packageName = classPackageName;
		this.imports = imports;
		this.annotations = annotations;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessType == null) ? 0 : accessType.hashCode());
		result = prime * result + ((annotations == null) ? 0 : annotations.hashCode());
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((constructors == null) ? 0 : constructors.hashCode());
		result = prime * result + ((documentation == null) ? 0 : documentation.hashCode());
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		result = prime * result + ((imports == null) ? 0 : imports.hashCode());
		result = prime * result + ((interfaces == null) ? 0 : interfaces.hashCode());
		result = prime * result + (isStatic ? 1231 : 1237);
		result = prime * result + ((methods == null) ? 0 : methods.hashCode());
		result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
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
		JavaClass other = (JavaClass) obj;
		if (accessType != other.accessType)
			return false;
		if (annotations == null) {
			if (other.annotations != null)
				return false;
		} else if (!annotations.equals(other.annotations))
			return false;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (constructors == null) {
			if (other.constructors != null)
				return false;
		} else if (!constructors.equals(other.constructors))
			return false;
		if (documentation == null) {
			if (other.documentation != null)
				return false;
		} else if (!documentation.equals(other.documentation))
			return false;
		if (fields == null) {
			if (other.fields != null)
				return false;
		} else if (!fields.equals(other.fields))
			return false;
		if (imports == null) {
			if (other.imports != null)
				return false;
		} else if (!imports.equals(other.imports))
			return false;
		if (interfaces == null) {
			if (other.interfaces != null)
				return false;
		} else if (!interfaces.equals(other.interfaces))
			return false;
		if (isStatic != other.isStatic)
			return false;
		if (methods == null) {
			if (other.methods != null)
				return false;
		} else if (!methods.equals(other.methods))
			return false;
		if (packageName == null) {
			if (other.packageName != null)
				return false;
		} else if (!packageName.equals(other.packageName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JavaClass [isStatic=" + isStatic + ", className=" + className + ", accessType=" + accessType
				+ ", interfaces=" + interfaces + ", methods=" + methods + ", fields=" + fields + ", imports=" + imports
				+ ", constructors=" + constructors + ", packageName=" + packageName + ", annotations=" + annotations
				+ ", documentation=" + documentation + "]";
	}

	public StringBuilder toJavaString() {
		StringBuilder builder = new StringBuilder();

		builder.append("package " + packageName + ";");
		builder.append("\n");
		builder.append("\n");

		for (String importClass : imports) {
			builder.append("import ");
			builder.append(importClass);
			builder.append(";");
			builder.append("\n");
		}
		builder.append("\n");

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

		boolean isDatatypeProperty = false;
		for (JavaAnnotation annotation : annotations) {
			isDatatypeProperty |= annotation.annotation == EAnnotation.DATA_TYPE_ANNOTATION;
			builder.append("\n");
			builder.append("@");
			builder.append(annotation.annotation.annotationClassName);

			if (annotation.annotation == EAnnotation.ASSIGNABLE_SUB_CLASSES
					|| annotation.annotation == EAnnotation.DIRECT_SIBLINGS
					|| annotation.annotation == EAnnotation.SUPER_ROOT_CLASSES) {
				builder.append("(");
				builder.append("get=");
				builder.append("{");
				for (String parameterClass : annotation.parameter) {
					builder.append(parameterClass + ".class, ");
				}
				builder.append("}");
				builder.append(")\n");
			}

			if (annotation.annotation == EAnnotation.DIRECT_INTERFACE) {
				builder.append("(");
				builder.append("get=");
				for (String parameterClass : annotation.parameter) {
					builder.append(parameterClass + ".class");
				}
				builder.append(")\n");
			}

		}
		builder.append(" " + accessType.name().toLowerCase());
		if (isStatic)
			builder.append(" static");
		builder.append(" class ");
		builder.append(className);

		List<String> is = new ArrayList<>(interfaces);

		if (!interfaces.isEmpty()) {

			builder.append(" implements");

			for (int i = 0; i < is.size(); i++) {
				builder.append(" ");
				builder.append(is.get(i));
				if (i != is.size() - 1)
					builder.append(",");
			}
		}

		builder.append("{\n");
		builder.append("\n");

		/*
		 * TODO: insert
		 */

		if (!isDatatypeProperty) {

			builder.append("final public static " + IndividualFactory.class.getSimpleName() + "<" + className
					+ "Individual> individualFactory = new " + IndividualFactory.class.getSimpleName() + "<>();\n");
			builder.append("final public static Class<? extends " + AbstractOBIEIndividual.class.getSimpleName()
					+ "> individualClassType = " + className + "Individual.class;\n");

			builder.append(
					"static class " + className + "Individual extends " + AbstractOBIEIndividual.class.getSimpleName()
							+ " {\n" + "\n" + "" + "	private static final long serialVersionUID = 1L;"
							+ "		public " + className + "Individual(String namespace, String name) {\n"
							+ "			super(namespace, name);\n" + "		}\n" + "\n" + "		@Override\n"
							+ "		public String toString() {\n" + "			return \"" + className
							+ "Individual [name=\" + name + \", nameSpace=\" + nameSpace + \"]\";\n" + "		}\n"
							+ "\n" + "	}\n" + "");

			builder.append("	public " + IndividualFactory.class.getSimpleName() + "<" + className
					+ "Individual> getIndividualFactory() {\n" + "		return individualFactory;\n" + "	}\n" + "\n"
					+ "	public final " + className + "Individual individual;");

			builder.append("\n" + "	@Override\n" + "	public AbstractOBIEIndividual getIndividual() {\n"
					+ "		return individual;\n" + "	}");

		}

		List<JavaField> fs = new ArrayList<>(fields);
		Collections.sort(fs);

		for (JavaField field : fs) {
			builder.append("\t");
			builder.append(field.toJavaString());
			builder.append("\n");
		}

		builder.append("\n");
		builder.append("\n");

		for (JavaConstructor constructor : constructors) {
			builder.append("\t");
			builder.append(constructor.toJavaString());
			builder.append("\n");
		}

		builder.append("\n");
		builder.append("\n");

		List<JavaMethod> ms = new ArrayList<>(methods);
		Collections.sort(ms);

		for (JavaMethod method : ms) {
			builder.append("\t");
			builder.append(method.toJavaString());
			builder.append("\n");
		}

		builder.append("\n");
		builder.append("\n");

		builder.append(generateToString(isDatatypeProperty));
		builder.append("\n\n");

		builder.append("}");

		return builder;
	}

	private StringBuilder generateToString(boolean isDatatypeProperty) {
		StringBuilder toString = new StringBuilder();

		toString.append("@Override");
		toString.append("\npublic " + String.class.getSimpleName() + " toString(){\n");
		toString.append("return \"");
		toString.append(JavaClassNamingTools.normalizeClassName(className) + " [");

		if (!isDatatypeProperty) {
			toString.append("individual=\"+individual+\",");
		}

		List<JavaField> fs = new ArrayList<>(this.fields);
		Collections.sort(fs);

		for (int i = 0; i < fs.size(); i++) {

			if (fs.get(i).isStatic()
					&& !fs.get(i).getClassVariableName().equals(EField.SERIAL_VERSION_UID.variableName))
				continue;

			toString.append(fs.get(i).getClassVariableName() + "=\"+" + fs.get(i).getClassVariableName() + "+\"");
			if (i != fs.size() - 1) {
				toString.append(",");
			}
		}

		toString.append("]\";}\n");
		return toString;

	}

	@Override
	public int compareTo(JavaClass o) {
		return className.compareTo(o.className);
	}

}
