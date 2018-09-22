package de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.AbstractOntologyBuilderEnvironment;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.AssignableSubClasses;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.AssignableSubInterfaces;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.DatatypeProperty;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.DirectInterface;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.DirectSiblings;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.ImplementationClass;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.NamedIndividual;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.OntologyModelContent;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.RelationTypeCollection;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.SuperRootClasses;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.TextMention;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.interfaces.IDataType;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.interfaces.IOBIEThing;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.owlreader.OWLReader;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.owlreader.container.OntologyClass;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.builder.AnnotationBuilder;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.builder.ClassMethodBuilder;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.builder.ConstructorBuilder;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.builder.FieldBuilder;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.builder.InterfaceMethodBuilder;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.enums.EAccessType;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.enums.EField;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.java.JavaAnnotation;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.java.JavaClass;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.java.JavaConstructor;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.java.JavaField;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.java.JavaInterface;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.java.JavaMethod;

/**
 * @author hterhors
 *
 * @date Jul 14, 2017
 */
public class OWLToJavaBinaries {

	/**
	 * TODO: Remove somehow.
	 */
	final private static String QUDT_QUANTITY = "http://data.nasa.gov/qudt/owl/qudt#Quantity";

	private final AbstractOntologyBuilderEnvironment environment;
	private final static String classPackageName = "classes";
	private final static String interfacePackageName = "interfaces";
	private static final String JAVA_CLASS_SUFFIX = ".java";
	private static final int CAN_NOT_WRITE_JAVA_CLASS_TO_FILE = -18795623;

	private final OWLReader owlDataReader;

	private final String importClassesPackage;
	private final String importInterfacesPackage;

	private final String classesSrcLocation;
	private final String interfacesSrcLocation;

	private final String classesPackageLocation;
	private final String interfacesPackageLocation;

	/**
	 * A list of fully qualified class names from the ontology that are not part of
	 * the actual ontology model but rather serves as collective class.
	 * 
	 */
	private final Set<String> collectiveClasses;

	private final FieldBuilder fieldBuilder;
	private final ClassMethodBuilder methodBuilder;
	private final InterfaceMethodBuilder methodInterfaceBuilder;
	private final ConstructorBuilder constructorBuilder;
	private final AnnotationBuilder annotationBuilder;

	public OWLToJavaBinaries(AbstractOntologyBuilderEnvironment environment) {
		this.environment = environment;

		this.owlDataReader = new OWLReader(this.environment.getOwlClassFilter(),
				this.environment.getAdditionalPropertyNames(), this.environment.getAdditionalPrefixes(),
				this.environment.getOntologyFile());

		this.classesSrcLocation = environment.getOntologySourceLocation() + classPackageName;
		this.interfacesSrcLocation = environment.getOntologySourceLocation() + interfacePackageName;

		this.classesPackageLocation = environment.getBasePackage() + classPackageName;
		this.interfacesPackageLocation = environment.getBasePackage() + interfacePackageName;

		this.importClassesPackage = this.classesPackageLocation + ".*";
		this.importInterfacesPackage = this.interfacesPackageLocation + ".*";

		this.collectiveClasses = environment.getCollectiveClasses();

		this.annotationBuilder = new AnnotationBuilder();

		this.fieldBuilder = new FieldBuilder(annotationBuilder);
		this.methodBuilder = new ClassMethodBuilder(annotationBuilder);
		this.methodInterfaceBuilder = new InterfaceMethodBuilder(this.environment.getOntologyThingClassSimpleName());
		this.constructorBuilder = new ConstructorBuilder();

		new File(this.classesSrcLocation).mkdirs();
		new File(this.interfacesSrcLocation).mkdirs();
	}

	private void createOntologyThing() {

		Set<JavaMethod> methods = new HashSet<>();
		Set<String> imports = new HashSet<>();
		Set<JavaInterface> innerInterfaces = new HashSet<>();
		Set<JavaAnnotation> annotations = new HashSet<>();

		annotations.add(annotationBuilder.buildAssignableInterfacesAnnotation(owlDataReader.classes));
		imports.add(importInterfacesPackage);
		imports.add(AssignableSubClasses.class.getTypeName());
		imports.add(AssignableSubInterfaces.class.getTypeName());
		imports.add(DatatypeProperty.class.getTypeName());
		imports.add(DirectInterface.class.getTypeName());
		imports.add(DirectSiblings.class.getTypeName());
		imports.add(ImplementationClass.class.getTypeName());
		imports.add(NamedIndividual.class.getTypeName());
		imports.add(OntologyModelContent.class.getTypeName());
		imports.add(RelationTypeCollection.class.getTypeName());
		imports.add(SuperRootClasses.class.getTypeName());
		imports.add(TextMention.class.getTypeName());
		imports.add(Model.class.getTypeName());
		methods.add(JavaMethod.methods.get("get" + EField.ONTOLOGY_NAME.methodName).getInterfaceForMethod());
		methods.add(JavaMethod.methods.get("get" + EField.ANNOTATION_ID_FIELD.methodName).getInterfaceForMethod());
		methods.add(JavaMethod.methods.get("getRDFModel").getInterfaceForMethod());
		methods.add(JavaMethod.methods.get("isEmpty").getInterfaceForMethod());
		methods.add(JavaMethod.methods.get("get" + EField.TEXT_MENTION.methodName).getInterfaceForMethod());
		methods.add(JavaMethod.methods.get("get" + EField.CHARACTER_OFFSET.methodName).getInterfaceForMethod());
		methods.add(JavaMethod.methods.get("get" + EField.CHARACTER_ONSET.methodName).getInterfaceForMethod());
		methods.add(JavaMethod.methods.get("set" + EField.CHARACTER_OFFSET.methodName).getInterfaceForMethod());
		methods.add(JavaMethod.methods.get("set" + EField.CHARACTER_ONSET.methodName).getInterfaceForMethod());
		methods.add(JavaMethod.methods.get("getResourceName").getInterfaceForMethod());

		final String additionalContent = " public static String " + EField.RDF_MODEL_NAMESPACE.variableName + " = \""
				+ this.environment.getDataNameSpace() + "\";\n\n";

		JavaInterface ontologyArtificalSuperClass = new JavaInterface(null, annotations, additionalContent, false,
				EAccessType.PUBLIC, new HashSet<>(Arrays.asList(IOBIEThing.class.getTypeName())),
				this.environment.getOntologyThingClassSimpleName(), methods, imports, interfacesPackageLocation,
				innerInterfaces);

		try {
			writeAsJavaInterfaceToFile(ontologyArtificalSuperClass);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(CAN_NOT_WRITE_JAVA_CLASS_TO_FILE);
		}

	}

	private JavaClass buildClass(final OntologyClass dataClass) {

		if (dataClass.isArtificialUnionOfRangeClass) {
			System.err.println("Do not build artifical unionOfRange class: " + dataClass.javaClassName);
			return null;
		} else {
			System.out.println("Build class: " + dataClass.javaClassName);
		}

		final String className = dataClass.javaClassName;

		Set<String> implementations = new LinkedHashSet<>();
		Set<JavaMethod> methods = new HashSet<>();
		Set<String> imports = new HashSet<>();
		Set<JavaField> fields = new HashSet<>();
		Set<JavaConstructor> constructors = new HashSet<>();
		Set<JavaAnnotation> annotations = new HashSet<>();

		if (dataClass.isNamedIndividual)
			annotations.add(annotationBuilder.buildNamedIndividualAnnotation());

		boolean isDatatypeClass = dataClass.isDataType || isSubClassOfRootClass(QUDT_QUANTITY, dataClass);

		if (isDatatypeClass) {
			annotations.add(annotationBuilder.buildDataTypePropertyValueAnnotation());
		}
		annotations.add(annotationBuilder.buildAssignableSubClassesAnnotation(
				traverseSubclassesTopDown(dataClass, new HashSet<OntologyClass>())));

		Set<OntologyClass> superRootClasses = new HashSet<>();

		collectSuperRootClasses(dataClass, superRootClasses);

		if (superRootClasses.isEmpty())
			superRootClasses.add(dataClass);

		annotations.add(annotationBuilder.buildRootSuperClassAnnotation(superRootClasses));

		Set<OntologyClass> siblings = new HashSet<>();
		if (!collectiveClasses.contains(dataClass.fullyQualifiedOntolgyName)) {
			for (OntologyClass superClass : dataClass.superclasses) {
				if (!collectiveClasses.contains(superClass.fullyQualifiedOntolgyName)) {
					siblings.addAll(superClass.subclasses);
				}
			}
		}
		annotations.add(annotationBuilder.buildDirectSiblingClassesAnnotation(siblings));

		annotations.add(annotationBuilder.buildDirectInterfaceAnnotation(dataClass));

		imports.add(importInterfacesPackage);
		imports.add(AssignableSubClasses.class.getTypeName());
		imports.add(AssignableSubInterfaces.class.getTypeName());
		imports.add(DatatypeProperty.class.getTypeName());
		imports.add(DirectInterface.class.getTypeName());
		imports.add(DirectSiblings.class.getTypeName());
		imports.add(ImplementationClass.class.getTypeName());
		imports.add(NamedIndividual.class.getTypeName());
		imports.add(OntologyModelContent.class.getTypeName());
		imports.add(RelationTypeCollection.class.getTypeName());
		imports.add(SuperRootClasses.class.getTypeName());
		imports.add(TextMention.class.getTypeName());
		imports.add(IOBIEThing.class.getTypeName());
		imports.add(Model.class.getTypeName());
		imports.add(Resource.class.getTypeName());
		imports.add(ModelFactory.class.getTypeName());
		imports.add(HashMap.class.getTypeName());
		imports.add(Map.class.getTypeName());
		imports.add(List.class.getTypeName());
		imports.add(ArrayList.class.getTypeName());
		imports.add(Serializable.class.getTypeName());
		imports.add(InstantiationException.class.getTypeName());
		imports.add(IllegalAccessException.class.getTypeName());
		imports.add(IllegalArgumentException.class.getTypeName());
		imports.add(InvocationTargetException.class.getTypeName());
		imports.add(NoSuchMethodException.class.getTypeName());
		imports.add(SecurityException.class.getTypeName());
		imports.add(IDataType.class.getTypeName());
		implementations.add(dataClass.javaInterfaceName);

		methods.addAll(methodBuilder.generateGetters(dataClass));
		methods.addAll(methodBuilder.generateSetters(dataClass));
		methods.addAll(methodBuilder.generateAdders(dataClass));

		fields.addAll(fieldBuilder.generateRDFModelFields(dataClass));

		if (isDatatypeClass)
			fields.add(fieldBuilder.generateDatatypePropteryValueField());

		fields.add(fieldBuilder.generateAnnotationIDField());
		fields.add(fieldBuilder.generateJavaSerializable(String.valueOf(this.environment.getOntologyVersion())));
		fields.add(fieldBuilder.generateMentionField(dataClass));
		fields.add(fieldBuilder.generateResourceFactoryField(dataClass.javaClassName));
		fields.add(fieldBuilder.generateOntologyNameField(dataClass.namespace + dataClass.ontologyClassName));
		fields.addAll(fieldBuilder.generateAnnotationOnAndOffset());

		constructors.add(constructorBuilder.generateFinalFieldsConstructor(className, fields));
		constructors.add(constructorBuilder.emptyConstructor(className, fields));
		constructors.add(constructorBuilder.cloneConstructor(className, fields));

		if (isDatatypeClass)
			constructors.add(constructorBuilder.semanticValueConstructor(className, fields));

		if (isDatatypeClass)
			methods.add(methodBuilder.generateValueGetter());

		methods.add(methodBuilder.generateOntologyNameGetter());
		methods.add(methodBuilder.generateAnnotationIDGetter());
		methods.add(methodBuilder.generateMentionGetter());
		methods.add(methodBuilder.generateGetRDFModelMethod(this.environment.getOntologyThingClassSimpleName(), fields,
				className, dataClass.isDataType));
		methods.add(methodBuilder.generateIsEmptyMethod(fields, dataClass.isDataType));
		methods.add(methodBuilder.generateGetResourceNameMethod(this.environment.getOntologyThingClassSimpleName(),
				dataClass.isNamedIndividual, className));
		methods.addAll(methodBuilder.generateOffAndOnSetGetter());
		methods.addAll(methodBuilder.generateOffAndOnSetSetter());
		methods.add(methodBuilder.generateHashCodeMethod(fields));
		methods.add(methodBuilder.generateEqualsMethod(fields, className));

		JavaClass c = new JavaClass.Builder().setAccessType(EAccessType.PUBLIC).setAnnotations(annotations)
				.setClassName(className).setConstructors(constructors).setDocumentation(dataClass.documentation)
				.setFields(fields).setImports(imports).setInterfaces(implementations).setMethods(methods)
				.setPackageName(classesPackageLocation).setStatic(false).build();

		return c;
	}

	/**
	 * Checks if the potentialSubClass is sub*class of the given rootClass. This
	 * includes direct and indirect subclass relations.
	 * 
	 * @param rootClass
	 * @param potentialSubClass
	 * @return true if so else false.
	 */
	private boolean isSubClassOfRootClass(final String rootClass, final OntologyClass potentialSubClass) {

		if (potentialSubClass.superclasses.isEmpty())
			return false;

		boolean b = potentialSubClass.superclasses.stream().map(s -> s.fullyQualifiedOntolgyName)
				.collect(Collectors.toSet()).contains(rootClass);

		for (OntologyClass superClass : potentialSubClass.superclasses) {

			b |= isSubClassOfRootClass(rootClass, superClass);

		}
		return b;
	}

	private JavaInterface buildInterface(final OntologyClass ontologyClass) {

		System.out.println("Build interface: " + ontologyClass.javaClassName);

		final EAccessType accessType = EAccessType.PUBLIC;
		final String normedClassName = ontologyClass.javaInterfaceName;
		final boolean isStatic = false;

		Set<String> extendsClassNames = new HashSet<>();
		Set<JavaMethod> methods = new HashSet<>();
		Set<String> imports = new HashSet<>();
		Set<JavaInterface> innerInterfaces = new HashSet<>();
		Set<JavaAnnotation> annotations = new HashSet<>();

		imports.add(IOBIEThing.class.getTypeName());
		imports.add(IDataType.class.getTypeName());
		imports.add(List.class.getTypeName());
		imports.add(Collection.class.getTypeName());
//		imports.add(Collections2.class.getTypeName());
		imports.add(Collectors.class.getTypeName());
		imports.add(IntStream.class.getTypeName());
		imports.add(AssignableSubClasses.class.getTypeName());
		imports.add(AssignableSubInterfaces.class.getTypeName());
		imports.add(DatatypeProperty.class.getTypeName());
		imports.add(DirectInterface.class.getTypeName());
		imports.add(DirectSiblings.class.getTypeName());
		imports.add(ImplementationClass.class.getTypeName());
		imports.add(NamedIndividual.class.getTypeName());
		imports.add(OntologyModelContent.class.getTypeName());
		imports.add(RelationTypeCollection.class.getTypeName());
		imports.add(SuperRootClasses.class.getTypeName());
		imports.add(TextMention.class.getTypeName());
		imports.add(importClassesPackage);

		boolean isDatatypeClass = ontologyClass.isDataType || isSubClassOfRootClass(QUDT_QUANTITY, ontologyClass);

		if (isDatatypeClass) {
			annotations.add(annotationBuilder.buildDataTypePropertyValueAnnotation());
			extendsClassNames.add(IDataType.class.getSimpleName());
		}

		if (!ontologyClass.isArtificialUnionOfRangeClass) {
			annotations.add(annotationBuilder.buildClassImplAnnotation(ontologyClass));
		}
		extendsClassNames.addAll(getExtendedInterfaces(ontologyClass));

		Set<OntologyClass> subClasses = traverseSubclassesTopDown(ontologyClass, new HashSet<OntologyClass>());

		annotations.add(annotationBuilder.buildAssignableInterfacesAnnotation(subClasses));

		// if
		// (OWLToJavaClassConverter.dataProvider.dataTypeClasses.contains(scioClass))
		// methods.add(OntologyMethod.methods.get("get" +
		// EField.DATATYPE_PROPERTY_VALUE_ANNOTATION_NAME.methodName)
		// .getInterfaceForMethod());

		methods.addAll(methodInterfaceBuilder.generateGetterInterface(ontologyClass));
		methods.addAll(methodInterfaceBuilder.generateSetterInterface(ontologyClass, ontologyClass));
		methods.addAll(methodInterfaceBuilder.generateAdderInterface(ontologyClass, ontologyClass));

		// if (isDataTypeProperty) {
		// methods.add(MethodInterfaceBuilder.generateDataTypeCompareToClassInterfaceMethod(methods,
		// normedClassName,
		// extendsClasses, isDataTypeProperty));
		// } else if
		// (OWLToJavaClassConverter.dataProvider.namedIndividuals.contains(scioClass))
		// {
		// methods.add(MethodInterfaceBuilder.generateLeaveClassCompareToClassInterfaceMethod(methods,
		// normedClassName,
		// extendsClasses, isDataTypeProperty));
		// } else {
		// methods.add(MethodInterfaceBuilder.generateDefaultCompareToClassInterfaceMethod(methods,
		// normedClassName,
		// extendsClasses, isDataTypeProperty));
		// }
		//
		// methods.add(MethodInterfaceBuilder.generateDefaultCompareToInterfaceMethod(normedClassName));
		// methods.add(MethodInterfaceBuilder.generateDefaultCompareToWithScoreInterfaceMethod(normedClassName));

		JavaInterface i = new JavaInterface(ontologyClass.documentation, annotations, isStatic, accessType,
				extendsClassNames, normedClassName, methods, imports, interfacesPackageLocation, innerInterfaces);

		return i;
	}

	/**
	 * Returns a list of interface names that this class extends. If no direct
	 * Interface is extends the list contains only the super root class of this
	 * ontology.
	 * 
	 * @param ontologyClass
	 * @return a list of names
	 */
	private List<String> getExtendedInterfaces(OntologyClass ontologyClass) {
		final List<String> interfaces = ontologyClass.superclasses.stream().map(c -> c.javaInterfaceName)
				.collect(Collectors.toList());

		if (interfaces.size() == 0)
			interfaces.add(this.environment.getOntologyThingClassSimpleName());

		return interfaces;
	}

	/**
	 * Traverses through the ontology subclasses and adds all children to the list
	 * of possible values. It then calls itself recursively with each children.
	 * 
	 * @param parentScioClass     the parent class.
	 * @param scioJavaClass       the class
	 * @param possibleCandidates2
	 * @throws ClassNotFoundException
	 */
	private Set<OntologyClass> traverseSubclassesTopDown(OntologyClass parentClass,
			Set<OntologyClass> possibleSubclasses) {
		for (OntologyClass child : parentClass.subclasses) {
			possibleSubclasses.add(child);
			traverseSubclassesTopDown(child, possibleSubclasses);
		}
		return possibleSubclasses;
	}

	/**
	 * Finds the super root class of the given input class. Classes that are
	 * mentioned in {@link #collectiveClasses} are excluded as they are not part of
	 * the actual model.
	 * 
	 * In case there are more than one super class
	 * 
	 * @param ontologyClass
	 * @return the most general super class.
	 */
	private void collectSuperRootClasses(OntologyClass ontologyClass, Set<OntologyClass> superRootClasses) {

		if (ontologyClass.superclasses.isEmpty()) {
			superRootClasses.add(ontologyClass);
		} else {
			for (OntologyClass superClass : ontologyClass.superclasses) {

				if (collectiveClasses.contains(superClass.fullyQualifiedOntolgyName))
					continue;

				collectSuperRootClasses(superClass, superRootClasses);
			}
		}
	}

	public void buildAndWriteClasses() {

		final Set<JavaClass> classes = new HashSet<>();
		for (OntologyClass dataClass : owlDataReader.classes) {

			final JavaClass c = buildClass(dataClass);

			if (c != null)

				classes.add(c);
		}

		for (JavaClass javaClass : classes) {
			try {
				writeAsJavaClassToFile(javaClass);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(CAN_NOT_WRITE_JAVA_CLASS_TO_FILE);
			}

		}

	}

	private void writeAsJavaClassToFile(JavaClass ontologyClass) throws IOException {
		PrintStream ps = new PrintStream(classesSrcLocation + "/" + ontologyClass.className + JAVA_CLASS_SUFFIX);
		ps.println(ontologyClass.toJavaString());
		ps.close();
	}

	private void writeAsJavaInterfaceToFile(JavaInterface ontologyInterface) throws IOException {
		PrintStream ps = new PrintStream(
				interfacesSrcLocation + "/" + ontologyInterface.getInterfaceName() + JAVA_CLASS_SUFFIX);
		ps.println(ontologyInterface.toJavaString());
		ps.close();
	}

	public void buildAndWriteInterfaces() {
		final Set<JavaInterface> interfaces = new HashSet<>();

		for (OntologyClass interfaceName : owlDataReader.classes) {
			final JavaInterface i = buildInterface(interfaceName);

			if (i != null)
				interfaces.add(i);
		}

		createOntologyThing();

		for (JavaInterface javaInterface : interfaces) {
			try {
				writeAsJavaInterfaceToFile(javaInterface);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(CAN_NOT_WRITE_JAVA_CLASS_TO_FILE);
			}

		}

	}

}
