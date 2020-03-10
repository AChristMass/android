package fr.igm.robotmissions.objects.ifc;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class IfcPointDeserializer implements JsonDeserializer<IfcPoint> {
    public IfcPoint deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        float x = jsonElement.getAsJsonArray().get(0).getAsFloat();
        float y = jsonElement.getAsJsonArray().get(1).getAsFloat();
        return new IfcPoint(x, y);
    }
}