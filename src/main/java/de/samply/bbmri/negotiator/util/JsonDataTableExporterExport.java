package de.samply.bbmri.negotiator.util;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.export.DataTableExporter;
import org.primefaces.component.export.ExportConfiguration;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.EscapeUtils;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class JsonDataTableExporterExport extends DataTableExporter {

    private StringBuilder sb;

    protected StringBuilder createStringBuilder() {
        return new StringBuilder();
    }
    protected StringBuilder getStringBuilder() {
        return this.sb;
    }

    @Override
    protected void doExport(FacesContext context, DataTable table, ExportConfiguration exportConfiguration, int index) throws IOException {

        String pattern = "dd-MM-yyyy HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(new Date());
        JSONObject jsonObject = new JSONObject();

        ExternalContext externalContext = context.getExternalContext();
        this.configureResponse(externalContext, exportConfiguration.getOutputFileName(), exportConfiguration.getEncodingType());
        this.sb = this.createStringBuilder();
        this.sb.append("{ 'queryTable': [");

        this.exportAll(context, table, this.sb);

        this.sb.append("], date: '" + date + "'}");
        Writer writer = externalContext.getResponseOutputWriter();

        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        JSONObject elements = new JSONObject();
        try {
            String s = this.sb.toString();
            elements = (JSONObject) parser.parse(this.sb.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        writer.write(jsonObject.escape(elements.toJSONString()));
        writer.flush();
        writer.close();
    }

    void createJsonObject(FacesContext context, DataTable table) {
        int first = table.getFirst();
        int rowCount = table.getRowCount();
        int rows = table.getRows();
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            exportRow(table, rowIndex);
        }
    }

    @Override
    protected void preRowExport(DataTable table, Object document) {
        ((StringBuilder)document).append("\t{");
    }

    @Override
    protected void postRowExport(DataTable table, Object document) {
        ((StringBuilder)document).append("\t},\n");
    }

    @Override
    protected void exportCells(DataTable table, Object document) {
        StringBuilder builder = (StringBuilder)document;
        Iterator columnsIterator = table.getColumns().iterator();

        while(columnsIterator.hasNext()) {
            UIColumn col = (UIColumn)columnsIterator.next();
            if (col instanceof DynamicColumn) {
                ((DynamicColumn)col).applyStatelessModel();
            }

            if (col.isRendered() && col.isExportable()) {
                String columnTag = this.getColumnTag(col);

                try {
                    this.addColumnValue(builder, col.getChildren(), columnTag, col);
                } catch (IOException var8) {
                    throw new FacesException(var8);
                }
            }
        }
    }

    protected String getColumnTag(UIColumn column) {
        String headerText = column.getExportHeaderValue() != null ? column.getExportHeaderValue() : column.getHeaderText();
        UIComponent facet = column.getFacet("header");
        String columnTag;
        if (headerText != null) {
            columnTag = headerText.toLowerCase();
        } else {
            if (!ComponentUtils.shouldRenderFacet(facet)) {
                throw new FacesException("No suitable xml tag found for " + column);
            }

            columnTag = this.exportValue(FacesContext.getCurrentInstance(), facet).toLowerCase();
        }

        return EscapeUtils.forXmlTag(columnTag);
    }

    protected void addColumnValue(StringBuilder builder, List<UIComponent> components, String tag, UIColumn column) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        builder.append("\t\t'" + tag + "': ");
        if(!tag.equals("jsontext")) {
            builder.append("'");
        }
        if (column.getExportFunction() != null) {
            builder.append(EscapeUtils.forXml(this.exportColumnByFunction(context, column)));
        } else {
            Iterator iterator = components.iterator();

            while(iterator.hasNext()) {
                UIComponent component = (UIComponent)iterator.next();
                if (component.isRendered()) {
                    String value = this.exportValue(context, component);
                    if (value != null) {
                        if(!tag.equals("jsontext")) {
                            builder.append(EscapeUtils.forXml(value).replaceAll("\n", "\\n"));
                        } else {
                            builder.append(value);
                        }

                    }
                }
            }
        }
        if(!tag.equals("jsontext")) {
            builder.append("'");
        }
        builder.append(",\n");
    }

    protected void configureResponse(ExternalContext externalContext, String filename, String encodingType) {
        externalContext.setResponseContentType("application/json");
        externalContext.setResponseHeader("Expires", "0");
        externalContext.setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        externalContext.setResponseHeader("Pragma", "public");
        externalContext.setResponseHeader("Content-disposition", ComponentUtils.createContentDisposition("attachment", filename + ".json"));
        externalContext.addResponseCookie("primefaces.download", "true", Collections.emptyMap());
    }

}
