package org.reactome.server.tools.analysis.exporter.playground.pdfelements;

import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import org.reactome.server.tools.analysis.exporter.playground.domains.DataSet;
import org.reactome.server.tools.analysis.exporter.playground.domains.Pathway;

import java.text.NumberFormat;

/**
 * @author Chuan-Deng <dengchuanbio@gmail.com>
 */
public class OverviewTable extends Table {
    public OverviewTable(PdfProperties properties, DataSet dataSet) {
        super(UnitValue.createPercentArray(new float[]{5, 1, 1, 1, 3, 3, 1, 1, 1, 2}), false);
        this.setWidthPercent(100);
        this.setFontSize(6)
                .setTextAlignment(TextAlignment.CENTER);
//                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        String[] headers = {"Pathway name", "Entities found", "Entities Total", "Entities ratio", "Entities pValue", "Entities FDR", "Reactions found", "Reactions total", "Reactions ratio", "Species name"};
        for (String header : headers)
            this.addHeaderCell(header);
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(4);
        Pathway[] pathways = dataSet.getPathways();
        for (int i = 0; i < properties.getNumberOfPathwaysToShow(); i++) {
            this.addCell(new Cell().add(new Paragraph(new Link(pathways[i].getName(), PdfAction.createGoTo(pathways[i].getName())))).setVerticalAlignment(VerticalAlignment.MIDDLE));
            this.addCell(new Cell().add(pathways[i].getEntities().getFound() + "").setVerticalAlignment(VerticalAlignment.MIDDLE));
            this.addCell(new Cell().add(pathways[i].getEntities().getTotal() + "").setVerticalAlignment(VerticalAlignment.MIDDLE));
            this.addCell(new Cell().add(numberFormat.format(pathways[i].getEntities().getRatio())).setVerticalAlignment(VerticalAlignment.MIDDLE));
            this.addCell(new Cell().add(pathways[i].getEntities().getpValue() + "").setVerticalAlignment(VerticalAlignment.MIDDLE));
            this.addCell(new Cell().add(pathways[i].getEntities().getFdr() + "").setVerticalAlignment(VerticalAlignment.MIDDLE));
            this.addCell(new Cell().add(pathways[i].getReactions().getFound() + "").setVerticalAlignment(VerticalAlignment.MIDDLE));
            this.addCell(new Cell().add(pathways[i].getReactions().getTotal() + "").setVerticalAlignment(VerticalAlignment.MIDDLE));
            this.addCell(new Cell().add(numberFormat.format(pathways[i].getReactions().getRatio())).setVerticalAlignment(VerticalAlignment.MIDDLE));
            this.addCell(new Cell().add(pathways[i].getSpecies().getName()).setVerticalAlignment(VerticalAlignment.MIDDLE));
        }
    }
}