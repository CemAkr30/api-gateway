package tr.gov.gib.ebyn.api.gateway.core.services;

public interface IAuthService {
    boolean isAuthorized(String apiKey, String token);
}
