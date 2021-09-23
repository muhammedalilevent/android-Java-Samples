package com.levent.mqtt11.model;

 import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1

public class PttRequest {
    @JsonProperty("UniqueId")
    public String uniqueId;
    @JsonProperty("Stream")
    public byte[] stream;
    @JsonProperty("IsFinished")
    public boolean isFinished;
    @JsonProperty("ChunkSize")
    public int chunkSize;
    @JsonProperty("Extenstion")
    public String extenstion;
    @JsonProperty("ClientID")
    public String clientId;
}
