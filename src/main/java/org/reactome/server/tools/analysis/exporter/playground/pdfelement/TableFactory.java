package org.reactome.server.tools.analysis.exporter.playground.pdfelement;

import com.itextpdf.layout.element.Table;
import org.reactome.server.tools.analysis.exporter.playground.exception.TableTypeNotFoundException;
import org.reactome.server.tools.analysis.exporter.playground.model.DataSet;
import org.reactome.server.tools.analysis.exporter.playground.model.Identifier;
import org.reactome.server.tools.analysis.exporter.playground.pdfelement.table.*;

/**
 * @author Chuan-Deng <dengchuanbio@gmail.com>
 */
public class TableFactory {
    private static PdfProperties pdfProperties;
    private static DataSet dataSet;

    public TableFactory(PdfProperties pdfProperties, DataSet dataSet) {
        this.pdfProperties = pdfProperties;
        this.dataSet = dataSet;
    }

    public Table getTable(TableTypeEnum type) throws TableTypeNotFoundException {
        switch (type) {
            case OVERVIEW_TABLE:
                return new OverviewTable(pdfProperties, dataSet);
            case IdentifiersWasFound:
                return new IdentifiersWasFoundTable(dataSet);
            case IDENTIFIERS_WAS_FOUND_NO_EXP:
                return new IdentifiersWasFoundTableNoEXP(dataSet);
            case IDENTIFIERS_WAS_NOT_FOUND:
                return new IdentifiersWasNotFoundTable(dataSet);
            case IDENTIFIERS_WAS_NOT_FOUND_NO_EXP:
                return new IdentifiersWasNotFoundTableNoEXP(dataSet);
            default:
                throw new TableTypeNotFoundException(String.format("No table type:%s was found", type));
        }
    }

    public Table getTable(Identifier[] identifiers) {
        return new IdentifiersWasFoundInPathwayTable(identifiers);
    }
}