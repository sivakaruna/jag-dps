package ca.bc.gov.open.pssg.rsbc.dps.cache;

public interface StorageService {

    void put(String key, byte[] content);

    byte[] get(String key);

}
