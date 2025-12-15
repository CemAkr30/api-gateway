package tr.gov.gib.ebyn.api.gateway.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SecurityTokenCheckResponse {
  private DataResponse data;
  private ServiceStatus serviceStatus;

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class DataResponse {
    private UserInfo userInfo;
  }

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ServiceStatus {
    private String code;
    private String message;
    private String messageDetails;
    private String time;
  }

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class UserInfo {
    private String kullaniciKodu;
    private String ad;
    private String soyad;
    private String tcNo;
    private String vergiNo;
    private Integer kullaniciTipi;
    private String adSoyad;
  }
}
