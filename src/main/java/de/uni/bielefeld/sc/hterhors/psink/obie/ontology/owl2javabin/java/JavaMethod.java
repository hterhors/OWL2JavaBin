package de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.enums.EAccessType;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.enums.EMethodType;

import java.util.Set;

public class JavaMethod implements Comparable<JavaMethod> {

	/**
	 * Name, Method
	 */
	public static final Map<String, JavaMethod> methods = new HashMap<>();

	static public class OntologyMethodBody {

		final boolean isInterface;
		final String methodBody;

		public OntologyMethodBody(boolean isInterface) {
			this.isInterface = isInterface;
			this.methodBody = "";
		}

		public OntologyMethodBody(String methodBody) {
			this.isInterface = false;
			this.methodBody = methodBody;
		}

		public OntologyMethodBody() {
			this.isInterface = false;
			this.methodBody = "";
		}

		public boolean isInterface() {
			return isInterface;
		}

		public String getMethodBody() {
			return methodBody;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (isInterface ? 1231 : 1237);
			result = prime * result + ((methodBody == null) ? 0 : methodBody.hashCode());
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
			OntologyMethodBody other = (OntologyMethodBody) obj;
			if (isInterface != other.isInterface)
				return false;
			if (methodBody == null) {
				if (other.methodBody != null)
					return false;
			} else if (!methodBody.equals(other.methodBody))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "MethodBody [isInterface=" + isInterface + ", methodBody=" + methodBody + "]";
		}

		public StringBuilder toJavaString() {
			StringBuilder builder = new StringBuilder();
			if (isInterface) {
				builder.append(";");
				return builder;
			}

			builder.append("{\n");
			builder.append("\t\t");
			builder.append(methodBody);
			builder.append("}");

			return builder;
		}
	}

	final private boolean isStatic;
	final private List<String> imports;
	final private EAccessType accessType;
	final private EMethodType methodType;
	final private boolean isDefaultInterface;
	final private String methodName;
	final private String returnType;
	final private String returnInnerTypeName;
	final private List<JavaMethodParameter> arguments;
	final private Set<JavaAnnotation> annotations;
	final private OntologyMethodBody methodBody;
	final private Set<String> exceptions;

	final private Map<String, List<String>> documentation;

	private JavaMethod(Map<String, List<String>> documentation, List<String> imports, EAccessType accessType,
			EMethodType methodType, boolean isDefaultInterface, String methodName, String returnType,
			String returnInnerTypeName, List<JavaMethodParameter> arguments, Set<JavaAnnotation> annotations,
			OntologyMethodBody methodBody, Set<String> exceptions, boolean isStatic) {
		this.documentation = documentation;
		this.imports = imports;
		this.accessType = accessType;
		this.methodType = methodType;
		this.isDefaultInterface = isDefaultInterface;
		this.methodName = methodName;
		this.returnType = returnType;
		this.returnInnerTypeName = returnInnerTypeName;
		this.arguments = arguments;
		this.annotations = annotations;
		this.methodBody = methodBody;
		this.exceptions = exceptions;
		this.isStatic = isStatic;
	}

	public JavaMethod getInterfaceForMethod() {
		return new OntologyMethodBuilder().setAccessType(this.accessType).setArguments(this.arguments)
				.setExceptions(this.exceptions).setMethodName(this.methodName).setReturnType(this.returnType)
				.setMethodBody(new OntologyMethodBody(true)).setReturnInnerTypeName(this.returnInnerTypeName).build();

	}

	public static class OntologyMethodBuilder {
		private boolean isStatic = false;
		private List<String> imports = new ArrayList<>();
		private EAccessType accessType = null;
		private EMethodType methodType = null;
		private boolean isDefaultInterface = false;
		private String methodName = null;
		private String returnType = null;
		private String returnInnerTypeName = null;
		private List<JavaMethodParameter> arguments = new ArrayList<>();
		private Set<JavaAnnotation> annotations = new HashSet<>();
		private OntologyMethodBody methodBody = null;
		private Set<String> exceptions = new HashSet<>();
		private Map<String, List<String>> documentation;

		public OntologyMethodBuilder() {
		}

		public OntologyMethodBuilder setStatic(boolean isStatic) {
			this.isStatic = isStatic;
			return this;
		}

		public OntologyMethodBuilder setImports(List<String> imports) {
			this.imports = imports;
			return this;
		}

		public OntologyMethodBuilder setAccessType(EAccessType accessType) {
			this.accessType = accessType;
			return this;
		}

		public OntologyMethodBuilder setMethodType(EMethodType methodType) {
			this.methodType = methodType;
			return this;
		}

		public OntologyMethodBuilder setDefaultInterface(boolean isDefaultInterface) {
			this.isDefaultInterface = isDefaultInterface;
			return this;
		}

		public OntologyMethodBuilder setMethodName(String methodName) {
			this.methodName = methodName;
			return this;
		}

		public OntologyMethodBuilder setReturnType(String returnType) {
			this.returnType = returnType;
			return this;
		}

		public OntologyMethodBuilder setReturnInnerTypeName(String returnInnerTypeName) {
			this.returnInnerTypeName = returnInnerTypeName;
			return this;
		}

		public OntologyMethodBuilder setArguments(List<JavaMethodParameter> arguments) {
			this.arguments = arguments;
			return this;
		}

		public OntologyMethodBuilder addArgument(JavaMethodParameter argument) {
			this.arguments.add(argument);
			return this;
		}

		public OntologyMethodBuilder setAnnotations(Set<JavaAnnotation> annotations) {
			this.annotations = annotations;
			return this;
		}

		public OntologyMethodBuilder addAnnotation(JavaAnnotation annotation) {
			this.annotations.add(annotation);
			return this;
		}

		public OntologyMethodBuilder setMethodBody(OntologyMethodBody methodBody) {
			this.methodBody = methodBody;
			return this;
		}

		public OntologyMethodBuilder setExceptions(Set<String> exceptions) {
			this.exceptions = exceptions;
			return this;
		}

		public JavaMethod build() {
			methods.put(methodName,
					new JavaMethod(documentation, imports, accessType, methodType, isDefaultInterface, methodName,
							returnType, returnInnerTypeName, arguments, annotations, methodBody, exceptions, isStatic));
			return methods.get(methodName);
		}

		public OntologyMethodBuilder setDocumentation(Map<String, List<String>> documentation) {
			this.documentation = documentation;
			return this;
		}

	}

	public String getReturnInnerTypeName() {
		return returnInnerTypeName;
	}

	public EMethodType getMethodType() {
		return methodType;
	}

	public boolean isDefaultInterface() {
		return isDefaultInterface;
	}

	public List<String> getImports() {
		return imports;
	}

	public EAccessType getAccessType() {
		return accessType;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getReturnType() {
		return returnType;
	}

	public List<JavaMethodParameter> getArguments() {
		return arguments;
	}

	public Set<JavaAnnotation> getAnnotations() {
		return annotations;
	}

	public OntologyMethodBody getMethodBody() {
		return methodBody;
	}

	public Set<String> getExceptions() {
		return exceptions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessType == null) ? 0 : accessType.hashCode());
		result = prime * result + ((annotations == null) ? 0 : annotations.hashCode());
		result = prime * result + ((arguments == null) ? 0 : arguments.hashCode());
		result = prime * result + ((exceptions == null) ? 0 : exceptions.hashCode());
		result = prime * result + ((imports == null) ? 0 : imports.hashCode());
		result = prime * result + (isDefaultInterface ? 1231 : 1237);
		result = prime * result + ((methodBody == null) ? 0 : methodBody.hashCode());
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result + ((methodType == null) ? 0 : methodType.hashCode());
		result = prime * result + ((returnInnerTypeName == null) ? 0 : returnInnerTypeName.hashCode());
		result = prime * result + ((returnType == null) ? 0 : returnType.hashCode());
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
		JavaMethod other = (JavaMethod) obj;
		if (accessType != other.accessType)
			return false;
		if (annotations == null) {
			if (other.annotations != null)
				return false;
		} else if (!annotations.equals(other.annotations))
			return false;
		if (arguments == null) {
			if (other.arguments != null)
				return false;
		} else if (!arguments.equals(other.arguments))
			return false;
		if (exceptions == null) {
			if (other.exceptions != null)
				return false;
		} else if (!exceptions.equals(other.exceptions))
			return false;
		if (imports == null) {
			if (other.imports != null)
				return false;
		} else if (!imports.equals(other.imports))
			return false;
		if (isDefaultInterface != other.isDefaultInterface)
			return false;
		if (methodBody == null) {
			if (other.methodBody != null)
				return false;
		} else if (!methodBody.equals(other.methodBody))
			return false;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		if (methodType != other.methodType)
			return false;
		if (returnInnerTypeName == null) {
			if (other.returnInnerTypeName != null)
				return false;
		} else if (!returnInnerTypeName.equals(other.returnInnerTypeName))
			return false;
		if (returnType == null) {
			if (other.returnType != null)
				return false;
		} else if (!returnType.equals(other.returnType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Method [imports=" + imports + ", accessType=" + accessType + ", methodType=" + methodType
				+ ", isDefaultInterface=" + isDefaultInterface + ", methodName=" + methodName + ", returnType="
				+ returnType + ", returnInnerTypeName=" + returnInnerTypeName + ", arguments=" + arguments
				+ ", annotations=" + annotations + ", methodBody=" + methodBody + ", exceptions=" + exceptions + "]";
	}

	public StringBuilder toJavaString() {
		StringBuilder builder = new StringBuilder();

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
		builder.append("*/");
		builder.append("\n");

		for (JavaAnnotation annotation : annotations) {
			builder.append("@");
			builder.append(annotation.annotation.annotationClassName);
			builder.append("\n");
		}

		builder.append("\t");
		if (isDefaultInterface) {
			builder.append("default");
			builder.append(" ");
		}

		if (accessType != EAccessType.NONE) {
			builder.append(accessType.value);
			builder.append(" ");
		}

		if (isStatic) {
			builder.append("static");
			builder.append(" ");
		}

		if (returnType == null) {
			builder.append("void");
		} else {
			builder.append(returnType);
		}
		builder.append(" ");
		builder.append(methodName);

		builder.append("(");
		for (int i = 0; i < arguments.size(); i++) {
			builder.append(arguments.get(i).getType());
			builder.append(" ");
			builder.append(arguments.get(i).getName());
			if (i != arguments.size() - 1) {
				builder.append(", ");
			}
		}
		builder.append(")");

		if (!exceptions.isEmpty()) {
			builder.append(" throws ");
			final int max = exceptions.size();
			int count = 0;
			for (String e : exceptions) {
				builder.append(e);
				if (count != max - 1)
					builder.append(", ");
				count++;
			}
		}
		builder.append(methodBody.toJavaString());

		return builder;
	}

	@Override
	public int compareTo(JavaMethod o) {
		return getMethodName().compareTo(o.getMethodName());
	}

}
