package tr.gov.gib.ebyn.api.gateway.core.services;

public interface ILogService {
    void logRequest(String message, String className, String methodName, String additionalInfo);
    void logError(String message, String className, String methodName, String additionalInfo, Throwable throwable);
}
