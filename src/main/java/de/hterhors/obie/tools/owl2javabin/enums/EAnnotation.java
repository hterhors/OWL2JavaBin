package de.hterhors.obie.tools.owl2javabin.enums;

import org.apache.jena.ontology.DatatypeProperty;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.AssignableSubClasses;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.AssignableSubInterfaces;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.DirectInterface;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.DirectSiblings;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.ImplementationClass;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.OntologyModelContent;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.RelationTypeCollection;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.SuperRootClasses;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.TextMention;

public enum EAnnotation {
	OVERRIDE(Override.class.getSimpleName()), DIRECT_INTERFACE(DirectInterface.class.getSimpleName()),
	IMPLEMENTATION_CLASS(ImplementationClass.class.getSimpleName()),
	SUPER_ROOT_CLASSES(SuperRootClasses.class.getSimpleName()), DIRECT_SIBLINGS(DirectSiblings.class.getSimpleName()),
	ASSIGNABLE_SUB_CLASSES(AssignableSubClasses.class.getSimpleName()),
	ASSIGNABLE_SUB_INTERFACES(AssignableSubInterfaces.class.getSimpleName()),
	ONTOLOGY_MODEL_CONTENT_ANNOTATION_NAME(OntologyModelContent.class.getSimpleName()),
	MENTION_ANNOTATION_NAME(TextMention.class.getSimpleName()),
	COLLECTION_RELATION_ANNOTATION_NAME(RelationTypeCollection.class.getSimpleName()),
	DATA_TYPE_ANNOTATION(DatatypeProperty.class.getSimpleName());

	final public String annotationClassName;

	EAnnotation(final String annotationClassName) {
		this.annotationClassName = annotationClassName;
	}

}