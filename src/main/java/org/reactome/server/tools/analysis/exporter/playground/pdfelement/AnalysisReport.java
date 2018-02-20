package org.reactome.server.tools.analysis.exporter.playground.pdfelement;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import org.reactome.server.tools.analysis.exporter.playground.analysisexporter.ReportArgs;
import org.reactome.server.tools.analysis.exporter.playground.pdfelement.profile.PdfProfile;
import org.reactome.server.tools.analysis.exporter.playground.util.PdfUtils;

import java.io.OutputStream;

/**
 * @author Chuan-Deng dengchuanbio@gmail.com
 */
public class AnalysisReport extends Document {

    private Image linkIcon;
    private Color linkColor;
    private PdfProfile profile;
    private ReportArgs reportArgs;
    private Rectangle currentPageArea;

    public AnalysisReport(PdfProfile profile, ReportArgs reportArgs, OutputStream destination) {
        super(new PdfDocument(new PdfWriter(destination, new WriterProperties()
                .setFullCompressionMode(true))));
        this.profile = profile;
        this.reportArgs = reportArgs;
        getPdfDocument().addEventHandler(PdfDocumentEvent.END_PAGE, new FooterEventHandler(this));
        AnalysisFont.setUp();
//        setFont(profile.getPdfFont())
        setFont(AnalysisFont.REGULAR)
                .setTextAlignment(TextAlignment.JUSTIFIED);
        setMargins(profile.getMargin().getTop()
                , profile.getMargin().getRight()
                , profile.getMargin().getBottom()
                , profile.getMargin().getLeft());
        currentPageArea = getPageEffectiveArea(this.getPdfDocument().getDefaultPageSize());
        linkColor = PdfUtils.createColor("#2F9EC2");
    }

    public Image getLinkIcon() {
        return linkIcon;
    }

    public void setLinkIcon(Image linkIcon) {
        this.linkIcon = linkIcon;
    }

    public Color getLinkColor() {
        return linkColor;
    }

    public PdfProfile getProfile() {
        return profile;
    }

    public ReportArgs getReportArgs() {
        return reportArgs;
    }

    public Rectangle getCurrentPageArea() {
        return currentPageArea;
    }

    public List createList(float indent) {
        List list = new List();
        list.setListSymbol("\u2022");
        list.setSymbolIndent(indent);
        return list;
    }

    public void addAsList(java.util.List<? extends Paragraph> paragraphList) {
        List list = new List().setListSymbol("\u2022").setSymbolIndent(this.getLeftMargin() * 0.5f);
        ListItem item;
        for (Paragraph paragraph : paragraphList) {
            item = new ListItem();
            item.add(paragraph);
            list.add(item);
        }
        add(list);
    }
}