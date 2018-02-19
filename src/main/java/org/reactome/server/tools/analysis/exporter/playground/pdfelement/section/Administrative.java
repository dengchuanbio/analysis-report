package org.reactome.server.tools.analysis.exporter.playground.pdfelement.section;

import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import org.reactome.server.analysis.core.result.AnalysisStoredResult;
import org.reactome.server.tools.analysis.exporter.playground.constant.FontSize;
import org.reactome.server.tools.analysis.exporter.playground.constant.MarginLeft;
import org.reactome.server.tools.analysis.exporter.playground.constant.URL;
import org.reactome.server.tools.analysis.exporter.playground.pdfelement.AnalysisReport;
import org.reactome.server.tools.analysis.exporter.playground.util.GraphCoreHelper;
import org.reactome.server.tools.analysis.exporter.playground.util.PdfUtils;

/**
 * @author Chuan-Deng dengchuanbio@gmail.com
 */
public class Administrative implements Section {

    private static final int numOfIdentifiersToShow = 5;
    private static final String[] ADMINISTRATIVE = PdfUtils.getText("src/main/resources/texts/administrative.txt");

    public void render(AnalysisReport report, AnalysisStoredResult result) throws Exception {
        StringBuilder identifiers = new StringBuilder();
        result.getAnalysisIdentifiers().stream().limit(numOfIdentifiersToShow).forEach(analysisIdentifier -> identifiers.append(analysisIdentifier.getId()).append(","));
        report.addNormalTitle("Administrative", FontSize.H2, 0)
                .addParagraph(new Paragraph().setFontSize(FontSize.H5)
                        .setMarginLeft(MarginLeft.M2)
                        .add(ADMINISTRATIVE[0])
                        .add(new Text(result.getSummary().getToken()).setFontColor(report.getLinkColor()).setAction(PdfAction.createURI(URL.ANALYSIS.concat(result.getSummary().getToken()))))
//                        .add(String.format(ADMINISTRATIVE[0], result.getSummary().getToken().toLowerCase()))
//                        .add(PdfUtils.createUrlLinkIcon(report.getLinkIcon(), FontSize.H5, URL.ANALYSIS + result.getSummary().getToken()))
                        .add(String.format(ADMINISTRATIVE[1], identifiers.toString(), GraphCoreHelper.getDBVersion(), PdfUtils.getTimeStamp())));
    }
}