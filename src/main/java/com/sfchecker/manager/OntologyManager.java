package com.sfchecker.manager;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asSet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sfchecker.util.NamedClasses;
import com.sfchecker.util.NamedObjectProp;
import com.sfchecker.util.OntoNFVUtil;

public class OntologyManager {

	private OWLOntologyManager manager = null;
	private OWLOntology ontology = null;
	private OWLReasoner reasoner = null;
	
	protected @Nonnull OWLDataFactory factory = OWLManager.getOWLDataFactory();
	protected @Nonnull OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
	private static final @Nonnull Logger LOG = LoggerFactory.getLogger(OntologyManager.class);
	protected @Nonnull StreamDocumentTarget target = new StreamDocumentTarget(new ByteArrayOutputStream());


	public void loadOntology() {
		OWLOntologyManager manager = this.getManager();
		if (manager == null) {
			System.out.println("Manager does not generated!");
		}
		OWLOntology onto;
		try {
			onto = manager.loadOntologyFromOntologyDocument(OntoNFVUtil.ONTOFILE);
			this.setOntology(onto);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createOWLManager() {
		this.setManager(OWLManager.createOWLOntologyManager());
	}

	public void createReasoner() {
		OWLOntology onto = this.getOntology();
		OWLReasonerConfiguration config = new SimpleConfiguration();
		OWLReasoner reasoner = reasonerFactory.createReasoner(onto, config);
		this.setReasoner(reasoner);
	}

	public void startOntologyProcessing() {
		this.createOWLManager();
		this.loadOntology();
		this.createReasoner();
	}

	public void listAllClasses() {
		OWLOntology onto = this.getOntology();
		// These are the named classes referenced by axioms in the ontology.
		onto.classesInSignature().forEach(cls ->
		// use the class for whatever purpose
		System.out.println(cls.getIRI()));
	}

	public void listAllObjectProperties() {
		OWLOntology onto = this.getOntology();
		// These are all the Object Properties in the ontology.
		onto.objectPropertiesInSignature().forEach(op ->
		// use the class for whatever purpose
		System.out.println(op));
	}

	public void listAllDatatypeProperties() {
		OWLOntology onto = this.getOntology();
		// These are all the Datatype Properties in the ontology.
		onto.dataPropertiesInSignature().forEach(data ->
		// use the class for whatever purpose
		System.out.println(data));
	}

	public void listAllAxioms() {
		OWLOntology onto = this.getOntology();
		// These are all Axioms in the ontology.
		onto.axioms().forEach(ax ->
		// use the class for whatever purpose
		System.out.println(ax));
	}

	public void listAllIndividuals() {
		OWLOntology onto = this.getOntology();
		// These are all Individuals in the ontology.
		onto.individualsInSignature().forEach(in ->
		// use the class for whatever purpose
		System.out.println(in));
	}

	public void saveOntologyMemoryBuffer() {
		OWLOntologyManager manager = this.getManager();
		OWLOntology onto = this.getOntology();
		try {
			manager.saveOntology(onto, target);
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
	}

	public void saveOntologyFile() {
		OWLOntologyManager manager = this.getManager();
		OWLOntology onto = this.getOntology();
		try {
			System.out.println("Saving Onto-NFV...");
			manager.saveOntology(onto);
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
	}

	public void saveNewOntologyFile(String fileName) {
		OWLOntologyManager manager = this.getManager();
		OWLOntology onto = this.getOntology();
		try {
			System.out.println("Saving Onto-NFV in file: "+fileName+"...");
			File newOntoNFVFile = new File(OntoNFVUtil.ONTODIR+fileName); 
			manager.saveOntology(onto,IRI.create(newOntoNFVFile.toURI()));
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
	}
	
	public OWLNamedIndividual createIndividual(String nameInd, String nameCls) {
		OWLOntologyManager manager = this.getManager();
		OWLOntology onto = this.getOntology();
		OWLNamedIndividual ind = null;
		IRI iri = IRI.create(OntoNFVUtil.NFV_IRI + "#", nameCls);

		if (onto.containsClassInSignature(iri)) {
			OWLClass cls = factory.getOWLClass(OntoNFVUtil.NFV_IRI + "#", nameCls);
			ind = factory.getOWLNamedIndividual(OntoNFVUtil.NFV_IRI + "#", nameInd);
			OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(cls, ind);
			manager.addAxiom(onto, classAssertion);
			if (nameCls.contains(NamedClasses.NFVINODE)) {
				nameCls = NamedClasses.NFVINODE;
			}
			makeDifferentFromOtherIndividuals(ind, nameCls);
			saveOntologyMemoryBuffer();
		} else {
			System.out.println("Class not found!");
		}

		return ind;
	}

	public void removeIndividual(String nameInd) {
		OWLOntologyManager manager = this.getManager();
		OWLOntology onto = this.getOntology();
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(OntoNFVUtil.NFV_IRI + "#", nameInd);
		OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(onto));
		ind.accept(remover);
		manager.applyChanges(remover.getChanges());

		this.saveOntologyMemoryBuffer();
	}

	public Set<OWLNamedIndividual> getClassIndividuals(String nameCls) {
		OWLClass cls = factory.getOWLClass(OntoNFVUtil.NFV_IRI + "#", nameCls);
		OWLReasoner reasoner = this.getReasoner();
		reasoner.precomputeInferences();
		NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(cls, false);
		Set<OWLNamedIndividual> individuals = asSet(individualsNodeSet.entities());
		return individuals;
	}

	public Set<OWLNamedIndividual> getObjectPropertiesForIndividual(String nameInd, String nameOp) {
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(OntoNFVUtil.NFV_IRI + "#", nameInd);
		OWLObjectProperty contains = factory.getOWLObjectProperty(OntoNFVUtil.NFV_IRI + "#", NamedObjectProp.CONTAINS);
		OWLReasoner reasoner = this.getReasoner();
		reasoner.precomputeInferences();
		NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getObjectPropertyValues(ind, contains);
		Set<OWLNamedIndividual> individuals = asSet(individualsNodeSet.entities());
		return individuals;
	}

	public void listClassIndividuals(String nameCls) {
		OWLClass cls = factory.getOWLClass(OntoNFVUtil.NFV_IRI + "#", nameCls);
		OWLReasoner reasoner = this.getReasoner();
		reasoner.precomputeInferences();
		NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(cls, false);
		Set<OWLNamedIndividual> individuals = asSet(individualsNodeSet.entities());
		for (OWLNamedIndividual i : individuals) {
			System.out.println(i);
		}
	}

	// Make an individual different from the other of same type
	public void makeDifferentFromOtherIndividuals(OWLNamedIndividual individual, String cls) {
		Set<OWLNamedIndividual> individuals = this.getClassIndividuals(cls);
		for (OWLNamedIndividual i : individuals) {
			if (!i.equals(individual)) {
				this.createDifferentIndividualsAxiom(individual, i);
			}
		}
	}

	public void createDifferentIndividualsAxiom(OWLNamedIndividual individual1, OWLNamedIndividual individual2) {
		OWLOntologyManager manager = this.getManager();
		OWLOntology onto = this.getOntology();

		manager.addAxiom(onto, factory.getOWLDifferentIndividualsAxiom(individual1, individual2));
	}

	public void createObjectPropertyAssertionAxiom(String domainIndName, String rangeIndName, String objectPropName) {
		OWLOntologyManager manager = this.getManager();
		OWLOntology onto = this.getOntology();

		OWLNamedIndividual domainInd = factory.getOWLNamedIndividual(OntoNFVUtil.NFV_IRI + "#", domainIndName);
		OWLNamedIndividual rangeInd = factory.getOWLNamedIndividual(OntoNFVUtil.NFV_IRI + "#", rangeIndName);
		OWLObjectProperty objectProp = factory.getOWLObjectProperty(OntoNFVUtil.NFV_IRI + "#", objectPropName);
		OWLObjectPropertyAssertionAxiom propAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProp,
				domainInd, rangeInd);

		AddAxiom addAxiomChange = new AddAxiom(onto, propAssertion);
		manager.applyChange(addAxiomChange);

		this.saveOntologyMemoryBuffer();
	}

	public void createObjectPropertyAssertionAxiom(String domainIndName, OWLNamedIndividual rangeInd,
			String objectPropName) {
		OWLOntologyManager manager = this.getManager();
		OWLOntology onto = this.getOntology();

		OWLNamedIndividual domainInd = factory.getOWLNamedIndividual(OntoNFVUtil.NFV_IRI + "#", domainIndName);
		OWLObjectProperty objectProp = factory.getOWLObjectProperty(OntoNFVUtil.NFV_IRI + "#", objectPropName);
		OWLObjectPropertyAssertionAxiom propAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProp,
				domainInd, rangeInd);

		AddAxiom addAxiomChange = new AddAxiom(onto, propAssertion);
		manager.applyChange(addAxiomChange);

		this.saveOntologyMemoryBuffer();
	}

	public void createObjectPropertyAssertionAxiom(OWLNamedIndividual domainInd, String rangeIndName,
			String objectPropName) {
		OWLOntologyManager manager = this.getManager();
		OWLOntology onto = this.getOntology();

		OWLNamedIndividual rangeInd = factory.getOWLNamedIndividual(OntoNFVUtil.NFV_IRI + "#", rangeIndName);
		OWLObjectProperty objectProp = factory.getOWLObjectProperty(OntoNFVUtil.NFV_IRI + "#", objectPropName);
		OWLObjectPropertyAssertionAxiom propAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProp,
				domainInd, rangeInd);

		AddAxiom addAxiomChange = new AddAxiom(onto, propAssertion);
		manager.applyChange(addAxiomChange);

		this.saveOntologyMemoryBuffer();
	}

	public void createObjectPropertyAssertionAxiom(OWLNamedIndividual domainInd, OWLNamedIndividual rangeInd,
			String objectPropName) {
		OWLOntologyManager manager = this.getManager();
		OWLOntology onto = this.getOntology();

		OWLObjectProperty objectProp = factory.getOWLObjectProperty(OntoNFVUtil.NFV_IRI + "#", objectPropName);
		OWLObjectPropertyAssertionAxiom propAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProp,
				domainInd, rangeInd);

		AddAxiom addAxiomChange = new AddAxiom(onto, propAssertion);
		manager.applyChange(addAxiomChange);

		this.saveOntologyMemoryBuffer();
	}

	public void createDataPropertyAssertionAxiom(String domainIndName, OWLLiteral value, String dataPropName) {
		OWLOntologyManager manager = this.getManager();
		OWLOntology onto = this.getOntology();

		OWLNamedIndividual domainInd = factory.getOWLNamedIndividual(OntoNFVUtil.NFV_IRI + "#", domainIndName);
		OWLDataProperty dataProp = factory.getOWLDataProperty(OntoNFVUtil.NFV_IRI + "#", dataPropName);
		OWLDataPropertyAssertionAxiom dataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProp,
				domainInd, value);

		manager.addAxiom(onto, dataPropertyAssertion);

		this.saveOntologyMemoryBuffer();
	}

	public OWLOntologyManager getManager() {
		return manager;
	}

	public void setManager(OWLOntologyManager manager) {
		this.manager = manager;
	}

	public OWLOntology getOntology() {
		return ontology;
	}

	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}

	
	public OWLDataFactory getFactory() {
		return factory;
	}

	public void setFactory(OWLDataFactory factory) {
		this.factory = factory;
	}

	public OWLReasoner getReasoner() {
		return reasoner;
	}

	public void setReasoner(OWLReasoner reasoner) {
		this.reasoner = reasoner;
	}

}
