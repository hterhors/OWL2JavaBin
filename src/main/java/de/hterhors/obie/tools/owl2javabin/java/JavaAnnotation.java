package de.hterhors.obie.tools.owl2javabin.java;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.hterhors.obie.tools.owl2javabin.enums.EAnnotation;

public class JavaAnnotation {

	public final EAnnotation annotation;

	public Set<String> parameter;

	public static class Builder {

		private EAnnotation annotation;
		private Set<String> parameter = new HashSet<>();

		public EAnnotation getAnnotation() {
			return annotation;
		}

		public Builder setAnnotation(EAnnotation annotation) {
			this.annotation = annotation;
			return this;
		}

		public Set<String> getParameter() {
			return parameter;
		}

		public Builder setParameter(Set<String> parameter) {
			this.parameter = parameter;
			return this;
		}

		public Builder addAllParameter(Set<String> parameter) {
			this.parameter.addAll(parameter);
			return this;
		}

		public Builder addParameter(String parameter) {
			this.parameter.add(parameter);
			return this;
		}

		public JavaAnnotation build() {
			return new JavaAnnotation(annotation, Collections.unmodifiableSet(parameter));
		}

	}

	public JavaAnnotation(EAnnotation annotation, Set<String> parameter) {
		this.annotation = annotation;
		this.parameter = parameter;
	}

	@Override
	public String toString() {
		return "JavaAnnotation [annotation=" + annotation + ", parameter=" + parameter + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annotation == null) ? 0 : annotation.hashCode());
		result = prime * result + ((parameter == null) ? 0 : parameter.hashCode());
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
		JavaAnnotation other = (JavaAnnotation) obj;
		if (annotation != other.annotation)
			return false;
		if (parameter == null) {
			if (other.parameter != null)
				return false;
		} else if (!parameter.equals(other.parameter))
			return false;
		return true;
	}

	
	
}
