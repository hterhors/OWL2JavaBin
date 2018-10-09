package de.hterhors.obie.tools.owl2javabin.builder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hterhors.obie.core.ontology.OntologyFieldNames;
import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;
import de.hterhors.obie.core.owlreader.ECardinalityType;
import de.hterhors.obie.core.owlreader.container.OntologyClass;
import de.hterhors.obie.core.owlreader.container.OntologySlotData;
import de.hterhors.obie.core.tools.JavaClassNamingTools;
import de.hterhors.obie.tools.owl2javabin.enums.EAccessType;
import de.hterhors.obie.tools.owl2javabin.enums.EField;
import de.hterhors.obie.tools.owl2javabin.java.JavaField;

public class FieldBuilder {

	private final AnnotationBuilder annotationBuilder;

	public FieldBuilder(AnnotationBuilder annotationBuilder) {
		this.annotationBuilder = annotationBuilder;
	}

	public JavaField generateDatatypePropteryValueField() {
		return new JavaField().setAccessType(EAccessType.PRIVATE)
				// .addAnnotation(AnnotationBuilder.buildDataTypePropertyValueAnnotation())
				// .addAnnotation(AnnotationBuilder.buildOntologyModelContentAnnotation())
				.setOntologyName(EField.DATATYPE_PROPERTY_VALUE_ANNOTATION_NAME.variableName).setFinal(true)
				.setRelationType(ECardinalityType.SINGLE).setTypeName(String.class.getSimpleName());
	}

	public List<JavaField> generateAnnotationOnAndOffset() {

		List<JavaField> l = new ArrayList<>();

		l.add(new JavaField().setAccessType(EAccessType.PRIVATE)
				// .addAnnotation(AnnotationProvider.buidAnnotationFieldAnnotation())
				.setOntologyName(EField.CHARACTER_OFFSET.variableName).setFinal(false)
				.setRelationType(ECardinalityType.SINGLE).setTypeName(Integer.class.getSimpleName()));

		l.add(new JavaField().setAccessType(EAccessType.PRIVATE)
				// .addAnnotation(AnnotationProvider.buidAnnotationFieldAnnotation())
				.setOntologyName(EField.CHARACTER_ONSET.variableName).setFinal(false)
				.setRelationType(ECardinalityType.SINGLE).setTypeName(Integer.class.getSimpleName()));

		return l;

	}

	public JavaField generateJavaSerializable(String serializeVersion) {
		return new JavaField().setAccessType(EAccessType.PRIVATE)
				.setOntologyName(EField.SERIAL_VERSION_UID.variableName).setFinal(true).setStatic(true)
				.setRelationType(ECardinalityType.SINGLE).setTypeName(long.class.getSimpleName())
				.setInitialization(serializeVersion + "L");
	}

	public JavaField generateAnnotationIDField() {
		return new JavaField().setAccessType(EAccessType.PUBLIC)
				.setOntologyName(EField.ANNOTATION_ID_FIELD.variableName).setFinal(true)
				.setRelationType(ECardinalityType.SINGLE).setTypeName(String.class.getSimpleName());
	}

	public JavaField generateMentionField(final OntologyClass className) {
		return new JavaField().setAccessType(EAccessType.PRIVATE)
				.addAnnotation(annotationBuilder.buildTextMentionAnnotation())
				.setOntologyName(OntologyFieldNames.TEXT_MENTION_FIELD_NAME).setFinal(true)
				.setRelationType(ECardinalityType.SINGLE).setTypeName(String.class.getSimpleName());
	}

	public JavaField generateResourceFactoryField(String className) {
		return new JavaField().setAccessType(EAccessType.PRIVATE)
				.setOntologyName(EField.RESOURCE_FACTORY_FIELD_NAME.variableName).setFinal(true).setStatic(true)
				.setRelationType(ECardinalityType.SINGLE)
				.setTypeName(Map.class.getSimpleName() + "<" + IOBIEThing.class.getSimpleName() + ", String>")
				.setInitialization("new HashMap<>()");
	}

	public Set<JavaField> generateRDFModelFields(OntologyClass className) {
		return generateModelFields(className);
	}

	private Set<JavaField> generateModelFields(OntologyClass scioClass) {
		Set<JavaField> fields = new HashSet<>();
		if (!scioClass.domainRangeRelations.isEmpty()) {
			for (OntologySlotData relation : scioClass.domainRangeRelations.keySet()) {

				for (OntologyClass range : scioClass.domainRangeRelations.get(relation)) {

					ECardinalityType cardinalityType = relation.cardinalityType != ECardinalityType.UNDEFINED
							? relation.cardinalityType
							: ECardinalityType.SINGLE;

					JavaField f;

					if (cardinalityType == ECardinalityType.SINGLE) {
						f = new JavaField().setAccessType(EAccessType.PRIVATE)
								.setOntologyName(JavaClassNamingTools.combineRelationWithClassNameAsClassName(
										relation.javaClassPropertyName, range.javaClassName))
								.setRelationType(cardinalityType).setTypeName(range.javaInterfaceName);
						if (range.isDataType) {
							f.setDatatypeName(range.javaClassName);
						}
					} else {
						f = new JavaField(true).setAccessType(EAccessType.PRIVATE)
								.setOntologyName(JavaClassNamingTools.combineRelationWithClassNameAsPluralClassName(
										relation.javaClassPropertyName, range.javaClassName))
								.setRelationType(cardinalityType).setTypeName("List<>")
								.setInitialization("new ArrayList<>()")
								.addAnnotation(annotationBuilder.buildOneToManyAnnotation())
								.setInnerTypeName(range.javaInterfaceName);
						if (!range.isDataType) {
							f.setDatatypeName(range.javaClassName);
						}
					}
					String ontologyName = relation.namespace + relation.ontologyPropertyName;
					f.addAnnotation(annotationBuilder.buildOntologyModelContentAnnotation(ontologyName));

					if (range.isDataType) {
						f.addAnnotation(annotationBuilder.buildDataTypePropertyValueAnnotation());
						// f.addAnnotation(AnnotationBuilder.buildSingleValueLeafEntityAnnotation());
					}
					fields.add(f);
				}
			}
		}

		/*
		 * Add all fields from super classes recursively.
		 */

		for (OntologyClass superClass : scioClass.superclasses) {
			if (superClass.subclasses.isEmpty())
				continue;
			/*
			 * Rec call
			 */
			fields.addAll(generateModelFields(superClass));
		}

		return fields;
	}

	public JavaField generateOntologyNameField(final String ontologyName) {
		return new JavaField(false).setAccessType(EAccessType.PUBLIC).setOntologyName(EField.ONTOLOGY_NAME.variableName)
				.setFinal(true).setStatic(true).setRelationType(ECardinalityType.SINGLE)
				.setTypeName(String.class.getSimpleName()).setInitialization("\"" + ontologyName + "\"");
	}

}
