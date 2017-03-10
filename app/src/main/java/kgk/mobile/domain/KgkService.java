package kgk.mobile.domain;


import java.util.List;

public interface KgkService {

    List<SalesOutlet> getSalesOutlets();

    boolean isConnectionAvailable();
}
