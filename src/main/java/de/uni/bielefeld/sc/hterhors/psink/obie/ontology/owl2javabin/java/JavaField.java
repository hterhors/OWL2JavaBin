package de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.owlreader.ECardinalityType;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.JavaClassNamingTools;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.enums.EAccessType;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.enums.EAnnotation;

public class JavaField implements Comparable<JavaField> {

	private String typeName;
	private boolean hasInnerType = false;
	private String innerTypeName;
	private boolean isFinal = false;
	private boolean isStatic = false;
	private EAccessType accessType = EAccessType.NONE;
	private List<String> imports = new ArrayList<>();

	/**
	 * The name of the variable in the generated SCIO Class. E.g. remove "has" in
	 * "hasSupplier" and lowercase "S".
	 * 
	 * The java-conform variable name.
	 */
	private String classVariableName;

	/**
	 * The real name as it is in the ontology.
	 */
	private String ontologyName;

	private Set<JavaAnnotation> annotations = new HashSet<>();
	private String initialization;
	private ECardinalityType relationType;

	private boolean rename;

	public JavaField(boolean rename) {
		this.rename = rename;
		this.hasInnerType = false;
	}

	public JavaField() {
		this.hasInnerType = false;
		this.rename = true;
	}

	public String getClassVariableName() {
		return classVariableName;
	}

	public String getOntologyName() {
		return ontologyName;
	}

	public boolean isCollection() {
		return hasInnerType;
	}

	private void setClassVariableName(String classVariableName) {
		this.classVariableName = classVariableName;
	}

	public JavaField setOntologyName(String ontologyName) {
		this.ontologyName = ontologyName;

		if (rename) {
			String classVariableName = JavaClassNamingTools.getVariableName(ontologyName);
			this.setClassVariableName(classVariableName);
		} else {
			this.setClassVariableName(ontologyName);
		}

		return this;
	}

	public JavaField setImports(List<String> imports) {
		this.imports = imports;
		return this;
	}

	public JavaField setFinal(boolean isFinal) {
		this.isFinal = isFinal;
		return this;
	}

	public JavaField setStatic(boolean isStatic) {
		this.isStatic = isStatic;
		return this;
	}

	public JavaField setAccessType(EAccessType accessType) {
		this.accessType = accessType;
		return this;
	}

	public JavaField setTypeName(String typeName) {
		this.typeName = typeName;
		return this;
	}

	public JavaField setInnerTypeName(String innerTypeName) {
		this.hasInnerType = true;
		this.innerTypeName = innerTypeName;
		return this;
	}

	public JavaField setAnnotations(Set<JavaAnnotation> annotations) {
		this.annotations = annotations;
		return this;
	}

	public JavaField addAnnotation(JavaAnnotation annotation) {
		this.annotations.add(annotation);
		return this;
	}

	public JavaField setInitialization(String initialization) {
		this.initialization = initialization;
		return this;
	}

	public JavaField setRelationType(ECardinalityType relationType) {
		this.relationType = relationType;
		return this;
	}

	public List<String> getImports() {
		return imports;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public EAccessType getAccessType() {
		return accessType;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getInnerTypeName() {
		if (!hasInnerType)
			throw new NoSuchElementException("No inner type available.");
		return innerTypeName;
	}

	public Set<JavaAnnotation> getAnnotations() {
		return annotations;
	}

	public String getInitialization() {
		return initialization;
	}

	public ECardinalityType getRelationType() {
		return relationType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessType == null) ? 0 : accessType.hashCode());
		result = prime * result + ((annotations == null) ? 0 : annotations.hashCode());
		result = prime * result + ((classVariableName == null) ? 0 : classVariableName.hashCode());
		result = prime * result + (hasInnerType ? 1231 : 1237);
		result = prime * result + ((imports == null) ? 0 : imports.hashCode());
		result = prime * result + ((initialization == null) ? 0 : initialization.hashCode());
		result = prime * result + ((innerTypeName == null) ? 0 : innerTypeName.hashCode());
		result = prime * result + (isFinal ? 1231 : 1237);
		result = prime * result + (isStatic ? 1231 : 1237);
		result = prime * result + ((ontologyName == null) ? 0 : ontologyName.hashCode());
		result = prime * result + ((relationType == null) ? 0 : relationType.hashCode());
		result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
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
		JavaField other = (JavaField) obj;
		if (accessType != other.accessType)
			return false;
		if (annotations == null) {
			if (other.annotations != null)
				return false;
		} else if (!annotations.equals(other.annotations))
			return false;
		if (classVariableName == null) {
			if (other.classVariableName != null)
				return false;
		} else if (!classVariableName.equals(other.classVariableName))
			return false;
		if (hasInnerType != other.hasInnerType)
			return false;
		if (imports == null) {
			if (other.imports != null)
				return false;
		} else if (!imports.equals(other.imports))
			return false;
		if (initialization == null) {
			if (other.initialization != null)
				return false;
		} else if (!initialization.equals(other.initialization))
			return false;
		if (innerTypeName == null) {
			if (other.innerTypeName != null)
				return false;
		} else if (!innerTypeName.equals(other.innerTypeName))
			return false;
		if (isFinal != other.isFinal)
			return false;
		if (isStatic != other.isStatic)
			return false;
		if (ontologyName == null) {
			if (other.ontologyName != null)
				return false;
		} else if (!ontologyName.equals(other.ontologyName))
			return false;
		if (relationType != other.relationType)
			return false;
		if (typeName == null) {
			if (other.typeName != null)
				return false;
		} else if (!typeName.equals(other.typeName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OntologyClassField [typeName=" + typeName + ", hasInnerType=" + hasInnerType + ", innerTypeName="
				+ innerTypeName + ", isFinal=" + isFinal + ", isStatic=" + isStatic + ", accessType=" + accessType
				+ ", imports=" + imports + ", classVariableName=" + classVariableName + ", ontologyName=" + ontologyName
				+ ", annotations=" + annotations + ", initialization=" + initialization + ", relationType="
				+ relationType + "]";
	}

	public StringBuilder toJavaString() {
		StringBuilder builder = new StringBuilder();

		for (JavaAnnotation annotation : annotations) {
			builder.append("@");
			builder.append(annotation.annotation.annotationClassName);

			if (annotation.annotation == EAnnotation.ONTOLOGY_MODEL_CONTENT_ANNOTATION_NAME) {
				builder.append("(");
				builder.append("ontologyName=");
				builder.append("\"");
				if (annotation.parameter.size() != 1) {
					System.err.println(
							"OntologyClassField.toJavaClassString()-Error: Parameter size for ONTOLOGY_MODEL_CONTENT_ANNOTATION_NAME must be 1.");
					System.exit(1);
				}
				for (String parameter : annotation.parameter) {
					builder.append(parameter);
				}
				builder.append("\"");
				builder.append(")");
			}
			builder.append("\n");
		}
		if (isFinal)
			builder.append("final ");
		if (isStatic)
			builder.append("static ");

		builder.append(accessType.name().toLowerCase());
		builder.append(" ");
		builder.append(resolveInnerType(typeName));
		builder.append(" ");
		builder.append(classVariableName);

		if (initialization != null && !initialization.isEmpty()) {
			builder.append(" = ");
			builder.append(initialization);
		}

		builder.append(";");
		return builder;
	}

	private String resolveInnerType(String resolveTypeName) {
		/*
		 * TODO: HACK
		 */
		return hasInnerType ? resolveTypeName.replaceAll(">", getInnerTypeName() + ">") : resolveTypeName;
	}

	// private String getPropertyName() {
	// return "has" + Character.toUpperCase(ontologyName.charAt(0)) +
	// ontologyName.substring(1);
	// }
	//

	@Override
	public int compareTo(JavaField o) {
		return this.getClassVariableName().compareTo(o.getClassVariableName());
	}

}
