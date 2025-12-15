package tr.gov.gib.ebyn.api.gateway.responses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityTokenCheckResponseTest {

  @Test
  void testMainClassConstructorsAndGetters() {
    // Test no-args constructor
    SecurityTokenCheckResponse response1 = new SecurityTokenCheckResponse();
    assertNull(response1.getData());
    assertNull(response1.getServiceStatus());

    // Test all-args constructor
    SecurityTokenCheckResponse.DataResponse data = new SecurityTokenCheckResponse.DataResponse();
    SecurityTokenCheckResponse.ServiceStatus status = new SecurityTokenCheckResponse.ServiceStatus();
    SecurityTokenCheckResponse response2 = new SecurityTokenCheckResponse(data, status);

    assertEquals(data, response2.getData());
    assertEquals(status, response2.getServiceStatus());
  }

  @Test
  void testDataResponseClass() {
    // Test no-args constructor
    SecurityTokenCheckResponse.DataResponse data1 = new SecurityTokenCheckResponse.DataResponse();
    assertNull(data1.getUserInfo());

    // Test all-args constructor
    SecurityTokenCheckResponse.UserInfo userInfo = new SecurityTokenCheckResponse.UserInfo();
    SecurityTokenCheckResponse.DataResponse data2 = new SecurityTokenCheckResponse.DataResponse(userInfo);

    assertEquals(userInfo, data2.getUserInfo());
  }

  @Test
  void testServiceStatusClass() {
    // Test no-args constructor
    SecurityTokenCheckResponse.ServiceStatus status1 = new SecurityTokenCheckResponse.ServiceStatus();
    assertNull(status1.getCode());
    assertNull(status1.getMessage());
    assertNull(status1.getMessageDetails());
    assertNull(status1.getTime());

    // Test all-args constructor
    String code = "200";
    String message = "Success";
    String details = "Operation completed";
    String time = "2023-01-01T00:00:00";

    SecurityTokenCheckResponse.ServiceStatus status2 =
        new SecurityTokenCheckResponse.ServiceStatus(code, message, details, time);

    assertEquals(code, status2.getCode());
    assertEquals(message, status2.getMessage());
    assertEquals(details, status2.getMessageDetails());
    assertEquals(time, status2.getTime());
  }

  @Test
  void testUserInfoClass() {
    // Test no-args constructor
    SecurityTokenCheckResponse.UserInfo user1 = new SecurityTokenCheckResponse.UserInfo();
    assertNull(user1.getKullaniciKodu());
    assertNull(user1.getAd());
    assertNull(user1.getSoyad());
    assertNull(user1.getTcNo());
    assertNull(user1.getVergiNo());
    assertNull(user1.getKullaniciTipi());
    assertNull(user1.getAdSoyad());

    // Test all-args constructor
    String kullaniciKodu = "user123";
    String ad = "John";
    String soyad = "Doe";
    String tcNo = "12345678901";
    String vergiNo = "9876543210";
    Integer kullaniciTipi = 1;
    String adSoyad = "John Doe";

    SecurityTokenCheckResponse.UserInfo user2 = new SecurityTokenCheckResponse.UserInfo(
        kullaniciKodu, ad, soyad, tcNo, vergiNo, kullaniciTipi, adSoyad);

    assertEquals(kullaniciKodu, user2.getKullaniciKodu());
    assertEquals(ad, user2.getAd());
    assertEquals(soyad, user2.getSoyad());
    assertEquals(tcNo, user2.getTcNo());
    assertEquals(vergiNo, user2.getVergiNo());
    assertEquals(kullaniciTipi, user2.getKullaniciTipi());
    assertEquals(adSoyad, user2.getAdSoyad());
  }

  @Test
  void testToString() {
    // Main class
    SecurityTokenCheckResponse response = new SecurityTokenCheckResponse();
    assertTrue(response.toString().contains("SecurityTokenCheckResponse"));

    // DataResponse class
    SecurityTokenCheckResponse.DataResponse data = new SecurityTokenCheckResponse.DataResponse();
    assertTrue(data.toString().contains("DataResponse"));

    // ServiceStatus class
    SecurityTokenCheckResponse.ServiceStatus status = new SecurityTokenCheckResponse.ServiceStatus();
    assertTrue(status.toString().contains("ServiceStatus"));

    // UserInfo class
    SecurityTokenCheckResponse.UserInfo user = new SecurityTokenCheckResponse.UserInfo();
    assertTrue(user.toString().contains("UserInfo"));
  }

  @Test
  void testSetters() {
    // Test setters for all classes
    SecurityTokenCheckResponse response = new SecurityTokenCheckResponse();
    SecurityTokenCheckResponse.DataResponse data = new SecurityTokenCheckResponse.DataResponse();
    SecurityTokenCheckResponse.ServiceStatus status = new SecurityTokenCheckResponse.ServiceStatus();
    SecurityTokenCheckResponse.UserInfo user = new SecurityTokenCheckResponse.UserInfo();

    response.setData(data);
    response.setServiceStatus(status);

    data.setUserInfo(user);

    status.setCode("200");
    status.setMessage("OK");
    status.setMessageDetails("Details");
    status.setTime("now");

    user.setKullaniciKodu("test");
    user.setAd("Test");
    user.setSoyad("User");
    user.setTcNo("123");
    user.setVergiNo("456");
    user.setKullaniciTipi(1);
    user.setAdSoyad("Test User");

    assertEquals(data, response.getData());
    assertEquals(status, response.getServiceStatus());
    assertEquals(user, data.getUserInfo());
    assertEquals("200", status.getCode());
    assertEquals("Test", user.getAd());
  }
}