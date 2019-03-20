package de.hterhors.obie.tools.owl2javabin.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;

import de.hterhors.obie.core.ontology.OntologyFieldNames;
import de.hterhors.obie.core.ontology.OntologyInitializer;
import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;
import de.hterhors.obie.core.owlreader.ECardinalityType;
import de.hterhors.obie.core.owlreader.container.OntologyClass;
import de.hterhors.obie.core.owlreader.container.OntologySlotData;
import de.hterhors.obie.core.tools.JavaClassNamingTools;
import de.hterhors.obie.tools.owl2javabin.enums.EAccessType;
import de.hterhors.obie.tools.owl2javabin.enums.EAnnotation;
import de.hterhors.obie.tools.owl2javabin.enums.EField;
import de.hterhors.obie.tools.owl2javabin.enums.EMethodType;
import de.hterhors.obie.tools.owl2javabin.java.JavaField;
import de.hterhors.obie.tools.owl2javabin.java.JavaMethod;
import de.hterhors.obie.tools.owl2javabin.java.JavaMethod.OntologyMethodBody;
import de.hterhors.obie.tools.owl2javabin.java.JavaMethod.OntologyMethodBuilder;
import de.hterhors.obie.tools.owl2javabin.java.JavaMethodParameter;

public class ClassMethodBuilder {

	private final AnnotationBuilder annotationBuilder;

	public ClassMethodBuilder(AnnotationBuilder annotationBuilder) {
		this.annotationBuilder = annotationBuilder;

	}

	public JavaMethod generateGetResourceNameMethod(String ontologyThingName, boolean isnamedIndividual,
			final String className) {

		StringBuilder methodBodyContent = new StringBuilder();
		if (isnamedIndividual) {
			methodBodyContent.append("return ONTOLOGY_NAME;");
		} else {
			methodBodyContent.append("if (resourceFactory.containsKey(this)) {\n");
			methodBodyContent.append("return " + ontologyThingName + "." + EField.RDF_MODEL_NAMESPACE.variableName
					+ " + resourceFactory.get(this);\n");
			methodBodyContent.append("} else {\n");
			methodBodyContent.append(
					"final String resourceName = getClass().getSimpleName() + \"_\" + resourceFactory.size();\n");
			methodBodyContent.append("resourceFactory.put(this, resourceName);\n");
			methodBodyContent.append("return " + ontologyThingName + "." + EField.RDF_MODEL_NAMESPACE.variableName
					+ " + resourceName;}\n");

		}

		return new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
				.addAnnotation(annotationBuilder.buildOverrideAnnotation())
				.setMethodBody(new OntologyMethodBody(methodBodyContent.toString())).setMethodName("getResourceName")
				.setMethodType(EMethodType.GETTER).setReturnType(String.class.getSimpleName()).build();

	}

	public JavaMethod generateIsEmptyMethod(Set<JavaField> fields, boolean isDTPropertyClass) {

		StringBuilder methodBodyContent = new StringBuilder();

		List<JavaField> filteredFields = fields.stream()
				.filter(f -> f.getAnnotations().contains(annotationBuilder.buildOntologyModelContentAnnotation()))
				.collect(Collectors.toList());

		methodBodyContent.append("boolean isEmpty = true;\n");
		if (isDTPropertyClass) {
			methodBodyContent.append(
					"isEmpty &= this." + EField.DATATYPE_PROPERTY_VALUE_ANNOTATION_NAME.variableName + " == null;\n");
			methodBodyContent.append("if(!isEmpty) return false;\n");
			methodBodyContent.append("\n");
			methodBodyContent.append("return true;");
		} else {

			if (filteredFields.isEmpty()) {
				methodBodyContent.append("return false;");
			} else {

				for (JavaField field : filteredFields) {
					methodBodyContent.append("isEmpty &= this." + field.getClassVariableName() + " == null || this."
							+ field.getClassVariableName() + " .isEmpty();\n");
					methodBodyContent.append("if(!isEmpty) return false;\n");
					methodBodyContent.append("\n");
				}
				methodBodyContent.append("return true;");
			}

		}

		return new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
				.addAnnotation(annotationBuilder.buildOverrideAnnotation())
				.setMethodBody(new OntologyMethodBody(methodBodyContent.toString())).setMethodName("isEmpty")
				.setMethodType(EMethodType.ELSE).setReturnType(boolean.class.getSimpleName()).build();

	}

	public JavaMethod generateEqualsMethod(Set<JavaField> fields, final String className, boolean isDatatype) {
		StringBuilder methodBodyContent = new StringBuilder();
		methodBodyContent.append("if (this == obj)\n");
		methodBodyContent.append("return true;\n");
		methodBodyContent.append("if (obj == null)\n");
		methodBodyContent.append("return false;\n");
		methodBodyContent.append("if (getClass() != obj.getClass())\n");
		methodBodyContent.append("return false;\n");
		methodBodyContent.append(className + " other = (" + className + ") obj;\n");

		if (!isDatatype) {
			methodBodyContent.append("if (" + OntologyInitializer.INDIVIDUAL_FIELD_NAME + " == null) {\n");
			methodBodyContent.append("if (other." + OntologyInitializer.INDIVIDUAL_FIELD_NAME + "!= null)\n");
			methodBodyContent.append("return false;\n");
			methodBodyContent.append("} else if (!" + OntologyInitializer.INDIVIDUAL_FIELD_NAME + ".equals(other."
					+ OntologyInitializer.INDIVIDUAL_FIELD_NAME + "))\n");
			methodBodyContent.append("return false;\n");

			methodBodyContent.append("if (investigationRestriction == null) {\n");
			methodBodyContent.append("if (other.investigationRestriction!= null)\n");
			methodBodyContent.append("return false;\n");
			methodBodyContent.append("} else if (!investigationRestriction.equals(other.investigationRestriction))\n");
			methodBodyContent.append("return false;\n");
		}

		for (JavaField field : fields) {

			if (field.isStatic())
				continue;

			methodBodyContent.append("if (" + field.getClassVariableName() + " == null) {\n");
			methodBodyContent.append("if (other." + field.getClassVariableName() + "!= null)\n");
			methodBodyContent.append("return false;\n");
			methodBodyContent.append("} else if (!" + field.getClassVariableName() + ".equals(other."
					+ field.getClassVariableName() + "))\n");
			methodBodyContent.append("return false;\n");
		}
		methodBodyContent.append("return true;\n");

		return new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
				.addAnnotation(annotationBuilder.buildOverrideAnnotation())
				.setMethodBody(new OntologyMethodBody(methodBodyContent.toString())).setMethodName("equals")
				.addArgument(new JavaMethodParameter(Object.class.getSimpleName(), "obj"))
				.setMethodType(EMethodType.ELSE).setReturnType(boolean.class.getSimpleName()).build();
	}

	public JavaMethod generateHashCodeMethod(Set<JavaField> fields, boolean isDatatype) {

		StringBuilder methodBodyContent = new StringBuilder();
		methodBodyContent.append("final int prime = 31;\n");
		methodBodyContent.append("int result = 1;\n");

		if (!isDatatype) {

			methodBodyContent.append("result = prime * result + ((this." + OntologyInitializer.INDIVIDUAL_FIELD_NAME
					+ " == null) ? 0 : this." + OntologyInitializer.INDIVIDUAL_FIELD_NAME + ".hashCode());\n");
			methodBodyContent.append(
					"result = prime * result + ((this.investigationRestriction == null) ? 0 : this.investigationRestriction.hashCode());\n");
		}

		for (JavaField field : fields) {

			if (field.isStatic())
				continue;

			methodBodyContent.append("result = prime * result + ((this." + field.getClassVariableName()
					+ " == null) ? 0 : this." + field.getClassVariableName() + ".hashCode());\n");

		}

		methodBodyContent.append("return result;");

		return new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
				.addAnnotation(annotationBuilder.buildOverrideAnnotation())
				.setMethodBody(new OntologyMethodBody(methodBodyContent.toString())).setMethodName("hashCode")
				.setMethodType(EMethodType.ELSE).setReturnType(int.class.getSimpleName()).build();
	}

	public List<JavaMethod> generateOffAndOnSetGetter() {

		List<JavaMethod> methods = new ArrayList<>();

		methods.add(new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
				.addAnnotation(annotationBuilder.buildOverrideAnnotation())
				.setMethodBody(new OntologyMethodBody("return " + EField.CHARACTER_OFFSET.variableName + ";"))
				.setMethodName("get" + EField.CHARACTER_OFFSET.methodName).setMethodType(EMethodType.GETTER)
				.setReturnType(Integer.class.getSimpleName()).build());

		methods.add(new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
				.addAnnotation(annotationBuilder.buildOverrideAnnotation())
				.setMethodBody(new OntologyMethodBody("return " + EField.CHARACTER_ONSET.variableName + ";"))
				.setMethodName("get" + EField.CHARACTER_ONSET.methodName).setMethodType(EMethodType.GETTER)
				.setReturnType(Integer.class.getSimpleName()).build());

		return methods;

	}

	public List<JavaMethod> generateOffAndOnSetSetter() {

		List<JavaMethod> methods = new ArrayList<>();

//		List<JavaMethodParameter> offsetArgs = new ArrayList<>();
//		offsetArgs.add(new JavaMethodParameter(Integer.class.getSimpleName(), "offset"));
//
//		methods.add(new OntologyMethodBuilder().setAccessType(EAccessType.PRIVATE).setArguments(offsetArgs)
//				.addAnnotation(annotationBuilder.buildOverrideAnnotation())
//				.setMethodBody(new OntologyMethodBody("this." + EField.CHARACTER_OFFSET.variableName + " = offset;"))
//				.setMethodName("set" + EField.CHARACTER_OFFSET.methodName).setMethodType(EMethodType.SETTER).build());

		List<JavaMethodParameter> onsetArgs = new ArrayList<>();
		onsetArgs.add(new JavaMethodParameter(Integer.class.getSimpleName(), "onset"));

		methods.add(
				new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
						.addAnnotation(annotationBuilder.buildOverrideAnnotation()).setArguments(onsetArgs)
						.setMethodBody(new OntologyMethodBody("this." + EField.CHARACTER_ONSET.variableName
								+ " = onset;\n this.characterOffset = onset + " + EField.TEXT_MENTION.variableName
								+ ".length();"))
						.setMethodName("set" + EField.CHARACTER_ONSET.methodName).setMethodType(EMethodType.SETTER)
						.build());

		return methods;

	}

//	@Deprecated
//	public JavaMethod generateAnnotationIDGetter() {
//		return new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
//				.addAnnotation(annotationBuilder.buildOverrideAnnotation())
//				.setMethodBody(new OntologyMethodBody("return " + EField.ANNOTATION_ID_FIELD.variableName + ";"))
//				.setMethodName("get" + EField.ANNOTATION_ID_FIELD.methodName).setMethodType(EMethodType.GETTER)
//				.setReturnType(String.class.getSimpleName()).build();
//	}

	public JavaMethod generateMentionGetter() {
		return new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
				.addAnnotation(annotationBuilder.buildOverrideAnnotation())
				.setMethodBody(new OntologyMethodBody("return " + OntologyFieldNames.TEXT_MENTION_FIELD_NAME + ";"))
				.setMethodName(
						"get" + JavaClassNamingTools.normalizeClassName(OntologyFieldNames.TEXT_MENTION_FIELD_NAME))
				.setMethodType(EMethodType.GETTER).setReturnType(String.class.getSimpleName()).build();
	}

	public JavaMethod generateOntologyNameGetter() {
		return new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
				.addAnnotation(annotationBuilder.buildOverrideAnnotation())
				.setMethodBody(new OntologyMethodBody("return " + EField.ONTOLOGY_NAME.variableName + ";"))
				.setMethodName("get" + EField.ONTOLOGY_NAME.methodName).setMethodType(EMethodType.GETTER)
				.setReturnType(String.class.getSimpleName()).build();
	}

	public JavaMethod generateGetThisMethod() {
		return new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
				.addAnnotation(annotationBuilder.buildOverrideAnnotation())
				.setMethodBody(new OntologyMethodBody("return this;")).setMethodName("getThis")
				.setMethodType(EMethodType.ELSE).setReturnType(IOBIEThing.class.getSimpleName()).build();
	}

	public JavaMethod generateValueGetter() {
		return new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
				.addAnnotation(annotationBuilder.buildOverrideAnnotation())
				.setMethodBody(new OntologyMethodBody(
						"return " + EField.DATATYPE_PROPERTY_VALUE_ANNOTATION_NAME.variableName + ";"))
				.setMethodName("get" + EField.DATATYPE_PROPERTY_VALUE_ANNOTATION_NAME.methodName)
				.setMethodType(EMethodType.GETTER).setReturnType(String.class.getSimpleName()).build();
	}

	public List<JavaMethod> generateAdders(OntologyClass scioClass) {
		return generateAdders(scioClass, scioClass);
	}

	public List<JavaMethod> generateAdders(OntologyClass scioClass, OntologyClass rootClass) {
		List<JavaMethod> methods = new ArrayList<>();

		if (!scioClass.domainRangeRelations.isEmpty()) {
			for (OntologySlotData relation : scioClass.domainRangeRelations.keySet()) {

				for (OntologyClass range : scioClass.domainRangeRelations.get(relation)) {

					ECardinalityType cardinalityType = relation.cardinalityType != ECardinalityType.UNDEFINED
							? relation.cardinalityType
							: ECardinalityType.SINGLE;

					if (cardinalityType != ECardinalityType.SINGLE) {
						final StringBuilder methodBodyContent = new StringBuilder();
						methodBodyContent.append("this."
								+ JavaClassNamingTools.combineRelationWithClassNameAsPluralVariableName(
										relation.javaClassPropertyName, range.javaClassName)
								+ ".add(" + range.javaClassFieldName + ");\n");
						methodBodyContent.append("return this;");

						methods.add(new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
								.addAnnotation(annotationBuilder.buildOverrideAnnotation())
								.addArgument(new JavaMethodParameter(range.javaInterfaceName, range.javaClassFieldName))
								.setMethodBody(new OntologyMethodBody(methodBodyContent.toString()))
								.setDocumentation(relation.documentation)
								.setMethodName("add" + JavaClassNamingTools.combineRelationWithClassNameAsClassName(
										relation.javaClassPropertyName, range.javaClassName))
								.setMethodType(EMethodType.ADDER).setReturnType(rootClass.javaClassName).build());
					}
				}
			}
		}

		/*
		 * Recursive for all super classes.
		 */

		for (OntologyClass superClass : scioClass.superclasses) {

			if (superClass.subclasses.isEmpty())
				continue;

			methods.addAll(generateAdders(superClass, rootClass));
		}

		return methods;
	}

	public List<JavaMethod> generateGetters(OntologyClass scioClass) {
		List<JavaMethod> methods = new ArrayList<>();
		if (!scioClass.domainRangeRelations.isEmpty()) {
			for (OntologySlotData relation : scioClass.domainRangeRelations.keySet()) {

				for (OntologyClass range : scioClass.domainRangeRelations.get(relation)) {
					ECardinalityType cardinalityType = relation.cardinalityType != ECardinalityType.UNDEFINED
							? relation.cardinalityType
							: ECardinalityType.SINGLE;
					JavaMethod m;

					if (cardinalityType == ECardinalityType.SINGLE) {
						m = new OntologyMethodBuilder().setDocumentation(relation.documentation)
								.setAccessType(EAccessType.PUBLIC)
								.addAnnotation(annotationBuilder.buildOverrideAnnotation())
								.setMethodBody(new OntologyMethodBody(
										"return " + JavaClassNamingTools.combineRelationWithClassNameAsVariableName(
												relation.javaClassPropertyName, range.javaClassName) + ";"))
								.setMethodName("get" + JavaClassNamingTools.combineRelationWithClassNameAsClassName(
										relation.javaClassPropertyName, range.javaClassName))
								.setMethodType(EMethodType.GETTER).setReturnType(range.javaInterfaceName).build();
					} else {
						m = new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
								.setDocumentation(relation.documentation)
								.addAnnotation(annotationBuilder.buildOverrideAnnotation())
								.setMethodBody(new OntologyMethodBody("return "
										+ JavaClassNamingTools.combineRelationWithClassNameAsPluralVariableName(
												relation.javaClassPropertyName, range.javaClassName)
										+ ";"))
								.setMethodName(
										"get" + JavaClassNamingTools.combineRelationWithClassNameAsPluralClassName(
												relation.javaClassPropertyName, range.javaClassName))
								.setMethodType(EMethodType.GETTER)
								.setReturnType("List<" + range.javaInterfaceName + ">")
								.setReturnInnerTypeName(range.javaInterfaceName).build();
					}
					methods.add(m);
				}
			}
		}

		/*
		 * Recursive for all super classes.
		 */

		for (OntologyClass superClass : scioClass.superclasses) {

			if (superClass.subclasses.isEmpty())
				continue;

			methods.addAll(generateGetters(superClass));
		}

		return methods;
	}

	public List<JavaMethod> generateSetters(OntologyClass scioClass) {
		return generateSetters(scioClass, scioClass);
	}

	public List<JavaMethod> generateSetters(OntologyClass scioClass, OntologyClass rootClass) {
		List<JavaMethod> methods = new ArrayList<>();

		if (!scioClass.domainRangeRelations.isEmpty()) {
			for (OntologySlotData relation : scioClass.domainRangeRelations.keySet()) {

				for (OntologyClass range : scioClass.domainRangeRelations.get(relation)) {

					ECardinalityType cardinalityType = relation.cardinalityType != ECardinalityType.UNDEFINED
							? relation.cardinalityType
							: ECardinalityType.SINGLE;

					JavaMethod m;

					if (cardinalityType == ECardinalityType.SINGLE) {

						final StringBuilder methodBodyContent = new StringBuilder();
						final String parameterVariableName = range.javaClassFieldName;
						methodBodyContent.append("this."
								+ JavaClassNamingTools.combineRelationWithClassNameAsVariableName(
										relation.javaClassPropertyName, range.javaClassName)
								+ " = " + parameterVariableName + ";\n");
						methodBodyContent.append("return this;");
						m = new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
								.setDocumentation(relation.documentation)

								.addAnnotation(annotationBuilder.buildOverrideAnnotation())
								.addArgument(new JavaMethodParameter(range.javaInterfaceName, parameterVariableName))
								.setMethodBody(new OntologyMethodBody(methodBodyContent.toString()))
								.setMethodName("set" + JavaClassNamingTools.combineRelationWithClassNameAsClassName(
										relation.javaClassPropertyName, range.javaClassName))
								.setMethodType(EMethodType.SETTER).setReturnType(rootClass.javaClassName).build();
					} else {
						final StringBuilder methodBodyContent = new StringBuilder();
						final String parameterVariableName = range.javaClassFieldPluralName;

						methodBodyContent.append("if(" + parameterVariableName
								+ "==null){throw new IllegalArgumentException(\"Can not set list objects to null.\");}");
						methodBodyContent.append("this."
								+ JavaClassNamingTools.combineRelationWithClassNameAsPluralVariableName(
										relation.javaClassPropertyName, range.javaClassName)
								+ " = " + parameterVariableName + ";\n");
						methodBodyContent.append("return this;");

						m = new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
								.setDocumentation(relation.documentation)
								.addAnnotation(annotationBuilder.buildOverrideAnnotation())
								.addArgument(new JavaMethodParameter("List<" + range.javaInterfaceName + ">",
										parameterVariableName))
								.setMethodBody(new OntologyMethodBody(methodBodyContent.toString()))
								.setMethodName(
										"set" + JavaClassNamingTools.combineRelationWithClassNameAsPluralClassName(
												relation.javaClassPropertyName, range.javaClassName))
								.setMethodType(EMethodType.GETTER).setReturnType(rootClass.javaClassName)
								.setReturnInnerTypeName(range.javaInterfaceName).build();
					}
					methods.add(m);
				}
			}
		}

		/*
		 * Recursive for all super classes.
		 */
		for (OntologyClass superClass : scioClass.superclasses) {

			if (superClass.subclasses.isEmpty())
				continue;

			methods.addAll(generateSetters(superClass, rootClass));
		}

		return methods;
	}

	public JavaMethod generateGetRDFModelMethod(final String ontologyNameThing, final Set<JavaField> fields,
			final String className, final boolean isDataTypeClass) {

		final String resourceIDPrefix = "resourceIDPrefix";

		StringBuilder toRDFModel = new StringBuilder();

		toRDFModel.append("Model model = ModelFactory.createDefaultModel();\n");
		/*
		 * If datatype class return empty model.
		 */
		if (!isDataTypeClass) {
			toRDFModel.append("Resource group = model.createResource(getResourceName());\n");

			for (JavaField field : fields) {

				if (!field.getAnnotations().contains(annotationBuilder.buildOntologyModelContentAnnotation()))
					continue;

				toRDFModel.append("if(" + field.getClassVariableName() + "!= null && !(" + field.getClassVariableName()
						+ ".getClass().isAnnotationPresent(" + EAnnotation.DATA_TYPE_ANNOTATION.annotationClassName
						+ ".class) && " + field.getClassVariableName() + ".isEmpty()) ){\n");

				if (field.getAnnotations().contains(annotationBuilder.buildTextMentionAnnotation())) {
					toRDFModel.append("model.add(group, model.createProperty(" + ontologyNameThing + "."
							+ EField.RDF_MODEL_NAMESPACE + " + \"has"
							+ JavaClassNamingTools.normalizeClassName(OntologyFieldNames.TEXT_MENTION_FIELD_NAME)
							+ "\")," + "model.createLiteral(" + field.getClassVariableName() + "));");
					toRDFModel.append("\n}\n");
				} else {
					final String variableName;
					/*
					 * Get the parameter from the ontology-model annotation. This is the
					 * ontologyName for that property.
					 */
					final String ontologyName = field.getAnnotations().stream()
							.filter(a -> a.annotation == EAnnotation.ONTOLOGY_MODEL_CONTENT_ANNOTATION_NAME)
							.map(a -> a.parameter.stream().findFirst().get()).reduce("", String::concat);

					switch (field.getRelationType()) {
					case COLLECTION:
						variableName = "_" + field.getClassVariableName();
						toRDFModel.append("for (" + field.getInnerTypeName() + " " + variableName + " : "
								+ field.getClassVariableName() + "){");
						toRDFModel.append("if (" + variableName + "!= null){");
						if (field.getAnnotations().contains(annotationBuilder.buildDataTypePropertyValueAnnotation())) {
							addLiteralToModel(resourceIDPrefix, toRDFModel, variableName, ontologyName);
							// addResourceToModel(resourceIDPrefix, toRDFModel,
							// variableName, ontologyName);
							toRDFModel.append("\n" + "\n");
						} else {
							addResourceToModel(resourceIDPrefix, toRDFModel, variableName, ontologyName);
						}
						toRDFModel.append("\n}}}\n");
						break;
					case SINGLE:
					default:
						variableName = field.getClassVariableName();

						if (field.getAnnotations().contains(annotationBuilder.buildDataTypePropertyValueAnnotation())) {
							addLiteralToModel(resourceIDPrefix, toRDFModel, variableName, ontologyName);
							// addResourceToModel(resourceIDPrefix, toRDFModel,
							// variableName, ontologyName);
							toRDFModel.append("\n \n");
						} else {
							addResourceToModel(resourceIDPrefix, toRDFModel, variableName, ontologyName);
						}
						toRDFModel.append("\n}\n");

						break;
					}
				}
			}

			toRDFModel.append(
					"model.add(group, model.createProperty(\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\"),");
			toRDFModel.append("model.createResource(" + EField.ONTOLOGY_NAME.variableName + "));");
		}

		toRDFModel.append("\nreturn model;\n");

		return new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
				.addAnnotation(annotationBuilder.buildOverrideAnnotation())
				.addArgument(new JavaMethodParameter(String.class.getSimpleName(), resourceIDPrefix))
				.setMethodBody(new OntologyMethodBody(toRDFModel.toString())).setMethodName("getRDFModel")
				.setMethodType(EMethodType.ELSE).setReturnType(Model.class.getSimpleName()).build();

	}

	private void addLiteralToModel(final String resourceIDPrefix, StringBuilder getModel, final String variableName,
			final String ontologyPropertyName) {

		/*
		 * TODO: Check if this works without checking for named individuals
		 */
//		getModel.append("if(!" + variableName + ".getClass().isAnnotationPresent("
//				+ EAnnotation.NAMED_INDIVIDUAL.annotationClassName + ".class))");

		getModel.append("model.add(" + variableName + ".getRDFModel(" + resourceIDPrefix + "));\n");

		getModel.append("model.add(group, " + "model.createProperty(" + "\"" + ontologyPropertyName + "\"),"
				+ "model.createLiteral(" + variableName + ".get"
				+ JavaClassNamingTools.normalizeClassName(EField.DATATYPE_PROPERTY_VALUE_ANNOTATION_NAME.variableName)
				+ "()));");

		// getModel.append("model.add(group, model.createProperty(" +
		// OWLToJavaClassConverter.ONTOLOGY_THING_INTERFACE
		// + "." + EField.RDF_MODEL_NAMESPACE + " + \"" + ontologyName + "\"),
		// model.createLiteral(" + variableName
		// + ".get" + EField.DATATYPE_PROPERTY_VALUE_ANNOTATION_NAME.methodName
		// + "()));\n");
	}

	private void addResourceToModel(final String resourceIDPrefix, StringBuilder getModel, final String variableName,
			final String ontologyPropertyName) {

		/*
		 * TODO: Check if this works without checking for named individuals
		 */
//		getModel.append("if(!" + variableName + ".getClass().isAnnotationPresent("
//				+ EAnnotation.NAMED_INDIVIDUAL.annotationClassName + ".class))");
		getModel.append("model.add(" + variableName + ".getRDFModel(" + resourceIDPrefix + "));\n");

		getModel.append("model.add(group, " + "model.createProperty(" + "\"" + ontologyPropertyName + "\"),"
				+ "model.createResource(" + variableName + ".getResourceName()));");
	}

}
