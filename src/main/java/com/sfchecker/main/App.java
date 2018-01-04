package com.sfchecker.main;

import java.io.IOException;

import com.sfchecker.manager.OntoNFVManager;

import examples.TopologyExample;

public class App {

	
	public static void main(String[] args) throws IOException {

		OntoNFVManager manager = new OntoNFVManager();
		TopologyExample topology = new TopologyExample(manager);
		
		topology.createTopologyExample();
		
		manager.getOntoManager().saveNewOntologyFile("onto-nfv-with-topo.owl");
		
		//topology.createUseCaseRestrictions1();

		//manager.getOntoManager().saveNewOntologyFile("onto-nfv-with-topo-uc1.owl");
		
		//topology.createUseCaseRestrictions2();

		//manager.getOntoManager().saveNewOntologyFile("onto-nfv-with-topo-uc2.owl");
		
		//topology.createUseCaseRestrictions3();

		//manager.getOntoManager().saveNewOntologyFile("onto-nfv-with-topo-uc3.owl");
		
		//topology.createUseCaseRestrictions4();

		//manager.getOntoManager().saveNewOntologyFile("onto-nfv-with-topo-uc4.owl");
		
		topology.createUseCaseRestrictions5();

		manager.getOntoManager().saveNewOntologyFile("onto-nfv-with-topo-uc5.owl");
		
		manager.getCdmManager().testOntoConsistency();

	}

}