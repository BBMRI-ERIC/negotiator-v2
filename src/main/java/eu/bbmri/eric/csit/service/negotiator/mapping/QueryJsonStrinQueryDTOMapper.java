package eu.bbmri.eric.csit.service.negotiator.mapping;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.samply.bbmri.negotiator.rest.RestApplication;
import de.samply.bbmri.negotiator.rest.dto.QueryDTO;

import java.io.IOException;

public class QueryJsonStrinQueryDTOMapper {
    /**
     * Convert the string to an object, so that we can store it in the database.
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    public static QueryDTO getQueryDTO(String queryString) throws JsonParseException, JsonMappingException, IOException {
        RestApplication.NonNullObjectMapper mapperProvider = new RestApplication.NonNullObjectMapper();
        ObjectMapper mapper = mapperProvider.getContext(ObjectMapper.class);
        return mapper.readValue(queryString, QueryDTO.class);
    }
}
