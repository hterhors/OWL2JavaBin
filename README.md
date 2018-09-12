# OWL2JavaBin

A Framework for converting OWL files into java Classes / binaries -- Part of the OBIE-Framework


This tool is used to convert an ontology (information model) provided in OWL into corresponding java classes. These classes are used by the Ontology Based Information Extraction (OBIE) framework. 

**OWL Side**
Every ontology needs to follow the following rules, examples taken from the SoccerPlayerOntology: 

1)  Classes are defined as:
  
        <owl:Class rdf:about="http://psink.de/dbpedia/Place"/>

2)  SubClass relations are defined as:

        <owl:Class rdf:about="http://psink.de/dbpedia/BirthPlace">
            <rdfs:subClassOf rdf:resource="http://psink.de/dbpedia/Place" />
        </owl:Class>
  
3)  1:1 cardinalities for properties of a specific class need to be defined as:

        <rdfs:subClassOf>
            <owl:Restriction>
                <owl:onProperty rdf:resource="http://psink.de/dbpedia/hasBirthYear"/>
                <owl:qualifiedCardinality rdf:datatype="http://www.w3.org/2001/XMLSchema#nonNegativeInteger">1</owl:qualifiedCardinality>
            </owl:Restriction>
        </rdfs:subClassOf>
        
4)  1:n cardinalities for properties of a specific class need to be defined as:

        <rdfs:subClassOf>
            <owl:Restriction>
                <owl:onProperty rdf:resource="http://psink.de/dbpedia/hasTeam"/>
                <owl:minQualifiedCardinality rdf:datatype="http://www.w3.org/2001/XMLSchema#nonNegativeInteger">1</owl:minQualifiedCardinality>
                <owl:onClass rdf:resource="http://psink.de/dbpedia/Team"/>
            </owl:Restriction>
        </rdfs:subClassOf>

5)  Datatype properties are specified as:

        <owl:DatatypeProperty rdf:about="http://psink.de/dbpedia/hasBirthYear">
           <rdfs:domain rdf:resource="http://psink.de/dbpedia/SoccerPlayer" />
        </owl:DatatypeProperty>

6)  Object properties are specified as:

        <owl:ObjectProperty rdf:about="http://psink.de/dbpedia/hasBirthPlace">
          <rdfs:domain rdf:resource="http://psink.de/dbpedia/SoccerPlayer" />
          <rdfs:range rdf:resource="http://psink.de/dbpedia/BirthPlace" />
        </owl:ObjectProperty>

7)  Properties are inherited by all super classes through the full hierarchy:
 
8) In general, if an ontological class does not have an properties or sub classes it should be declared as a owl:NamedIndividual:

            <owl:NamedIndividual rdf:about="http://psink.de/dbpedia/Turin"><rdf:type rdf:resource="http://psink.de/dbpedia/BirthPlace"/></owl:NamedIndividual>


**Java Side**
The generation of java classes follows the following rules: 

1)  SubClass dependencies are expressed as interfaces to allow multiple sub classes (java extends). 
That means, for each specified class in the ontology an instantiation class is generated and a directly linked interface.

2)  The class naming follows the following rule: Ontology class names are converted into CamelCase,
special character (regExp: \W) are omitted. If an ontology class name begins with a number an underscore (\_) is used as prefix. Corresponding interfaces have the prefix *I*.

Examples: 

2.1)  Defender (association football) --> DefenderAssociationFootball --> IDefenderAssociationFootball

2.2)  4th-Rule of being: cool --> \_4thRuleOfBeingCool --> I\_4thRuleOfBeingCool

3)  Every generated class is subclass of the super root class IOBIEThing that can be found in the OBIECore project. Each class implements various getter and setter (Builder Style) and a method that converts the class into an RDF-readable format (N-triples)

4)  Artificial classes are generated for Datatype properties in order to make them reference-able. Each generated datatype property has a field that stores the exact textmention and a field that stores the interpreted value of the textmention.   

5)  After conversion into JavaClasses we can instantiate ontology classes by simply using java mechanism. Creating a SoccerPlayer as defined by the SoccerPlayerOntology might look like this: Exemplary filled with data from: https://en.wikipedia.org/wiki/Greig_Young 

<code>new HerbieWilliams(randomAnnotationID, "Herbert John Williams, Jr.").setBirthYear(new BirthYear("1940"))
				.setPosition(new InsideForward()).addBirthPlace(new Swansea()).addTeam(new WalesNationalFootballTeam());</code>

**NOTE**
If the created instance should be linked to the text, one may want to use setter for character onset and offset.
<code>new BirthYear("1940").setCharacterOnset(45).setChacterOffset(49)</code>

TIP:
**The OBIE Machine Learning FrameWork provides a simple regular expression Named Entity Recognition and Linking tool that generates regular expression based on the java class name. For that it is mandatory that class names are chosen meaning full. **

##################################################
Ontology Builder Environment:


In order to use this tool, one needs to specify an OntologyBuilderEnvironment that contains various parameters. 
Here you can specify additional prefixes and properties (axioms)  that should be read while creating java classes. 

A common example would be: rdfs:comment, rdfs:description, rdfs:label etc.
All additional properties that are not part of the structure are integrated into the java documentation of the individual classes. 

Further it is possible to specify a filter that is applied before generating java classes. this could be for instance: 
Filter all classes that have a specific property. The filter is implemented as a simple interface **IClassFilter** that checks for conditions. E.g:

	return new IClassFilter() {
			@Override
			public boolean matchesCondition(OntologyClass oc) {
				return oc.documentation.get("rdfs:label").isEmpty();
			}
		};

would filter all classes that do not have a rdfs:label specified in the ontology. 

##################################################


**LIMITATIONS**

In the current version of this tool every class individual and instance that is part of the ontology is converted into java class binaries. Which makes this version not suitable for ontologies with many classes (I tested it with approx. 5-6k classes which needed 4GB permsize of my IDE, Eclipse Photon for building it)

The number of classes can be obviously reduced by redesigning the ontology. For instance one can change some properties from object type to datatype. However this has other implications that will slow down th machine learning speed. For more details about this see OBIEMachineLearningFrameWork project.


