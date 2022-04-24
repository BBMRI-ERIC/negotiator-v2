package de.samply.bbmri.negotiator.control.researcher;

import de.samply.bbmri.negotiator.control.QueryBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "requestTypeConverter")
public class RequestTypeConverter implements Converter {
    private static final Logger logger = LogManager.getLogger(RequestTypeConverter.class);

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent uiComponent, String queryTypeId) {
        //logger.info("getAsObject:" + queryTypeId);

        ValueExpression vex = ctx.getApplication().getExpressionFactory().createValueExpression(ctx.getELContext(),
                "#{queryBean}", QueryBean.class);

        QueryBean queryBean = (QueryBean) vex.getValue(ctx.getELContext());
        return queryBean.getQueryTypeFromList(Integer.valueOf(queryTypeId));
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object queryType) {
        if(queryType == null){
            logger.warn("queryType is NULL!");
            return null;
        }
        return ((RequestType) queryType).getId().toString();
    }
}
