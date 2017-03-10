package kgk.mobile.external.kgkservicesocketnio;


final class ResponseHandler {

    private byte[] response = null;

    ////

    synchronized boolean handleResponse(byte[] response) {
        this.response = response;
        this.notify();
        return true;
    }

    synchronized void waitForResponse() {
        while(this.response == null) {
            try {
                this.wait();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                // TODO Consider logging
            }
        }
    }
}
