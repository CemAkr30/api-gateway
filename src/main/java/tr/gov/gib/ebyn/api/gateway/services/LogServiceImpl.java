package tr.gov.gib.ebyn.api.gateway.services;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import tr.gov.gib.ebyn.api.gateway.core.services.ILogService;


@Service
@Log4j2
public class LogServiceImpl
    implements ILogService {

    public void logRequest(String message, String className, String methodName, String additionalInfo) {
        log.info(formatLogMessage(message, className, methodName, additionalInfo));
    }

    public void logError(String message, String className, String methodName, String additionalInfo, Throwable throwable) {
        log.error(formatLogMessage(message, className, methodName, additionalInfo), throwable);
    }

    private String formatLogMessage(String message, String className, String methodName, String additionalInfo) {
        return String.format("[Class: %s, Method: %s] %s - %s", className, methodName, message, additionalInfo);
    }
}
