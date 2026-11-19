package com.example.TrujillanosSAC.compras.service.generic;

public interface CrudService<REQ, RES, ID> {
    RES create(REQ request);
    RES update(ID id, REQ request);
    RES read(ID id);
    void delete(ID id);
    Iterable<RES> readAll();
}
