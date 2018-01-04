package com.sfchecker.manager;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import com.sfchecker.classes.VNFC;
import com.sfchecker.util.IndividualUtil;
import com.sfchecker.util.NamedClasses;
import com.sfchecker.util.NamedObjectProp;
import com.sfchecker.util.OntoNFVUtil;

public class RestrictionManager {

	protected OntologyManager ontomanager;
	protected OWLDataFactory factory;
	
	public RestrictionManager (OntologyManager ontomanager) {
		this.ontomanager = ontomanager;
		this.factory = ontomanager.getFactory();
	}
	
	public void createNFVINodeRestriction(String subClsName, String superClsName, int maxNumVNFs) {
		OWLOntologyManager manager = ontomanager.getManager();
		OWLOntology onto = ontomanager.getOntology();

		IRI iri = IRI.create(OntoNFVUtil.NFV_IRI + "#", subClsName);

		if (!onto.containsClassInSignature(iri)) {
			OWLClass subCls = factory.getOWLClass(OntoNFVUtil.NFV_IRI + "#", subClsName);
			OWLClass superCls = factory.getOWLClass(OntoNFVUtil.NFV_IRI + "#", superClsName);
			OWLClass fillerCls = factory.getOWLClass(OntoNFVUtil.NFV_IRI + "#", NamedClasses.VNFC);
			// Now create the Subclass axiom
			OWLSubClassOfAxiom axiom1 = factory.getOWLSubClassOfAxiom(subCls, superCls);
			// add the axiom to the ontology.
			AddAxiom addAxiom1 = new AddAxiom(onto, axiom1);
			// We now use the manager to apply the change
			manager.applyChange(addAxiom1);
			// Now create the Restriction axiom
			OWLObjectProperty implement = factory.getOWLObjectProperty(OntoNFVUtil.NFV_IRI + "#", NamedObjectProp.IMPLEMENTS);
			OWLClassExpression implementMaxVNFCs = factory.getOWLObjectMaxCardinality(maxNumVNFs, implement, fillerCls);
			OWLSubClassOfAxiom axiom2 = factory.getOWLSubClassOfAxiom(subCls, implementMaxVNFCs);
			// add the axiom to the ontology.
			AddAxiom addAxiom2 = new AddAxiom(onto, axiom2);
			// We now use the manager to apply the change
			manager.applyChange(addAxiom2);
		}
	}

	public void createPrecendenceRestriction(String netFunctionNameIndPre, String netFunctionNameIndPos) {
		ontomanager.createObjectPropertyAssertionAxiom(netFunctionNameIndPre, netFunctionNameIndPos,
				NamedObjectProp.HASNETWORKFUNCTIONTYPEPRECEDENCE);
	}

	public void createSameNodeRestriction(VNFC vnfc1, VNFC vnfc2) {
		String vnfc1IndName = IndividualUtil.processNameForIndividual(vnfc1.getName());
		String vnfc2IndName = IndividualUtil.processNameForIndividual(vnfc2.getName());
		ontomanager.createObjectPropertyAssertionAxiom(vnfc1IndName, vnfc2IndName, NamedObjectProp.HASSAMENFVINODE);
	}

	public void createSameNFVIPoPRestriction(VNFC vnfc1, VNFC vnfc2) {
		String vnfc1IndName = IndividualUtil.processNameForIndividual(vnfc1.getName());
		String vnfc2IndName = IndividualUtil.processNameForIndividual(vnfc2.getName());
		ontomanager.createObjectPropertyAssertionAxiom(vnfc1IndName, vnfc2IndName, NamedObjectProp.HASSAMENFVIPOP);
	}

	public void createSameNFVIRestriction(VNFC vnfc1, VNFC vnfc2) {
		String vnfc1IndName = IndividualUtil.processNameForIndividual(vnfc1.getName());
		String vnfc2IndName = IndividualUtil.processNameForIndividual(vnfc2.getName());
		ontomanager.createObjectPropertyAssertionAxiom(vnfc1IndName, vnfc2IndName, NamedObjectProp.HASSAMENFVI);
	}

	public void createNotSameNodeRestriction(VNFC vnfc1, VNFC vnfc2) {
		String vnfc1IndName = IndividualUtil.processNameForIndividual(vnfc1.getName());
		String vnfc2IndName = IndividualUtil.processNameForIndividual(vnfc2.getName());
		ontomanager.createObjectPropertyAssertionAxiom(vnfc1IndName, vnfc2IndName, NamedObjectProp.HASNOTSAMENFVINODE);
	}

	public void createNotSameNFVIPoPRestriction(VNFC vnfc1, VNFC vnfc2) {
		String vnfc1IndName = IndividualUtil.processNameForIndividual(vnfc1.getName());
		String vnfc2IndName = IndividualUtil.processNameForIndividual(vnfc2.getName());
		ontomanager.createObjectPropertyAssertionAxiom(vnfc1IndName, vnfc2IndName, NamedObjectProp.HASNOTSAMENFVIPOP);
	}

	public void createNotSameNFVIRestriction(VNFC vnfc1, VNFC vnfc2) {
		String vnfc1IndName = IndividualUtil.processNameForIndividual(vnfc1.getName());
		String vnfc2IndName = IndividualUtil.processNameForIndividual(vnfc2.getName());
		ontomanager.createObjectPropertyAssertionAxiom(vnfc1IndName, vnfc2IndName, NamedObjectProp.HASNOTSAMENFVI);
	}

	public void createSameNodeRestriction(String netFunction1, String netFunction2) {
		ontomanager.createObjectPropertyAssertionAxiom(netFunction1, netFunction2, NamedObjectProp.HASSAMENFVINODE);
	}
	
}
