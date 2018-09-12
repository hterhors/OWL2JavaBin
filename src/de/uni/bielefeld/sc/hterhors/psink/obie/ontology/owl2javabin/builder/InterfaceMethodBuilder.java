package de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.interfaces.IOBIEThing;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.JavaClassNamingTools;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.owlreader.ECardinalityType;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.owlreader.container.OntologyClass;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.owlreader.container.OntologySlotData;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.enums.EAccessType;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.enums.EMethodType;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.java.JavaMethod;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.java.JavaMethodParameter;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.java.JavaMethod.OntologyMethodBody;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.java.JavaMethod.OntologyMethodBuilder;

public class InterfaceMethodBuilder {
	final String ontologyThingName;

	public InterfaceMethodBuilder(String ontologyThingName) {
		this.ontologyThingName = ontologyThingName;
	}

	public JavaMethod generateDataTypeCompareToClassInterfaceMethod(final Set<JavaMethod> methods,
			final String className, final Set<String> interfaces, boolean isDataTypeProperty) {

		StringBuilder methodBodyContent = new StringBuilder();

		methodBodyContent.append("s.maxValue+=2;\n");
		methodBodyContent.append("if(o == null ){" + "	s.value+=1;	\n" + "\nreturn s;" + "}");
		methodBodyContent.append("if (o.getClass().getInterfaces()[0].isAssignableFrom(this.getClass())) {\n");

		final String variableName = JavaClassNamingTools.getVariableName(className);

		methodBodyContent.append(className + " " + variableName + " = (" + className + ") o;\n");

		methodBodyContent.append("if(this.getValue() == null && " + variableName + "." + "getValue() == null){\n");
		methodBodyContent.append("	s.value+=1;	\n");
		methodBodyContent
				.append("	}else if(this.getValue() == null &&" + variableName + "." + "getValue() != null){\n");
		methodBodyContent.append("	\n");
		methodBodyContent.append("	}else{\n");
		methodBodyContent.append("	s.value+=this.getValue().equals(" + variableName + "." + "getValue())?2:0;\n");
		methodBodyContent.append("	}\n");

		methodBodyContent.append("}\n");

		methodBodyContent.append("return s;\n");

		return new OntologyMethodBuilder().setDefaultInterface(true).setAccessType(EAccessType.NONE)
				.addArgument(new JavaMethodParameter(ontologyThingName, "o"))
				.addArgument(new JavaMethodParameter(IOBIEThing.Score.class.getSimpleName(), "s"))
				.setMethodBody(new OntologyMethodBody(methodBodyContent.toString()))
				.setMethodName("compareTo" + className).setMethodType(EMethodType.ELSE)
				.setReturnType(IOBIEThing.Score.class.getSimpleName()).build();

	}

	// default Score compareToIMale(IPSINKThing o, Score s) {
	// s.maxValue += 2;
	// if (o == null) {
	// s.value++;
	// return s;
	// }
	// if (o.getClass().getInterfaces()[0].isAssignableFrom(this.getClass())) {
	// this.compareToIGender(o, s);
	// if (o instanceof IMale) {
	// s.value += 2;
	// }
	// }
	// return s;
	// }
	public JavaMethod generateLeaveClassCompareToClassInterfaceMethod(final Set<JavaMethod> methods,
			final String className, final Set<String> interfaces, boolean isDataTypeProperty) {

		StringBuilder methodBodyContent = new StringBuilder();

		methodBodyContent.append("s.maxValue+=2;\n");
		methodBodyContent.append("if(o == null ){" + "	s.value+=1;	\n" + "\nreturn s;" + "}");
		methodBodyContent.append("if (o.getClass().getInterfaces()[0].isAssignableFrom(this.getClass())) {\n");
		for (String superInterface : interfaces) {
			if (superInterface.equals(ontologyThingName))
				continue;

			methodBodyContent.append("	this.compareTo" + superInterface + "(o,s);\n");

		}
		methodBodyContent.append("	if(o instanceof " + className + "){\n");
		methodBodyContent.append("		s.value+=2;\n");
		methodBodyContent.append("	}\n");

		methodBodyContent.append("}\n");

		methodBodyContent.append("return s;\n");

		return new OntologyMethodBuilder().setDefaultInterface(true).setAccessType(EAccessType.NONE)
				.addArgument(new JavaMethodParameter(ontologyThingName, "o"))
				.addArgument(new JavaMethodParameter(IOBIEThing.Score.class.getSimpleName(), "s"))
				.setMethodBody(new OntologyMethodBody(methodBodyContent.toString()))
				.setMethodName("compareTo" + className).setMethodType(EMethodType.ELSE)
				.setReturnType(IOBIEThing.Score.class.getSimpleName()).build();

	}

	public JavaMethod generateDefaultCompareToClassInterfaceMethod(final Set<JavaMethod> methods,
			final String className, final Set<String> interfaces, boolean isDataTypeProperty) {

		StringBuilder methodBodyContent = new StringBuilder();

		methodBodyContent.append("s.maxValue++;\n");
		methodBodyContent.append("if(o == null )\nreturn s;");
		methodBodyContent.append("if (o.getClass().getInterfaces()[0].isAssignableFrom(this.getClass())) {\n");
		for (String superInterface : interfaces) {
			if (superInterface.equals(ontologyThingName))
				continue;

			methodBodyContent.append("	this.compareTo" + superInterface + "(o,s);\n");

		}
		methodBodyContent.append("	if(o instanceof " + className + "){\n");
		methodBodyContent.append("		s.value++;\n");
		methodBodyContent.append("	}\n");

		final String variableName = JavaClassNamingTools.getVariableName(className);

		Set<JavaMethod> filteredMethods = methods.stream().filter(m -> m.getMethodType() == EMethodType.GETTER)
				.collect(Collectors.toSet());

		if (!filteredMethods.isEmpty())
			methodBodyContent.append(className + " " + variableName + " = (" + className + ") o;\n");

		for (JavaMethod m : filteredMethods) {
			if (m.getReturnType().startsWith("List<")) {

				methodBodyContent.append("if ((this." + m.getMethodName() + "() == null && " + variableName + "."
						+ m.getMethodName() + "() != null)|| (this." + m.getMethodName() + "() != null && "
						+ variableName + "." + m.getMethodName() + "() == null)) {\n");
				methodBodyContent.append("		s.value += 0;\n");
				methodBodyContent.append("		s.maxValue++;\n");
				methodBodyContent.append("}else if (this." + m.getMethodName() + "() == null && " + variableName + "."
						+ m.getMethodName() + "() == null) {\n");
				methodBodyContent.append("		s.value += 1;\n");
				methodBodyContent.append("		s.maxValue++;\n");
				methodBodyContent.append("}else if (this." + m.getMethodName() + "().size() == 0 || " + variableName
						+ "." + m.getMethodName() + "().size() == 0) {\n");
				methodBodyContent.append("		s.value += 1;\n");
				methodBodyContent.append("		s.maxValue++;\n");
				methodBodyContent.append("	} else if (this." + m.getMethodName() + "().size() == 1 && " + variableName
						+ "." + m.getMethodName() + "().size() == 1) {\n");
				methodBodyContent.append("	s.value += this." + m.getMethodName() + "().get(1).compareTo("
						+ variableName + "." + m.getMethodName() + "().get(1));\n");
				methodBodyContent.append("	} else {\n");

				final String methodInnerRetType = m.getReturnInnerTypeName();
				final String bestPermScoreName = "bestPermutationScore" + methodInnerRetType;

				methodBodyContent.append("double " + bestPermScoreName + " = 0;\n");

				final String indexPermutationsName = "indexPermutations" + methodInnerRetType;

				methodBodyContent.append("		Collection<List<Integer>> " + indexPermutationsName
						+ " = Collections2.permutations(\n");
				methodBodyContent.append("				IntStream.range(0, this." + m.getMethodName()
						+ "().size()).boxed().collect(Collectors.toList()));\n");

				final String indexPerm = "indexPerm" + methodInnerRetType;

				methodBodyContent.append("	for (List<Integer> " + indexPerm + " : " + indexPermutationsName + ") {\n");

				final String currentPermutationScore = "currentPermutationScore" + methodInnerRetType;

				methodBodyContent.append("	double " + currentPermutationScore + " = 0;\n");
				methodBodyContent.append("		for (int j = 0; j < " + indexPerm + ".size(); j++) {\n");

				final String thisM = "this" + methodInnerRetType;

				methodBodyContent.append("			" + methodInnerRetType + " " + thisM + " = this."
						+ m.getMethodName() + "().get(j);\n");

				final String _M = "_" + methodInnerRetType;

				methodBodyContent.append("				" + methodInnerRetType + " " + _M + " = " + variableName + "."
						+ m.getMethodName() + "().get(" + indexPerm + ".get(j));\n");
				methodBodyContent
						.append("			" + currentPermutationScore + " += " + thisM + ".compareTo(" + _M + ");\n");
				methodBodyContent.append("		}\n");
				methodBodyContent.append("		" + bestPermScoreName + " = Math.max(" + bestPermScoreName + ",\n");
				methodBodyContent.append("				" + currentPermutationScore + ");\n");
				methodBodyContent.append("		}\n");
				methodBodyContent.append("	s.value+= " + bestPermScoreName + ";\n");
				methodBodyContent.append("	s.maxValue+=this." + m.getMethodName() + "().size();\n");
				methodBodyContent.append("	}\n");

			} else {
				methodBodyContent.append("if(this." + m.getMethodName() + "() == null && " + variableName + "."
						+ m.getMethodName() + "() == null){\n");
				methodBodyContent.append("	s.value+=1;	\n");
				methodBodyContent.append("	s.maxValue++;\n");
				methodBodyContent.append("	}else if(this." + m.getMethodName() + "() == null &&" + variableName + "."
						+ m.getMethodName() + "() != null){\n");
				methodBodyContent.append("		s.maxValue++;\n");
				methodBodyContent.append("	}else{\n");
				if (m.getReturnType().equals(String.class.getSimpleName())) {
					methodBodyContent.append("	s.value+=this." + m.getMethodName() + "().equals(" + variableName + "."
							+ m.getMethodName() + "())?1:0;\n");
					methodBodyContent.append("	s.maxValue++;\n");
				} else {

					methodBodyContent.append("		this." + m.getMethodName() + "().compareToWithScore" + "("
							+ variableName + "." + m.getMethodName() + "(),s);\n");
				}

				methodBodyContent.append("	}\n");

			}

		}
		methodBodyContent.append("}\n");

		methodBodyContent.append("return s;\n");

		return new OntologyMethodBuilder().setDefaultInterface(true).setAccessType(EAccessType.NONE)
				.addArgument(new JavaMethodParameter(ontologyThingName, "o"))
				.addArgument(new JavaMethodParameter(IOBIEThing.Score.class.getSimpleName(), "s"))
				.setMethodBody(new OntologyMethodBody(methodBodyContent.toString()))
				.setMethodName("compareTo" + className).setMethodType(EMethodType.ELSE)
				.setReturnType(IOBIEThing.Score.class.getSimpleName()).build();

	}

	public JavaMethod generateDefaultCompareToWithScoreInterfaceMethod(final String className) {
		StringBuilder methodBodyContent = new StringBuilder();

		methodBodyContent.append("return compareTo" + className + "(o,s);\n");

		return new OntologyMethodBuilder().setDefaultInterface(true).setAccessType(EAccessType.NONE)
				.addArgument(new JavaMethodParameter(ontologyThingName, "o"))
				.addArgument(new JavaMethodParameter(IOBIEThing.Score.class.getSimpleName(), "s"))
				.setMethodBody(new OntologyMethodBody(methodBodyContent.toString())).setMethodName("compareToWithScore")
				.setMethodType(EMethodType.ELSE).setReturnType(IOBIEThing.Score.class.getSimpleName()).build();
	}

	public JavaMethod generateDefaultCompareToInterfaceMethod(final String className) {
		StringBuilder methodBodyContent = new StringBuilder();

		methodBodyContent.append(IOBIEThing.Score.class.getSimpleName() + " s = compareTo" + className + "(o,new "
				+ IOBIEThing.Score.class.getSimpleName() + "());\n");
		methodBodyContent.append("return s.value/s.maxValue;\n");

		return new OntologyMethodBuilder().setDefaultInterface(true).setAccessType(EAccessType.NONE)
				.addArgument(new JavaMethodParameter(ontologyThingName, "o"))
				.setMethodBody(new OntologyMethodBody(methodBodyContent.toString())).setMethodName("compareTo")
				.setMethodType(EMethodType.ELSE).setReturnType(double.class.getSimpleName()).build();
	}

	public List<JavaMethod> generateGetterInterface(OntologyClass scioClass) {
		List<JavaMethod> methods = new ArrayList<>();

		if (!scioClass.domainRangeRelations.isEmpty()) {

			for (OntologySlotData relation : scioClass.domainRangeRelations.keySet()) {

				for (OntologyClass range : scioClass.domainRangeRelations.get(relation)) {

					ECardinalityType cardinalityType = relation.cardinalityType != ECardinalityType.UNDEFINED
							? relation.cardinalityType
							: ECardinalityType.SINGLE;

					JavaMethod m;

					if (cardinalityType == ECardinalityType.SINGLE) {
						m = new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
								.setDocumentation(relation.documentation).setMethodBody(new OntologyMethodBody(true))
								.setMethodName("get" + JavaClassNamingTools.combineRelationWithClassNameAsClassName(
										relation.javaClassPropertyName, range.javaClassName))
								.setMethodType(EMethodType.GETTER).setReturnType(range.javaInterfaceName).build();
					} else {
						m = new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
								.setDocumentation(relation.documentation).setMethodBody(new OntologyMethodBody(true))
								.setMethodName("get" + JavaClassNamingTools.combineRelationWithClassNameAsPluralClassName(
										relation.javaClassPropertyName, range.javaClassName))
								.setMethodType(EMethodType.GETTER)
								.setReturnType("List<" + range.javaInterfaceName + ">")
								.setReturnInnerTypeName(range.javaInterfaceName).build();
					}
					methods.add(m);
				}
			}
		}

		return methods;
	}

	public List<JavaMethod> generateAdderInterface(OntologyClass scioClass) {
		return generateAdderInterface(scioClass, scioClass);
	}

	public List<JavaMethod> generateAdderInterface(OntologyClass scioClass, OntologyClass rootClass) {
		List<JavaMethod> methods = new ArrayList<>();

		if (!scioClass.domainRangeRelations.isEmpty()) {
			for (OntologySlotData relation : scioClass.domainRangeRelations.keySet()) {

				for (OntologyClass range : scioClass.domainRangeRelations.get(relation)) {
					ECardinalityType cardinalityType = relation.cardinalityType != ECardinalityType.UNDEFINED
							? relation.cardinalityType
							: ECardinalityType.SINGLE;

					if (cardinalityType != ECardinalityType.SINGLE) {
						methods.add(new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
								.setDocumentation(relation.documentation)
								.addArgument(new JavaMethodParameter(range.javaInterfaceName, range.javaClassFieldName))
								.setMethodBody(new OntologyMethodBody(true))
								.setMethodName("add" + JavaClassNamingTools.combineRelationWithClassNameAsClassName(
										relation.javaClassPropertyName, range.javaClassName))
								.setMethodType(EMethodType.ADDER).setReturnType(rootClass.javaInterfaceName).build());
					}
				}
			}
		}

		return methods;
	}

	public List<JavaMethod> generateSetterInterface(OntologyClass scioClass) {
		return generateSetterInterface(scioClass, scioClass);
	}

	public List<JavaMethod> generateSetterInterface(OntologyClass scioClass, OntologyClass rootClass) {
		List<JavaMethod> methods = new ArrayList<>();

		if (!scioClass.domainRangeRelations.isEmpty()) {
			for (OntologySlotData relation : scioClass.domainRangeRelations.keySet()) {

				for (OntologyClass range : scioClass.domainRangeRelations.get(relation)) {

					ECardinalityType cardinalityType = relation.cardinalityType != ECardinalityType.UNDEFINED
							? relation.cardinalityType
							: ECardinalityType.SINGLE;

					JavaMethod m;

					if (cardinalityType == ECardinalityType.SINGLE) {
						m = new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
								.setDocumentation(relation.documentation)
								.addArgument(new JavaMethodParameter(range.javaInterfaceName,
										JavaClassNamingTools.combineRelationWithClassNameAsVariableName(
												relation.javaClassPropertyName, range.javaClassName)))
								.setMethodBody(new OntologyMethodBody(true))
								.setMethodName("set" + JavaClassNamingTools.combineRelationWithClassNameAsClassName(
										relation.javaClassPropertyName, range.javaClassName))
								.setMethodType(EMethodType.SETTER).setReturnType(rootClass.javaInterfaceName).build();
					} else {
						m = new OntologyMethodBuilder().setAccessType(EAccessType.PUBLIC)
								.setDocumentation(relation.documentation)
								.addArgument(new JavaMethodParameter("List<" + range.javaInterfaceName + ">",
										JavaClassNamingTools.combineRelationWithClassNameAsPluralVariableName(
												relation.javaClassPropertyName, range.javaClassName)))
								.setMethodBody(new OntologyMethodBody(true))
								.setMethodName("set" + JavaClassNamingTools.combineRelationWithClassNameAsPluralClassName(
										relation.javaClassPropertyName, range.javaClassName))
								.setMethodType(EMethodType.SETTER).setReturnType(rootClass.javaInterfaceName).build();
					}
					methods.add(m);
				}
			}
		}

		return methods;
	}
}
