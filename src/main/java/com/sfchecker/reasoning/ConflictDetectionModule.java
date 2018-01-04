package com.sfchecker.reasoning;

import java.util.Set;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clarkparsia.owlapi.explanation.BlackBoxExplanation;
import com.clarkparsia.owlapi.explanation.HSTExplanationGenerator;
import com.sfchecker.manager.OntologyManager;

public class ConflictDetectionModule {

	private OntologyManager ontomanager;

	public ConflictDetectionModule(OntologyManager ontomanager) {
		this.ontomanager = ontomanager;
	}

	// Testing Ontology Consistency
	public void testOntoConsistency() {
		OWLOntology onto = ontomanager.getOntology();
		// Now we can start and create the reasoner. Since explanation is not
		// natively supported by
		// HermiT and is realized in the OWL API, we need to instantiate HermiT
		// as an OWLReasoner. This is done via a ReasonerFactory object.
		ReasonerFactory reasonerFactory = new ReasonerFactory();
		// We don't want HermiT to thrown an exception for inconsistent
		// ontologies because then we
		// can't explain the inconsistency. This can be controlled via a
		// configuration setting.
		Configuration configuration = new Configuration();
		configuration.throwInconsistentOntologyException = false;
		// The factory can now be used to obtain an instance of HermiT as an
		// OWLReasoner.
		OWLReasoner reasoner = reasonerFactory.createReasoner(onto, configuration);

		if (!reasoner.isConsistent()) {
			System.out.println("Ontology is inconsistent!");
			System.out.println("Computing explanations for the inconsistency...");
			reasonerFactory = new ReasonerFactory() {
				protected OWLReasoner createHermiTOWLReasoner(org.semanticweb.HermiT.Configuration configuration,
						OWLOntology onto) {
					// don't throw an exception since otherwise we cannot
					// compute
					// explanations
					configuration.throwInconsistentOntologyException = false;
					return new Reasoner(configuration, onto);
				}
			};
			BlackBoxExplanation exp = new BlackBoxExplanation(onto, reasonerFactory, reasoner);
			HSTExplanationGenerator multExplanator = new HSTExplanationGenerator(exp);
			// Now we can get explanations for the inconsistency
			Set<Set<OWLAxiom>> explanations = multExplanator.getExplanations(ontomanager.getFactory().getOWLThing());

			// Let us print them. Each explanation is one possible set of axioms
			// that cause the
			// unsatisfiability.
			for (Set<OWLAxiom> explanation : explanations) {
				System.out.println("------------------");
				System.out.println("Axioms causing the inconsistency: ");
				for (OWLAxiom causingAxiom : explanation) {
					System.out.println(causingAxiom);
				}
				System.out.println("------------------");
			}

		} else {
			System.out.println("Ontology is consistent!");
		}

	}

	public static class LoggingReasonerProgressMonitor implements ReasonerProgressMonitor {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private static Logger logger;

		public LoggingReasonerProgressMonitor(Logger log) {
			logger = log;
		}

		public LoggingReasonerProgressMonitor(Logger log, String methodName) {
			String loggerName = log.getName() + '.' + methodName;
			logger = LoggerFactory.getLogger(loggerName);
		}

		@Override
		public void reasonerTaskStarted(String taskName) {
			logger.info("Reasoner Task Started: {}.", taskName);
		}

		@Override
		public void reasonerTaskStopped() {
			logger.info("Task stopped.");
		}

		@Override
		public void reasonerTaskProgressChanged(int value, int max) {
			logger.info("Reasoner Task made progress: {}/{}", Integer.valueOf(value), Integer.valueOf(max));
		}

		@Override
		public void reasonerTaskBusy() {
			logger.info("Reasoner Task is busy");
		}
	}

}
