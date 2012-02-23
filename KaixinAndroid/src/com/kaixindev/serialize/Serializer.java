package com.kaixindev.serialize;

public interface Serializer {
    public Object unserialize(byte[] data);
    public byte[] serialize(Object object);
}
