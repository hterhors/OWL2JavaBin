package de.hterhors.obie.tools.owl2javabin.builder;

import java.util.Set;
import java.util.stream.Collectors;

import de.hterhors.obie.core.owlreader.container.OntologyClass;
import de.hterhors.obie.tools.owl2javabin.enums.EAnnotation;
import de.hterhors.obie.tools.owl2javabin.java.JavaAnnotation;

public class AnnotationBuilder {

	public static final String OWL_LITERAL = "xs:string";

	public JavaAnnotation buildOverrideAnnotation() {
		return new JavaAnnotation.Builder().setAnnotation(EAnnotation.OVERRIDE).build();
	}

	public JavaAnnotation buildOntologyModelContentAnnotation(final String className) {
		return new JavaAnnotation.Builder().setAnnotation(EAnnotation.ONTOLOGY_MODEL_CONTENT_ANNOTATION_NAME)
				.addParameter(className).build();
	}

	public JavaAnnotation buildOntologyModelContentAnnotation() {
		return new JavaAnnotation.Builder().setAnnotation(EAnnotation.ONTOLOGY_MODEL_CONTENT_ANNOTATION_NAME)
				.addParameter(OWL_LITERAL).build();
	}

	public JavaAnnotation buildTextMentionAnnotation() {
		return new JavaAnnotation.Builder().setAnnotation(EAnnotation.MENTION_ANNOTATION_NAME).build();
	}

	public JavaAnnotation buildDataTypePropertyValueAnnotation() {
		return new JavaAnnotation.Builder().setAnnotation(EAnnotation.DATA_TYPE_ANNOTATION).build();
	}

	public JavaAnnotation buildOneToManyAnnotation() {
		return new JavaAnnotation.Builder().setAnnotation(EAnnotation.COLLECTION_RELATION_ANNOTATION_NAME).build();
	}

	public JavaAnnotation buildAssignableSubClassesAnnotation(Set<OntologyClass> assignableSubClasses) {
		return new JavaAnnotation.Builder()
				.setAnnotation(EAnnotation.ASSIGNABLE_SUB_CLASSES).setParameter(assignableSubClasses.stream()
						.filter(sc -> !sc.isNamedIndividual).map(sc -> sc.javaClassName).collect(Collectors.toSet()))
				.build();
	}

	public JavaAnnotation buildDirectSiblingClassesAnnotation(Set<OntologyClass> directSiblings) {
		return new JavaAnnotation.Builder()
				.setAnnotation(EAnnotation.DIRECT_SIBLINGS).setParameter(directSiblings.stream()
						.filter(sc -> !sc.isNamedIndividual).map(sc -> sc.javaClassName).collect(Collectors.toSet()))
				.build();
	}

	public JavaAnnotation buildClassImplAnnotation(OntologyClass implementationClassName) {
		return new JavaAnnotation.Builder().setAnnotation(EAnnotation.IMPLEMENTATION_CLASS)
				.addParameter(implementationClassName.javaClassName).build();
	}

	public JavaAnnotation buildRootSuperClassAnnotation(Set<OntologyClass> superRootClasses) {
		return new JavaAnnotation.Builder().setAnnotation(EAnnotation.SUPER_ROOT_CLASSES)
				.setParameter(superRootClasses.stream().map(c -> c.javaClassName).collect(Collectors.toSet())).build();
	}

	public JavaAnnotation buildDirectInterfaceAnnotation(OntologyClass dataClass) {
		return new JavaAnnotation.Builder().setAnnotation(EAnnotation.DIRECT_INTERFACE)
				.addParameter(dataClass.javaInterfaceName).build();
	}

	public JavaAnnotation buildAssignableInterfacesAnnotation(Set<OntologyClass> assignableSubInterfaces) {
		return new JavaAnnotation.Builder().setAnnotation(EAnnotation.ASSIGNABLE_SUB_INTERFACES)
				.setParameter(assignableSubInterfaces.stream().filter(sc -> !sc.isNamedIndividual)
						.map(sc -> sc.javaInterfaceName).collect(Collectors.toSet()))
				.build();
	}
}
