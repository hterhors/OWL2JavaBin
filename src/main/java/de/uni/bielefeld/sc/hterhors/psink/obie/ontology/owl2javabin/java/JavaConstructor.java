package de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.JavaClassNamingTools;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.enums.EAccessType;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.enums.EField;

public class JavaConstructor {

	final private List<String> imports;
	final private EAccessType accessType;
	final private String className;
	final private Set<JavaField> fields;
	final private boolean initializeNull;
	final private boolean initializeScioValueOnly;
	final private boolean buildCloneConstructor;
	final private boolean isDatatype;

	public JavaConstructor(boolean initializeNull, boolean initializeScioValueOnly, EAccessType accessType,
			String className, Set<JavaField> fields, boolean buildCloneConstructor, boolean isDatatype) {
		this.isDatatype = isDatatype;
		this.accessType = accessType;
		this.className = className;
		this.initializeNull = initializeNull;
		this.initializeScioValueOnly = initializeScioValueOnly;
		this.fields = fields;
		this.imports = new ArrayList<>();
		this.imports.add(className);
		for (JavaField field : fields) {
			this.imports.add(field.getTypeName());
		}
		this.buildCloneConstructor = buildCloneConstructor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessType == null) ? 0 : accessType.hashCode());
		result = prime * result + (buildCloneConstructor ? 1231 : 1237);
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		result = prime * result + ((imports == null) ? 0 : imports.hashCode());
		result = prime * result + (initializeNull ? 1231 : 1237);
		result = prime * result + (initializeScioValueOnly ? 1231 : 1237);
		result = prime * result + (isDatatype ? 1231 : 1237);
		return result;
	}

	@Override
	public String toString() {
		return "JavaConstructor [imports=" + imports + ", accessType=" + accessType + ", className=" + className
				+ ", fields=" + fields + ", initializeNull=" + initializeNull + ", initializeScioValueOnly="
				+ initializeScioValueOnly + ", buildCloneConstructor=" + buildCloneConstructor + ", isDatatype="
				+ isDatatype + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JavaConstructor other = (JavaConstructor) obj;
		if (accessType != other.accessType)
			return false;
		if (buildCloneConstructor != other.buildCloneConstructor)
			return false;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
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
		if (initializeNull != other.initializeNull)
			return false;
		if (initializeScioValueOnly != other.initializeScioValueOnly)
			return false;
		if (isDatatype != other.isDatatype)
			return false;
		return true;
	}

	public StringBuilder toJavaString() {

		List<JavaField> fs = new ArrayList<>(fields);
		Collections.sort(fs);

		StringBuilder builder = new StringBuilder();

		builder.append(accessType.name().toLowerCase());
		builder.append(" ");
		builder.append(className);
		builder.append("(");

		if (!buildCloneConstructor) {

			if (!initializeNull) {
				if (!isDatatype)
					builder.append("String individualURI, ");

				for (int i = 0; i < fs.size(); i++) {
					builder.append(fs.get(i).getTypeName());
					builder.append(" ");
					builder.append(fs.get(i).getClassVariableName());
					if (i != fs.size() - 1) {
						builder.append(", ");
					}
				}
			}
			if (initializeNull && initializeScioValueOnly) {
				// for (int i = 0; i < fs.size(); i++) {
				//
				// if ( .equals("value")
				// ) {
				/*
				 * HACK: BAD HACK!!!!
				 */
				builder.append(String.class.getSimpleName());
				builder.append(" ");
				builder.append(EField.DATATYPE_PROPERTY_VALUE_ANNOTATION_NAME.variableName);

				// }
				// }
			}

			builder.append("){\n");
			if (!initializeNull) {
				if (!isDatatype)
					builder.append("this.individual = \n" + "				" + className
							+ ".individualFactory.getIndividualByURI(individualURI);\n");

				for (int i = 0; i < fs.size(); i++) {
					builder.append("this.");
					builder.append(fs.get(i).getClassVariableName());
					builder.append(" = ");
					builder.append(fs.get(i).getClassVariableName());
					builder.append(";");
					builder.append("\n");
				}
			} else {
				if (!isDatatype)
					builder.append("this.individual = null;\n");
				for (int i = 0; i < fs.size(); i++) {
					builder.append("this.");
					builder.append(fs.get(i).getClassVariableName());
					if (initializeScioValueOnly && fs.get(i).getClassVariableName()
							.equals(EField.DATATYPE_PROPERTY_VALUE_ANNOTATION_NAME.variableName)
					// .contains(AnnotationBuilder.buildDataTypePropertyValueAnnotation())
					) {
						builder.append(" = ");
						builder.append(fs.get(i).getClassVariableName());
						builder.append(";");
					} else {
						builder.append(" = null;");
					}
					builder.append("\n");
				}
			}
		} else {
			buildCloneConstructor(fs, builder);
		}
		builder.append("}");

		return builder;
	}

	private void buildCloneConstructor(List<JavaField> fs, StringBuilder builder) {
		builder.append(this.className + " " + JavaClassNamingTools.getVariableName(className));
		builder.append(
				")throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,"
						+ "NoSuchMethodException, SecurityException{\n");
		if (!isDatatype)
			builder.append("this.individual = " + JavaClassNamingTools.getVariableName(className) + ".individual;\n");
		for (int i = 0; i < fs.size(); i++) {
			if (fs.get(i).getTypeName().equals("Integer") || fs.get(i).getTypeName().equals("String")
					|| fs.get(i).getTypeName().equals("String")) {
				builder.append("this.");
				builder.append(fs.get(i).getClassVariableName());
				builder.append(" = ");
				builder.append(JavaClassNamingTools.getVariableName(className) + ".get"
						+ JavaClassNamingTools.normalizeClassName(fs.get(i).getClassVariableName()) + "()");
				builder.append(";");
			} else if (fs.get(i).isCollection()) {
				builder.append("for (int j = 0; j < " + JavaClassNamingTools.getVariableName(className) + ".get"
						+ JavaClassNamingTools.normalizeClassName(fs.get(i).getClassVariableName())
						+ "().size(); j++) {");
				builder.append("if(" + JavaClassNamingTools.getVariableName(className) + ".get"
						+ JavaClassNamingTools.normalizeClassName(fs.get(i).getClassVariableName())
						+ "().get(j)!=null){");
				builder.append(fs.get(i).getClassVariableName() + ".add(");
				builder.append(JavaClassNamingTools.getVariableName(className) + ".get"
						+ JavaClassNamingTools.normalizeClassName(fs.get(i).getClassVariableName())
						+ "().get(j).getClass().getDeclaredConstructor("
						+ JavaClassNamingTools.getVariableName(className) + ".get"
						+ JavaClassNamingTools.normalizeClassName(fs.get(i).getClassVariableName())
						+ "().get(j).getClass()).newInstance(" + JavaClassNamingTools.getVariableName(className)
						+ ".get" + JavaClassNamingTools.normalizeClassName(fs.get(i).getClassVariableName())
						+ "().get(j))");
				builder.append(");");
				builder.append("} else{" + fs.get(i).getClassVariableName() + ".add(null);}");
				builder.append("}");

			} else {
				builder.append("if(" + JavaClassNamingTools.getVariableName(className) + ".get"
						+ JavaClassNamingTools.normalizeClassName(fs.get(i).getClassVariableName()) + "()!=null)");
				builder.append("this.");
				builder.append(fs.get(i).getClassVariableName());
				builder.append(" = ");
				builder.append(JavaClassNamingTools.getVariableName(className) + ".get"
						+ JavaClassNamingTools.normalizeClassName(fs.get(i).getClassVariableName())
						+ "().getClass().getDeclaredConstructor(" + JavaClassNamingTools.getVariableName(className)
						+ ".get" + JavaClassNamingTools.normalizeClassName(fs.get(i).getClassVariableName())
						+ "().getClass()).newInstance(" + JavaClassNamingTools.getVariableName(className) + ".get"
						+ JavaClassNamingTools.normalizeClassName(fs.get(i).getClassVariableName()) + "())");
				builder.append(";");
			}

			builder.append("\n");
		}
	}

}
