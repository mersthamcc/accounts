package cricket.merstham.website.accounts.services;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

import static java.util.Objects.isNull;

public class SerializationService {
    private static final Logger LOG = LoggerFactory.getLogger(SerializationService.class);
    private static SerializationService instance;

    private final ObjectMapper objectMapper;

    public SerializationService() {
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.findAndRegisterModules();
    }

    public <T> String serialise(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOG.error("Error serialising object", e);
            throw new RuntimeException("Error serialising object", e);
        }
    }

    public <T> T deserialise(InputStream serialised, Class<T> clazz) {
        try {
            return objectMapper.readValue(serialised, clazz);
        } catch (IOException e) {
            LOG.error("Error deserialising object", e);
            throw new RuntimeException("Error serialising object", e);
        }
    }

    public <T> T deserialise(String serialised, Class<T> clazz) {
        try {
            return objectMapper.readValue(serialised, clazz);
        } catch (JsonProcessingException e) {
            LOG.error("Error deserialising object", e);
            throw new RuntimeException("Error serialising object", e);
        }
    }

    public JsonNode deserialise(String serialised) {
        try {
            return objectMapper.readTree(serialised);
        } catch (JsonProcessingException e) {
            LOG.error("Error deserialising object", e);
            throw new RuntimeException("Error serialising object", e);
        }
    }

    public static SerializationService getInstance() {
        if (isNull(instance)) {
            instance = new SerializationService();
        }
        return instance;
    }
}
