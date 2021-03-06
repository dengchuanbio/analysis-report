package org.reactome.server.tools.analysis.exporter.section;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import org.reactome.server.analysis.core.result.model.IdentifierSummary;
import org.reactome.server.tools.analysis.exporter.AnalysisData;
import org.reactome.server.tools.analysis.exporter.style.PdfProfile;

/**
 * @author Chuan-Deng dengchuanbio@gmail.com
 */
public class IdentifierNotFound implements Section {

	@Override
	public void render(Document document, PdfProfile profile, AnalysisData analysisData) {
		document.add(new AreaBreak());
		document.add(profile.getH1("7. Identifiers not found").setDestination("not-found"));
		final Table table = new Table(UnitValue.createPercentArray(8));
		table.useAllAvailableWidth();
		int i = 0;
		int row = 0;
		for (IdentifierSummary summary : analysisData.getAnalysisStoredResult().getNotFoundIdentifiers()) {
			row = i / 8;
			table.addCell(profile.getBodyCell(summary.getId(), row));
			i += 1;
		}
		final int n = 8 - analysisData.getAnalysisStoredResult().getNotFoundIdentifiers().size() % 8;
		for (int j = 0; j < n; j++) table.addCell(profile.getBodyCell("", row));
		document.add(table);
	}
}
