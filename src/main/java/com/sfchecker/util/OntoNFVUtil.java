package com.sfchecker.util;

import java.io.File;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.IRI;

public class OntoNFVUtil {

	public static final @Nonnull IRI NFV_IRI = IRI.create("http://cin.ufpe.br/msb6/ontologies/2018/1/", "onto-nfv.owl");
	public static final @Nonnull String ONTODIR = "ontology/";
	public static final @Nonnull File ONTOFILE = new File(ONTODIR+"onto-nfv.owl");
	
}
