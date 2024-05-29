package com.nhnacademy.aiot.ruleengine.dto.rule;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import lombok.*;

import java.io.IOException;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonSerialize(keyUsing = MqttInInfoKeySerializer.class)
@JsonDeserialize(keyUsing = MqttInInfoKeyDeserializer.class)
public class MqttInInfo {
    private String mqttUrl;
    private String topic;

    public String getPlace() {
        return getTopic().split("/")[Constants.PLACE_INDEX];
    }

    public String getMeasurement() {
        return getTopic().split("/")[Constants.MEASUREMENT_INDEX];
    }
}

class MqttInInfoKeySerializer extends JsonSerializer<MqttInInfo> {
    @Override
    public void serialize(MqttInInfo value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeFieldName(value.getMqttUrl() + "|" + value.getTopic());
    }
}

class MqttInInfoKeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt){
        String[] parts = key.split("\\|");
        return new MqttInInfo(parts[0], parts[1]);
    }
}

