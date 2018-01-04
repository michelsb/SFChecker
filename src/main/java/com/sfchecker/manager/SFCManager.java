package com.sfchecker.manager;

import org.semanticweb.owlapi.model.OWLDataFactory;

import com.sfchecker.classes.Path;
import com.sfchecker.classes.VNF;
import com.sfchecker.classes.VNFC;
import com.sfchecker.classes.VNFFG;
import com.sfchecker.classes.VirtualLink;
import com.sfchecker.util.IndividualUtil;
import com.sfchecker.util.NamedClasses;
import com.sfchecker.util.NamedDataProp;
import com.sfchecker.util.NamedNetFunction;
import com.sfchecker.util.NamedObjectProp;
import com.sfchecker.util.OntoNFVUtil;

public class SFCManager {

	protected OntologyManager ontomanager;
	protected NFVIManager nfvimanager;
	protected OWLDataFactory factory;
	
	public SFCManager (NFVIManager nfvimanager) {
		this.nfvimanager = nfvimanager;
		this.ontomanager = nfvimanager.getManager();
		this.factory = nfvimanager.getFactory();
	}

	public void createVNFFGIndividual(VNFFG vnffg) {
		String vnffgIndName = IndividualUtil.processNameForIndividual(vnffg.getName());
		ontomanager.createIndividual(vnffgIndName, NamedClasses.VNFFG);
		ontomanager.createDataPropertyAssertionAxiom(vnffgIndName, factory.getOWLLiteral(OntoNFVUtil.NFV_IRI + "#" + vnffgIndName),
				NamedDataProp.ID);
		ontomanager.createDataPropertyAssertionAxiom(vnffgIndName, factory.getOWLLiteral(vnffg.getName()),
				NamedDataProp.HASNAME);

		if (vnffg.getVnfs().size() > 1) {
			for (int i = 1; i < vnffg.getVnfs().size(); i++) {
				VNF vnfSource = vnffg.getVnfs().get(i - 1);
				VNF vnfSink = vnffg.getVnfs().get(i);
				VNFC vnfcSource = vnfSource.getVnfcs().get(vnfSource.getVnfcs().size() - 1);
				VNFC vnfcSink = vnfSink.getVnfcs().get(0);
				String vnfSourceIndName = this.createVNFIndividual(vnfSource);
				String vnfSinkIndName = this.createVNFIndividual(vnfSink);
				String vLinkIndName = this.createVirtualConnectionBetweenVNFCs(vnfcSource, 1, vnfcSink, 1, 100000);
				ontomanager.createObjectPropertyAssertionAxiom(vnffgIndName, vnfSourceIndName, NamedObjectProp.CONTAINS);
				ontomanager.createObjectPropertyAssertionAxiom(vnffgIndName, vnfSinkIndName, NamedObjectProp.CONTAINS);
				ontomanager.createObjectPropertyAssertionAxiom(vnffgIndName, vLinkIndName, NamedObjectProp.CONTAINS);
			}
		} else {
			VNF vnf = vnffg.getVnfs().get(0);
			String vnfIndName = this.createVNFIndividual(vnf);
			ontomanager.createObjectPropertyAssertionAxiom(vnffgIndName, vnfIndName, NamedObjectProp.CONTAINS);
		}
	}

	public String createVNFIndividual(VNF vnf) {

		String vnfIndName = IndividualUtil.processNameForIndividual(vnf.getName());
		ontomanager.createIndividual(vnfIndName, NamedClasses.VNF);
		ontomanager.createDataPropertyAssertionAxiom(vnfIndName, factory.getOWLLiteral(OntoNFVUtil.NFV_IRI + "#" + vnfIndName),
				NamedDataProp.ID);
		ontomanager.createDataPropertyAssertionAxiom(vnfIndName, factory.getOWLLiteral(vnf.getName()), NamedDataProp.HASNAME);

		String ingressPortIndName = "";
		String egressPortIndName = "";

		if (vnf.getVnfcs().size() > 1) {
			for (int i = 1; i < vnf.getVnfcs().size(); i++) {
				VNFC vnfcSource = vnf.getVnfcs().get(i - 1);
				VNFC vnfcSink = vnf.getVnfcs().get(i);
				if (i == 1) {
					ingressPortIndName = IndividualUtil.processNameForIndividual(vnfcSource.getName() + " Interface" + 1 + " In");
				}
				if (i == vnf.getVnfcs().size() - 1) {
					egressPortIndName = IndividualUtil.processNameForIndividual(vnfcSink.getName() + " Interface" + 1 + " Out");
				}
				String vnfcSourceIndName = this.createVNFCIndividual(vnfcSource, false);
				String vnfcSinkIndName = this.createVNFCIndividual(vnfcSink, false);
				String vLinkIndName = this.createVirtualConnectionBetweenVNFCs(vnfcSource, 1, vnfcSink, 1, 100000);
				ontomanager.createObjectPropertyAssertionAxiom(vnfIndName, vnfcSourceIndName, NamedObjectProp.CONTAINS);
				ontomanager.createObjectPropertyAssertionAxiom(vnfIndName, vnfcSinkIndName, NamedObjectProp.CONTAINS);
				ontomanager.createObjectPropertyAssertionAxiom(vnfIndName, vLinkIndName, NamedObjectProp.CONTAINS);
			}
		} else {
			VNFC vnfc = vnf.getVnfcs().get(0);
			String vnfcIndName = this.createVNFCIndividual(vnfc, false);
			ontomanager.createObjectPropertyAssertionAxiom(vnfIndName, vnfcIndName, NamedObjectProp.CONTAINS);
			ingressPortIndName = IndividualUtil.processNameForIndividual(vnfc.getName() + " Interface" + 1 + " In");
			egressPortIndName = IndividualUtil.processNameForIndividual(vnfc.getName() + " Interface" + 1 + " Out");
		}

		ontomanager.createObjectPropertyAssertionAxiom(vnfIndName, ingressPortIndName, NamedObjectProp.HASINGRESSPORT);
		ontomanager.createObjectPropertyAssertionAxiom(vnfIndName, egressPortIndName, NamedObjectProp.HASEGRESSPORT);

		return vnfIndName;

	}

	public String createVNFCIndividual(VNFC vnfc, boolean temporaly) {
		String vnfcIndName = IndividualUtil.processNameForIndividual(vnfc.getName());
		String nameCls = "";

		switch (vnfc.getNetFunction()) {
		case NamedNetFunction.FIREWALL:
			nameCls = NamedClasses.VNFCFIREWALL;
			break;
		case NamedNetFunction.NAT:
			nameCls = NamedClasses.VNFCNAT;
			break;
		case NamedNetFunction.INTRUSIONDETECTION:
			nameCls = NamedClasses.VNFCINTRUSIONDETECTION;
			break;
		case NamedNetFunction.L2SWITCH:
			nameCls = NamedClasses.VNFCL2SWITCH;
			break;
		case NamedNetFunction.L3SWITCH:
			nameCls = NamedClasses.VNFCL3SWITCH;
			break;
		case NamedNetFunction.LOADBALANCE:
			nameCls = NamedClasses.VNFCLOADBALANCE;
			break;
		case NamedNetFunction.WANOPTIMIZATION:
			nameCls = NamedClasses.VNFCWANOPTIMIZATION;
			break;
		case NamedNetFunction.DPI:
			nameCls = NamedClasses.VNFCDPI;
			break;
		}

		ontomanager.createIndividual(vnfcIndName, nameCls);
		ontomanager.createDataPropertyAssertionAxiom(vnfcIndName, factory.getOWLLiteral(OntoNFVUtil.NFV_IRI + "#" + vnfcIndName),
				NamedDataProp.ID);
		ontomanager.createDataPropertyAssertionAxiom(vnfcIndName, factory.getOWLLiteral(vnfc.getName()),
				NamedDataProp.HASNAME);

		// Creating CPU
		String cpuIndName = nfvimanager.createCPUIndividual(vnfc.getCpu());
		ontomanager.createObjectPropertyAssertionAxiom(vnfcIndName, cpuIndName, NamedObjectProp.HASCOMPONENT);
		// Creating Memory
		String memIndName = nfvimanager.createMemoryIndividual(vnfc.getMemory());
		ontomanager.createObjectPropertyAssertionAxiom(vnfcIndName, memIndName, NamedObjectProp.HASCOMPONENT);
		// Creating Storage
		String stoIndName = nfvimanager.createStorageIndividual(vnfc.getStorage());
		ontomanager.createObjectPropertyAssertionAxiom(vnfcIndName, stoIndName, NamedObjectProp.HASCOMPONENT);

		// Creating Interface
		nfvimanager.createInterfaces(vnfcIndName, vnfc.getNumInterfaces());

		String method = "";

		if (temporaly) {
			method = NamedObjectProp.IMPLEMENTEDTEMPORALYBY;
		} else {
			method = NamedObjectProp.IMPLEMENTEDBY;
		}

		if (vnfc.getGuestNode() != null) {
			String nfviNodeIndName = IndividualUtil.processNameForIndividual(vnfc.getGuestNode().getName());
			ontomanager.createObjectPropertyAssertionAxiom(vnfcIndName, nfviNodeIndName, method);
			ontomanager.createDataPropertyAssertionAxiom(vnfcIndName, factory.getOWLLiteral(true), NamedDataProp.HASGUEST);
		} else {
			ontomanager.createDataPropertyAssertionAxiom(vnfcIndName, factory.getOWLLiteral(false), NamedDataProp.HASGUEST);
		}

		return vnfcIndName;

	}
	
	public String createVirtualLinkIndividual(VirtualLink vLink) {
		String vLinkIndName = IndividualUtil.processNameForIndividual(vLink.getName());
		ontomanager.createIndividual(vLinkIndName, NamedClasses.VIRTUALLINK);
		ontomanager.createDataPropertyAssertionAxiom(vLinkIndName, factory.getOWLLiteral(OntoNFVUtil.NFV_IRI + "#" + vLinkIndName),
				NamedDataProp.ID);
		ontomanager.createDataPropertyAssertionAxiom(vLinkIndName, factory.getOWLLiteral(vLink.getName()),
				NamedDataProp.HASNAME);
		ontomanager.createDataPropertyAssertionAxiom(vLinkIndName, factory.getOWLLiteral(vLink.getCapacity()),
				NamedDataProp.HASCAPACITY);
		ontomanager.createDataPropertyAssertionAxiom(vLinkIndName, factory.getOWLLiteral(vLink.getAvailableCapacity()),
				NamedDataProp.HASAVAILABLECAPACITY);

		if (vLink.getPath() != null) {
			String pathIndName = IndividualUtil.processNameForIndividual(vLink.getPath().getName());
			ontomanager.createObjectPropertyAssertionAxiom(vLinkIndName, pathIndName, NamedObjectProp.PROVISIONEDBY);
		}

		return vLinkIndName;
	}
	
	public String createVirtualConnectionBetweenVNFCs(VNFC vnfc1, int inteIndexVNFC1, VNFC vnfc2, int inteIndexVNFC2,
			float speed) {

		String outInteVNFC1IndName = IndividualUtil.processNameForIndividual(vnfc1.getName() + " Interface" + inteIndexVNFC1 + " Out");
		String inInteVNFC2IndName = IndividualUtil.processNameForIndividual(vnfc2.getName() + " Interface" + inteIndexVNFC2 + " In");

		String nodeSourceIndName = IndividualUtil.processNameForIndividual(vnfc1.getGuestNode().getName());
		String nodeSinkIndName = IndividualUtil.processNameForIndividual(vnfc2.getGuestNode().getName());

		String outInteNodeSourceIndName = IndividualUtil.processNameForIndividual(
				vnfc1.getGuestNode().getName() + " Interface" + inteIndexVNFC1 + " Out");
		String inInteNodeSinkIndName = IndividualUtil.processNameForIndividual(
				vnfc2.getGuestNode().getName() + " Interface" + inteIndexVNFC2 + " In");

		VirtualLink vLink = new VirtualLink();
		vLink.setName("Virtual Link " + vnfc1.getName() + "-" + vnfc2.getName());
		vLink.setCapacity(speed);
		vLink.setAvailableCapacity(speed);

		if (nodeSourceIndName != nodeSinkIndName) {
			Path path = new Path();
			path.setName(IndividualUtil.processNameForIndividual(
					"Path " + vnfc1.getGuestNode().getName() + "-" + vnfc2.getGuestNode().getName()));
			vLink.setPath(path);
		}

		String vLinkIndName = this.createVirtualLinkIndividual(vLink);
		ontomanager.createObjectPropertyAssertionAxiom(vLinkIndName, outInteVNFC1IndName, NamedObjectProp.HASSOURCE);
		ontomanager.createObjectPropertyAssertionAxiom(vLinkIndName, inInteVNFC2IndName, NamedObjectProp.HASSINK);

		ontomanager.createObjectPropertyAssertionAxiom(outInteVNFC1IndName, outInteNodeSourceIndName,
				NamedObjectProp.IMPLEMENTEDBY);
		ontomanager.createObjectPropertyAssertionAxiom(inInteVNFC2IndName, inInteNodeSinkIndName,
				NamedObjectProp.IMPLEMENTEDBY);

		return vLinkIndName;

	}
	
}
