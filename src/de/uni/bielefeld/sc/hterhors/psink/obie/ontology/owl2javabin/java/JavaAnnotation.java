package de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.java;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.enums.EAnnotation;

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

}
