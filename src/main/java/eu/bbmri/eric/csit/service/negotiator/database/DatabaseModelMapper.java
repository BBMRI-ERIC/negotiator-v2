package eu.bbmri.eric.csit.service.negotiator.database;

import org.jooq.Record;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.jooq.RecordValueReader;

import java.util.ArrayList;
import java.util.List;

public class DatabaseModelMapper {

    ModelMapper modelMapper;

    public DatabaseModelMapper() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().addValueReader(new RecordValueReader());
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.UNDERSCORE);
        modelMapper.getConfiguration().setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE);
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
    }

    public <T> T map(Record record, Class<? extends T> clazz) {
        return modelMapper.map(record, clazz);
    }

    public <T> List<T> map(List<? extends Record> records, Class<? extends T> clazz) {
        List<T> target = new ArrayList<>();

        for(Record record : records) {
            target.add(map(record, clazz));
        }
        return target;
    }
}
