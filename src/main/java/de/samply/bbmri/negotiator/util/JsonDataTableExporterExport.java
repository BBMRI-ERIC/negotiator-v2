package de.samply.bbmri.negotiator.util;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.export.DataTableExporter;
import org.primefaces.component.export.ExportConfiguration;
import org.primefaces.util.EscapeUtils;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

public class JsonDataTableExporterExport extends DataTableExporter {

    private OutputStreamWriter osw;
    private PrintWriter writer;

    @Override
    protected void exportCells(DataTable dataTable, Object document) {
        PrintWriter writer = (PrintWriter) document;
        for (UIColumn col : dataTable.getColumns()) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }

            if (col.isRendered() && col.isExportable()) {
                String columnTag = getColumnTag(col);
                addColumnValue(writer, col.getChildren(), columnTag, col);
            }
        }
    }

    @Override
    protected void doExport(FacesContext facesContext, DataTable dataTable, ExportConfiguration exportConfiguration, int index) throws IOException {
        writer.append("" + dataTable.getId() + "\n");

        if (exportConfiguration.isPageOnly()) {
            exportPageOnly(facesContext, dataTable, writer);
        } else if (exportConfiguration.isSelectionOnly()) {
            exportSelectionOnly(facesContext, dataTable, writer);
        } else {
            exportAll(facesContext, dataTable, writer);
        }

        writer.append("" + dataTable.getId() + "");

        dataTable.setRowIndex(-1);
    }

    protected String getColumnTag(UIColumn column) {
        String headerText = (column.getExportHeaderValue() != null) ? column.getExportHeaderValue() : column.getHeaderText();
        UIComponent facet = column.getFacet("header");
        String columnTag;

        if (headerText != null) {
            columnTag = headerText.toLowerCase();
        }
        else if (facet != null) {
            columnTag = exportValue(FacesContext.getCurrentInstance(), facet).toLowerCase();
        }
        else {
            throw new FacesException("No suitable xml tag found for " + column);
        }

        return EscapeUtils.forXmlTag(columnTag);
    }

    protected void addColumnValue(PrintWriter writer, List<UIComponent> components, String tag, UIColumn column) {
        FacesContext context = FacesContext.getCurrentInstance();

        writer.append("\t\t" + tag + "");

        if (column.getExportFunction() != null) {
            writer.append(EscapeUtils.forXml(exportColumnByFunction(context, column)));
        }
        else {
            for (UIComponent component : components) {
                if (component.isRendered()) {
                    String value = exportValue(context, component);
                    if (value != null) {
                        writer.append(EscapeUtils.forXml(value));
                    }
                }
            }
        }

        writer.append("" + tag + "\n");
    }
}
