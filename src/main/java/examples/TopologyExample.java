package examples;

import java.util.ArrayList;

import com.sfchecker.classes.CPU;
import com.sfchecker.classes.Memory;
import com.sfchecker.classes.NFVI;
import com.sfchecker.classes.NFVIPoP;
import com.sfchecker.classes.Node;
import com.sfchecker.classes.Storage;
import com.sfchecker.classes.SwitchingMatrix;
import com.sfchecker.classes.VNF;
import com.sfchecker.classes.VNFC;
import com.sfchecker.classes.VNFFG;
import com.sfchecker.manager.OntoNFVManager;
import com.sfchecker.util.NamedClasses;
import com.sfchecker.util.NamedNetFunction;

public class TopologyExample {

	public OntoNFVManager manager;

	public TopologyExample(OntoNFVManager manager) {
		this.manager = manager;
	}

	public void createVNFFGExample() {
		CPU cpu = new CPU();
		cpu.setName("Virtual CPU");
		cpu.setNumCores(1);
		cpu.setAvailableCores(1);
		cpu.setSpeed(1330000);

		Memory mem = new Memory();
		mem.setName("Virtual Memory");
		mem.setSize(2);
		mem.setAvailableSize(2);

		Storage sto = new Storage();
		sto.setName("Virtual HD");
		sto.setSize(100);
		sto.setAvailableSize(100);

		Node nfvinode1 = new Node();
		nfvinode1.setName("NFVINode1 FOR");

		Node nfvinode2 = new Node();
		nfvinode2.setName("NFVINode2 FOR");

		Node nfvinode3 = new Node();
		nfvinode3.setName("NFVINode3 FOR");

		VNFC vnfc1 = new VNFC();
		vnfc1.setName("NS1 VNF1 VNFC DPI");
		vnfc1.setCpu(cpu);
		vnfc1.setMemory(mem);
		vnfc1.setStorage(sto);
		vnfc1.setNumInterfaces(1);
		vnfc1.setGuestNode(nfvinode1);
		vnfc1.setNetFunction(NamedNetFunction.DPI);

		VNFC vnfc2 = new VNFC();
		vnfc2.setName("NS1 VNF2 VNFC Firewall");
		vnfc2.setCpu(cpu);
		vnfc2.setMemory(mem);
		vnfc2.setStorage(sto);
		vnfc2.setNumInterfaces(1);
		vnfc2.setGuestNode(nfvinode2);
		vnfc2.setNetFunction(NamedNetFunction.FIREWALL);

		VNFC vnfc3 = new VNFC();
		vnfc3.setName("NS1 VNF2 VNFC NAT");
		vnfc3.setCpu(cpu);
		vnfc3.setMemory(mem);
		vnfc3.setStorage(sto);
		vnfc3.setNumInterfaces(1);
		vnfc3.setGuestNode(nfvinode3);
		vnfc3.setNetFunction(NamedNetFunction.NAT);

		ArrayList<VNFC> vnfcs1 = new ArrayList<VNFC>();

		VNF vnf1 = new VNF();
		vnf1.setName("NS1 VNF1");
		vnfcs1.add(vnfc1);
		vnf1.setVnfcs(vnfcs1);

		ArrayList<VNFC> vnfcs2 = new ArrayList<VNFC>();

		VNF vnf2 = new VNF();
		vnf2.setName("NS1 VNF2");
		vnfcs2.add(vnfc2);
		vnfcs2.add(vnfc3);
		vnf2.setVnfcs(vnfcs2);

		ArrayList<VNF> vnfs = new ArrayList<VNF>();

		VNFFG vnffg1 = new VNFFG();
		vnffg1.setName("NS1");
		vnfs.add(vnf1);
		vnfs.add(vnf2);
		vnffg1.setVnfs(vnfs);

		manager.getSfcManager().createVNFFGIndividual(vnffg1);

		//manager.getOntoManager().saveOntologyFile();

	}

	public void createTopologyExample() {
		CPU cpu = new CPU();
		cpu.setName("Intel I5");
		cpu.setNumCores(4);
		cpu.setAvailableCores(4);
		cpu.setSpeed(1330000);

		Memory mem = new Memory();
		mem.setName("Kingston DDR3");
		mem.setSize(16);
		mem.setAvailableSize(16);

		Storage sto = new Storage();
		sto.setName("Sansung HD");
		sto.setSize(1000);
		sto.setAvailableSize(1000);

		SwitchingMatrix swt1 = new SwitchingMatrix();
		swt1.setName("Switching Matrix");

		NFVI nfvi = new NFVI();
		nfvi.setName("NFVI BR");

		NFVIPoP nfvipop1 = new NFVIPoP();
		nfvipop1.setName("NFVIPoP FOR");
		nfvipop1.setNfvi(nfvi);

		NFVIPoP nfvipop2 = new NFVIPoP();
		nfvipop2.setName("NFVIPoP RE");
		nfvipop2.setNfvi(nfvi);

		Node nfvinode1 = new Node();
		nfvinode1.setName("NFVINode1 FOR");
		nfvinode1.setCpu(cpu);
		nfvinode1.setMemory(mem);
		nfvinode1.setStorage(sto);
		nfvinode1.setNumInterfaces(1);
		nfvinode1.setNfvipop(nfvipop1);
		nfvinode1.setMaxNumVNFs(1);

		Node nfvinode2 = new Node();
		nfvinode2.setName("NFVINode2 FOR");
		nfvinode2.setCpu(cpu);
		nfvinode2.setMemory(mem);
		nfvinode2.setStorage(sto);
		nfvinode2.setNumInterfaces(1);
		nfvinode2.setNfvipop(nfvipop1);
		nfvinode2.setMaxNumVNFs(2);

		Node nfvinode3 = new Node();
		nfvinode3.setName("NFVINode3 FOR");
		nfvinode3.setCpu(cpu);
		nfvinode3.setMemory(mem);
		nfvinode3.setStorage(sto);
		nfvinode3.setNumInterfaces(1);
		nfvinode3.setNfvipop(nfvipop1);

		Node nfvinode4 = new Node();
		nfvinode4.setName("NFVINode1 REC");
		nfvinode4.setCpu(cpu);
		nfvinode4.setMemory(mem);
		nfvinode4.setStorage(sto);
		nfvinode4.setNumInterfaces(1);
		nfvinode4.setNfvipop(nfvipop2);

		Node nfvinode5 = new Node();
		nfvinode5.setName("NFVINode2 REC");
		nfvinode5.setCpu(cpu);
		nfvinode5.setMemory(mem);
		nfvinode5.setStorage(sto);
		nfvinode5.setNumInterfaces(1);
		nfvinode5.setNfvipop(nfvipop2);

		Node nfvinode6 = new Node();
		nfvinode6.setName("NFVINode3 REC");
		nfvinode6.setCpu(cpu);
		nfvinode6.setMemory(mem);
		nfvinode6.setStorage(sto);
		nfvinode6.setNumInterfaces(1);
		nfvinode6.setNfvipop(nfvipop2);

		Node switchnode1 = new Node();
		switchnode1.setName("Switch1 FOR");
		switchnode1.setCpu(cpu);
		switchnode1.setMemory(mem);
		switchnode1.setStorage(sto);
		switchnode1.setSwitchingMatrix(swt1);
		switchnode1.setNumInterfaces(4);
		switchnode1.setNfvipop(nfvipop1);

		Node switchnode2 = new Node();
		switchnode2.setName("Switch1 REC");
		switchnode2.setCpu(cpu);
		switchnode2.setMemory(mem);
		switchnode2.setStorage(sto);
		switchnode2.setSwitchingMatrix(swt1);
		switchnode2.setNumInterfaces(4);
		switchnode2.setNfvipop(nfvipop2);

		manager.getNfviManager().createNFVIIndividual(nfvi);

		manager.getNfviManager().createNFVIPoPIndividual(nfvipop1);
		manager.getNfviManager().createNFVIPoPIndividual(nfvipop2);

		manager.getNfviManager().createNFVINodeIndividual(nfvinode1);
		manager.getNfviManager().createNFVINodeIndividual(nfvinode2);
		manager.getNfviManager().createNFVINodeIndividual(nfvinode3);
		manager.getNfviManager().createNFVINodeIndividual(nfvinode4);
		manager.getNfviManager().createNFVINodeIndividual(nfvinode5);
		manager.getNfviManager().createNFVINodeIndividual(nfvinode6);

		manager.getNfviManager().createNetworkNodeIndividual(switchnode1);
		manager.getNfviManager().createNetworkNodeIndividual(switchnode2);

		manager.getNfviManager().createPhysicalConnectionBetweenHosts(nfvinode1, 1, switchnode1, 1, 1000000);
		manager.getNfviManager().createPhysicalConnectionBetweenHosts(nfvinode2, 1, switchnode1, 2, 1000000);
		manager.getNfviManager().createPhysicalConnectionBetweenHosts(nfvinode3, 1, switchnode1, 3, 1000000);
		manager.getNfviManager().createPhysicalConnectionBetweenHosts(nfvinode4, 1, switchnode2, 1, 1000000);
		manager.getNfviManager().createPhysicalConnectionBetweenHosts(nfvinode5, 1, switchnode2, 2, 1000000);
		manager.getNfviManager().createPhysicalConnectionBetweenHosts(nfvinode6, 1, switchnode2, 3, 1000000);

		manager.getNfviManager().createPhysicalConnectionBetweenPoPs(switchnode1, 4, switchnode2, 4, 1000000, 1000);

		ArrayList<Node> intermediaryNodes1 = new ArrayList<Node>();
		intermediaryNodes1.add(switchnode1);
		manager.getNfviManager().createPathBetweenHosts(nfvinode1, intermediaryNodes1, nfvinode2);
		manager.getNfviManager().createPathBetweenHosts(nfvinode1, intermediaryNodes1, nfvinode3);
		manager.getNfviManager().createPathBetweenHosts(nfvinode2, intermediaryNodes1, nfvinode3);

		ArrayList<Node> intermediaryNodes2 = new ArrayList<Node>();
		intermediaryNodes2.add(switchnode2);
		manager.getNfviManager().createPathBetweenHosts(nfvinode4, intermediaryNodes2, nfvinode5);
		manager.getNfviManager().createPathBetweenHosts(nfvinode4, intermediaryNodes2, nfvinode6);
		manager.getNfviManager().createPathBetweenHosts(nfvinode5, intermediaryNodes2, nfvinode6);

		System.out.println("---------------NFVI----------------");
		manager.getOntoManager().listClassIndividuals(NamedClasses.NFVI);
		System.out.println("---------------NFVIPoP----------------");
		manager.getOntoManager().listClassIndividuals(NamedClasses.NFVIPOP);
		System.out.println("---------------NFVI Node----------------");
		manager.getOntoManager().listClassIndividuals(NamedClasses.NFVINODE);
		System.out.println("---------------Switch----------------");
		manager.getOntoManager().listClassIndividuals(NamedClasses.NODE);

		//manager.getOntoManager().saveOntologyFile();
		createVNFFGExample();

	}

	// Use Case 1 - Testing Conflicts on Precedence Restrictions on Network Functions
	// hasNetworkFunctionTypePrecedence(DPI, Firewall)
	// hasNetworkFunctionTypePrecedence(Firewall, DPI) 
	public void createUseCaseRestrictions1() {

		// NFVI Operator
		manager.getRestManager().createPrecendenceRestriction(NamedNetFunction.DPI, NamedNetFunction.FIREWALL);
		// SFC Request
		manager.getRestManager().createPrecendenceRestriction(NamedNetFunction.FIREWALL, NamedNetFunction.DPI);

	}

	// Use Case 2 - Testing Conflicts on Resource Usage Constraints
	// implementedTemporalyBy(vnfc-wanoptimization, nfvinode1-for)
	public void createUseCaseRestrictions2() {

		CPU cpu = new CPU();
		cpu.setName("Virtual CPU");
		cpu.setNumCores(1);
		cpu.setAvailableCores(1);
		cpu.setSpeed(1330000);

		Memory mem = new Memory();
		mem.setName("Virtual Memory");
		mem.setSize(2);
		mem.setAvailableSize(2);

		Storage sto = new Storage();
		sto.setName("Virtual HD");
		sto.setSize(100);
		sto.setAvailableSize(100);

		Node nfvinode1 = new Node();
		nfvinode1.setName("NFVINode1 FOR");

		VNFC vnfc1 = new VNFC();
		vnfc1.setName("VNFC WANOPTIMIZATION");
		vnfc1.setCpu(cpu);
		vnfc1.setMemory(mem);
		vnfc1.setStorage(sto);
		vnfc1.setNumInterfaces(1);
		vnfc1.setGuestNode(nfvinode1);
		vnfc1.setNetFunction(NamedNetFunction.WANOPTIMIZATION);

		// SFC Request
		manager.getSfcManager().createVNFCIndividual(vnfc1, true);

	}

	/*
	 * Use Case 3 - Testing Conflicts on Affinity and Anti-Affinity Restrictions Only on an SFC Request
	 * hasSameNFVINode(vnfc-dpi,vnfc-nat) 
	 * implementedTemporalyBy(vnfc-nat, nfvinode3-for)
     * hasNotSameNFVINode(vnfc-dpi,vnfc-firewall) 
     * implementedTemporalyBy(vnfc-firewall, nfvinode3-for)
	 */
	public void createUseCaseRestrictions3() {

		CPU cpu = new CPU();
		cpu.setName("Virtual CPU");
		cpu.setNumCores(1);
		cpu.setAvailableCores(1);
		cpu.setSpeed(1330000);

		Memory mem = new Memory();
		mem.setName("Virtual Memory");
		mem.setSize(2);
		mem.setAvailableSize(2);

		Storage sto = new Storage();
		sto.setName("Virtual HD");
		sto.setSize(100);
		sto.setAvailableSize(100);

		Node nfvinode3 = new Node();
		nfvinode3.setName("NFVINode3 FOR");

		// Guest Host was not identified
		VNFC vnfc1 = new VNFC();
		vnfc1.setName("NS2 VNF1 VNFC DPI");
		vnfc1.setCpu(cpu);
		vnfc1.setMemory(mem);
		vnfc1.setStorage(sto);
		vnfc1.setNumInterfaces(1);
		vnfc1.setNetFunction(NamedNetFunction.DPI);

		// Guest Host was identified
		VNFC vnfc2 = new VNFC();
		vnfc2.setName("NS2 VNF2 VNFC Firewall");
		vnfc2.setCpu(cpu);
		vnfc2.setMemory(mem);
		vnfc2.setStorage(sto);
		vnfc2.setNumInterfaces(1);
		vnfc2.setGuestNode(nfvinode3);
		vnfc2.setNetFunction(NamedNetFunction.FIREWALL);

		// Guest Host was identified
		VNFC vnfc3 = new VNFC();
		vnfc3.setName("NS2 VNF2 VNFC NAT");
		vnfc3.setCpu(cpu);
		vnfc3.setMemory(mem);
		vnfc3.setStorage(sto);
		vnfc3.setNumInterfaces(1);
		vnfc3.setGuestNode(nfvinode3);
		vnfc3.setNetFunction(NamedNetFunction.NAT);

		// SFC Request
		manager.getSfcManager().createVNFCIndividual(vnfc2, true);
		manager.getSfcManager().createVNFCIndividual(vnfc3, true);
		manager.getRestManager().createSameNodeRestriction(vnfc1, vnfc2);
		manager.getRestManager().createNotSameNodeRestriction(vnfc1, vnfc3);
	}

	/*
	 * Use Case 4 - Testing Conflicts on Affinity and Anti-Affinity Restrictions Between NVFI and NS Policies
	 * hasSameNFVINode(NAT,Firewall)
	 * implementedTemporalyBy(vnfc-nat, nfvinode2-for)
	 * implementedTemporalyBy(vnfc-firewall, nfvinode3-for) 
	 */
	public void createUseCaseRestrictions4() {

		CPU cpu = new CPU();
		cpu.setName("Virtual CPU");
		cpu.setNumCores(1);
		cpu.setAvailableCores(1);
		cpu.setSpeed(1330000);

		Memory mem = new Memory();
		mem.setName("Virtual Memory");
		mem.setSize(2);
		mem.setAvailableSize(2);

		Storage sto = new Storage();
		sto.setName("Virtual HD");
		sto.setSize(100);
		sto.setAvailableSize(100);

		Node nfvinode2 = new Node();
		nfvinode2.setName("NFVINode2 FOR");

		Node nfvinode3 = new Node();
		nfvinode3.setName("NFVINode3 FOR");

		// Guest Host was not identified
		VNFC vnfc1 = new VNFC();
		vnfc1.setName("NS2 VNF1 VNFC NAT");
		vnfc1.setCpu(cpu);
		vnfc1.setMemory(mem);
		vnfc1.setStorage(sto);
		vnfc1.setNumInterfaces(1);
		vnfc1.setGuestNode(nfvinode2);
		vnfc1.setNetFunction(NamedNetFunction.NAT);

		// Guest Host was identified
		VNFC vnfc2 = new VNFC();
		vnfc2.setName("NS2 VNF2 VNFC Firewall");
		vnfc2.setCpu(cpu);
		vnfc2.setMemory(mem);
		vnfc2.setStorage(sto);
		vnfc2.setNumInterfaces(1);
		vnfc2.setGuestNode(nfvinode3);
		vnfc2.setNetFunction(NamedNetFunction.FIREWALL);

		// SFC Request
		manager.getSfcManager().createVNFCIndividual(vnfc1, true);
		manager.getSfcManager().createVNFCIndividual(vnfc2, true);
		// NFVI Operator
		manager.getRestManager().createSameNodeRestriction(NamedNetFunction.NAT, NamedNetFunction.FIREWALL);
	}

	/*
	 * Use Case 5 - Testing Conflicts on Resouce Usage and Affinity Restrictions Between NVFI and NS Policies
	 * hasSameNFVINode(NAT,Firewall) 
	 * implementedTemporalyBy(vnfc-nat, nfvinode2-for)
	 */
	public void createUseCaseRestrictions5() {

		CPU cpu = new CPU();
		cpu.setName("Virtual CPU");
		cpu.setNumCores(1);
		cpu.setAvailableCores(1);
		cpu.setSpeed(1330000);

		Memory mem = new Memory();
		mem.setName("Virtual Memory");
		mem.setSize(2);
		mem.setAvailableSize(2);

		Storage sto = new Storage();
		sto.setName("Virtual HD");
		sto.setSize(100);
		sto.setAvailableSize(100);

		Node nfvinode2 = new Node();
		nfvinode2.setName("NFVINode2 FOR");

		// Guest Host was not identified
		VNFC vnfc1 = new VNFC();
		vnfc1.setName("NS2 VNF1 VNFC NAT");
		vnfc1.setCpu(cpu);
		vnfc1.setMemory(mem);
		vnfc1.setStorage(sto);
		vnfc1.setNumInterfaces(1);
		vnfc1.setGuestNode(nfvinode2);
		vnfc1.setNetFunction(NamedNetFunction.NAT);

		// Guest Host was identified
		VNFC vnfc2 = new VNFC();
		vnfc2.setName("NS2 VNF2 VNFC Firewall");
		vnfc2.setCpu(cpu);
		vnfc2.setMemory(mem);
		vnfc2.setStorage(sto);
		vnfc2.setNumInterfaces(1);
		vnfc2.setNetFunction(NamedNetFunction.FIREWALL);

		// SFC Request
		manager.getSfcManager().createVNFCIndividual(vnfc1, true);
		manager.getSfcManager().createVNFCIndividual(vnfc2, true);
		// NFVI Operator
		manager.getRestManager().createSameNodeRestriction(NamedNetFunction.NAT, NamedNetFunction.FIREWALL);
	}

}
