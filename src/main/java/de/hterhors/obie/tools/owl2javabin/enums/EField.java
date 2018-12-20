package de.hterhors.obie.tools.owl2javabin.enums;

import de.hterhors.obie.core.ontology.OntologyFieldNames;
import de.hterhors.obie.core.tools.JavaClassNamingTools;

public enum EField {
	RDF_MODEL_NAMESPACE(OntologyFieldNames.RDF_MODEL_NAMESPACE_FIELD_NAME),
	ONTOLOGY_NAME(OntologyFieldNames.ONTOLOGY_NAME_FIELD_NAME),
//	ANNOTATION_ID_FIELD(OntologyFieldNames.ANNOTATION_ID_FIELD_NAME),
	RESOURCE_FACTORY_FIELD_NAME(OntologyFieldNames.RESOURCE_FACTORY_NAME),
	DATATYPE_PROPERTY_VALUE_ANNOTATION_NAME(OntologyFieldNames.INTERPRETED_VALUE_FIELD_NAME),
	CHARACTER_OFFSET(OntologyFieldNames.CHARACTER_OFFSET_FIELD_NAME),
	CHARACTER_ONSET(OntologyFieldNames.CHARACTER_ONSET_FIELD_NAME),
	TEXT_MENTION(OntologyFieldNames.TEXT_MENTION_FIELD_NAME),
	SERIAL_VERSION_UID(OntologyFieldNames.SERIAL_VERSION_UID_FIELD_NAME);

	final public String variableName;
	final public String methodName;

	private EField(final String variableName) {
		this.variableName = variableName;
		this.methodName = JavaClassNamingTools.normalizeClassName(variableName);
	}
}