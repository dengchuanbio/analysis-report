package org.reactome.server.tools.analysis.exporter.playground.pdfelement.section;

import com.itextpdf.kernel.events.PdfDocumentEvent;
import org.reactome.server.tools.analysis.exporter.playground.model.DataSet;
import org.reactome.server.tools.analysis.exporter.playground.pdfelement.AnalysisReport;
import org.reactome.server.tools.analysis.exporter.playground.pdfelement.FooterEventHandler;
import org.reactome.server.tools.analysis.exporter.playground.pdfelement.PdfProperties;

/**
 * @author Chuan-Deng <dengchuanbio@gmail.com>
 */
public class Footer implements Section {
    public void render(AnalysisReport report, PdfProperties properties, DataSet dataSet) throws Exception {
        report.getPdfDocument().addEventHandler(PdfDocumentEvent.END_PAGE, new FooterEventHandler(properties.getFont(), properties.getMargin()));
    }
}