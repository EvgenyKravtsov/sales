package kgk.mobile.external.network.json;


import org.json.JSONObject;

public class JsonAnswer {

    ////

    private final JsonAnswerType type;
    private final JSONObject message;

    ////

    JsonAnswer(JsonAnswerType type, JSONObject message) {
        this.type = type;
        this.message = message;
    }

    ////

    public JsonAnswerType getType() {
        return type;
    }

    public JSONObject getMessage() {
        return message;
    }
}
