package org.reactome.server.tools.analysis.report.renderer;

import org.reactome.server.analysis.core.result.model.FoundElements;
import org.reactome.server.analysis.core.result.model.ResourceSummary;
import org.reactome.server.graph.domain.model.DatabaseObject;
import org.reactome.server.graph.domain.model.Disease;
import org.reactome.server.graph.domain.model.Pathway;
import org.reactome.server.graph.domain.result.DiagramResult;
import org.reactome.server.graph.service.DiagramService;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import org.reactome.server.tools.analysis.report.AnalysisData;
import org.reactome.server.tools.analysis.report.PathwayData;
import org.reactome.server.tools.analysis.report.document.TexDocument;
import org.reactome.server.tools.analysis.report.util.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PathwayDetailRenderer implements TexRenderer {

	private static final DiagramService SERVICE = ReactomeGraphCore.getService(DiagramService.class);
	private static final String PATHWAY_DETAIL = "https://reactome.org/content/detail/";

	@Override
	public void render(TexDocument document, AnalysisData analysisData) {
		document.commandln(TexDocument.SECTION, "Pathway details");
		analysisData.getPathways().forEach(pathwayData -> {
			final Pathway pathway = pathwayData.getPathway();
			final String link = PATHWAY_DETAIL + pathway.getStId();
			final String title = TextUtils.scape(pathway.getDisplayName()) + " (\\href{" + link + "}{" + pathway.getStId() + "})";
			document.commandln(TexDocument.SUB_SECTION, title);
			addDiagram(document, pathwayData);
			addDatabaseObjectList(document, "Cellular compartments", pathway.getCompartment());
			addRelatedDiseases(document, pathway);
			addDatabaseObjectList(document, "Inferred from", pathway.getInferredFrom());

			addSummations(document, pathway);
			addReferences(document, pathway);
			addEditTable(document, pathway);
			addFoundElements(document, analysisData, pathway);

			document.commandln(TexDocument.NEW_PAGE);
		});
	}

	private void addDiagram(TexDocument document, PathwayData pathwayData) {
		final DiagramResult result = SERVICE.getDiagramResult(pathwayData.getSummary().getStId());
		// 210mm - 20mm - 25mm = 165mm
		final int width = (int) (Math.min(result.getWidth(), 165));
		document.commandln(TexDocument.BEGIN, null, TexDocument.FIGURE, "H")
				.commandln(TexDocument.CENTERING)
				.commandln(TexDocument.INCLUDE_GRAPHICS, "width=" + width + "mm", pathwayData.getSummary().getStId())
				.commandln(TexDocument.END, TexDocument.FIGURE)
				.ln();
	}

	private void addRelatedDiseases(TexDocument document, Pathway pathwayDetail) {
		if (pathwayDetail.getDisease() != null) {
			final java.util.List<Disease> diseases = pathwayDetail.getDisease().stream()
					.filter(disease -> !disease.getDisplayName().equals("disease"))
					.collect(Collectors.toList());
			addDatabaseObjectList(document, "Diseases", diseases);
		}
	}

	private void addDatabaseObjectList(TexDocument document, String title, Collection<? extends DatabaseObject> objects) {
		if (objects != null && !objects.isEmpty()) {
			final java.util.List<String> list = objects.stream()
					.map(DatabaseObject::getDisplayName)
					.collect(Collectors.toList());
			final String body = String.join(", ", list) + ".";
			document.command("noindent")
					.command(TexDocument.TEXT_BF, title + ": ")
					.paragraph(body);
		}
	}

	private void addSummations(TexDocument document, Pathway pathway) {
		pathway.getSummation().forEach(summation -> HtmlParser.parseText(document, summation));
	}

	private void addReferences(TexDocument document, Pathway pathway) {
		if (pathway.getLiteratureReference() != null) {
			document.commandln(TexDocument.SUB_SUB_SECTION + "*", "References");
			document.commandln(TexDocument.BEGIN, "itemize");
			pathway.getLiteratureReference().stream()
					.limit(5)
					.map(ReferenceFactory::createPublication)
					.forEach(reference -> document.command("item").textln(" " + reference));
			document.commandln(TexDocument.END, "itemize");
		}
	}

	private void addEditTable(TexDocument document, Pathway pathway) {
		final List<List<String>> rows = Editions.from(pathway).stream()
				.map(edition -> Arrays.asList(edition.getDate(), edition.getType(), edition.getAuthors()))
				.collect(Collectors.toList());
		final TexTable table = new TexTable(Arrays.asList("Date", "Action", "Author"), rows);
		table.setAlignment("ccZ");
		document.ln().commandln(TexDocument.SUB_SUB_SECTION + "*", "Edit history");
		table.render(document);
	}

	private void addFoundElements(TexDocument document, AnalysisData analysisData, Pathway pathway) {
		document.commandln(TexDocument.SUB_SUB_SECTION + "*", "Elements found in this pathway");
		if (analysisData.getResource().equalsIgnoreCase("total")) {
			for (ResourceSummary summary : analysisData.getAnalysisStoredResult().getResourceSummary()) {
				if (summary.getResource().equalsIgnoreCase("total")) continue;
				final FoundElements foundElements = analysisData.getAnalysisStoredResult().getFoundElmentsForPathway(pathway.getStId(), summary.getResource());
				if (foundElements.getFoundEntities() > 0)
					addIdentifiers(document, foundElements, analysisData.beautify(summary.getResource()));
			}
		} else {
			final FoundElements foundElements = analysisData.getAnalysisStoredResult().getFoundElmentsForPathway(pathway.getStId(), analysisData.getResource());
			addIdentifiers(document, foundElements, analysisData.getBeautifiedResource());
		}
	}

	private void addIdentifiers(TexDocument document, FoundElements elements, String resource) {
		if (elements.getExpNames() == null || elements.getExpNames().isEmpty())
			Tables.identifiersTable(elements.getEntities(), resource).render(document);
		else Tables.identifiersTable(elements.getEntities(), resource, elements.getExpNames()).render(document);
	}

}
