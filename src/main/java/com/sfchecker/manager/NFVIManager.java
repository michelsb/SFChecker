package com.sfchecker.manager;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import com.sfchecker.classes.BidirectionalInterface;
import com.sfchecker.classes.BidirectionalLink;
import com.sfchecker.classes.CPU;
import com.sfchecker.classes.InterPoPSwitch;
import com.sfchecker.classes.Interface;
import com.sfchecker.classes.Link;
import com.sfchecker.classes.Memory;
import com.sfchecker.classes.NFVI;
import com.sfchecker.classes.NFVIPoP;
import com.sfchecker.classes.Node;
import com.sfchecker.classes.Path;
import com.sfchecker.classes.Storage;
import com.sfchecker.classes.SwitchingMatrix;
import com.sfchecker.util.IndividualUtil;
import com.sfchecker.util.NamedClasses;
import com.sfchecker.util.NamedDataProp;
import com.sfchecker.util.NamedObjectProp;
import com.sfchecker.util.OntoNFVUtil;

public class NFVIManager {
	
	protected OntologyManager ontomanager;
	protected RestrictionManager restmanager;
	protected OWLDataFactory factory;
	
	public NFVIManager (OntologyManager ontomanager,RestrictionManager restmanager) {
		this.ontomanager = ontomanager;
		this.factory = ontomanager.getFactory();
		this.restmanager = restmanager;
	}
	
	public void createNFVIIndividual(NFVI nfvi) {
		String nfviIndName = IndividualUtil.processNameForIndividual(nfvi.getName());
		ontomanager.createIndividual(nfviIndName, NamedClasses.NFVI);
		ontomanager.createDataPropertyAssertionAxiom(nfviIndName, factory.getOWLLiteral(OntoNFVUtil.NFV_IRI + "#" + nfviIndName),
				NamedDataProp.ID);
		ontomanager.createDataPropertyAssertionAxiom(nfviIndName, factory.getOWLLiteral(nfvi.getName()),
				NamedDataProp.HASNAME);
	}

	public void createNFVIPoPIndividual(NFVIPoP nfvipop) {
		String nfvipopIndName = IndividualUtil.processNameForIndividual(nfvipop.getName());
		ontomanager.createIndividual(nfvipopIndName, NamedClasses.NFVIPOP);
		ontomanager.createDataPropertyAssertionAxiom(nfvipopIndName, factory.getOWLLiteral(OntoNFVUtil.NFV_IRI + "#" + nfvipopIndName),
				NamedDataProp.ID);
		ontomanager.createDataPropertyAssertionAxiom(nfvipopIndName, factory.getOWLLiteral(nfvipop.getName()),
				NamedDataProp.HASNAME);

		String nfviIndName = IndividualUtil.processNameForIndividual(nfvipop.getNfvi().getName());
		ontomanager.createObjectPropertyAssertionAxiom(nfviIndName, nfvipopIndName, NamedObjectProp.CONTAINS);
	}

	public void createNFVINodeIndividual(Node nfvinode) {
		String nfvinodeIndName = IndividualUtil.processNameForIndividual(nfvinode.getName());
		String nameCls = "";

		if (nfvinode.getMaxNumVNFs() == 0) {
			nameCls = NamedClasses.NFVINODE10;
		} else {
			nameCls = "NFVINode" + nfvinode.getMaxNumVNFs();
			restmanager.createNFVINodeRestriction(nameCls, NamedClasses.NFVINODE, nfvinode.getMaxNumVNFs());
		}

		ontomanager.createIndividual(nfvinodeIndName, nameCls);
		ontomanager.createDataPropertyAssertionAxiom(nfvinodeIndName, factory.getOWLLiteral(OntoNFVUtil.NFV_IRI + "#" + nfvinodeIndName),
				NamedDataProp.ID);
		ontomanager.createDataPropertyAssertionAxiom(nfvinodeIndName, factory.getOWLLiteral(nfvinode.getName()),
				NamedDataProp.HASNAME);

		// Creating CPU
		String cpuIndName = this.createCPUIndividual(nfvinode.getCpu());
		ontomanager.createObjectPropertyAssertionAxiom(nfvinodeIndName, cpuIndName, NamedObjectProp.HASCOMPONENT);
		// Creating Memory
		String memIndName = this.createMemoryIndividual(nfvinode.getMemory());
		ontomanager.createObjectPropertyAssertionAxiom(nfvinodeIndName, memIndName, NamedObjectProp.HASCOMPONENT);
		// Creating Storage
		String stoIndName = this.createStorageIndividual(nfvinode.getStorage());
		ontomanager.createObjectPropertyAssertionAxiom(nfvinodeIndName, stoIndName, NamedObjectProp.HASCOMPONENT);

		// Creating Interface
		this.createInterfaces(nfvinodeIndName, nfvinode.getNumInterfaces());

		// Contains
		String nfvipopIndName = IndividualUtil.processNameForIndividual(nfvinode.getNfvipop().getName());
		ontomanager.createObjectPropertyAssertionAxiom(nfvipopIndName, nfvinodeIndName, NamedObjectProp.CONTAINS);
	}

	public void createNetworkNodeIndividual(Node netnode) {
		String netnodeIndName = IndividualUtil.processNameForIndividual(netnode.getName());
		ontomanager.createIndividual(netnodeIndName, NamedClasses.NODE);
		ontomanager.createDataPropertyAssertionAxiom(netnodeIndName, factory.getOWLLiteral(OntoNFVUtil.NFV_IRI + "#" + netnodeIndName),
				NamedDataProp.ID);
		ontomanager.createDataPropertyAssertionAxiom(netnodeIndName, factory.getOWLLiteral(netnode.getName()),
				NamedDataProp.HASNAME);

		// Creating CPU
		String cpuIndName = this.createCPUIndividual(netnode.getCpu());
		ontomanager.createObjectPropertyAssertionAxiom(netnodeIndName, cpuIndName, NamedObjectProp.HASCOMPONENT);
		// Creating Memory
		String memIndName = this.createMemoryIndividual(netnode.getMemory());
		ontomanager.createObjectPropertyAssertionAxiom(netnodeIndName, memIndName, NamedObjectProp.HASCOMPONENT);
		// Creating Storage
		String stoIndName = this.createStorageIndividual(netnode.getStorage());
		ontomanager.createObjectPropertyAssertionAxiom(netnodeIndName, stoIndName, NamedObjectProp.HASCOMPONENT);
		// Creating Switching Matrix
		String swtIndName = this.createSwitchingMatrixIndividual(netnode.getSwitchingMatrix());
		ontomanager.createObjectPropertyAssertionAxiom(netnodeIndName, swtIndName, NamedObjectProp.HASCOMPONENT);

		// Creating Interface
		this.createInterfaces(netnodeIndName, netnode.getNumInterfaces());

		// Contains
		String nfvipopIndName = IndividualUtil.processNameForIndividual(netnode.getNfvipop().getName());
		ontomanager.createObjectPropertyAssertionAxiom(nfvipopIndName, netnodeIndName, NamedObjectProp.CONTAINS);
	}
	
	/* SUBCOMPONENTS */

	public String createCPUIndividual(CPU cpu) {
		String cpuIndName = IndividualUtil.processNameForIndividual(cpu.getName() + "-" + UUID.randomUUID());
		ontomanager.createIndividual(cpuIndName, NamedClasses.CPU);
		ontomanager.createDataPropertyAssertionAxiom(cpuIndName, factory.getOWLLiteral(OntoNFVUtil.NFV_IRI + "#" + cpuIndName),
				NamedDataProp.ID);
		ontomanager.createDataPropertyAssertionAxiom(cpuIndName, factory.getOWLLiteral(cpu.getName()), NamedDataProp.HASNAME);
		ontomanager.createDataPropertyAssertionAxiom(cpuIndName, factory.getOWLLiteral(cpu.getNumCores()),
				NamedDataProp.HASCORES);
		ontomanager.createDataPropertyAssertionAxiom(cpuIndName, factory.getOWLLiteral(cpu.getAvailableCores()),
				NamedDataProp.HASAVAILABLECORES);
		ontomanager.createDataPropertyAssertionAxiom(cpuIndName, factory.getOWLLiteral(cpu.getSpeed()),
				NamedDataProp.HASCPUSPEED);
		return cpuIndName;
	}

	public String createMemoryIndividual(Memory mem) {
		String memIndName = IndividualUtil.processNameForIndividual(mem.getName() + "-" + UUID.randomUUID());
		ontomanager.createIndividual(memIndName, NamedClasses.MEMORY);
		ontomanager.createDataPropertyAssertionAxiom(memIndName, factory.getOWLLiteral(OntoNFVUtil.NFV_IRI + "#" + memIndName),
				NamedDataProp.ID);
		ontomanager.createDataPropertyAssertionAxiom(memIndName, factory.getOWLLiteral(mem.getName()), NamedDataProp.HASNAME);
		ontomanager.createDataPropertyAssertionAxiom(memIndName, factory.getOWLLiteral(mem.getSize()),
				NamedDataProp.HASMEMORYSIZE);
		ontomanager.createDataPropertyAssertionAxiom(memIndName, factory.getOWLLiteral(mem.getAvailableSize()),
				NamedDataProp.HASAVAILABLEMEMORYSIZE);
		return memIndName;
	}

	public String createStorageIndividual(Storage sto) {
		String stoIndName = IndividualUtil.processNameForIndividual(sto.getName() + "-" + UUID.randomUUID());
		ontomanager.createIndividual(stoIndName, NamedClasses.STORAGE);
		ontomanager.createDataPropertyAssertionAxiom(stoIndName, factory.getOWLLiteral(OntoNFVUtil.NFV_IRI + "#" + stoIndName),
				NamedDataProp.ID);
		ontomanager.createDataPropertyAssertionAxiom(stoIndName, factory.getOWLLiteral(sto.getName()), NamedDataProp.HASNAME);
		ontomanager.createDataPropertyAssertionAxiom(stoIndName, factory.getOWLLiteral(sto.getSize()),
				NamedDataProp.HASSTORAGESIZE);
		ontomanager.createDataPropertyAssertionAxiom(stoIndName, factory.getOWLLiteral(sto.getAvailableSize()),
				NamedDataProp.HASAVAILABLESTORAGESIZE);
		return stoIndName;
	}

	public String createSwitchingMatrixIndividual(SwitchingMatrix swt) {
		String swtIndName = IndividualUtil.processNameForIndividual(swt.getName() + "-" + UUID.randomUUID());
		ontomanager.createIndividual(swtIndName, NamedClasses.SWITCHINGMATRIX);
		ontomanager.createDataPropertyAssertionAxiom(swtIndName, factory.getOWLLiteral(OntoNFVUtil.NFV_IRI + "#" + swtIndName),
				NamedDataProp.ID);
		ontomanager.createDataPropertyAssertionAxiom(swtIndName, factory.getOWLLiteral(swt.getName()), NamedDataProp.HASNAME);
		return swtIndName;
	}
	
	/* NETWORK ELEMENTS */

	public String createInterfaceIndividual(Interface inte) {
		String inteIndName = IndividualUtil.processNameForIndividual(inte.getName());
		ontomanager.createIndividual(inteIndName, NamedClasses.INTERFACE);
		ontomanager.createDataPropertyAssertionAxiom(inteIndName, factory.getOWLLiteral(OntoNFVUtil.NFV_IRI + "#" + inteIndName),
				NamedDataProp.ID);
		ontomanager.createDataPropertyAssertionAxiom(inteIndName, factory.getOWLLiteral(inte.getName()),
				NamedDataProp.HASNAME);
		return inteIndName;
	}

	public String createBiInterfaceIndividual(BidirectionalInterface biInte) {
		String biInterfaceIndName = IndividualUtil.processNameForIndividual(biInte.getName());
		ontomanager.createIndividual(biInterfaceIndName, NamedClasses.BIDIRECTIONALINTERFACE);
		ontomanager.createDataPropertyAssertionAxiom(biInterfaceIndName,
				factory.getOWLLiteral(OntoNFVUtil.NFV_IRI + "#" + biInterfaceIndName), NamedDataProp.ID);
		ontomanager.createDataPropertyAssertionAxiom(biInterfaceIndName, factory.getOWLLiteral(biInte.getName()),
				NamedDataProp.HASNAME);
		return biInterfaceIndName;
	}

	public String createLinkIndividual(Link link) {
		String linkIndName = IndividualUtil.processNameForIndividual(link.getName());
		ontomanager.createIndividual(linkIndName, NamedClasses.LINK);
		ontomanager.createDataPropertyAssertionAxiom(linkIndName, factory.getOWLLiteral(OntoNFVUtil.NFV_IRI + "#" + linkIndName),
				NamedDataProp.ID);
		ontomanager.createDataPropertyAssertionAxiom(linkIndName, factory.getOWLLiteral(link.getName()),
				NamedDataProp.HASNAME);
		ontomanager.createDataPropertyAssertionAxiom(linkIndName, factory.getOWLLiteral(link.getCapacity()),
				NamedDataProp.HASCAPACITY);
		ontomanager.createDataPropertyAssertionAxiom(linkIndName, factory.getOWLLiteral(link.getAvailableCapacity()),
				NamedDataProp.HASAVAILABLECAPACITY);
		return linkIndName;
	}

	public String createInterPoPSwitchIndividual(InterPoPSwitch link) {
		String linkIndName = IndividualUtil.processNameForIndividual(link.getName());
		ontomanager.createIndividual(linkIndName, NamedClasses.INTERPOPSWITCH);
		ontomanager.createDataPropertyAssertionAxiom(linkIndName, factory.getOWLLiteral(OntoNFVUtil.NFV_IRI + "#" + linkIndName),
				NamedDataProp.ID);
		ontomanager.createDataPropertyAssertionAxiom(linkIndName, factory.getOWLLiteral(link.getName()),
				NamedDataProp.HASNAME);
		ontomanager.createDataPropertyAssertionAxiom(linkIndName, factory.getOWLLiteral(link.getCapacity()),
				NamedDataProp.HASCAPACITY);
		ontomanager.createDataPropertyAssertionAxiom(linkIndName, factory.getOWLLiteral(link.getAvailableCapacity()),
				NamedDataProp.HASAVAILABLECAPACITY);
		ontomanager.createDataPropertyAssertionAxiom(linkIndName, factory.getOWLLiteral(link.getVxlanId() + ""),
				NamedDataProp.HASVXLANID);
		return linkIndName;
	}

	public String createBiLinkIndividual(BidirectionalLink biLink) {
		String biLinkIndName = IndividualUtil.processNameForIndividual(biLink.getName());
		ontomanager.createIndividual(biLinkIndName, NamedClasses.BIDIRECTIONALLINK);
		ontomanager.createDataPropertyAssertionAxiom(biLinkIndName, factory.getOWLLiteral(OntoNFVUtil.NFV_IRI + "#" + biLinkIndName),
				NamedDataProp.ID);
		ontomanager.createDataPropertyAssertionAxiom(biLinkIndName, factory.getOWLLiteral(biLink.getName()),
				NamedDataProp.HASNAME);
		return biLinkIndName;
	}

	public String createPathIndividual(Path path) {
		String pathIndName = IndividualUtil.processNameForIndividual(path.getName());
		ontomanager.createIndividual(pathIndName, NamedClasses.PATH);
		ontomanager.createDataPropertyAssertionAxiom(pathIndName, factory.getOWLLiteral(OntoNFVUtil.NFV_IRI + "#" + pathIndName),
				NamedDataProp.ID);
		ontomanager.createDataPropertyAssertionAxiom(pathIndName, factory.getOWLLiteral(path.getName()),
				NamedDataProp.HASNAME);
		return pathIndName;
	}

	public void createInterfaces(String nodeIdName, int numInterfaces) {
		for (int i = 1; i <= numInterfaces; i++) {
			BidirectionalInterface biInterface = new BidirectionalInterface();
			biInterface.setName(nodeIdName + " Interface" + i);

			Interface inboundInterface = new Interface();
			inboundInterface.setName(biInterface.getName() + " In");
			Interface outboundInterface = new Interface();
			outboundInterface.setName(biInterface.getName() + " Out");

			String biInterfaceIndName = this.createBiInterfaceIndividual(biInterface);
			String inInteIndName = this.createInterfaceIndividual(inboundInterface);
			String outInteIndName = this.createInterfaceIndividual(outboundInterface);

			ontomanager.createObjectPropertyAssertionAxiom(nodeIdName, inInteIndName, NamedObjectProp.HASINBOUNDINTERFACE);
			ontomanager.createObjectPropertyAssertionAxiom(nodeIdName, outInteIndName, NamedObjectProp.HASOUTBOUNDINTERFACE);

			ontomanager.createObjectPropertyAssertionAxiom(biInterfaceIndName, inInteIndName, NamedObjectProp.CONTAINS);
			ontomanager.createObjectPropertyAssertionAxiom(biInterfaceIndName, outInteIndName, NamedObjectProp.CONTAINS);
		}
	}
	
	/* CONNECTIONS */

	public void createPhysicalConnectionBetweenHosts(Node node1, int inteIndexNode1, Node node2, int inteIndexNode2,
			float speed) {

		String node1IndName = IndividualUtil.processNameForIndividual(node1.getName());
		String node2IndName = IndividualUtil.processNameForIndividual(node2.getName());

		String inInteNode1IndName = IndividualUtil.processNameForIndividual(node1.getName() + " Interface" + inteIndexNode1 + " In");
		String outInteNode1IndName = IndividualUtil.processNameForIndividual(node1.getName() + " Interface" + inteIndexNode1 + " Out");
		String inInteNode2IndName = IndividualUtil.processNameForIndividual(node2.getName() + " Interface" + inteIndexNode2 + " In");
		String outInteNode2IndName = IndividualUtil.processNameForIndividual(node2.getName() + " Interface" + inteIndexNode2 + " Out");

		BidirectionalLink biLink = new BidirectionalLink();
		biLink.setName("PhyLink " + node1.getName() + "-" + node2.getName());

		Link linkForward = new Link();
		linkForward.setName(biLink.getName() + " Forward");
		linkForward.setCapacity(speed);
		linkForward.setAvailableCapacity(speed);
		Link linkBackward = new Link();
		linkBackward.setName(biLink.getName() + " Backward");
		linkBackward.setCapacity(speed);
		linkBackward.setAvailableCapacity(speed);

		Path pathForward = new Path();
		pathForward.setName("Path " + node1.getName() + "-" + node2.getName());
		Path pathBackward = new Path();
		pathBackward.setName("Path " + node2.getName() + "-" + node1.getName());

		String biLinkIndName = this.createBiLinkIndividual(biLink);
		String linkForwardIndName = this.createLinkIndividual(linkForward);
		String linkBackwardIndName = this.createLinkIndividual(linkBackward);
		String pathForwardIndName = this.createPathIndividual(pathForward);
		String pathBackwardIndName = this.createPathIndividual(pathBackward);

		ontomanager.createObjectPropertyAssertionAxiom(biLinkIndName, linkForwardIndName, NamedObjectProp.CONTAINS);
		ontomanager.createObjectPropertyAssertionAxiom(biLinkIndName, linkBackwardIndName, NamedObjectProp.CONTAINS);

		ontomanager.createObjectPropertyAssertionAxiom(linkForwardIndName, outInteNode1IndName, NamedObjectProp.HASSOURCE);
		ontomanager.createObjectPropertyAssertionAxiom(linkForwardIndName, inInteNode2IndName, NamedObjectProp.HASSINK);

		ontomanager.createObjectPropertyAssertionAxiom(linkBackwardIndName, outInteNode2IndName, NamedObjectProp.HASSOURCE);
		ontomanager.createObjectPropertyAssertionAxiom(linkBackwardIndName, inInteNode1IndName, NamedObjectProp.HASSINK);

		ontomanager.createObjectPropertyAssertionAxiom(node1IndName, node2IndName, NamedObjectProp.CONNECTEDTO);

		ontomanager.createObjectPropertyAssertionAxiom(pathForwardIndName, outInteNode1IndName, NamedObjectProp.CONTAINS);
		ontomanager.createObjectPropertyAssertionAxiom(pathForwardIndName, linkForwardIndName, NamedObjectProp.CONTAINS);
		ontomanager.createObjectPropertyAssertionAxiom(pathForwardIndName, inInteNode2IndName, NamedObjectProp.CONTAINS);

		ontomanager.createObjectPropertyAssertionAxiom(pathBackwardIndName, outInteNode2IndName, NamedObjectProp.CONTAINS);
		ontomanager.createObjectPropertyAssertionAxiom(pathBackwardIndName, linkBackwardIndName, NamedObjectProp.CONTAINS);
		ontomanager.createObjectPropertyAssertionAxiom(pathBackwardIndName, inInteNode1IndName, NamedObjectProp.CONTAINS);

	}

	public void createPhysicalConnectionBetweenPoPs(Node node1, int inteIndexNode1, Node node2, int inteIndexNode2,
			float speed, int vxlanId) {

		String node1IndName = IndividualUtil.processNameForIndividual(node1.getName());
		String node2IndName = IndividualUtil.processNameForIndividual(node2.getName());

		String inInteNode1IndName = IndividualUtil.processNameForIndividual(node1.getName() + " Interface" + inteIndexNode1 + " In");
		String outInteNode1IndName = IndividualUtil.processNameForIndividual(node1.getName() + " Interface" + inteIndexNode1 + " Out");
		String inInteNode2IndName = IndividualUtil.processNameForIndividual(node2.getName() + " Interface" + inteIndexNode2 + " In");
		String outInteNode2IndName = IndividualUtil.processNameForIndividual(node2.getName() + " Interface" + inteIndexNode2 + " Out");

		BidirectionalLink biLink = new BidirectionalLink();
		biLink.setName("InterPoPLink " + node1.getName() + "-" + node2.getName());

		InterPoPSwitch linkForward = new InterPoPSwitch();
		linkForward.setName(biLink.getName() + " Forward");
		linkForward.setCapacity(speed);
		linkForward.setAvailableCapacity(speed);
		linkForward.setVxlanId(vxlanId);
		InterPoPSwitch linkBackward = new InterPoPSwitch();
		linkBackward.setName(biLink.getName() + " Backward");
		linkBackward.setCapacity(speed);
		linkBackward.setAvailableCapacity(speed);
		linkBackward.setVxlanId(vxlanId);

		Path pathForward = new Path();
		pathForward.setName("Path " + node1.getName() + "-" + node2.getName());
		Path pathBackward = new Path();
		pathBackward.setName("Path " + node2.getName() + "-" + node1.getName());

		String biLinkIndName = this.createBiLinkIndividual(biLink);
		String linkForwardIndName = this.createInterPoPSwitchIndividual(linkForward);
		String linkBackwardIndName = this.createInterPoPSwitchIndividual(linkBackward);
		String pathForwardIndName = this.createPathIndividual(pathForward);
		String pathBackwardIndName = this.createPathIndividual(pathBackward);

		ontomanager.createObjectPropertyAssertionAxiom(biLinkIndName, linkForwardIndName, NamedObjectProp.CONTAINS);
		ontomanager.createObjectPropertyAssertionAxiom(biLinkIndName, linkBackwardIndName, NamedObjectProp.CONTAINS);

		ontomanager.createObjectPropertyAssertionAxiom(linkForwardIndName, outInteNode1IndName, NamedObjectProp.HASSOURCE);
		ontomanager.createObjectPropertyAssertionAxiom(linkForwardIndName, inInteNode2IndName, NamedObjectProp.HASSINK);

		ontomanager.createObjectPropertyAssertionAxiom(linkBackwardIndName, outInteNode2IndName, NamedObjectProp.HASSOURCE);
		ontomanager.createObjectPropertyAssertionAxiom(linkBackwardIndName, inInteNode1IndName, NamedObjectProp.HASSINK);

		ontomanager.createObjectPropertyAssertionAxiom(node1IndName, node2IndName, NamedObjectProp.CONNECTEDTO);

		ontomanager.createObjectPropertyAssertionAxiom(pathForwardIndName, outInteNode1IndName, NamedObjectProp.CONTAINS);
		ontomanager.createObjectPropertyAssertionAxiom(pathForwardIndName, linkForwardIndName, NamedObjectProp.CONTAINS);
		ontomanager.createObjectPropertyAssertionAxiom(pathForwardIndName, inInteNode2IndName, NamedObjectProp.CONTAINS);

		ontomanager.createObjectPropertyAssertionAxiom(pathBackwardIndName, outInteNode2IndName, NamedObjectProp.CONTAINS);
		ontomanager.createObjectPropertyAssertionAxiom(pathBackwardIndName, linkBackwardIndName, NamedObjectProp.CONTAINS);
		ontomanager.createObjectPropertyAssertionAxiom(pathBackwardIndName, inInteNode1IndName, NamedObjectProp.CONTAINS);

	}

	public void createPathBetweenHosts(Node nodeSource, ArrayList<Node> intermediaryNodes, Node nodeSink) {
		String nodeSourceIndName = IndividualUtil.processNameForIndividual(nodeSource.getName());
		String nodeSinkIndName = IndividualUtil.processNameForIndividual(nodeSink.getName());

		Path path = new Path();
		path.setName("Path " + nodeSource.getName() + "-" + nodeSink.getName());

		String pathIndName = this.createPathIndividual(path);

		Node currentNode = nodeSource;
		intermediaryNodes.add(nodeSink);

		for (Node node : intermediaryNodes) {
			String secPathIndName = IndividualUtil.processNameForIndividual("Path " + currentNode.getName() + "-" + node.getName());
			Set<OWLNamedIndividual> individuals = ontomanager.getObjectPropertiesForIndividual(secPathIndName,
					NamedObjectProp.CONTAINS);
			for (OWLNamedIndividual i : individuals) {
				ontomanager.createObjectPropertyAssertionAxiom(pathIndName, i, NamedObjectProp.CONTAINS);
			}
			currentNode = node;
		}

		ontomanager.createObjectPropertyAssertionAxiom(nodeSourceIndName, nodeSinkIndName, NamedObjectProp.CONNECTEDTO);

	}

	public OntologyManager getManager() {
		return ontomanager;
	}

	public void setManager(OntologyManager manager) {
		this.ontomanager = manager;
	}

	public OWLDataFactory getFactory() {
		return factory;
	}

	public void setFactory(OWLDataFactory factory) {
		this.factory = factory;
	}	
	
}
