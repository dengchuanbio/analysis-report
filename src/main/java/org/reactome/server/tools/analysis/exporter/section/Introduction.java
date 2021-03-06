package org.reactome.server.tools.analysis.exporter.section;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import org.reactome.server.tools.analysis.exporter.AnalysisData;
import org.reactome.server.tools.analysis.exporter.style.Images;
import org.reactome.server.tools.analysis.exporter.style.PdfProfile;
import org.reactome.server.tools.analysis.exporter.util.PdfUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Section Introduction contains the analysis introduction and Reactome relative
 * publications
 *
 * @author Chuan-Deng dengchuanbio@gmail.com
 */
public class Introduction implements Section {

	private static final List<String> INTRODUCTION = PdfUtils.getText(Introduction.class.getResourceAsStream("introduction.txt"));
	private static final List<Reference> PUBLICATIONS = PdfUtils.getText(Introduction.class.getResourceAsStream("references.txt"))
			.stream()
			.map(s -> s.split("\t"))
			.map(line -> new Reference(line[0], line[1]))
			.collect(Collectors.toList());

	@Override
	public void render(Document document, PdfProfile profile, AnalysisData analysisData) {
		document.add(new AreaBreak());
		document.add(profile.getH1("1. Introduction").setDestination("introduction"));
		INTRODUCTION.stream().map(profile::getParagraph).forEach(document::add);

		final List<Paragraph> paragraphs = new LinkedList<>();
		for (Reference publication : PUBLICATIONS) {
			final Image image = Images.getLink(publication.link, profile.getFontSize());
			paragraphs.add(profile.getParagraph(publication.text).add(image));
		}
		document.add(profile.getList(paragraphs));

	}

	private static class Reference {
		String text;
		String link;

		Reference(String text, String link) {
			this.text = text;
			this.link = link;
		}
	}
}
