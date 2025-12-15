package tr.gov.gib.ebyn.api.gateway.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.util.MultiValueMap;
import tr.gov.gib.ebyn.api.gateway.responses.SecurityTokenCheckResponse.UserInfo;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HeaderUtilTest {

  @InjectMocks
  private HeaderUtil headerUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testToPascalCaseWithHyphen() {
    assertEquals("Tc-No", headerUtil.toCaseWithHyphen("tcNo"));
    assertEquals("First-Name", headerUtil.toCaseWithHyphen("firstName"));
    assertEquals("Last-Name", headerUtil.toCaseWithHyphen("lastName"));
  }

  @Test
  void testConvertUserInfoToRequestHeadersInPascalCase() {
    UserInfo userInfo = new UserInfo();
    userInfo.setTcNo("12345612345");
    userInfo.setAd("Ad");
    userInfo.setSoyad("Soyad");
    userInfo.setVergiNo("12312312312");
    userInfo.setKullaniciKodu("123123");
    userInfo.setKullaniciTipi(1);

    Map<String, String> headers = headerUtil.convertUserInfoToRequestHeadersInCase(userInfo);
    assertEquals("12345612345", headers.get("Tc-No"));
    assertEquals("Ad", headers.get("Ad"));
    assertEquals("Soyad", headers.get("Soyad"));
    assertEquals("12312312312", headers.get("Vergi-No"));
    assertEquals("123123", headers.get("Kullanici-Kodu"));
    assertEquals("1", headers.get("Kullanici-Tipi"));
  }

  @Test
  void testConvertUserInfoToResponseHeadersInPascalCase() {
    UserInfo userInfo = new UserInfo();
    userInfo.setTcNo("123456");
    userInfo.setAd("Ad");
    userInfo.setSoyad("Soyad");
    userInfo.setVergiNo("12312312312");
    userInfo.setKullaniciKodu("123123");
    userInfo.setKullaniciTipi(1);

    MultiValueMap<String, String> headers = headerUtil.convertUserInfoToResponseHeadersInCase(userInfo);
    assertEquals(Collections.singletonList("123456"), headers.get("Tc-No"));
    assertEquals(Collections.singletonList("Ad"), headers.get("Ad"));
    assertEquals(Collections.singletonList("Soyad"), headers.get("Soyad"));
    assertEquals(Collections.singletonList("12312312312"), headers.get("Vergi-No"));
    assertEquals(Collections.singletonList("123123"), headers.get("Kullanici-Kodu"));
    assertEquals(Collections.singletonList("1"), headers.get("Kullanici-Tipi"));
  }
}