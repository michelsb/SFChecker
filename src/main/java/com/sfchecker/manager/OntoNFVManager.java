package com.sfchecker.manager;

import com.sfchecker.reasoning.ConflictDetectionModule;

public class OntoNFVManager {

	private OntologyManager ontoManager;
	private RestrictionManager restManager;
	private NFVIManager nfviManager;
	private SFCManager sfcManager;
	private ConflictDetectionModule cdmManager;
	
	public OntoNFVManager () {
		this.ontoManager = new OntologyManager();
		this.ontoManager.startOntologyProcessing();
		this.restManager = new RestrictionManager(ontoManager);
		this.nfviManager = new NFVIManager(ontoManager, restManager);
		this.sfcManager = new SFCManager(nfviManager);
		this.cdmManager = new ConflictDetectionModule(ontoManager);
	}

	public OntologyManager getOntoManager() {
		return ontoManager;
	}

	public void setOntoManager(OntologyManager ontoManager) {
		this.ontoManager = ontoManager;
	}

	public RestrictionManager getRestManager() {
		return restManager;
	}

	public void setRestManager(RestrictionManager restManager) {
		this.restManager = restManager;
	}

	public NFVIManager getNfviManager() {
		return nfviManager;
	}

	public void setNfviManager(NFVIManager nfviManager) {
		this.nfviManager = nfviManager;
	}

	public SFCManager getSfcManager() {
		return sfcManager;
	}

	public void setSfcManager(SFCManager sfcManager) {
		this.sfcManager = sfcManager;
	}

	public ConflictDetectionModule getCdmManager() {
		return cdmManager;
	}

	public void setCdmManager(ConflictDetectionModule cdmManager) {
		this.cdmManager = cdmManager;
	}
	
}
